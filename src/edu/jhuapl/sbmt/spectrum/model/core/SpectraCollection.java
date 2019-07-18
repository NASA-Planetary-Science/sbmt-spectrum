package edu.jhuapl.sbmt.spectrum.model.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import vtk.vtkActor;
import vtk.vtkProp;

import edu.jhuapl.saavtk.model.AbstractModel;
import edu.jhuapl.saavtk.util.Properties;
import edu.jhuapl.sbmt.client.ISmallBodyModel;
import edu.jhuapl.sbmt.client.SbmtSpectrumModelFactory;
import edu.jhuapl.sbmt.model.bennu.SearchSpec;
import edu.jhuapl.sbmt.spectrum.model.key.SpectrumKeyInterface;

public class SpectraCollection extends AbstractModel implements PropertyChangeListener
{
    private HashMap<Spectrum, List<vtkProp>> spectraActors = new HashMap<Spectrum, List<vtkProp>>();

    private HashMap<String, Spectrum> fileToSpectrumMap = new HashMap<String, Spectrum>();
    private HashMap<String, SearchSpec> fileToSpecMap = new HashMap<String, SearchSpec>();
    private HashMap<vtkProp, String> actorToFileMap = new HashMap<vtkProp, String>();
    private HashMap<Spectrum, List<vtkProp>> spectrumToActorsMap = new HashMap<Spectrum, List<vtkProp>>();
    private HashMap<vtkProp, Spectrum> actorToSpectrumMap = new HashMap<vtkProp, Spectrum>();
    private ISmallBodyModel shapeModel;

    boolean selectAll=false;
    final double minFootprintSeparation=0.001;
    double footprintSeparation=0.001;

    Map<Spectrum,Integer> ordinals=Maps.newHashMap();
    final static int defaultOrdinal=0;

    public SpectraCollection(ISmallBodyModel eros)
    {
        this.shapeModel = eros;
    }

    public void reshiftFootprints()
    {
        for (Spectrum spectrum : ordinals.keySet())
        {
            spectrum.shiftFootprintToHeight(footprintSeparation*(1+ordinals.get(spectrum)));
            //System.out.println(ordinals.get(spectrum)+" "+spectrum.isSelected);
        }
        //System.out.println();
        this.pcs.firePropertyChange(Properties.MODEL_CHANGED,null,null);
    }

    public void setOrdinal(Spectrum spectrum, int ordinal)
    {
        //System.out.println(spectrum);
        if (ordinals.containsKey(spectrum))
            ordinals.remove(spectrum);
        ordinals.put(spectrum, ordinal);
        //System.out.println(ordinals);
    }

    public void clearOrdinals()
    {
        ordinals.clear();
    }

    public void setFootprintSeparation(double val)
    {
        footprintSeparation=val;
        reshiftFootprints();
    }

    public void increaseFootprintSeparation(double val)
    {
        footprintSeparation+=val;
        reshiftFootprints();
    }

    public void decreaseFootprintSeparation(double val)
    {
        footprintSeparation-=val;
        if (footprintSeparation<minFootprintSeparation)
            footprintSeparation=minFootprintSeparation;
        reshiftFootprints();
    }

    public double getFootprintSeparation()
    {
        return footprintSeparation;
    }

    public double getMinFootprintSeparation()
    {
        return minFootprintSeparation;
    }

    public boolean containsKey(SpectrumKeyInterface key)
    {
        for (Spectrum spectrum : spectrumToActorsMap.keySet())
        {
            if (spectrum.getKey().equals(key))
                return true;
        }

        return false;
    }

    public Set<Spectrum> getSpectra()
    {
        return spectrumToActorsMap.keySet();
    }


    public Spectrum getSpectrumFromKey(SpectrumKeyInterface key)
    {
        for (Spectrum spectrum : spectrumToActorsMap.keySet())
        {
            if (spectrum.getKey().equals(key))
                return spectrum;
        }

        return null;
    }

    public Spectrum addSpectrum(SpectrumKeyInterface key) throws IOException
    {
        return addSpectrum(key.getName(), key.getInstrument(), false);
    }

