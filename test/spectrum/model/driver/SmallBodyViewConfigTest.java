package spectrum.model.driver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import edu.jhuapl.ses.jsqrl.api.Key;
import edu.jhuapl.ses.jsqrl.impl.FixedMetadata;
import edu.jhuapl.ses.jsqrl.impl.SettableMetadata;
import edu.jhuapl.ses.jsqrl.impl.gson.Serializers;
import edu.jhuapl.saavtk.config.ConfigArrayList;
import edu.jhuapl.saavtk.config.IBodyViewConfig;
import edu.jhuapl.saavtk.config.ViewConfig;
import edu.jhuapl.saavtk.model.ShapeModelBody;
import edu.jhuapl.saavtk.model.ShapeModelType;
import edu.jhuapl.saavtk.util.Configuration;
import edu.jhuapl.saavtk.util.FileCache;
import edu.jhuapl.saavtk.util.SafeURLPaths;
import edu.jhuapl.saavtk.util.UnauthorizedAccessException;
import edu.jhuapl.sbmt.config.BasicConfigInfo;
import edu.jhuapl.sbmt.config.SmallBodyViewConfig;
import edu.jhuapl.sbmt.config.SmallBodyViewConfigMetadataIO;
import edu.jhuapl.sbmt.core.body.BodyViewConfig;
import edu.jhuapl.sbmt.core.config.IFeatureConfig;
import edu.jhuapl.sbmt.core.config.ISmallBodyViewConfig;

/**
* A SmallBodyConfig is a class for storing all which models should be instantiated
* together with a particular small body. For example some models like Eros
* have imaging, spectral, and lidar data whereas other models may only have
* imaging data. This class is also used when creating (to know which tabs
* to create).
*/
public class SmallBodyViewConfigTest extends BodyViewConfig implements ISmallBodyViewConfig
{
	static public boolean fromServer = false;
    private static final Map<String, BasicConfigInfo> VIEWCONFIG_IDENTIFIERS = new HashMap<>();
    private static final Map<String, IBodyViewConfig> LOADED_VIEWCONFIGS = new HashMap<>();

    static public Map<String, BasicConfigInfo> getConfigIdentifiers() { return VIEWCONFIG_IDENTIFIERS; }

    static public SmallBodyViewConfigTest getSmallBodyConfig(BasicConfigInfo configInfo)
    {
    	ShapeModelBody bodyType = configInfo.getBody();
    	ShapeModelType authorType = configInfo.getAuthor();
    	String version = configInfo.getVersion();

    	if (authorType == ShapeModelType.CUSTOM)
    	{
    		return SmallBodyViewConfigTest.ofCustom(configInfo.getShapeModelName(), false);
    	}
    	if (version != null)
    	{
    		return getSmallBodyConfig(bodyType, authorType, version);
    	}
    	else
    	{
    		return getSmallBodyConfig(bodyType, authorType);
    	}


    }

    static public SmallBodyViewConfigTest getSmallBodyConfig(ShapeModelBody name, ShapeModelType author)
    {
    	String configID = author.toString() + "/" + name.toString();

    	if (!LOADED_VIEWCONFIGS.containsKey(configID))
    	{
    	    Preconditions.checkArgument(VIEWCONFIG_IDENTIFIERS.containsKey(configID), "No configuration available for model " + configID);

    		BasicConfigInfo info = VIEWCONFIG_IDENTIFIERS.get(configID);
    		IBodyViewConfig fetchedConfig = fetchRemoteConfig(configID, info.getConfigURL(), fromServer);
    		LOADED_VIEWCONFIGS.put(configID, fetchedConfig);

    		return (SmallBodyViewConfigTest)fetchedConfig;
    	}
    	else
    		return (SmallBodyViewConfigTest)LOADED_VIEWCONFIGS.get(configID);

//        return (SmallBodyViewConfig) getConfig(name, author, null);
    }

