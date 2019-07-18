package edu.jhuapl.sbmt.spectrum.ui;

import java.awt.Component;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import com.google.common.collect.Lists;

import edu.jhuapl.sbmt.spectrum.model.core.SpectrumColoringStyle;

public class SpectrumColoringPanel extends JPanel
{
    private JCheckBox grayscaleCheckBox;
    private JButton customFunctionsButton;
    private JComboBox<String> redComboBox;
    private JSpinner redMinSpinner;
    private JSpinner redMaxSpinner;
    private JComboBox<String> greenComboBox;
    private JSpinner greenMinSpinner;
    private JSpinner greenMaxSpinner;
    private JComboBox<String> blueComboBox;
    private JSpinner blueMinSpinner;
    private JSpinner blueMaxSpinner;
    private JPanel coloringDetailPanel;
    private JComboBox<SpectrumColoringStyle> coloringComboBox;
    private JPanel emissionAngleColoringPanel;
    private JPanel rgbColoringPanel;
    private JLabel redLabel;
    private JLabel greenLabel;
    private JLabel greenMinLabel;
    private JLabel greenMaxLabel;
    private JLabel blueLabel;
    private JLabel blueMinLabel;
    private JLabel blueMaxLabel;

    public SpectrumColoringPanel()
    {
        init();
    }