    public Spectrum addSpectrum(SpectrumKeyInterface key, boolean isCustom) throws IOException
    {
        return addSpectrum(key.getName(), key.getInstrument(), isCustom);
    }

    public Spectrum addSpectrum(String path, ISpectralInstrument instrument, SpectrumColoringStyle coloringStyle) throws IOException
    {
        return addSpectrum(path, instrument, coloringStyle, false);
    }

    public Spectrum addSpectrum(String path, ISpectralInstrument instrument, SpectrumColoringStyle coloringStyle, boolean isCustom) throws IOException
    {
        Spectrum spec = addSpectrum(path, instrument, isCustom);
        spec.setColoringStyle(coloringStyle);
        return spec;
    }


    public Spectrum addSpectrum(String path, ISpectralInstrument instrument, boolean isCustom) throws IOException
    {
        if (fileToSpectrumMap.containsKey(path))
        {
            Spectrum spec = fileToSpectrumMap.get(path);
            select(spec);
            spec.setVisible(true);
            return spec;
        }

        //NISSpectrum spectrum = NISSpectrum.NISSpectrumFactory.createSpectrum(path, erosModel);
        //NISSpectrum spectrum = new NISSpectrum(path, erosModel, instrument);
        Spectrum spectrum=null;
        try
        {
        	spectrum = SbmtSpectrumModelFactory.createSpectrum(path, shapeModel, instrument);

//        if (instrument.getDisplayName().equals(SpectraType.NIS_SPECTRA.getDisplayName()))
//        {
//            spectrum=new NISSpectrum(path, shapeModel, instrument);
//        }
//        else if (instrument.getDisplayName().equals(SpectraType.OTES_SPECTRA.getDisplayName()))
//        {
//            spectrum=new OTESSpectrum(path, shapeModel, instrument);
//        }
//        else if (instrument.getDisplayName().equals(SpectraType.OVIRS_SPECTRA.getDisplayName()))
//        {
//            spectrum=new OVIRSSpectrum(path, shapeModel, instrument);
//        }
//        else if (instrument.getDisplayName().equals(SpectraType.NIRS3_SPECTRA.getDisplayName()))
//        {
//            spectrum=new NIRS3Spectrum(path, shapeModel, instrument);
//        }
//        else throw new Exception(instrument.getDisplayName()+" not supported");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        spectrum.isCustomSpectra = isCustom;
        shapeModel.addPropertyChangeListener(spectrum);
        spectrum.addPropertyChangeListener(this);

        fileToSpectrumMap.put(path, spectrum);
        spectraActors.put(spectrum, new ArrayList<vtkProp>());

        spectrum.setMetadata(fileToSpecMap.get(path));

        List<vtkProp> props = spectrum.getProps();

        spectrumToActorsMap.put(spectrum, new ArrayList<vtkProp>());

        spectrumToActorsMap.get(spectrum).addAll(props);

        for (vtkProp act : props)
            actorToSpectrumMap.put(act, spectrum);


        /*
        for (vtkProp p : props)
        {
            vtkActor a=(vtkActor)p;
            vtkPolyDataMapper m=(vtkPolyDataMapper)a.GetMapper();
            System.out.println(m);
            vtkPolyData polyData=m.GetInput();
            System.out.println(polyData.GetNumberOfCells());
        }*/

        spectraActors.get(spectrum).addAll(props);

        for (vtkProp act : props)
            actorToFileMap.put(act, path);
        select(spectrum);
        this.pcs.firePropertyChange(Properties.MODEL_CHANGED, null, null);
        return spectrum;
    }

    public void removeSpectrum(SpectrumKeyInterface key)
    {
        if (!containsKey(key))
            return;

//        Spectrum image = getSpectrumFromKey(key);
//        System.out.println("SpectraCollection: removeSpectrum: file to spectrum map " + fileToSpectrumMap);
//        System.out.println("SpectraCollection: removeSpectrum: image full path "+ image.getFullPath());
//        System.out.println("SpectraCollection: removeSpectrum: key is " + key);
        removeSpectrum(key.getName());
    }

    public void removeSpectrum(String path)
    {
        Spectrum spectrum = fileToSpectrumMap.get(path);
        spectrum.setUnselected();

        List<vtkProp> actors = spectraActors.get(spectrum);

        for (vtkProp act : actors)
            actorToFileMap.remove(act);

        spectraActors.remove(spectrum);

        spectrumToActorsMap.remove(spectrum);

        fileToSpectrumMap.remove(path);

        spectrum.removePropertyChangeListener(this);
        shapeModel.removePropertyChangeListener(spectrum);
        spectrum.setShowFrustum(false);

        ordinals.remove(spectrum);

        for (vtkProp act : actors)
            actorToSpectrumMap.remove(act);

        this.pcs.firePropertyChange(Properties.MODEL_CHANGED, null, null);
        this.pcs.firePropertyChange(Properties.MODEL_REMOVED, null, spectrum);
    }

    public void removeAllSpectra()
    {
        HashMap<String, Spectrum> map = (HashMap<String, Spectrum>)fileToSpectrumMap.clone();
        for (String path : map.keySet())
            removeSpectrum(path);
    }

    public void removeAllSpectraForInstrument(ISpectralInstrument instrument)
    {
        HashMap<String, Spectrum> map = (HashMap<String, Spectrum>)fileToSpectrumMap.clone();
        for (String path : map.keySet())
        {
            if (map.get(path).getInstrument() == instrument)
                removeSpectrum(path);
        }
    }

    public void setVisibility(Spectrum spectrum, boolean visibility)
    {
        spectrum.setVisible(visibility);
        this.pcs.firePropertyChange(Properties.MODEL_CHANGED,null,null);
    }

    public void setFrustumVisibility(Spectrum spectrum, boolean visibility)
    {
        spectrum.setShowFrustum(visibility);
        this.pcs.firePropertyChange(Properties.MODEL_CHANGED,null,null);
    }

    public void toggleSelect(Spectrum spectrum)
    {
        if (spectrum.isSelected())
            spectrum.setUnselected();
        else
            spectrum.setSelected();
        this.pcs.firePropertyChange(Properties.MODEL_CHANGED,null,null);
        selectAll=false;
    }

    public void select(Spectrum spectrum)
    {
        spectrum.setSelected();
        this.pcs.firePropertyChange(Properties.MODEL_CHANGED,null,null);
        selectAll=false;
    }

    public void deselect(Spectrum spectrum)
    {
        spectrum.setUnselected();
        this.pcs.firePropertyChange(Properties.MODEL_CHANGED,null,null);
        selectAll=false;
    }

    public void toggleSelectAll()
    {
        if (!selectAll) // we're not in "select all" mode so go ahead and select all actors
        {
            for (Spectrum spectrum : fileToSpectrumMap.values())
                spectrum.setSelected();
            selectAll=true;
        }
        else
        {
            for (Spectrum spectrum : fileToSpectrumMap.values())
                spectrum.setUnselected();
            selectAll=false;
        }
        this.pcs.firePropertyChange(Properties.MODEL_CHANGED,null,null);
    }

    public void deselectAll()
    {
        for (Spectrum spectrum : fileToSpectrumMap.values())
            spectrum.setUnselected();
        selectAll=false;
        this.pcs.firePropertyChange(Properties.MODEL_CHANGED,null,null);
    }

    public List<Spectrum> getSelectedSpectra()
    {
        List<Spectrum> spectra=Lists.newArrayList();
        for (Spectrum s : fileToSpectrumMap.values())
            if (s.isSelected())
                spectra.add(s);
        return spectra;
    }

    public void propertyChange(PropertyChangeEvent evt)
    {
        this.pcs.firePropertyChange(Properties.MODEL_CHANGED, null, null);
    }

    public String getClickStatusBarText(vtkProp prop, int cellId, double[] pickPosition)
    {
        String filename = actorToFileMap.get(prop);
        Spectrum spectrum = this.fileToSpectrumMap.get(filename);
//        System.out.println("SpectraCollection: getClickStatusBarText: time is " + ((OTESSpectrum)spectrum).getTime());
        if (spectrum==null)
            return "";
        return spectrum.getInstrument().getDisplayName() + " spectrum " + filename.substring(16, 25) + " acquired at " + spectrum.getDateTime().toString() /*+ "(SCLK: " + ((OTESSpectrum)spectrum).getTime() + ")"*/;
    }

    public String getSpectrumName(vtkProp actor)
    {
        return actorToFileMap.get(actor);
    }

    public Spectrum getSpectrum(String file)
    {
        return fileToSpectrumMap.get(file);
    }

    public SearchSpec getSearchSpec(String file)
    {
        return fileToSpecMap.get(file);
    }

    public boolean containsSpectrum(String file)
    {
        return fileToSpectrumMap.containsKey(file);
    }

    public void tagSpectraWithMetadata(String filename, SearchSpec spec)
    {
        fileToSpecMap.put(filename, spec);
        Spectrum spectrum = fileToSpectrumMap.get(filename);
        if (spectrum != null) spectrum.setMetadata(spec);
    }

    public void tagSpectraWithMetadata(List<List<String>> filenames, SearchSpec spec)
    {
        for (List<String> list : filenames)
        {
            fileToSpecMap.put(list.get(0), spec);
            Spectrum spectrum = fileToSpectrumMap.get(list.get(0));
            if (spectrum != null) spectrum.setMetadata(spec);
        }
    }

    public void setColoringStyle(SpectrumColoringStyle style)
    {
        for (String file : this.fileToSpectrumMap.keySet())
        {
            Spectrum spectrum=this.fileToSpectrumMap.get(file);
            spectrum.setColoringStyle(style);
            spectrum.updateChannelColoring();
        }

        this.pcs.firePropertyChange(Properties.MODEL_CHANGED, null, null);
    }

    public void setColoringStyleForInstrument(SpectrumColoringStyle style, ISpectralInstrument instrument)
    {
        for (String file : this.fileToSpectrumMap.keySet())
        {
            Spectrum spectrum=this.fileToSpectrumMap.get(file);
            if (spectrum.getInstrument() == instrument)
            {
                spectrum.setColoringStyle(style);
                spectrum.updateChannelColoring();
            }
        }

        this.pcs.firePropertyChange(Properties.MODEL_CHANGED, null, null);
    }

    public void setChannelColoring(int[] channels, double[] mins, double[] maxs, ISpectralInstrument instrument)
    {
        for (String file : this.fileToSpectrumMap.keySet())
        {
            Spectrum spectrum=this.fileToSpectrumMap.get(file);
            if (spectrum.getInstrument() == instrument)
            {
                spectrum.setChannelColoring(channels, mins, maxs);
                spectrum.updateChannelColoring();
            }
        }

        this.pcs.firePropertyChange(Properties.MODEL_CHANGED, null, null);
    }



    public Spectrum getSpectrum(vtkActor actor)
    {
        return actorToSpectrumMap.get(actor);
    }

    public String getSpectrumName(vtkActor actor)
    {
        return actorToSpectrumMap.get(actor).getSpectrumName();
    }

    public boolean containsSpectrumName(String name)
    {
        for (Spectrum spec : actorToSpectrumMap.values())
        {
            if (spec.getSpectrumName().equals(name)) return true;
        }
        return false;
    }

    public Spectrum getSpectrumForName(String name)
    {
        for (Spectrum spec : actorToSpectrumMap.values())
        {
            if (spec.getSpectrumName().equals(name)) return spec;
        }
        return null;
    }

    @Override
    public List<vtkProp> getProps()
    {
        List<vtkProp> allProps=Lists.newArrayList();
        for (Spectrum s : spectraActors.keySet())
            allProps.addAll(spectraActors.get(s));
        return allProps;
    }

    public int getCount()
    {
        return fileToSpectrumMap.size();
    }
}
