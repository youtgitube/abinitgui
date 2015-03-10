/*
 Copyright (c) 2009-2014 Flavio Miguel ABREU ARAUJO (flavio.abreuaraujo@uclouvain.be)
                         Yannick GILLET (yannick.gillet@uclouvain.be)

Universit� catholique de Louvain, Louvain-la-Neuve, Belgium
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
notice, this list of conditions, and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright
notice, this list of conditions, and the disclaimer that follows
these conditions in the documentation and/or other materials
provided with the distribution.

3. The names of the author may not be used to endorse or promote
products derived from this software without specific prior written
permission.

In addition, we request (but do not require) that you include in the
end-user documentation provided with the redistribution and/or in the
software itself an acknowledgement equivalent to the following:
"This product includes software developed by the
Abinit Project (http://www.abinit.org/)."

THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED.  IN NO EVENT SHALL THE JDOM AUTHORS OR THE PROJECT
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
SUCH DAMAGE.

For more information on the Abinit Project, please see
<http://www.abinit.org/>.
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

            // Cr�ation du contenu du fichier de configuration (*.files)
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

            // Cr�ation du fichier de configuration
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
            // R�ception (copie) du fichier d'output si celui-ci est distant
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
            // R�ception (copie) du fichier d'output si celui-ci est distant
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