package edu.jhuapl.sbmt.spectrum.model.core.color;

import java.util.ArrayList;
import java.util.List;

import edu.jhuapl.sbmt.spectrum.model.core.BasicSpectrum;
import edu.jhuapl.sbmt.spectrum.model.core.BasicSpectrumInstrument;
import edu.jhuapl.sbmt.spectrum.model.core.interfaces.IBasicSpectrumRenderer;
import edu.jhuapl.sbmt.spectrum.model.core.interfaces.ISpectrumColorer;
import edu.jhuapl.sbmt.spectrum.model.core.interfaces.SpectrumColoringChangedListener;

/**
 * Coloring model that colors renderers based on mapping R, G and B to the same stretched value over the min/max ranges, resulting in a greyscale color
 * @author steelrj1
 *
 * @param <S>
 */
public class GreyscaleSpectrumColorer<S extends BasicSpectrum> implements ISpectrumColorer<S>
{
	private int greyScaleIndex;
	private Double greyMinVal = 0.0;
    private Double greyMaxVal = 0.0;
    private List<SpectrumColoringChangedListener> colorChangedListeners;
    private int[] channels;
    private double[] mins;
    private double[] maxs;
    protected boolean currentlyEditingUserDefinedFunction = false;


	public GreyscaleSpectrumColorer()
	{
		colorChangedListeners = new ArrayList<SpectrumColoringChangedListener>();
	}

	/**
	 * Returns the color for the passed in spectrumRenderer.  Can be done with built in or user defined values
	 */
	@Override
	public double[] getColorForSpectrum(IBasicSpectrumRenderer<S> spectrumRenderer)
	{
		double[] color = new double[3];
        BasicSpectrumInstrument instrument = spectrumRenderer.getSpectrum().getInstrument();
        int[] channelsToColorBy = channels;
        double[] channelsColoringMinValue = mins;
        double[] channelsColoringMaxValue = maxs;

        double val = 0.0;
        if (channelsToColorBy[0] < instrument.getBandCenters().length)
        {
            val = spectrumRenderer.getSpectrum().getSpectrum()[channelsToColorBy[0]];
        }
        else if (channelsToColorBy[0] < instrument.getBandCenters().length + instrument.getSpectrumMath().getDerivedParameters().length)
        {
            val = spectrumRenderer.getSpectrum().evaluateDerivedParameters(channelsToColorBy[0]-instrument.getBandCenters().length);
        }
        else
        {
            val = instrument.getSpectrumMath().evaluateUserDefinedDerivedParameters(channelsToColorBy[0]-instrument.getBandCenters().length-instrument.getSpectrumMath().getDerivedParameters().length, spectrumRenderer.getSpectrum().getSpectrum());
        }
        if (val < 0.0)
            val = 0.0;
        else if (val > 1.0)
            val = 1.0;

        double slope = 1.0 / (channelsColoringMaxValue[0] - channelsColoringMinValue[0]);
        color[0] = color[1] = color[2] = slope * (val - channelsColoringMinValue[0]);

        return color;
	}

	public int getGreyScaleIndex()
	{
		return greyScaleIndex;
	}

	public void setGreyScaleIndex(int greyScaleIndex)
	{
		this.greyScaleIndex = greyScaleIndex;
	}

	public Double getGreyMinVal()
	{
		return greyMinVal;
	}

	public void setGreyMinVal(Double greyMinVal)
	{
		this.greyMinVal = greyMinVal;
	}

	public Double getGreyMaxVal()
	{
		return greyMaxVal;
	}

	public void setGreyMaxVal(Double greyMaxVal)
	{
		this.greyMaxVal = greyMaxVal;
	}

	public int[] getChannels()
	{
		return channels;
	}

	public void setChannels(int[] channels)
	{
		this.greyScaleIndex = channels[0];
		this.channels = channels;
	}

	public double[] getMins()
	{
		return mins;
	}

	public void setMins(double[] mins)
	{
		this.mins = mins;
	}

	public double[] getMaxs()
	{
		return maxs;
	}

	public void setMaxs(double[] maxs)
	{
		this.greyMaxVal = maxs[0];
		this.maxs = maxs;
	}

	/**
	 * Adds a spectrum coloring changed listener to the list
	 * @param sccl
	 */
	public void addSpectrumColoringChangedListener(SpectrumColoringChangedListener sccl)
	{
		colorChangedListeners.add(sccl);
	}

	/**
	 * Fires the coloring changed listeners
	 */
	private void fireColoringChanged()
	{
		for (SpectrumColoringChangedListener sccl : colorChangedListeners) sccl.coloringChanged();
	}

	/**
	 * Updates the coloring based on the current min/max/index values
	 */
	public void updateColoring()
	{
		this.channels = new int[]{greyScaleIndex, greyScaleIndex, greyScaleIndex};
    	this.mins = new double[]{greyMinVal, greyMinVal, greyMinVal};
    	this.maxs = new double[]{greyMaxVal, greyMaxVal, greyMaxVal};
		fireColoringChanged();
	}

    /**
     * Updates the state describing whether the user defined color function is being edited
     * @param currentlyEditingUserDefinedFunction
     */
    public void setCurrentlyEditingUserDefinedFunction(
            boolean currentlyEditingUserDefinedFunction)
    {
        this.currentlyEditingUserDefinedFunction = currentlyEditingUserDefinedFunction;
    }

	/**
     * Returns state describing whether the user defined color function is being edited
     * @return
     */
    public boolean isCurrentlyEditingUserDefinedFunction()
    {
        return currentlyEditingUserDefinedFunction;
    }
}