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

package abinitgui.parser;

import abinitgui.core.MainFrame;
import java.awt.Color;
import java.awt.Component;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JTable;
//import javax.swing.*;
import javax.swing.table.*;
import net.sourceforge.jeval.EvaluationException;

public class GUIEditor extends JFrame {
    
    private String fileName;
    private MainFrame mf;
    private DatasetModel model;
    private ArrayList<HashMap<String,Object>> dataTable;
    
    private AbinitInputJEval inputEval = null;
    private AbinitInputMapping inputMapping = null;
    
    //private String[] toHide = new String[]{"ndtset","spgroup","bravais","kptns","nelect",
    //             "ntyppure","prtpmp","rprimd_orig", "rprimd","xclevel","ziontypat"};
    private HashMap<String,String> mapVar = new HashMap<>();
    
    /**
     * Creates new form GUIEditor
     */
    public GUIEditor() {
        initComponents();
        
        dataTable = new ArrayList<>();
        
        model = new DatasetModel();
        jTable1.setModel(model);
        
        jTable1.setDefaultRenderer(Object.class, new DatasetRenderer());
        
        mapVar.put("acell_orig", "acell");
        mapVar.put("rprimd_orig", "rprimd");
        mapVar.put("rprim_orig","rprim");
        mapVar.put("amu_orig", "amu");
        mapVar.put("xred_orig", "xred");
        mapVar.put("occ_orig","occ");
    }
    
    public void loadFile(String fileName)
    {
        dataTable.clear();
        this.fileName = fileName;
        
        inputEval = new AbinitInputJEval();

        try{
            inputMapping = inputEval.readFromFile(fileName);
            inputMapping.evaluateAll();
        } catch(IOException e)
        {
            MainFrame.printERR("Unable to parse fileName = "+fileName+".");
            MainFrame.printERR("Error = "+e.getMessage()+".");
            return;
        } catch (EvaluationException e) {
            MainFrame.printERR("Unable to parse fileName = "+fileName+".");
            MainFrame.printERR("Error = "+e.getMessage()+".");
        }
        
        Integer[] data;
        System.out.println("getNdtset = "+inputMapping.getNdtset());
        if(inputMapping.getNdtset() == 1 || inputMapping.getNdtset() == 0)
        {
            data = new Integer[1];
            data[0] = 1;
        }
        else
        {
            int ndtset = inputMapping.getNdtset();
            data = inputMapping.getJdtsets().toArray(new Integer[0]);
            System.out.println("getJdtsets = "+inputMapping.getJdtsets());
            System.out.println("data = "+data);
        }
        
        dtsetList.setListData(data);
        
        setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        dtsetList = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = jTable1 = new DatasetTable();
        jMolPreview2 = new abinitgui.jmol.JMolPreview();
        nbReplicaX = new javax.swing.JSpinner(new javax.swing.SpinnerNumberModel(1,1,99,1));
        jLabel2 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        nbReplicaY = new javax.swing.JSpinner(new javax.swing.SpinnerNumberModel(1,1,99,1));
        nbReplicaZ = new javax.swing.JSpinner(new javax.swing.SpinnerNumberModel(1,1,99,1));
        jLabel7 = new javax.swing.JLabel();
        viewGeomButton = new javax.swing.JButton();

        setTitle("GUI Editor");
        setResizable(false);

        dtsetList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        dtsetList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                dtsetListValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(dtsetList);

        jLabel1.setText("All datasets :");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        nbReplicaX.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jLabel2.setText("Nb. X");

        jLabel8.setText("Nb. Z");

        jLabel7.setText("Nb. Y");

        viewGeomButton.setText("View Geometry inside JMOL");
        viewGeomButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewGeomButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nbReplicaX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nbReplicaY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nbReplicaZ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(viewGeomButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jMolPreview2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jMolPreview2, javax.swing.GroupLayout.PREFERRED_SIZE, 416, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(nbReplicaY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(nbReplicaX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(nbReplicaZ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel8))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(viewGeomButton)))
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void viewGeomButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewGeomButtonActionPerformed

        Integer jdtset = (Integer)dtsetList.getSelectedValue();
        
        if(jdtset == null)
        {
            MainFrame.printERR("First select a dataset!");
            return;
        }
        
        
        String simName = fileName+jdtset;
        
        final String XYZFile = simName + ".aims";

        try {
            AbinitGeometry geom = new AbinitGeometry();
            
            /*if(jdtset == null)
            {
                return;
            }*/ // TODO: ? voir plus haut !!
            
            geom.loadData(inputMapping, jdtset);

            /*if (geom == null) { // TODO: si c'�tait null on aurait d�j� une exception apr�s la ligne pr�c�dente !!
                MainFrame.printERR("Unable to load data from the parser !");
            } else {*/
                geom.fillData();

                geom.computeReplicas((Integer) nbReplicaX.getValue(), (Integer) nbReplicaY.getValue(), (Integer) nbReplicaZ.getValue());

                geom.writeIntoAIMS(XYZFile);
                
                jMolPreview2.setMolecule(XYZFile);
//                new Thread(new Runnable() {
//                    public void run() {
//
//                        MainFrame.localCommand("java -jar Jmol.jar " + XYZFile);
//                    }
//                }).start();
            //}
        } catch (IOException e) {
            e.printStackTrace();
            MainFrame.printERR(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            MainFrame.printERR(e.getMessage());
        }
    }//GEN-LAST:event_viewGeomButtonActionPerformed

    private void loadDatabase()
    {
        Integer jdtset = (Integer)dtsetList.getSelectedValue();
        if(jdtset == null)
        {
            return;
        }
        AbinitDataset values;
        if(inputMapping.isUsejdtset())
        {
            values = inputMapping.getDataset(jdtset);
        }
        else
        {
            values = inputMapping.getDataset(0);
        }
        
        dataTable.clear();
        
        Iterator<AbinitVariable> iter = values.iterator();
        
        while(iter.hasNext())
        {
            AbinitVariable o = iter.next();
            
            Object value = o.getValue();
            System.out.println(""+o+" = "+value);
            
            HashMap<String,Object> map = new HashMap<>();
            
            map.put("name", o);
            map.put("value", value);
            
            dataTable.add(map);
        }
    }
    
    private void dtsetListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_dtsetListValueChanged

        boolean adjust = evt.getValueIsAdjusting();
        
        if(!adjust)
        {
            loadDatabase();

            model.fireTableDataChanged();
        }
    }//GEN-LAST:event_dtsetListValueChanged