    static public SmallBodyViewConfigTest getSmallBodyConfig(ShapeModelBody name, ShapeModelType author, String version)
    {
        String configID = author.toString() + "/" + name.toString() + " (" + version + ")";

        if (!LOADED_VIEWCONFIGS.containsKey(configID))
    	{
            Preconditions.checkArgument(VIEWCONFIG_IDENTIFIERS.containsKey(configID), "No configuration available for model " + configID);

    		IBodyViewConfig fetchedConfig = fetchRemoteConfig(configID, VIEWCONFIG_IDENTIFIERS.get(configID).getConfigURL(), fromServer);
    		LOADED_VIEWCONFIGS.put(configID, fetchedConfig);
    		return (SmallBodyViewConfigTest)fetchedConfig;
    	}
    	else
    		return (SmallBodyViewConfigTest)LOADED_VIEWCONFIGS.get(configID);

//        return (SmallBodyViewConfig) getConfig(name, author, version);
    }


    public void addInstrumentConfig(Class<?> configClass, IFeatureConfig instrumentConfig)
    {
    	List<IFeatureConfig> tmpInstrumentConfigs;
    	if (featureConfigs.containsKey(configClass))
    	{
    		tmpInstrumentConfigs = featureConfigs.get(configClass);
    		tmpInstrumentConfigs.add(instrumentConfig);
    		featureConfigs.replace(configClass, tmpInstrumentConfigs);
    	}
    	else
    	{
    		tmpInstrumentConfigs = new ArrayList<IFeatureConfig>();
    		tmpInstrumentConfigs.add(instrumentConfig);
    		featureConfigs.put(configClass, tmpInstrumentConfigs);
    	}
    }

    protected void clearInstrumentConfig()
    {
    	featureConfigs.clear();
    }

	public Map<Class<?>, List<IFeatureConfig>> getFeatureConfigs()
	{
		return featureConfigs;
	}


