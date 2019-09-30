package edu.jhuapl.sbmt.spectrum.ui.custom;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class CustomSpectraControlPanel extends JPanel
{
    JButton newButton;
    JButton editButton;
    JButton deleteButton;

    public CustomSpectraControlPanel()
    {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        newButton = new JButton("New...");
        add(newButton);

        editButton = new JButton("Edit...");
        add(editButton);

        deleteButton = new JButton("Delete...");
        add(deleteButton);
    }

    public JButton getNewButton()
    {
        return newButton;
    }

    public JButton getEditButton()
    {
        return editButton;
    }

    public JButton getDeleteButton()
    {
        return deleteButton;
    }
}