package edu.jhuapl.sbmt.spectrum.ui.color;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.SpinnerNumberModel;

import com.google.common.collect.Lists;

import vtk.vtkFunctionParser;

import edu.jhuapl.sbmt.spectrum.model.core.BasicSpectrum;
import edu.jhuapl.sbmt.spectrum.model.core.color.RGBSpectrumColorer;
import edu.jhuapl.sbmt.spectrum.model.sbmtCore.spectra.ISpectralInstrument;
import edu.jhuapl.sbmt.spectrum.ui.math.SpectrumMathPanel;

public class RGBColoringPanel<S extends BasicSpectrum> extends JPanel implements ISpectrumColoringPanel
{
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
    private JLabel redLabel;
    private JLabel greenLabel;
    private JLabel greenMinLabel;
    private JLabel greenMaxLabel;
    private JLabel blueLabel;
    private JLabel blueMinLabel;
    private JLabel blueMaxLabel;
    private ISpectralInstrument instrument;
    private RGBSpectrumColorer<S> model;
    private Dimension preferredSpinnerSize;
    private Dimension minSpinnerSize;
    private Dimension maxSpinnerSize;
    private Dimension preferredComboSize;
    private Dimension minComboSize;
    private Dimension maxComboSize;

	public RGBColoringPanel(RGBSpectrumColorer<S> model, ISpectralInstrument instrument)
	{
		this.model = model;
		this.instrument = instrument;

		initialize();
	}

