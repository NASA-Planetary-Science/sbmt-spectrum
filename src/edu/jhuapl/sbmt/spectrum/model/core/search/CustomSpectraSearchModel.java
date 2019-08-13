package edu.jhuapl.sbmt.spectrum.model.core.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import org.apache.commons.io.FileUtils;

import edu.jhuapl.saavtk.model.FileType;
import edu.jhuapl.saavtk.model.ModelManager;
import edu.jhuapl.saavtk.util.FileUtil;
import edu.jhuapl.saavtk.util.IdPair;
import edu.jhuapl.saavtk.util.MapUtil;
import edu.jhuapl.sbmt.client.SbmtSpectrumModelFactory;
import edu.jhuapl.sbmt.config.Strings;
import edu.jhuapl.sbmt.model.image.ImageType;
import edu.jhuapl.sbmt.spectrum.model.core.BasicSpectrum;
import edu.jhuapl.sbmt.spectrum.model.core.BasicSpectrumInstrument;
import edu.jhuapl.sbmt.spectrum.model.core.SpectrumInstrumentFactory;
import edu.jhuapl.sbmt.spectrum.model.core.interfaces.CustomSpectraResultsListener;
import edu.jhuapl.sbmt.spectrum.model.key.CustomSpectrumKey;
import edu.jhuapl.sbmt.spectrum.model.key.SpectrumKey;
import edu.jhuapl.sbmt.spectrum.model.sbmtCore.spectra.CustomSpectrumKeyInterface;
import edu.jhuapl.sbmt.spectrum.model.sbmtCore.spectra.Spectrum;
import edu.jhuapl.sbmt.spectrum.model.sbmtCore.spectra.SpectrumColoringStyle;
import edu.jhuapl.sbmt.spectrum.model.sbmtCore.spectra.SpectrumKeyInterface;
import edu.jhuapl.sbmt.spectrum.rendering.IBasicSpectrumRenderer;

import crucible.crust.metadata.api.Key;
import crucible.crust.metadata.api.Metadata;
import crucible.crust.metadata.api.Version;
import crucible.crust.metadata.impl.FixedMetadata;
import crucible.crust.metadata.impl.SettableMetadata;
import crucible.crust.metadata.impl.gson.Serializers;

public class CustomSpectraSearchModel extends BaseSpectrumSearchModel
{
    String fileExtension = "";
    private List<CustomSpectrumKeyInterface> customSpectra;
    private Vector<CustomSpectraResultsListener> customSpectraListeners;
    private boolean initialized = false;
    final Key<List<CustomSpectrumKeyInterface>> customSpectraKey = Key.of("customSpectra");

    public CustomSpectraSearchModel(boolean hasHierarchicalSpectraSearch, boolean hasHypertreeBasedSpectraSearch,
    		SpectraHierarchicalSearchSpecification<?> hierarchicalSpectraSearchSpecification, ModelManager modelManager,
    		BasicSpectrumInstrument instrument)
    {
        super(modelManager, instrument);
        this.customSpectra = new Vector<CustomSpectrumKeyInterface>();
        this.customSpectraListeners = new Vector<CustomSpectraResultsListener>();

        getColoringModel().setRedMaxVal(instrument.getRGBMaxVals()[0]);
        getColoringModel().setGreenMaxVal(instrument.getRGBMaxVals()[1]);
        getColoringModel().setBlueMaxVal(instrument.getRGBMaxVals()[2]);

        getColoringModel().setRedIndex(instrument.getRGBDefaultIndices()[0]);
        getColoringModel().setGreenIndex(instrument.getRGBDefaultIndices()[1]);
        getColoringModel().setBlueIndex(instrument.getRGBDefaultIndices()[2]);

        updateColoring();
    }

    public List<CustomSpectrumKeyInterface> getCustomSpectra()
    {
        return customSpectra;
    }

    public void setCustomSpectra(List<CustomSpectrumKeyInterface> customSpectra)
    {
        this.customSpectra = customSpectra;
    }

    public void addResultsChangedListener(CustomSpectraResultsListener listener)
    {
        customSpectraListeners.add(listener);
    }

    public void removeResultsChangedListener(CustomSpectraResultsListener listener)
    {
        customSpectraListeners.remove(listener);
    }

