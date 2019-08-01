package edu.jhuapl.sbmt.spectrum.model.rendering;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.ImmutableSet;

import vtk.vtkActor;
import vtk.vtkProp;

import edu.jhuapl.saavtk.model.AbstractModel;
import edu.jhuapl.saavtk.util.Properties;
import edu.jhuapl.sbmt.client.SmallBodyModel;
import edu.jhuapl.sbmt.spectrum.model.core.SpectrumBoundary;
import edu.jhuapl.sbmt.spectrum.model.sbmtCore.spectra.SpectrumKeyInterface;

import nom.tam.fits.FitsException;

public class SpectrumBoundaryCollection extends AbstractModel implements PropertyChangeListener
{
    private HashMap<SpectrumBoundary, List<vtkProp>> boundaryToActorsMap = new HashMap<SpectrumBoundary, List<vtkProp>>();
    private HashMap<vtkProp, SpectrumBoundary> actorToBoundaryMap = new HashMap<vtkProp, SpectrumBoundary>();
    private SmallBodyModel smallBodyModel;
    // Create a buffer of initial boundary colors to use. We cycle through these colors when creating new boundaries
    private Color[] initialColors = {Color.RED, Color.PINK.darker(), Color.ORANGE.darker(),
            Color.GREEN.darker(), Color.MAGENTA, Color.CYAN.darker(), Color.BLUE,
            Color.GRAY, Color.DARK_GRAY, Color.BLACK};
    private int initialColorIndex = 0;

    public SpectrumBoundaryCollection(SmallBodyModel smallBodyModel)
    {
        this.smallBodyModel = smallBodyModel;
    }

    protected SpectrumBoundary createBoundary(
            SpectrumKeyInterface key,
            SmallBodyModel smallBodyModel, SpectraCollection collection) throws IOException, FitsException
    {
        IBasicSpectrumRenderer spectrum = collection.getSpectrumFromKey(key);
        SpectrumBoundary boundary = new SpectrumBoundary(spectrum, smallBodyModel);
        boundary.setBoundaryColor(initialColors[initialColorIndex++]);
        if (initialColorIndex >= initialColors.length)
            initialColorIndex = 0;
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


    public void addBoundary(SpectrumKeyInterface key, SpectraCollection collection) throws FitsException, IOException
    {
        if (containsKey(key))
            return;

        if (collection.getSpectrumFromKey(key) == null) return;

        SpectrumBoundary boundary = createBoundary(key, smallBodyModel, collection);

        smallBodyModel.addPropertyChangeListener(boundary);
        boundary.addPropertyChangeListener(this);
        boundary.setVisibility(true);
        boundaryToActorsMap.put(boundary, new ArrayList<vtkProp>());

        List<vtkProp> boundaryPieces = boundary.getProps();
        boundaryToActorsMap.get(boundary).addAll(boundaryPieces);

        for (vtkProp act : boundaryPieces)
            actorToBoundaryMap.put(act, boundary);
        this.pcs.firePropertyChange(Properties.MODEL_CHANGED, null, null);
    }

    public void removeBoundary(SpectrumKeyInterface key)
    {
        SpectrumBoundary boundary = getBoundaryFromKey(key);

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

            this.pcs.firePropertyChange(Properties.MODEL_CHANGED, null, null);
        }
    }

    public void removeAllBoundaries()
    {
        HashMap<SpectrumBoundary, List<vtkProp>> map = (HashMap<SpectrumBoundary, List<vtkProp>>)boundaryToActorsMap.clone();
        for (SpectrumBoundary boundary : map.keySet())
            removeBoundary(boundary.getKey());
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

    public void propertyChange(PropertyChangeEvent evt)
    {
        if (Properties.MODEL_CHANGED.equals(evt.getPropertyName()))
        {
            this.pcs.firePropertyChange(Properties.MODEL_CHANGED, null, null);
        }
    }
}