    public class DatasetTable extends JTable
    {
        @Override
        public TableCellEditor getCellEditor(int row, int column)
        {
            return getDefaultEditor(getModel().getValueAt(row, column).getClass());
        }
        
    }
    public class DatasetModel extends DefaultTableModel
    {
        @Override
        public int getColumnCount()
        {
            return 2;
        }
        
        @Override
        public String getColumnName(int col)
        {
            if(col == 0)
            {
                return "name";
            }
            else if(col == 1)
            {
                return "value";
            }
            return null;
        }
        
        @Override
        public Object getValueAt(int row, int column)
        {
            if(column == 0)
            {
                return dataTable.get(row).get("name");
            }
            else if(column == 1)
            {
                Object o = dataTable.get(row).get("value");
                StringBuilder sb = new StringBuilder();
                if(o instanceof Number[])
                {
                    for(Number nb : (Number[])o)
                    {
                        sb.append(nb).append(" ");
                    }
                }
                else if(o instanceof Number[][])
                {
                    Number[][] tab = ((Number[][])o);
                    int length1 = tab.length;
                    int length2 = tab[0].length;
                    for(int i = 0; i < length2; i++)
                    {
                        for(int j = 0; j < length1; j++)
                        {
                            sb.append(tab[j][i]).append(" ");
                        }
                        sb.append(";");
                    }
                }
                else
                {
                    System.out.println(dataTable.get(row));
                    System.out.println("o = "+o);
                    sb.append(o.toString());
                }
                
                return sb.toString();
            }
            return null;
        }
        
        @Override
        public int getRowCount()
        {
            if(dataTable != null)
            {
                return dataTable.size();
            }
            else
            {
                return 0;
            }
        }
        
        @Override
        public boolean isCellEditable(int row, int column)
        {
//            return (column == 1); // Value of the variable
            return false;
        }
        
        @Override
        public void setValueAt(Object o, int row, int column)
        {
            if(column == 1)
            {
                loadDatabase();
                model.fireTableDataChanged();
            }
        }
    }
    
    public class DatasetRenderer extends DefaultTableCellRenderer
    {
        @Override
        public Component getTableCellRendererComponent(
              JTable table,
              Object value,
              boolean selected,
              boolean hasFocus,
              int row,
              int column) 
        {
            // Allow the original renderer to set up the label
            Component c = super.getTableCellRendererComponent(
                           table, value, selected, hasFocus,
                           row, column); 

            if(column == 0 || column == 2)
            {
                // if name, no special renderer
                c.setForeground(Color.BLACK);
                return c;
            }
            else
            {
                Object val = dataTable.get(row).get("value");
                Object valDefault = dataTable.get(row).get("default");
                
                if(val.equals(valDefault))
                {
                    c.setForeground(Color.BLACK);
                }
                else
                {
                    c.setForeground(Color.RED);
                }
                return c;
            }
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList dtsetList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private abinitgui.jmol.JMolPreview jMolPreview2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JSpinner nbReplicaX;
    private javax.swing.JSpinner nbReplicaY;
    private javax.swing.JSpinner nbReplicaZ;
    private javax.swing.JButton viewGeomButton;
    // End of variables declaration//GEN-END:variables
}