    protected void fireResultsChanged()
    {
        for (CustomSpectraResultsListener listener : customSpectraListeners)
        {
            listener.resultsChanged(customSpectra);
        }
    }

//    public void loadSpectrum(SpectrumKeyInterface key, SpectraCollection images) throws FitsException, IOException
//    {
//        images.addSpectrum(key, true);
//    }
//
//    public void loadSpectra(String name, CustomSpectrumKeyInterface info)
//    {
//    	try
//        {
//            if (!spectrumCollection.containsKey(info))
//            {
//                loadSpectrum(info, spectrumCollection);
//            }
//        }
//        catch (Exception e1) {
//            JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(null),
//                    "There was an error mapping the spectra.",
//                    "Error",
//                    JOptionPane.ERROR_MESSAGE);
//
//            e1.printStackTrace();
//        }
//   }
//
//    public void unloadSpectrum(SpectrumKeyInterface key, SpectraCollection spectra)
//    {
//        spectra.removeSpectrum(key);
//    }
//
//    public void unloadSpectrum(String name, CustomSpectrumKeyInterface key)
//    {
//    	unloadSpectrum(key, spectrumCollection);
//   }

    public List<SpectrumKeyInterface> createSpectrumKeys(String boundaryName, BasicSpectrumInstrument instrument)
    {
        List<SpectrumKeyInterface> result = new ArrayList<SpectrumKeyInterface>();
        result.add(createSpectrumKey(boundaryName, instrument));
        return result;
    }

    public SpectrumKeyInterface createSpectrumKey(String imagePathName, BasicSpectrumInstrument instrument)
    {
        SpectrumKeyInterface key = new SpectrumKey(customDataFolder + File.separator + imagePathName, null, null, instrument, "");
        return key;
    }