    private static List<ViewConfig> addRemoteEntries()
    {
    	ConfigArrayList configs = new ConfigArrayList();
        File allBodies = FileCache.getFileFromServer("allBodies.json");
        try
        {
            FixedMetadata metadata = Serializers.deserialize(allBodies, "AllBodies");
            for (Key key : metadata.getKeys())
            {
            	//Dynamically add a ShapeModelType if needed
            	SettableMetadata infoMetadata = (SettableMetadata)metadata.get(key);
            	BasicConfigInfo configInfo = new BasicConfigInfo();
            	configInfo.retrieve(infoMetadata);

            	VIEWCONFIG_IDENTIFIERS.put(key.toString(), configInfo);
            	if (configInfo.getUniqueName().equals("Gaskell/433 Eros"))
            	{
            		configs.add(getSmallBodyConfig(ShapeModelBody.EROS, ShapeModelType.GASKELL));
            	}
            }
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        return configs;

    }

    private static IBodyViewConfig fetchRemoteConfig(String name, String url, boolean fromServer)
    {
    	if (fromServer)
    		url = url.replaceFirst("https?://sbmt.jhuapl.edu", "file:///disks/d0180/htdocs-sbmt");

    	ConfigArrayList ioConfigs = new ConfigArrayList();
        ioConfigs.add(new SmallBodyViewConfig(ImmutableList.<String> copyOf(DEFAULT_GASKELL_LABELS_PER_RESOLUTION), ImmutableList.<Integer> copyOf(DEFAULT_GASKELL_NUMBER_PLATES_PER_RESOLUTION)));
        SmallBodyViewConfigMetadataIO io = new SmallBodyViewConfigMetadataIO(ioConfigs);
        try
        {
            File configFile = FileCache.getFileFromServer(url);
            FixedMetadata metadata = Serializers.deserialize(configFile, name);
            io.retrieve(metadata);
            return io.getConfigs().get(0);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
        	System.out.println("SmallBodyViewConfig: fetchRemoteConfig: IO exception ");
            e.printStackTrace();
            return null;
        }
        catch (UnauthorizedAccessException uae)
        {
        	System.err.println("No access allowed for URL, skipping");
        	return null;
        }
        catch (RuntimeException re)
        {
        	System.err.println("Can't access URL " + url + ", skipping");
        	re.printStackTrace();
        	return null;
        }

    }

    public static SmallBodyViewConfigTest ofCustom(String name, boolean temporary)
    {
        SmallBodyViewConfigTest config = new SmallBodyViewConfigTest(ImmutableList.<String>of(name), ImmutableList.<Integer>of(1));
        config.modelLabel = name;
        config.customTemporary = temporary;
        config.author = ShapeModelType.CUSTOM;

        SafeURLPaths safeUrlPaths = SafeURLPaths.instance();
        String fileName = temporary ? safeUrlPaths.getUrl(config.modelLabel) : safeUrlPaths.getUrl(safeUrlPaths.getString(Configuration.getImportedShapeModelsDir(), config.modelLabel, "model.vtk"));

        config.setShapeModelFileNames(new String[] { fileName });
//        config.shapeModelFileNames = new String[] { fileName };

        return config;
    }

    public static void initialize()
    {
    	ConfigArrayList configArray = getBuiltInConfigs();
        configArray.addAll(addRemoteEntries());

//        AsteroidConfigs.initialize(configArray);
//        BennuConfigs.initialize(configArray);
//        CometConfigs.initialize(configArray);
//        MarsConfigs.initialize(configArray);
//        NewHorizonsConfigs.initialize(configArray);
//        RyuguConfigs.initialize(configArray);
//        SaturnConfigs.initialize(configArray);
    }

    // Imaging instrument helper methods.
//    private static ImagingInstrument setupImagingInstrument(SBMTBodyConfiguration bodyConfig, ShapeModelConfiguration modelConfig, Instrument instrument, PointingSource[] imageSources, ImageType imageType)
//    {
//        SBMTFileLocator fileLocator = SBMTFileLocators.of(bodyConfig, modelConfig, instrument, ".fits", ".INFO", ".SUM", ".jpeg");
//        QueryBase queryBase = new FixedListQuery(fileLocator.get(SBMTFileLocator.TOP_PATH).getLocation(""), fileLocator.get(SBMTFileLocator.GALLERY_FILE).getLocation(""));
//        return setupImagingInstrument(fileLocator, bodyConfig, modelConfig, instrument, queryBase, imageSources, imageType);
//    }
//
//    private static ImagingInstrument setupImagingInstrument(SBMTBodyConfiguration bodyConfig, ShapeModelConfiguration modelConfig, Instrument instrument, QueryBase queryBase, PointingSource[] imageSources, ImageType imageType)
//    {
//        SBMTFileLocator fileLocator = SBMTFileLocators.of(bodyConfig, modelConfig, instrument, ".fits", ".INFO", ".SUM", ".jpeg");
//        return setupImagingInstrument(fileLocator, bodyConfig, modelConfig, instrument, queryBase, imageSources, imageType);
//    }
//
//    private static ImagingInstrument setupImagingInstrument(SBMTFileLocator fileLocator, SBMTBodyConfiguration bodyConfig, ShapeModelConfiguration modelConfig, Instrument instrument, QueryBase queryBase, PointingSource[] imageSources, ImageType imageType)
//    {
//        Builder<ImagingInstrumentConfiguration> imagingInstBuilder = ImagingInstrumentConfiguration.builder(instrument, SpectralImageMode.MONO, queryBase, imageSources, fileLocator, imageType);
//
//        // Put it all together in a session.
//        Builder<SessionConfiguration> builder = SessionConfiguration.builder(bodyConfig, modelConfig, fileLocator);
//        builder.put(SessionConfiguration.IMAGING_INSTRUMENT_CONFIG, imagingInstBuilder.build());
//        return BasicImagingInstrument.of(builder.build());
//    }
//
//    private List<ImageKeyInterface> imageMapKeys = null;

    public SmallBodyViewConfigTest(Iterable<String> resolutionLabels, Iterable<Integer> resolutionNumberElements)
    {
        super(resolutionLabels, resolutionNumberElements);
    }

    private SmallBodyViewConfigTest()
    {
        super(ImmutableList.<String>copyOf(DEFAULT_GASKELL_LABELS_PER_RESOLUTION), ImmutableList.<Integer>copyOf(DEFAULT_GASKELL_NUMBER_PLATES_PER_RESOLUTION));
    }

    @Override
    public SmallBodyViewConfig clone() // throws CloneNotSupportedException
    {
        SmallBodyViewConfig c = (SmallBodyViewConfig) super.clone();

        return c;
    }

    @Override
    public boolean isAccessible()
    {
        return FileCache.instance().isAccessible(getShapeModelFileNames()[0]);
    }

//    @Override
//    public Instrument getLidarInstrument()
//    {
//        // TODO Auto-generated method stub
//        return lidarInstrumentName;
//    }
//
//    public boolean hasHypertreeLidarSearch()
//    {
//        return hasHypertreeBasedLidarSearch;
//    }
//
//    public SpectraHierarchicalSearchSpecification<?> getHierarchicalSpectraSearchSpecification()
//    {
//        return hierarchicalSpectraSearchSpecification;
//    }

//    @Override
//    public List<ImageKeyInterface> getImageMapKeys()
//    {
//        if (!hasImageMap)
//        {
//            return ImmutableList.of();
//        }
//
//            if (imageMapKeys == null)
//            {
//                List<CustomCylindricalImageKey> imageMapKeys = ImmutableList.of();
//
//                // Newest/best way to specify maps is with metadata, if this model has it.
//                String metadataFileName = SafeURLPaths.instance().getString(serverPath("basemap"), "config.txt");
//                File metadataFile;
//                try
//                {
//                    metadataFile = FileCache.getFileFromServer(metadataFileName);
//                }
//                catch (Exception ignored)
//                {
//                    // This file is optional.
//                    metadataFile = null;
//                }
//
//                if (metadataFile != null && metadataFile.isFile())
//                {
//                    // Proceed using metadata.
//                    try
//                    {
//                        Metadata metadata = Serializers.deserialize(metadataFile, "CustomImages");
//                        imageMapKeys = metadata.get(Key.of("customImages"));
//                    }
//                    catch (Exception e)
//                    {
//                        // This ought to have worked so report this exception.
//                        e.printStackTrace();
//                    }
//                }
//                else
//                {
//                // Final option (legacy behavior). The key is hardwired. The file could be in
//                // either of two places.
//                    if (FileCache.isFileGettable(serverPath("image_map.png")))
//                    {
//                        imageMapKeys = ImmutableList.of(new CustomCylindricalImageKey("image_map", "image_map.png", ImageType.GENERIC_IMAGE, PointingSource.IMAGE_MAP, new Date(), "image_map"));
//                    }
//                    else if (FileCache.isFileGettable(serverPath("basemap/image_map.png")))
//                    {
//                        imageMapKeys = ImmutableList.of(new CustomCylindricalImageKey("image_map", "basemap/image_map.png", ImageType.GENERIC_IMAGE, PointingSource.IMAGE_MAP, new Date(), "image_map"));
//                    }
//                }
//
//                this.imageMapKeys = correctMapKeys(imageMapKeys);
//            }
//
//        return imageMapKeys;
//    }

    

	@Override
	public String getTimeHistoryFile()
	{
		return timeHistoryFile;
	}

	@Override
	public ShapeModelType getAuthor()
	{
		return author;
	}

	@Override
	public String getRootDirOnServer()
	{
		return rootDirOnServer;
	}

	@Override
	public boolean isCustomTemporary()
	{
		return isCustomTemporary();
	}

	@Override
	public double getDensity()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getRotationRate()
	{
		// TODO Auto-generated method stub
		return 0;
	}
}
