package edu.jhuapl.sbmt.spectrum.rendering;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.ImmutableSet;

import vtk.vtkActor;
import vtk.vtkProp;

import edu.jhuapl.saavtk.model.AbstractModel;
import edu.jhuapl.saavtk.util.Properties;
import edu.jhuapl.sbmt.client.SmallBodyModel;
import edu.jhuapl.sbmt.spectrum.model.core.BasicSpectrum;
import edu.jhuapl.sbmt.spectrum.model.core.SpectrumBoundary;
import edu.jhuapl.sbmt.spectrum.model.sbmtCore.spectra.SpectrumKeyInterface;

public class SpectrumBoundaryCollection extends AbstractModel implements PropertyChangeListener
{
    private HashMap<SpectrumBoundary, List<vtkProp>> boundaryToActorsMap = new HashMap<SpectrumBoundary, List<vtkProp>>();
    private HashMap<vtkProp, SpectrumBoundary> actorToBoundaryMap = new HashMap<vtkProp, SpectrumBoundary>();
    private HashMap<BasicSpectrum, SpectrumBoundary> spectrumToBoundaryMap = new HashMap<BasicSpectrum, SpectrumBoundary>();
    private SmallBodyModel smallBodyModel;
    // Create a buffer of initial boundary colors to use. We cycle through these colors when creating new boundaries
    private Color[] initialColors = {Color.RED, Color.PINK.darker(), Color.ORANGE.darker(),
            Color.GREEN.darker(), Color.MAGENTA, Color.CYAN.darker(), Color.BLUE,
            Color.GRAY, Color.DARK_GRAY, Color.BLACK};
    private int initialColorIndex = 0;
    private SpectraCollection spectrumCollection;

    public SpectrumBoundaryCollection(SmallBodyModel smallBodyModel, SpectraCollection spectrumCollection)
    {
        this.smallBodyModel = smallBodyModel;
        this.spectrumToBoundaryMap = new HashMap<BasicSpectrum, SpectrumBoundary>();
        this.spectrumCollection = spectrumCollection;
    }

//    protected SpectrumBoundary createBoundary(
//            SpectrumKeyInterface key,
//            SmallBodyModel smallBodyModel, SpectraCollection collection) throws IOException, FitsException
//    {
//        IBasicSpectrumRenderer spectrum = collection.getSpectrumFromKey(key);
//        SpectrumBoundary boundary = new SpectrumBoundary(spectrum, smallBodyModel);
//        boundary.setBoundaryColor(initialColors[initialColorIndex++]);
//        if (initialColorIndex >= initialColors.length)
//            initialColorIndex = 0;
//        spectrumToBoundaryMap.put(spectrum.getSpectrum(), boundary);
//        return boundary;
//    }

    protected SpectrumBoundary createBoundary(
            BasicSpectrum spec,
            SmallBodyModel smallBodyModel) //throws IOException, FitsException
    {
        IBasicSpectrumRenderer spectrum = spectrumCollection.getRendererForSpectrum(spec);
        SpectrumBoundary boundary = new SpectrumBoundary(spectrum, smallBodyModel);
        boundary.setBoundaryColor(initialColors[initialColorIndex++]);
        if (initialColorIndex >= initialColors.length)
            initialColorIndex = 0;
        spectrumToBoundaryMap.put(spec, boundary);
        return boundary;
    }

    private boolean containsKey(SpectrumKeyInterface key)
    {
        for (SpectrumBoundary boundary : boundaryToActorsMap.keySet())
        {
            if (boundary.getKey().equals(key))
                return true;
        }

        return false;
    }