    //TODO: UPDATE THIS TO SAVE SPECTRA
    public void saveSpectrum(int index, CustomSpectrumKeyInterface oldSpectrumInfo, CustomSpectrumKeyInterface newSpectrumInfo) throws IOException
    {
        String uuid = UUID.randomUUID().toString();

        // If newSpectrumInfo.imagefilename is null, that means we are in edit mode
        // and should continue to use the existing image
        if (newSpectrumInfo.getSpectrumFilename() == null)
        {
            newSpectrumInfo.setSpectrumFilename(oldSpectrumInfo.getSpectrumFilename());
        }
        else
        {
            String newFilename = newSpectrumInfo.getName() + "-" + uuid + ".spect";
            String newFilepath = customDataFolder + File.separator + newFilename;
            FileUtil.copyFile(newSpectrumInfo.getSpectrumFilename(),  newFilepath);
            String newFileInfoname = newSpectrumInfo.getName() + "-" + uuid + ".INFO";
            String newFileInfopath = customDataFolder + File.separator + newFileInfoname;
            FileUtil.copyFile(newSpectrumInfo.getPointingFilename(),  newFileInfopath);
            // Change newImageInfo.imagefilename to the new location of the file
            newSpectrumInfo.setSpectrumFilename(newFilename);
            newSpectrumInfo.setPointingFilename(newFileInfoname);
        }

        if (index >= customSpectra.size())
        {
            customSpectra.add(newSpectrumInfo);
        }
        else
        {
            customSpectra.set(index, newSpectrumInfo);
        }

        List<BasicSpectrum> tempResults = new ArrayList<BasicSpectrum>();
        for (CustomSpectrumKeyInterface info : customSpectra)
        {
        	IBasicSpectrumRenderer renderer = null;
			try
			{
				renderer = SbmtSpectrumModelFactory.createSpectrumRenderer(customDataFolder + File.separator + info.getSpectrumFilename(), SpectrumInstrumentFactory.getInstrumentForName(instrument.getDisplayName()));
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			tempResults.add(renderer.getSpectrum());
        }
        updateConfigFile();

        setSpectrumRawResults(tempResults);
        fireResultsChanged();
        fireResultsCountChanged(this.results.size());

    }

    //TODO is this really needed?
//    /**
//     * This function unmaps the image from the renderer and maps it again,
//     * if it is currently shown.
//     * @throws IOException
//     * @throws FitsException
//     */
//    public void remapSpectrumToRenderer(int index) throws FitsException, IOException
//    {
//        CustomSpectrumKeyInterface spectrumKey = customSpectra.get(index);
//        // Remove the image from the renderer
//
//        if (spectrumCollection.containsKey(spectrumKey))
//        {
//            IBasicSpectrumRenderer spectrum = spectrumCollection.getSpectrumFromKey(spectrumKey);
//            boolean visible = spectrum.isVisible();
//            if (visible)
//                spectrum.setVisible(false);
//            spectrumCollection.removeSpectrum(spectrumKey);
//            spectrumCollection.addSpectrum(spectrumKey);
//            if (visible)
//                spectrum.setVisible(true);
//        }
//    }

    public CustomSpectrumKeyInterface getSpectrumKeyForIndex(int index)
    {
        CustomSpectrumKeyInterface spectrumKey = customSpectra.get(index);
        return spectrumKey;
    }

    private boolean migrateConfigFileIfNeeded() throws IOException
    {
        MapUtil configMap = new MapUtil(getConfigFilename());
        if (configMap.getAsArray(Spectrum.SPECTRUM_NAMES) != null)
        {
            //backup the old config file
            FileUtils.copyFile(new File(getConfigFilename()), new File(getConfigFilename() + ".orig"));

            //migrate it to the new format
            boolean needToUpgradeConfigFile = false;
            String[] spectrumNames = configMap.getAsArray(Spectrum.SPECTRUM_NAMES);
            if (spectrumNames == null || (spectrumNames.length == 0)) return false;
            String[] spectrumFilenames = configMap.getAsArray(Spectrum.SPECTRUM_FILENAMES);
            String[] imageTypes = configMap.getAsArray(Spectrum.SPECTRUM_TYPES);
            if (spectrumFilenames == null)
            {
                // for backwards compatibility
                spectrumNames = new String[spectrumFilenames.length];
                imageTypes = new String[spectrumFilenames.length];

                for (int i=0; i<spectrumFilenames.length; ++i)
                {
                    spectrumNames[i] = new File(spectrumFilenames[i]).getName();
                    spectrumFilenames[i] = "image" + i + ".png";
                    imageTypes[i] = ImageType.GENERIC_IMAGE.toString();
                }

                // Mark that we need to upgrade config file to latest version
                // which we'll do at end of function.
                needToUpgradeConfigFile = true;
            }
            String[] sumfileNames = configMap.getAsArray(Strings.SUMFILENAMES.getName());
            String[] infofileNames = configMap.getAsArray(Strings.INFOFILENAMES.getName());

            int numImages = spectrumNames.length;
            for (int i=0; i<numImages; ++i)
            {
            	String name = spectrumNames[i];
            	String spectrumFilename = spectrumFilenames[i];
            	String infoname = infofileNames[i];
            	String sumname = sumfileNames[i];
            	String pointingFilename = "";
            	FileType fileType;
            	if (infoname.equals(""))
            	{
            		pointingFilename = sumname;
            		fileType = FileType.SUM;
            	}
            	else
            	{
            		pointingFilename = infoname;
            		fileType = FileType.INFO;
            	}

                CustomSpectrumKeyInterface spectrumInfo = new CustomSpectrumKey(name, fileType, getInstrument(), null, spectrumFilename, pointingFilename);

                customSpectra.add(spectrumInfo);
            }

            updateConfigFile();
            return true;
        }
        else
            return false;

    }

    public void updateConfigFile()
    {
        try
        {
            Serializers.serialize("CustomSpectra", this, new File(getConfigFilename()));
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void initializeSpecList() throws IOException
    {
        if (initialized)
            return;

        boolean updated = migrateConfigFileIfNeeded();
        if (!updated)
        {
            if (!(new File(getConfigFilename()).exists())) return;
            FixedMetadata metadata = Serializers.deserialize(new File(getConfigFilename()), "CustomSpectra");
            retrieve(metadata);
        }

        List<BasicSpectrum> tempResults = new ArrayList<BasicSpectrum>();
        for (CustomSpectrumKeyInterface info : customSpectra)
        {
        	IBasicSpectrumRenderer renderer = null;
			try
			{
				renderer = SbmtSpectrumModelFactory.createSpectrumRenderer(customDataFolder + File.separator + info.getSpectrumFilename(), SpectrumInstrumentFactory.getInstrumentForName(instrument.getDisplayName()));
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	tempResults.add(renderer.getSpectrum());
        }
        setSpectrumRawResults(tempResults);

        fireResultsChanged();
        fireResultsCountChanged(customSpectra.size());
    }

    public void setSpectrumVisibility(BasicSpectrum spectrum, boolean visible)
    {
    	fireFootprintVisibilityChanged(spectrum, visible);
//        if (spectrumCollection.containsKey(key))
//        {
//            IBasicSpectrumRenderer spectrum = spectrumCollection.getSpectrumFromKey(key);
//            spectrum.setVisible(visible);
//        }
    }

    @Override
    public CustomSpectrumKeyInterface[] getSelectedSpectrumKeys()
    {
        int[] indices = selectedImageIndices;
        CustomSpectrumKeyInterface[] selectedKeys = new CustomSpectrumKeyInterface[indices.length];
        if (indices.length > 0)
        {
            int i=0;
            for (int index : indices)
            {
                String spectrum = getSpectrumRawResults().get(index).getDataName();
                String name = new File(spectrum).getName();
                spectrum = spectrum.substring(0, spectrum.length()-4);
                CustomSpectrumKeyInterface selectedKey = getSpectrumKeyForIndex(index);
                selectedKeys[i++] = selectedKey;
            }
        }
        return selectedKeys;
    }

    private String getConfigFilename()
    {
        return customDataFolder + File.separator + "specConfig.txt";
    }

    public void showFootprints(IdPair idPair)
    {
        int startId = idPair.id1;
        int endId = idPair.id2;

        SpectrumColoringStyle style = SpectrumColoringStyle.getStyleForName(getColoringModel().getSpectrumColoringStyleName());
        for (int i=startId; i<endId; ++i)
        {
            if (i < 0)
                continue;
            else if(i >= getSpectrumRawResults().size())
                break;
            fireFootprintVisibilityChanged(results.get(i), true);

//            try
//            {
//            	String spectrum = getSpectrumRawResults().get(i).getDataName();
//                spectrumCollection.addSpectrum(SafeURLPaths.instance().getUrl(spectrum), instrument, style,true);
//
//            }
//            catch (IOException e1) {
//                e1.printStackTrace();
//            }
        }
        updateColoring();
    }

    @Override
    public Metadata store()
    {
    	SettableMetadata result = SettableMetadata.of(Version.of(1, 0));
    	result.put(customSpectraKey, customSpectra);
    	return result;
    }

    @Override
    public void retrieve(Metadata source)
    {
    	try
    	{
    		customSpectra = source.get(customSpectraKey);
    	}
    	catch (ClassCastException cce)
    	{
    		//not sure if we need to worry about this
//    		Key<Metadata[]> oldCustomSpectraKey = Key.of("customSpectra");
//    		Metadata[] oldCustomSpectra = source.get(oldCustomSpectraKey);
//    		List<CustomSpectrumKeyInterface> migratedSpectra = new ArrayList<CustomSpectrumKeyInterface>();
//    		for (Metadata meta : oldCustomSpectra)
//    		{
//    			migratedSpectra.add(CustomSpectrumKeyInterface.retrieveOldFormat(meta));
//    		}
//    		customSpectra = migratedSpectra;
//    		updateConfigFile();
    	}
    }

    public void saveSpectra(List<CustomSpectrumKeyInterface> customImages, String filename)
    {
        SettableMetadata configMetadata = SettableMetadata.of(Version.of(1, 0));

        final Key<List<CustomSpectrumKeyInterface>> customSpectraKey = Key.of("SavedSpectra");

        configMetadata.put(customSpectraKey, customImages);
        try
        {
            Serializers.serialize("SavedSpectra", configMetadata, new File(filename));
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void loadSpectra(String file)
    {
        FixedMetadata metadata;
        try
        {
            final Key<Metadata[]> customSpectraKey = Key.of("SavedSpectra");
            metadata = Serializers.deserialize(new File(file), "SavedSpectra");
            Metadata[] metadataArray = read(customSpectraKey, metadata);
            for (Metadata meta : metadataArray)
            {
                CustomSpectrumKeyInterface info = CustomSpectrumKeyInterface.retrieve(meta);
                customSpectra.add(info);
            }
            System.out.println("CustomSpectrumModel: loadSpectra: number of spectra now " + customSpectra.size());
            updateConfigFile();
            fireResultsChanged();

        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    protected <T> void write(Key<T> key, T value, SettableMetadata configMetadata)
    {
        if (value != null)
        {
            configMetadata.put(key, value);
        }
    }

    protected <T> T read(Key<T> key, Metadata configMetadata)
    {
        T value = configMetadata.get(key);
        if (value != null)
            return value;
        return null;
    }

    public String getSpectrumColoringStyleName()
    {
        return getColoringModel().getSpectrumColoringStyleName();
    }


    public void setSpectrumColoringStyleName(String spectrumColoringStyleName)
    {
        this.getColoringModel().setSpectrumColoringStyleName(spectrumColoringStyleName);
    }
}