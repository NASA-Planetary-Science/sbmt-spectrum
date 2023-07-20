package spectrum.model.driver;
//package edu.jhuapl.sbmt.spectrum.model.driver;
//
//import java.awt.EventQueue;
//import java.awt.Taskbar;
//import java.io.IOException;
//import java.io.PrintStream;
//import java.lang.reflect.InvocationTargetException;
//import java.net.CookieHandler;
//import java.net.CookieManager;
//import java.net.CookiePolicy;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import javax.swing.ImageIcon;
//import javax.swing.JDialog;
//import javax.swing.JOptionPane;
//import javax.swing.UIManager;
//import javax.swing.UnsupportedLookAndFeelException;
//
//import com.jgoodies.looks.LookUtils;
//
//import edu.jhuapl.saavtk.gui.TSConsole;
//import edu.jhuapl.saavtk.model.structure.EllipsePolygon;
//import edu.jhuapl.saavtk.model.structure.Line;
//import edu.jhuapl.saavtk.model.structure.Polygon;
//import edu.jhuapl.saavtk.util.Configuration;
//import edu.jhuapl.saavtk.util.Debug;
//import edu.jhuapl.saavtk.util.DownloadableFileState;
//import edu.jhuapl.saavtk.util.FileCache;
//import edu.jhuapl.saavtk.util.LatLon;
//import edu.jhuapl.saavtk.util.SafeURLPaths;
//import edu.jhuapl.saavtk.util.ServerSettingsManager;
//import edu.jhuapl.saavtk.util.ServerSettingsManager.ServerSettings;
//import edu.jhuapl.saavtk.util.UrlStatus;
//import edu.jhuapl.sbmt.common.client.SbmtSplash;
//import edu.jhuapl.sbmt.common.client.SmallBodyViewConfig;
//import edu.jhuapl.sbmt.core.image.CustomCylindricalImageKey;
//import edu.jhuapl.sbmt.core.image.CustomPerspectiveImageKey;
//import edu.jhuapl.sbmt.dtm.model.DEMKey;
//import edu.jhuapl.sbmt.model.bennu.spectra.otes.OTES;
//import edu.jhuapl.sbmt.model.bennu.spectra.ovirs.OVIRS;
//import edu.jhuapl.sbmt.model.eros.nis.NIS;
//import edu.jhuapl.sbmt.spectrum.model.core.SpectrumInstrumentMetadata;
//import edu.jhuapl.sbmt.spectrum.model.core.search.SpectrumSearchSpec;
//import edu.jhuapl.sbmt.spectrum.model.io.SpectrumInstrumentMetadataIO;
//
///**
// * This class contains the "main" function called at the start of the program.
// * This class sets up the top level window and other initialization. The main
// * function may take one optional argument. If there are no arguments specified,
// * then the tool starts up as usual showing Eros by default. If one argument is
// * specified, it is assumed to be a path to a temporary shape model which is
// * then loaded as a custom view though it is not retained the next time the tool
// * starts.
// */
//public class SbmtTesterMultiMissionTool
//{
//    private static final SafeURLPaths SAFE_URL_PATHS = SafeURLPaths.instance();
//
//    public enum Mission
//	{
//		APL_INTERNAL("b1bc7ed"),
//		PUBLIC_RELEASE("3ee38f0"),
//    	HAYABUSA2_DEV("133314b"),
//		HAYABUSA2_STAGE("244425c"),
//		HAYABUSA2_DEPLOY("355536d"),
//		OSIRIS_REX("7cd84586"),
//		OSIRIS_REX_STAGE("7cd84587"),
//		OSIRIS_REX_DEPLOY("7cd84588"),
//		OSIRIS_REX_MIRROR_DEPLOY("7cd84589"),
//		NH_DEPLOY("8ff86312"),
//		STAGE_APL_INTERNAL("f7e441b"),
//		STAGE_PUBLIC_RELEASE("8cc8e12"),
//		TEST_APL_INTERNAL("fb404a7"),
//		TEST_PUBLIC_RELEASE("a1a32b4");
//
//		private final String hashedName;
//
//		Mission(String hashedName)
//		{
//			this.hashedName = hashedName;
//		}
//
//		String getHashedName()
//		{
//			return hashedName;
//		}
//	}
//
//	private static final String OUTPUT_FILE_NAME = "sbmtLogFile.txt";
//	private static final PrintStream SAVED_OUT = System.out;
//	private static final PrintStream SAVED_ERR = System.err;
//	private static PrintStream outputStream = null;
//
//	// DO NOT change anything about this without also confirming the script set-released-mission.sh still works correctly!
//	// This field is used during the build process to "hard-wire" a release to point to a specific server.
//	private static final Mission RELEASED_MISSION = null;
//	private static Mission mission = RELEASED_MISSION;
//	private static boolean missionConfigured = false;
//    private static volatile JDialog offlinePopup = null;
//
//	private static boolean enableAuthentication;
//
//	static
//	{
//		if (Configuration.isMac())
//		{
//			System.setProperty("apple.laf.useScreenMenuBar", "true");
//			ImageIcon erosIcon = new ImageIcon(SbmtTesterMultiMissionTool.class.getResource("/edu/jhuapl/sbmt/data/erosMacDock.png"));
//			if (!Configuration.isHeadless())
//			{
//			    Taskbar.getTaskbar().setIconImage(erosIcon.getImage());
//			}
//		}
//
////		SBMTModelBootstrap.initialize();
//
//		// Structures.
//		LatLon.initializeSerializationProxy();
//		EllipsePolygon.initializeSerializationProxy();
//		Polygon.initializeSerializationProxy();
//		Line.initializeSerializationProxy();
//
//		// Images.
//		CustomCylindricalImageKey.initializeSerializationProxy();
//		CustomPerspectiveImageKey.initializeSerializationProxy();
////		SpectrumKey.initializeSerializationProxy();
////		CustomSpectrumKey.initializeSerializationProxy();
//		DEMKey.initializeSerializationProxy();
////		BasicSpectrumInstrument.initializeSerializationProxy();
////		OTESSpectrum.initializeSerializationProxy();
//		SpectrumInstrumentMetadataIO.initializeSerializationProxy();
//		SpectrumInstrumentMetadata.initializeSerializationProxy();
//		SpectrumSearchSpec.initializeSerializationProxy();
//		OTES.initializeSerializationProxy();
//		OVIRS.initializeSerializationProxy();
//		NIS.initializeSerializationProxy();
//	}
//
//	public static void setEnableAuthentication(boolean enableAuthentication)
//	{
//		SbmtTesterMultiMissionTool.enableAuthentication = enableAuthentication;
//	}
//
//	public static Mission getMission()
//	{
//		if (mission == null)
//		{
//			// Note that System.getProperty is inconsistent with regard to whether it includes quote marks.
//			// To be sure the mission identifier is processed consistently, exclude all non-word characters.
//			String missionIdentifier = System.getProperty("edu.jhuapl.sbmt.mission").replaceAll("\\W+", "");
//			if (missionIdentifier == null)
//			{
//				throw new IllegalArgumentException("Mission was not specified at build time or run time");
//			}
//			try
//			{
//				// First see if provided mission identifier matches the enumeration
//				// name.
//				mission = Mission.valueOf(missionIdentifier);
//			}
//			catch (IllegalArgumentException e)
//			{
//				// No mission identifier with that natural enumeration name,
//				// so see if instead this is a hashed mission identifier.
//				for (Mission each : Mission.values())
//				{
//					if (each.getHashedName().equalsIgnoreCase(missionIdentifier))
//					{
//						mission = each;
//						break;
//					}
//				}
//				if (mission == null)
//				{
//					throw new IllegalArgumentException("Invalid mission identifier specified at run time: " + missionIdentifier, e);
//				}
//			}
//		}
//
//		return mission;
//	}
//
//	public static Mission configureMission()
//	{
//		if (missionConfigured)
//		{
//			return mission;
//		}
//		Mission mission = getMission();
//		Configuration.setAppName("sbmt");
//		Configuration.setCacheVersion("2");
//		Configuration.setAppTitle("SBMT");
//		missionConfigured = true;
//		return mission;
//	}
//
//	public static void shutDown()
//	{
//		boolean showConsole = TSConsole.isConfigured();
//		if (showConsole)
//		{
//			System.err.println("Close this console window to exit.");
//			TSConsole.showStandaloneConsole();
//		}
//
//		restoreStreams();
//
//		if (!showConsole)
//		{
//			System.exit(1);
//		}
//	}
//
//	protected static void setupLookAndFeel()
//	{
//		if (!Configuration.isMac())
//		{
//			try
//			{
//				UIManager.put("ClassLoader", LookUtils.class.getClassLoader());
//			}
//			catch (Exception e)
//			{
//				e.printStackTrace();
//				try
//				{
//					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//				}
//				catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1)
//				{
//					throw new RuntimeException(e1);
//				}
//			}
//		}
//	}
//
//	protected static void displaySplash(Mission mission)
//	{
//	    if (Configuration.isHeadless())
//	    {
//	        return;
//	    }
//
//	    try
//        {
//            Configuration.runAndWaitOnEDT(() -> {
//
//                SbmtSplash splash = new SbmtSplash("resources", "splashLogo.png");
//
//                splash.setAlwaysOnTop(true);
//                splash.validate();
//                splash.setVisible(true);
//
//                if (TSConsole.isEnabled())
//                {
//                    TSConsole.showStandaloneConsole();
//                }
//
//                final SbmtSplash finalSplash = splash;
//                ExecutorService executor = Executors.newSingleThreadExecutor();
//                executor.execute(() -> {
//                    // Kill the splash screen after a suitable pause.
//                    try
//                    {
//                        Thread.sleep(3500);
//                    }
//                    catch (InterruptedException e)
//                    {
//                        // Ignore this one.
//                    }
//                    finally
//                    {
//                        EventQueue.invokeLater(() -> {
//                            finalSplash.setVisible(false);
//                        });
//                    }
//                });
//                executor.shutdown();
//            });
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//	}
//
//	protected static String getOption(String[] args, String option)
//	{
//		for (String arg : args)
//		{
//			arg = arg.toLowerCase();
//			option = option.toLowerCase();
//			if (arg.startsWith(option + "="))
//			{
//				return arg.substring(option.length() + 1);
//			}
//			else if (arg.startsWith(option))
//			{
//				return arg.substring(option.length());
//			}
//		}
//		return null;
//	}
//
//	protected static void restoreStreams()
//	{
//		if (outputStream != null)
//		{
//			System.setErr(SAVED_ERR);
//			System.setOut(SAVED_OUT);
//			outputStream.close();
//			outputStream = null;
//		}
//	}
//
//	private boolean clearCache;
//	private boolean redirectStreams;
//	private String initialShapeModelPath;
//
//	protected SbmtTesterMultiMissionTool()
//	{
//		this.clearCache = false;
//		this.redirectStreams = true;
//		this.initialShapeModelPath = null;
//	}
//
//	public void run(String[] args) throws IOException, InterruptedException, InvocationTargetException
//	{
//		processArguments(args);
//
//		setUpStreams();
//
//		setUpAuthentication();
//
//		clearCache();
//
//		// Display splash screen.
//		displaySplash(mission);
//
//		// Start up the client.
//		new SbmtTestRunnable(initialShapeModelPath).run();
//
//	}
//
//	protected void processArguments(String[] args)
//	{
//		// Get options.
//		redirectStreams = getOption(args, "--no-stream-redirect") == null;
//		clearCache = getOption(args, "--auto-clear-cache") != null;
//		SmallBodyViewConfig.betaMode = getOption(args, "--beta") != null;
//		if (getOption(args, "--debug") != null)
//		{
//			Debug.setEnabled(true);
//		}
//        if (getOption(args, "--debug-cache") != null)
//        {
//            FileCache.enableDebug(true);
//        }
//
//		// Get other arguments.
//		initialShapeModelPath = null;
//		for (String arg : args)
//		{
//			if (!arg.startsWith("-"))
//			{
//				// First non-option is an optional shape model path.
//				initialShapeModelPath = arg;
//				// No other non-option arguments.
//				break;
//			}
//		}
//	}
//
//	protected void setUpStreams() throws IOException, InvocationTargetException, InterruptedException
//	{
//		if (outputStream != null)
//		{
//			throw new IllegalStateException("Cannot call setUpStreams more than once");
//		}
//
//		if (redirectStreams)
//		{
//			Path outputFilePath = SAFE_URL_PATHS.get(Configuration.getApplicationDataDir(), OUTPUT_FILE_NAME);
//			outputStream = new PrintStream(Files.newOutputStream(outputFilePath));
//			System.setOut(outputStream);
//			System.setErr(outputStream);
//			TSConsole.configure(true, "Message Console", outputStream, outputStream);
//		}
//	}
//
//	protected void clearCache()
//	{
//		if (clearCache)
//		{
//			Configuration.clearCache();
//		}
//	}
//
//    protected void setUpAuthentication()
//    {
//        if (enableAuthentication)
//        {
//            ServerSettings serverSettings = ServerSettingsManager.instance().get();
//
//            if (serverSettings.isServerAccessible())
//            {
//                Configuration.getSwingAuthorizor().setUpAuthorization();
//            }
//            else
//            {
//                FileCache.setOfflineMode(true, Configuration.getCacheDir());
//                String message = "Unable to connect to server " + Configuration.getDataRootURL() + ". Starting in offline mode. See console log for more information.";
//                if (Configuration.isHeadless())
//                {
//                    System.err.println(message);
//                }
//                else
//                {
//                    Configuration.runOnEDT(() -> {
//                        JOptionPane pane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
//                        offlinePopup = pane.createDialog("No internet access");
//                        offlinePopup.setVisible(true);
//                        offlinePopup.dispose();
//                    });
//                }
//            }
//            FileCache.instance().queryAllInBackground(true);
//
//            FileCache.addServerUrlPropertyChangeListener(e -> {
//                if (e.getPropertyName().equals(DownloadableFileState.STATE_PROPERTY))
//                {
//                    DownloadableFileState rootState = (DownloadableFileState) e.getNewValue();
//                    if (rootState.getUrlState().getStatus() == UrlStatus.NOT_AUTHORIZED)
//                    {
//                        if (Configuration.getSwingAuthorizor().setUpAuthorization())
//                        {
//                            FileCache.instance().queryAllInBackground(true);
//                        }
//                    }
//                }
//            });
//        }
//    }
//
//	public static void main(String[] args)
//	{
//		SbmtTesterMultiMissionTool tool = null;
//		try
//		{
//			// Global (static) initializations.
//			setupLookAndFeel();
//
//			// The following line appears to be needed on some systems to prevent server redirect errors.
//			CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
//
//			configureMission();
//
//			tool = new SbmtTesterMultiMissionTool();
//			tool.run(args);
//		}
//		catch (Throwable throwable)
//		{
//			throwable.printStackTrace();
//			System.err.println("\nFatal error during launch. Please review the information above.");
//			shutDown();
//		}
//	}
//
//}