    private SpectrumBoundary getBoundaryFromKey(SpectrumKeyInterface key)
    {
        for (SpectrumBoundary boundary : boundaryToActorsMap.keySet())
        {
            if (boundary.getKey().equals(key))
                return boundary;
        }

        return null;
    }


//    public void addBoundary(SpectrumKeyInterface key, SpectraCollection collection) throws FitsException, IOException
    public SpectrumBoundary addBoundary(BasicSpectrum spec) //throws FitsException, IOException
    {
    	SpectrumBoundary boundary = null;
        if (spectrumToBoundaryMap.containsKey(spec))
            boundary = spectrumToBoundaryMap.get(spec);
        else
        	boundary = createBoundary(spec, smallBodyModel);
//        if (collection.getSpectrumFromKey(key) == null) return;

//        SpectrumBoundary boundary = createBoundary(key, smallBodyModel, collection);
//        SpectrumBoundary boundary = createBoundary(spec, smallBodyModel);

        smallBodyModel.addPropertyChangeListener(boundary);
        boundary.addPropertyChangeListener(this);
        boundary.setVisibility(true);
        boundaryToActorsMap.put(boundary, new ArrayList<vtkProp>());

        List<vtkProp> boundaryPieces = boundary.getProps();
        boundaryToActorsMap.get(boundary).addAll(boundaryPieces);

        for (vtkProp act : boundaryPieces)
            actorToBoundaryMap.put(act, boundary);
//        spectrumToBoundaryMap.put(collection.getSpectrumFromKey(key).getSpectrum(), boundary);
        spectrumToBoundaryMap.put(spec, boundary);
        this.pcs.firePropertyChange(Properties.MODEL_CHANGED, null, boundary);
        return boundary;
    }

//    public void removeBoundary(SpectrumKeyInterface key)
    public void removeBoundary(BasicSpectrum spectrum)
    {
//        SpectrumBoundary boundary = getBoundaryFromKey(key);
        SpectrumBoundary boundary = spectrumToBoundaryMap.get(spectrum);

        if(boundary != null)
        {
            List<vtkProp> actors = boundaryToActorsMap.get(boundary);

            if(actors != null)
            {
                for (vtkProp act : actors)
                    actorToBoundaryMap.remove(act);
            }

            boundaryToActorsMap.remove(boundary);
            boundary.setVisibility(false);
            boundary.removePropertyChangeListener(this);
            smallBodyModel.removePropertyChangeListener(boundary);

            this.pcs.firePropertyChange(Properties.MODEL_CHANGED, null, boundary);
        }
    }

    public void removeAllBoundaries()
    {
//        HashMap<SpectrumBoundary, List<vtkProp>> map = (HashMap<SpectrumBoundary, List<vtkProp>>)boundaryToActorsMap.clone();
        for (BasicSpectrum spectrum : spectrumToBoundaryMap.keySet())
            removeBoundary(spectrum);
    }

    public List<vtkProp> getProps()
    {
        return new ArrayList<vtkProp>(actorToBoundaryMap.keySet());
    }

    public String getClickStatusBarText(vtkProp prop, int cellId, double[] pickPosition)
    {
        SpectrumBoundary boundary = actorToBoundaryMap.get(prop);
        if(boundary == null)
        {
            return "";
        }
        File file = new File(boundary.getKey().getName());
        return "Boundary of image " + file.getName();
    }

    public String getBoundaryName(vtkActor actor)
    {
        return actorToBoundaryMap.get(actor).getKey().getName();
    }

    public ImmutableSet<SpectrumKeyInterface> getSpectrumKeys()
    {
        ImmutableSet.Builder<SpectrumKeyInterface> builder = ImmutableSet.builder();
        for (SpectrumBoundary boundary : boundaryToActorsMap.keySet())
        {
            builder.add(boundary.getKey());
        }
        return builder.build();
    }

    public SpectrumBoundary getBoundary(vtkActor actor)
    {
        return actorToBoundaryMap.get(actor);
    }

    public SpectrumBoundary getBoundary(SpectrumKeyInterface key)
    {
        return getBoundaryFromKey(key);
    }

    public boolean containsBoundary(SpectrumKeyInterface key)
    {
        return containsKey(key);
    }

    public void setVisibility(BasicSpectrum spec, boolean visible)
    {
    	SpectrumBoundary boundary = spectrumToBoundaryMap.get(spec);
    	if (boundary == null)
//			try
//			{
				boundary = addBoundary(spec);
//			} catch (FitsException | IOException e)
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

    	boundary.setVisibility(visible);
    	this.pcs.firePropertyChange(Properties.MODEL_CHANGED, null, boundary);
    }

    public boolean getVisibility(BasicSpectrum spec)
    {
    	if (spectrumToBoundaryMap.get(spec) == null) return false;
    	return spectrumToBoundaryMap.get(spec).getVisibility();
    }

    public void propertyChange(PropertyChangeEvent evt)
    {
        if (Properties.MODEL_CHANGED.equals(evt.getPropertyName()))
        {
            this.pcs.firePropertyChange(Properties.MODEL_CHANGED, null, null);
        }
    }
}