	private void initialize()
    {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel panel_10 = new JPanel();
        add(panel_10);
        panel_10.setLayout(new BoxLayout(panel_10, BoxLayout.X_AXIS));
        customFunctionsButton = new JButton("Custom Formulas");
        panel_10.add(customFunctionsButton);
        panel_10.add(Box.createHorizontalGlue());

        JPanel panel_11 = new JPanel();
        add(panel_11);
        panel_11.setLayout(new BoxLayout(panel_11, BoxLayout.X_AXIS));

        redLabel = new JLabel("Red");
        panel_11.add(redLabel);

        redComboBox = new JComboBox<String>();
        panel_11.add(redComboBox);
        panel_11.add(Box.createHorizontalGlue());
        JLabel lblMin = new JLabel("Min");
        panel_11.add(lblMin);

        preferredComboSize = new Dimension(250, 28);
        minComboSize = new Dimension(250, 28);
        maxComboSize = new Dimension(250, 28);

        SpinnerNumberModel spinnerNumberModel = new SpinnerNumberModel(Double.valueOf(0.05d), null, null, Double.valueOf(0.01d));
        preferredSpinnerSize = new Dimension(100, 28);
        minSpinnerSize = new Dimension(36, 22);
        maxSpinnerSize = new Dimension(100, 22);

        redMinSpinner = new JSpinner();
        redMinSpinner.setModel(spinnerNumberModel);
        redMinSpinner.setPreferredSize(preferredSpinnerSize);
        redMinSpinner.setMinimumSize(minSpinnerSize);
        redMinSpinner.setMaximumSize(maxSpinnerSize);
        panel_11.add(redMinSpinner);

        JLabel lblMax = new JLabel("Max");
        panel_11.add(lblMax);

        redMaxSpinner = new JSpinner();
        redMaxSpinner.setModel(spinnerNumberModel);
        redMaxSpinner.setPreferredSize(preferredSpinnerSize);
        redMaxSpinner.setMinimumSize(minSpinnerSize);
        redMaxSpinner.setMaximumSize(maxSpinnerSize);
        panel_11.add(redMaxSpinner);

        JPanel panel_12 = new JPanel();
        add(panel_12);
        panel_12.setLayout(new BoxLayout(panel_12, BoxLayout.X_AXIS));

        greenLabel = new JLabel("Green");
        panel_12.add(greenLabel);

        greenComboBox = new JComboBox<String>();
        panel_12.add(greenComboBox);
        panel_12.add(Box.createHorizontalGlue());
        greenMinLabel = new JLabel("Min");
        panel_12.add(greenMinLabel);

        greenMinSpinner = new JSpinner();
        greenMinSpinner.setModel(spinnerNumberModel);
        greenMinSpinner.setPreferredSize(preferredSpinnerSize);
        greenMinSpinner.setMinimumSize(minSpinnerSize);
        greenMinSpinner.setMaximumSize(maxSpinnerSize);
        panel_12.add(greenMinSpinner);

        greenMaxLabel = new JLabel("Max");
        panel_12.add(greenMaxLabel);

        greenMaxSpinner = new JSpinner();
        greenMaxSpinner.setModel(spinnerNumberModel);
        greenMaxSpinner.setPreferredSize(preferredSpinnerSize);
        greenMaxSpinner.setMinimumSize(minSpinnerSize);
        greenMaxSpinner.setMaximumSize(maxSpinnerSize);
        panel_12.add(greenMaxSpinner);

        JPanel panel_13 = new JPanel();
        add(panel_13);
        panel_13.setLayout(new BoxLayout(panel_13, BoxLayout.X_AXIS));

        blueLabel = new JLabel("Blue");
        panel_13.add(blueLabel);

        blueComboBox = new JComboBox<String>();
        panel_13.add(blueComboBox);
        panel_13.add(Box.createHorizontalGlue());
        blueMinLabel = new JLabel("Min");
        panel_13.add(blueMinLabel);

        blueMinSpinner = new JSpinner();
        blueMinSpinner.setModel(spinnerNumberModel);
        blueMinSpinner.setPreferredSize(preferredSpinnerSize);
        blueMinSpinner.setMinimumSize(minSpinnerSize);
        blueMinSpinner.setMaximumSize(maxSpinnerSize);
        panel_13.add(blueMinSpinner);

        blueMaxLabel = new JLabel("Max");
        panel_13.add(blueMaxLabel);

        blueMaxSpinner = new JSpinner();
        blueMaxSpinner.setModel(spinnerNumberModel);
        blueMaxSpinner.setPreferredSize(preferredSpinnerSize);
        blueMaxSpinner.setMinimumSize(minSpinnerSize);
        blueMaxSpinner.setMaximumSize(maxSpinnerSize);
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

        setupComboBoxes();

        getRedComboBox().addActionListener(evt -> redComboBoxActionPerformed(evt));
        getRedMaxSpinner().addChangeListener(evt -> redMaxSpinnerStateChanged());
        getRedMinSpinner().addChangeListener(evt -> redMinSpinnerStateChanged());
        getGreenComboBox().addActionListener(evt -> greenComboBoxActionPerformed(evt));
        getGreenMaxSpinner().addChangeListener(evt -> greenMaxSpinnerStateChanged());
        getGreenMinSpinner().addChangeListener(evt -> greenMinSpinnerStateChanged());
        getBlueComboBox().addActionListener(evt -> blueComboBoxActionPerformed(evt));
        getBlueMaxSpinner().addChangeListener(evt -> blueMaxSpinnerStateChanged());
        getBlueMinSpinner().addChangeListener(evt -> blueMinSpinnerStateChanged());
        getCustomFunctionsButton().addActionListener(evt -> customFunctionsButtonActionPerformed());
    }

	/**
     * Sets up the RGB coloring combo boxes
     */
    protected void setupComboBoxes()
    {
        for (int i=1; i<=instrument.getBandCenters().length; ++i)
        {
            String channel = new String("(" + i + ") " + instrument.getBandCenters()[i-1] + " " + instrument.getBandCenterUnit());
            getRedComboBox().addItem(channel);
            getGreenComboBox().addItem(channel);
            getBlueComboBox().addItem(channel);
        }
        getRedComboBox().setSelectedIndex(model.getRedIndex());
        getGreenComboBox().setSelectedIndex(model.getGreenIndex());
        getBlueComboBox().setSelectedIndex(model.getBlueIndex());

        String[] derivedParameters = instrument.getSpectrumMath().getDerivedParameters();
        for (int i=0; i<derivedParameters.length; ++i)
        {
            getRedComboBox().addItem(derivedParameters[i]);
            getGreenComboBox().addItem(derivedParameters[i]);
            getBlueComboBox().addItem(derivedParameters[i]);
        }

        for (vtkFunctionParser fp: instrument.getSpectrumMath().getAllUserDefinedDerivedParameters())
        {
            getRedComboBox().addItem(fp.GetFunction());
            getGreenComboBox().addItem(fp.GetFunction());
            getBlueComboBox().addItem(fp.GetFunction());
        }

        getRedMaxSpinner().setValue(model.getRedMaxVal());
        getGreenMaxSpinner().setValue(model.getGreenMaxVal());
        getBlueMaxSpinner().setValue(model.getBlueMaxVal());

        getRedComboBox().setPreferredSize(preferredComboSize);
        getRedComboBox().setMaximumSize(maxComboSize);
        getRedComboBox().setMinimumSize(minComboSize);

        getGreenComboBox().setPreferredSize(preferredComboSize);
        getGreenComboBox().setMaximumSize(maxComboSize);
        getGreenComboBox().setMinimumSize(minComboSize);

        getBlueComboBox().setPreferredSize(preferredComboSize);
        getBlueComboBox().setMaximumSize(maxComboSize);
        getBlueComboBox().setMinimumSize(minComboSize);
    }

