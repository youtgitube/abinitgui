/*
 AbinitGUI - Created in 2009
 
 Copyright (c) 2009-2015 Flavio Miguel ABREU ARAUJO (abreuaraujo.flavio@gmail.com)
                         Yannick GILLET (yannick.gillet@hotmail.com)

 Université catholique de Louvain, Louvain-la-Neuve, Belgium
 All rights reserved.

 AbinitGUI is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 AbinitGUI is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with AbinitGUI.  If not, see <http://www.gnu.org/licenses/>.

 For more information on the project, please see
 <http://gui.abinit.org/>.
 */

package abinitgui.projects;

import abinitgui.pseudos.Atom;
import abinitgui.core.MainFrame;
import abinitgui.core.RetMSG;
import abinitgui.core.Utils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class AnaddbSimulation extends Simulation {

    private String name = "default";
    private String inputFileName = "./test3.in";
    private String DDBFileName = "./test3o_DDB";

    public AnaddbSimulation() {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getInputFileName() {
        return inputFileName;
    }

    @Override
    public void setInputFileName(String fileName) {
        this.inputFileName = fileName;
    }
    
    public String getDDBFileName() {
        return DDBFileName;
    }

    public void setDDBFileName(String fileName) {
        this.DDBFileName = fileName;
    }

    @Override
    public String toString() {
        return name;
        //return "Simulation(name = " + name + "; fileName = " + inputFileName + ")";
    }
    
    @Override
    public void createFileTree(Machine mach)
    {
        String path = mach.getSimulationPath();
        if (path.equals("")) {
            path = ".";
        }
        mach.createTree(path);
        if(mach.getType() == Machine.REMOTE_MACHINE || mach.getType() == Machine.GATEWAY_MACHINE)
        {
            MainFrame.getLocalExec().createTree(path);
        }
        
    }
    
    @Override
    public boolean submit()
    {
        Machine mach = MainFrame.getMachineDatabase().getMachine(job.getMachineName());
        
        if(mach == null)
        {
            MainFrame.printERR("No machine selected for this simulation");
            return false;
        }
        
        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        boolean isLocalMachine = (mach.getType() == Machine.LOCAL_MACHINE);
        boolean isRemoteGatewayMachine = (mach.getType() == Machine.GATEWAY_MACHINE);
        boolean isRemoteAbinitMachine = (mach.getType() == Machine.REMOTE_MACHINE);

        String rootPath = mach.getSimulationPath();

        String pathToAbinit = mach.getAbinitPath();

        SubmissionScript script = getRemoteJob().getScript();

        String inputFile = inputFileName;
        String ddbFile = DDBFileName;

        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        if (isLocalMachine && Utils.osName().startsWith("Windows")) {
            MainFrame.printERR("Please connect to a remote host before submitting a simulation on Windows platform !");
            return false;
        }
        
        if(!mach.isConnected())
        {
            MainFrame.printOUT("Trying to connect ...");
            mach.connection();
        }

//        if ((remoteGatewayRadioButton_isSelected
//                || remoteAbinitRadioButton_isSelected) && remoteExec == null) {
//            mf.printERR("Please connect to a ABINIT host before submitting a simulation !");
//            return false;
//        }

        this.createFileTree(mach);

        String inputFolder = "input";
        String outputFolder = "output";
        String wholedataFolder = "wholedata";
        String pseudopotFolder = "pseudopot";
        String logfilesFolder = "logfiles";

        // *************************************************************

        String cwd;

        String CMD = "pwd";

        RetMSG retmsg = mach.sendCommand(CMD);
        if(retmsg.getRetCode() == RetMSG.SUCCES)
        {
            cwd = Utils.removeEndl(retmsg.getRetMSG());
        }
        else
        {
            MainFrame.printERR("Not able to get working directory !");
            return false;
        }

        // ********************************************************************************************************************************

        String inputFN;

        String sep = Utils.fileSeparator();
        inputFN = Utils.getLastToken(inputFile.replace('\\', '/'), "/");
        
        String inputDDB = Utils.getLastToken(ddbFile.replace('\\', '/'),"/");


        // Test de l'existance de inputfile
        if (!Utils.exists(inputFile)) {
            MainFrame.printERR("The file " + inputFile + " doesn't exist !");
            return false;
        }

        String simName = null;
        if (inputFN != null) {
            int idx = inputFN.indexOf('.');
            if (idx > 0 && idx < inputFN.length()) {
                simName = inputFN.substring(0, idx);
            } else {
                simName = inputFN;
            }
        }

        if (!inputFile.equals("")) {
            script.setAbinitPath(pathToAbinit + "anaddb");
            script.setInputPath(cwd + "/" + rootPath.replaceFirst("./", "") + "/" + simName + "_anaddb.files");
            script.setLogPath(cwd + "/"
                    + rootPath.replaceFirst("./", "") + "/" + logfilesFolder + "/" + simName + "_anaddb.log");
            
            
            script.setSimName(simName);

            // Envoie (copie) du fichier d'input
            String inputFileR = rootPath + "/" + inputFolder + "/" + inputFN;
            mach.putFile(inputFile + " " + inputFileR);
            
            String ddbFileR = rootPath + "/" + inputFolder + "/" + inputDDB;
            mach.putFile(ddbFile + " " + inputDDB);

            if (isRemoteGatewayMachine
                    || isRemoteAbinitMachine) {
                if (Utils.osName().startsWith("Windows")) {
                    mach.sendCommand("dos2unix " + inputFileR);
                    mach.sendCommand("dos2unix " + ddbFileR);
                    // TODO Util.dos2unix(new File(inputFileR)); // Transformer avant d'envoyer le fichier
                }
            }

            String configFileContent = "";
            configFileContent += cwd + "/" + rootPath.replaceFirst("./", "")
                    + "/" + inputFolder + "/" + inputFN + "\n";
            configFileContent += cwd + "/" + rootPath.replaceFirst("./", "")
                    + "/" + outputFolder + "/" + simName + "/" + simName + "_anaddb.out\n";
            configFileContent += cwd + "/" + rootPath.replaceFirst("./", "")
                    + "/" + inputFolder + "/" + inputDDB + "\n";
            configFileContent += cwd + "/" + rootPath.replaceFirst("./", "")
                    + "/" + wholedataFolder + "/" + simName + "/band_eps\n";
            configFileContent += cwd + "/" + rootPath.replaceFirst("./", "")
                    + "/" + wholedataFolder + "/" + simName + "/gkk\n";
            configFileContent += cwd + "/" + rootPath.replaceFirst("./", "")
                    + "/" + wholedataFolder + "/" + simName + "/elph\n";
            configFileContent += cwd + "/" + rootPath.replaceFirst("./", "")
                    + "/" + wholedataFolder + "/" + simName + "/ddk\n";

            try {
                String FILESfileName = rootPath + sep + simName + "_anaddb.files";
                OutputStreamWriter fw = new OutputStreamWriter(
                        new FileOutputStream(FILESfileName), Utils.getCharset());
                //FileWriter fw = new FileWriter(FILESfileName);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter pw = new PrintWriter(bw);
                pw.print(configFileContent);
                pw.close();
                bw.close();
                fw.close();
            } catch (IOException e) {
                MainFrame.printERR("The configuration file (*.files) could not"
                        + " be created !");
                return false;
            }
            
            if (isRemoteGatewayMachine
                    || isRemoteAbinitMachine) {
                // Envoie du fichier de configuration
                String configFile = rootPath + sep + simName + "_anaddb.files";
                String configFileR = rootPath + "/" + simName + "_anaddb.files";

                if (Utils.osName().startsWith("Windows")) {
                    Utils.dos2unix(new File(configFile));
                }

                mach.putFile(configFile + " " + configFileR);

                //if (Utils.osName().startsWith("Windows")) {
                //    sendCommand("dos2unix " + configFileR);
                //}
            }

            // Creation du dossier simName dans wholedataFolder
            Utils.mkdir(rootPath + "/" + wholedataFolder + "/" + simName);
            if (isRemoteGatewayMachine
                    || isRemoteAbinitMachine) {
                // We do it only if machine is remote !
                mach.mkdir(rootPath + "/" + wholedataFolder + "/" + simName);
            }

            RemoteJob rj = mach.submitSimulation(this, rootPath, simName);
            this.setRemoteJob(rj);
            //this.getRemoteJob().submit(rootPath,simName);
            //this.getRemoteJob().printInfos();
            
        } else {
            MainFrame.printERR("Please setup the inputfile textfield !");
            return false;
        }

        if (isLocalMachine) {
            MainFrame.printOUT("The simulation was submitted to the local Abinit machine.");
        } else {
            MainFrame.printOUT("The simulation was submitted to the remote Abinit"
                    + " machine "+mach.getName()+".");
        }
        MainFrame.printOUT("The submission thread ended successfully! (Abinit)");
        return true;
    }
    
    @Override
    public void downloadLog()
    {
        String machineName = getRemoteJob().getMachineName();
        Machine mach = MainFrame.getMachineDatabase().getMachine(machineName);
        if(mach == null)
        {
            MainFrame.printERR("Please select a machine!");
            return;
        }

        if(!mach.isConnected())
        {
            mach.connection();
        }

        String rootPath = mach.getSimulationPath();
        String outputFolder = "logfiles";

        String inputFile;
        String inputFileName;

        MainFrame.getLocalExec().createTree(rootPath);

        inputFile = getInputFileName();
        inputFileName = Utils.getLastToken(inputFile.replace('\\', '/'), "/");


        // Test de l'existance de inputfile
        if (!Utils.exists(inputFile)) {
            MainFrame.printERR("The file " + inputFile + " doesn't exist !");
            return;
        }

        String simName = null;
        if (inputFileName != null) {
            if (!inputFileName.equals("")) {
                int idx = inputFileName.indexOf('.');
                if (idx > 0 && idx < inputFileName.length()) {
                    simName = inputFileName.substring(0, idx);
                } else {
                    simName = inputFileName;
                }
            } else {
                MainFrame.printERR("inputFileName == \"\"");
                return;
            }
        } else {
            MainFrame.printERR("inputFileName == null");
            return;
        }

        if (!inputFile.equals("")) {

            String outputPath = rootPath + "/" + outputFolder;
            String fileName = outputPath + "/" + simName + "_anaddb.log";
            System.out.println(fileName);
            if (mach.getType() == Machine.REMOTE_MACHINE || mach.getType() == Machine.GATEWAY_MACHINE) {
                mach.getFile(fileName + " " + fileName);
                if (Utils.osName().startsWith("Windows")) {
                    //sendCommand("dos2unix " + file);
                    Utils.unix2dos(new File(fileName));
                }
            }

            // ****************************************************************************
            // Tester l'existence du fichier
            MainFrame.editFile(fileName, false);
            // ****************************************************************************
        } else {
            MainFrame.printERR("Please setup the inputfile textfield !");
        }
    }
    
    @Override
    public void downloadOutput()
    {
        String machineName = getRemoteJob().getMachineName();
        Machine mach = MainFrame.getMachineDatabase().getMachine(machineName);
        if(mach == null)
        {
            MainFrame.printERR("Please select a machine !");
        }

        if(!mach.isConnected())
        {
            mach.connection();
        }

        String rootPath = mach.getSimulationPath();
        String outputFolder = "output";

        String inputFile = "";
        String inputFileName = "";

        MainFrame.getLocalExec().createTree(rootPath);

        inputFile = getInputFileName();
        inputFileName = Utils.getLastToken(inputFile.replace('\\', '/'), "/");

        // Test de l'existance de inputfile
        if (!Utils.exists(inputFile)) {
            MainFrame.printERR("The file " + inputFile + " doesn't exist !");
            return;
        }

        String simName = null;
        if (inputFileName != null) {
            if (!inputFileName.equals("")) {
                int idx = inputFileName.indexOf('.');
                if (idx > 0 && idx < inputFileName.length()) {
                    simName = inputFileName.substring(0, idx);
                } else {
                    simName = inputFileName;
                }
            } else {
                MainFrame.printERR("inputFileName == \"\"");
                return;
            }
        } else {
            MainFrame.printERR("inputFileName == null");
            return;
        }

        if (!inputFile.equals("")) {

            String outputPath = rootPath + "/" + outputFolder;
            String fileName = outputPath + "/" + simName + "_anaddb.out";
            //if (mach.getType() == Machine.REMOTE_MACHINE || mach.getType() == Machine.GATEWAY_MACHINE) {
                String file = "";
                String outputFiles = mach.getOutputFiles(fileName + "*");
                StringTokenizer st = new StringTokenizer(outputFiles, "\n");
                while (st.hasMoreElements()) {
                    file = st.nextToken();
                    MainFrame.printOUT("File = " + file);
                    //if (Utils.osName().startsWith("Windows")) {
                    //    sendCommand("unix2dos " + file);
                    //}
                    if (mach.getType() == Machine.REMOTE_MACHINE || mach.getType() == Machine.GATEWAY_MACHINE) {
                        mach.getFile(file + " " + file);
                        if (Utils.osName().startsWith("Windows")) {
                            //sendCommand("dos2unix " + file);
                            Utils.unix2dos(new File(file));
                        }
                    }
                }
                fileName = file; // Prend le nom du dernier fichier!
            //}
            System.out.println(fileName);

            // ****************************************************************************
            // Tester l'existence du fichier
            MainFrame.editFile(fileName, false);
            // ****************************************************************************
        } else {
            MainFrame.printERR("Please setup the inputfile textfield !");
            return;
        }
    }
}