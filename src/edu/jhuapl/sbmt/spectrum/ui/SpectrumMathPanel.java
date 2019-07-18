/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * CustomImageLoaderPanel.java
 *
 * Created on Jun 5, 2012, 3:56:56 PM
 */
package edu.jhuapl.sbmt.spectrum.ui;

import java.awt.Dialog;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import vtk.vtkFunctionParser;

import edu.jhuapl.sbmt.spectrum.model.core.ISpectralInstrument;


public class SpectrumMathPanel extends javax.swing.JDialog {

    private JComboBox[] comboBoxes;
    ISpectralInstrument instrument;

    /** Creates new form CustomImageLoaderPanel */
    public SpectrumMathPanel(
            java.awt.Frame parent,
            JComboBox[] comboBoxes, ISpectralInstrument instrument)
    {
        super(parent, true);
        this.instrument=instrument;

        this.comboBoxes = comboBoxes;

        initComponents();

        setLocationRelativeTo(parent);
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        pack();

        functionList.setModel(new DefaultListModel());

        updateFunctionList();
    }


    private void updateFunctionList()
    {
        List<vtkFunctionParser> functions = instrument.getSpectrumMath().getAllUserDefinedDerivedParameters();

        ((DefaultListModel)functionList.getModel()).clear();

        for (vtkFunctionParser p : functions)
        {
            ((DefaultListModel)functionList.getModel()).addElement(p.GetFunction());
        }
    }



    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        functionList = new javax.swing.JList();
        newButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        closeButton = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setViewportView(functionList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 377;
        gridBagConstraints.ipady = 241;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        getContentPane().add(jScrollPane1, gridBagConstraints);

        newButton.setText("Add...");
        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 10, 7, 0);
        getContentPane().add(newButton, gridBagConstraints);

        deleteButton.setText("Delete");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 6, 7, 0);
        getContentPane().add(deleteButton, gridBagConstraints);

        editButton.setText("Edit...");
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 6, 7, 0);
        getContentPane().add(editButton, gridBagConstraints);

        jLabel1.setText("Formula List");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 2, 0);
        getContentPane().add(jLabel1, gridBagConstraints);

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 6, 7, 0);
        getContentPane().add(closeButton, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newButtonActionPerformed
        SpectrumMathNewFunctionDialog newFunctionDialog =
                new SpectrumMathNewFunctionDialog(JOptionPane.getFrameForComponent(this), true, null, instrument.getSpectrumMath());
        newFunctionDialog.setVisible(true);
        String function = newFunctionDialog.getFunction();
        if (function != null)
        {
            instrument.getSpectrumMath().addUserDefinedDerivedParameter(function);
            updateFunctionList();

            // add the function to the combo boxes
            for (JComboBox comboBox : comboBoxes)
                comboBox.addItem(function);
        }
    }//GEN-LAST:event_newButtonActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        int selectedItem = functionList.getSelectedIndex();
        if (selectedItem >= 0)
        {
            List<vtkFunctionParser> functions = instrument.getSpectrumMath().getAllUserDefinedDerivedParameters();

            SpectrumMathNewFunctionDialog newFunctionDialog =
                    new SpectrumMathNewFunctionDialog(
                            JOptionPane.getFrameForComponent(this),
                            true,
                            functions.get(selectedItem).GetFunction(), instrument.getSpectrumMath());

            newFunctionDialog.setVisible(true);
            String function = newFunctionDialog.getFunction();
            if (function != null)
            {
                instrument.getSpectrumMath().editUserDefinedDerivedParameter(selectedItem, function);
                updateFunctionList();

                // replace the function in the combo boxes, by first removing it and then inserting
                // a new one. If the item was selected, reselect it.
                int comboBoxUserDefinedFunctionsStartIndex = instrument.getBandCenters().length + instrument.getSpectrumMath().getDerivedParameters().length;
                for (JComboBox comboBox : comboBoxes)
                {
                    int comboBoxIndex = comboBoxUserDefinedFunctionsStartIndex + selectedItem;
                    boolean isSelected = comboBox.getSelectedIndex() == comboBoxIndex;
                    comboBox.removeItemAt(comboBoxIndex);
                    comboBox.insertItemAt(function, comboBoxIndex);
                    if (isSelected)
                        comboBox.setSelectedIndex(comboBoxIndex);
                }
            }
        }
    }//GEN-LAST:event_editButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        int selectedItem = functionList.getSelectedIndex();
        if (selectedItem >= 0)
        {
            instrument.getSpectrumMath().removeUserDefinedDerivedParameters(selectedItem);
            updateFunctionList();

            // delete the function from the combo boxes
            int comboBoxUserDefinedFunctionsStartIndex = instrument.getBandCenters().length + instrument.getSpectrumMath().getDerivedParameters().length;
            for (JComboBox comboBox : comboBoxes)
                comboBox.removeItemAt(comboBoxUserDefinedFunctionsStartIndex + selectedItem);
        }
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        setVisible(false);
    }//GEN-LAST:event_closeButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton editButton;
    private javax.swing.JList functionList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton newButton;
    // End of variables declaration//GEN-END:variables
}