    /**
     * Generates and displays the custom function panel, and updates the coloring as requested by that panel's input.
     */
    private void customFunctionsButtonActionPerformed() {
        SpectrumMathPanel customFunctionsPanel = new SpectrumMathPanel(
                JOptionPane.getFrameForComponent(this),
                new JComboBox[]{getRedComboBox(), getGreenComboBox(), getBlueComboBox()}, instrument);
        model.setCurrentlyEditingUserDefinedFunction(true);
        customFunctionsPanel.setVisible(true);
        model.setCurrentlyEditingUserDefinedFunction(false);
        model.updateColoring();
    }

  //Helper methods related to actions for the various spinners and combo boxes

    private void redComboBoxActionPerformed(ActionEvent evt) {
    	model.setRedIndex(getRedComboBox().getSelectedIndex());
    	model.updateColoring();
    }

    private void greenComboBoxActionPerformed(ActionEvent evt) {
    	model.setGreenIndex(getGreenComboBox().getSelectedIndex());
    	model.updateColoring();
    }

    private void blueComboBoxActionPerformed(ActionEvent evt) {
    	model.setBlueIndex(getBlueComboBox().getSelectedIndex());
    	model.updateColoring();
    }

    private void redMinSpinnerStateChanged() {
        checkValidMinMax(0, true);
    }

    private void greenMinSpinnerStateChanged() {
        checkValidMinMax(1, true);
    }

    private void blueMinSpinnerStateChanged() {
        checkValidMinMax(2, true);
    }

    private void redMaxSpinnerStateChanged() {
        checkValidMinMax(0, false);
    }

    private void greenMaxSpinnerStateChanged() {
        checkValidMinMax(1, false);
    }

    private void blueMaxSpinnerStateChanged() {
        checkValidMinMax(2, false);
    }

    private void checkValidMinMax(int channel, boolean minimunStateChange)
    {
        JSpinner minSpinner = null;
        JSpinner maxSpinner = null;

        if (channel == 0)
        {
            minSpinner = getRedMinSpinner();
            maxSpinner = getRedMaxSpinner();
        }
        else if (channel == 1)
        {
            minSpinner = getGreenMinSpinner();
            maxSpinner = getGreenMaxSpinner();
        }
        else if (channel == 2)
        {
            minSpinner = getBlueMinSpinner();
            maxSpinner = getBlueMaxSpinner();
        }

        Double minVal = (Double)minSpinner.getValue();
        Double maxVal = (Double)maxSpinner.getValue();
        if (channel == 0)
        {
            model.setRedMinVal(minVal);
            model.setRedMaxVal(maxVal);
        }
        else if (channel == 1)
        {
            model.setGreenMinVal(minVal);
            model.setGreenMaxVal(maxVal);
        }
        else if (channel == 2)
        {
            model.setBlueMinVal(minVal);
            model.setBlueMaxVal(maxVal);
        }
        if (minVal > maxVal)
        {
            if (minimunStateChange)
                minSpinner.setValue(maxSpinner.getValue());
            else
                maxSpinner.setValue(minSpinner.getValue());
        }
        model.updateColoring();
    }


    //Getters

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

	public JPanel getJPanel()
	{
		return this;
	}


}