    private void init()
    {
        setBorder(new TitledBorder(null, "Coloring", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        coloringComboBox = new JComboBox<SpectrumColoringStyle>();

        add(coloringComboBox);

        coloringDetailPanel = new JPanel();
        add(coloringDetailPanel);

        emissionAngleColoringPanel = new JPanel();
        emissionAngleColoringPanel.setVisible(false);
        coloringDetailPanel.setLayout(new BoxLayout(coloringDetailPanel, BoxLayout.Y_AXIS));
        coloringDetailPanel.add(emissionAngleColoringPanel);
        emissionAngleColoringPanel.setLayout(new BoxLayout(emissionAngleColoringPanel, BoxLayout.X_AXIS));

        JLabel lblNewLabel_15 = new JLabel("Coloring by Avg Emission Angle (OREX Scalar Ramp, 0 to 90)");
        emissionAngleColoringPanel.add(lblNewLabel_15);

        rgbColoringPanel = new JPanel();
        coloringDetailPanel.add(rgbColoringPanel);
        rgbColoringPanel.setLayout(new BoxLayout(rgbColoringPanel, BoxLayout.Y_AXIS));

        JPanel panel_10 = new JPanel();
        rgbColoringPanel.add(panel_10);
        panel_10.setLayout(new BoxLayout(panel_10, BoxLayout.X_AXIS));

        grayscaleCheckBox = new JCheckBox("Grayscale");
        panel_10.add(grayscaleCheckBox);

        Component horizontalGlue_2 = Box.createHorizontalGlue();
        panel_10.add(horizontalGlue_2);

        customFunctionsButton = new JButton("Custom Formulas");
        panel_10.add(customFunctionsButton);

        JPanel panel_11 = new JPanel();
        rgbColoringPanel.add(panel_11);
        panel_11.setLayout(new BoxLayout(panel_11, BoxLayout.X_AXIS));

        redLabel = new JLabel("Red");
        panel_11.add(redLabel);

        redComboBox = new JComboBox<String>();
        panel_11.add(redComboBox);

        JLabel lblMin = new JLabel("Min");
        panel_11.add(lblMin);

        redMinSpinner = new JSpinner();
        redMinSpinner.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(0.05d), null, null, Double.valueOf(0.01d)));
        redMinSpinner.setPreferredSize(new java.awt.Dimension(100, 28));
        redMinSpinner.setMinimumSize(new java.awt.Dimension(36, 22));
        redMinSpinner.setMaximumSize(new java.awt.Dimension(100, 22));
        panel_11.add(redMinSpinner);

        JLabel lblMax = new JLabel("Max");
        panel_11.add(lblMax);

        redMaxSpinner = new JSpinner();
        redMaxSpinner.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(0.05d), null, null, Double.valueOf(0.01d)));
        redMaxSpinner.setPreferredSize(new java.awt.Dimension(100, 28));
        redMaxSpinner.setMinimumSize(new java.awt.Dimension(36, 22));
        redMaxSpinner.setMaximumSize(new java.awt.Dimension(100, 22));
        panel_11.add(redMaxSpinner);

        JPanel panel_12 = new JPanel();
        rgbColoringPanel.add(panel_12);
        panel_12.setLayout(new BoxLayout(panel_12, BoxLayout.X_AXIS));

        greenLabel = new JLabel("Green");
        panel_12.add(greenLabel);

        greenComboBox = new JComboBox<String>();
        panel_12.add(greenComboBox);

        greenMinLabel = new JLabel("Min");
        panel_12.add(greenMinLabel);

        greenMinSpinner = new JSpinner();
        greenMinSpinner.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(0.05d), null, null, Double.valueOf(0.01d)));
        greenMinSpinner.setPreferredSize(new java.awt.Dimension(100, 28));
        greenMinSpinner.setMinimumSize(new java.awt.Dimension(36, 22));
        greenMinSpinner.setMaximumSize(new java.awt.Dimension(100, 22));
        panel_12.add(greenMinSpinner);

        greenMaxLabel = new JLabel("Max");
        panel_12.add(greenMaxLabel);

        greenMaxSpinner = new JSpinner();
        greenMaxSpinner.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(0.05d), null, null, Double.valueOf(0.01d)));
        greenMaxSpinner.setPreferredSize(new java.awt.Dimension(100, 28));
        greenMaxSpinner.setMinimumSize(new java.awt.Dimension(36, 22));
        greenMaxSpinner.setMaximumSize(new java.awt.Dimension(100, 22));
        panel_12.add(greenMaxSpinner);

        JPanel panel_13 = new JPanel();
        rgbColoringPanel.add(panel_13);
        panel_13.setLayout(new BoxLayout(panel_13, BoxLayout.X_AXIS));

        blueLabel = new JLabel("Blue");
        panel_13.add(blueLabel);

        blueComboBox = new JComboBox<String>();
        panel_13.add(blueComboBox);

        blueMinLabel = new JLabel("Min");
        panel_13.add(blueMinLabel);

        blueMinSpinner = new JSpinner();
        blueMinSpinner.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(0.05d), null, null, Double.valueOf(0.01d)));
        blueMinSpinner.setPreferredSize(new java.awt.Dimension(100, 28));
        blueMinSpinner.setMinimumSize(new java.awt.Dimension(36, 22));
        blueMinSpinner.setMaximumSize(new java.awt.Dimension(100, 22));
        panel_13.add(blueMinSpinner);

        blueMaxLabel = new JLabel("Max");
        panel_13.add(blueMaxLabel);

        blueMaxSpinner = new JSpinner();
        blueMaxSpinner.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(0.05d), null, null, Double.valueOf(0.01d)));
        blueMaxSpinner.setPreferredSize(new java.awt.Dimension(100, 28));
        blueMaxSpinner.setMinimumSize(new java.awt.Dimension(36, 22));
        blueMaxSpinner.setMaximumSize(new java.awt.Dimension(100, 22));
        panel_13.add(blueMaxSpinner);

        List<JSpinner> spinners=Lists.newArrayList(blueMaxSpinner, blueMinSpinner, redMaxSpinner, redMinSpinner,
                greenMaxSpinner, greenMinSpinner);

        for (JSpinner spinner : spinners)
        {
            spinner.setModel(new SpinnerNumberModel(Double.valueOf(0.0d), null, null, Double.valueOf(0.0000001d)));
            NumberEditor editor = (NumberEditor)spinner.getEditor();
            DecimalFormat format = editor.getFormat();
            format.setMinimumFractionDigits(8);
        }
    }

    public JCheckBox getGrayscaleCheckBox()
    {
        return grayscaleCheckBox;
    }

    public JButton getCustomFunctionsButton()
    {
        return customFunctionsButton;
    }

    public JComboBox<String> getRedComboBox()
    {
        return redComboBox;
    }

    public JSpinner getRedMinSpinner()
    {
        return redMinSpinner;
    }

    public JSpinner getRedMaxSpinner()
    {
        return redMaxSpinner;
    }

    public JComboBox<String> getGreenComboBox()
    {
        return greenComboBox;
    }

    public JSpinner getGreenMinSpinner()
    {
        return greenMinSpinner;
    }

    public JSpinner getGreenMaxSpinner()
    {
        return greenMaxSpinner;
    }

    public JComboBox<String> getBlueComboBox()
    {
        return blueComboBox;
    }

    public JSpinner getBlueMinSpinner()
    {
        return blueMinSpinner;
    }

    public JSpinner getBlueMaxSpinner()
    {
        return blueMaxSpinner;
    }

    public JPanel getColoringDetailPanel()
    {
        return coloringDetailPanel;
    }

    public JComboBox<SpectrumColoringStyle> getColoringComboBox()
    {
        return coloringComboBox;
    }

    public JPanel getEmissionAngleColoringPanel()
    {
        return emissionAngleColoringPanel;
    }

    public JPanel getRgbColoringPanel()
    {
        return rgbColoringPanel;
    }

    public JLabel getGreenLabel()
    {
        return greenLabel;
    }

    public JLabel getRedLabel()
    {
        return redLabel;
    }

    public JLabel getBlueLabel()
    {
        return blueLabel;
    }

    public JLabel getGreenMinLabel()
    {
        return greenMinLabel;
    }

    public JLabel getGreenMaxLabel()
    {
        return greenMaxLabel;
    }

    public JLabel getBlueMinLabel()
    {
        return blueMinLabel;
    }

    public JLabel getBlueMaxLabel()
    {
        return blueMaxLabel;
    }
}
