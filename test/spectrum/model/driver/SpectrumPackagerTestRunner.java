package spectrum.model.driver;
//package edu.jhuapl.sbmt.spectrum.model.driver;
//
//import edu.jhuapl.saavtk.util.Configuration;
//import edu.jhuapl.sbmt.client.SbmtMultiMissionTool;
//
///**
// * This class contains the "main" function called at the start of the program
// * for the APL internal version. It sets up some APL version specific
// * configuration options and then calls the public (non-APL) version's main
// * function.
// */
//public class SpectrumPackagerTestRunner
//{
//	public static void main(String[] args)
//	{
//		String opSysName = System.getProperty("os.name").toLowerCase();
//		if (opSysName.contains("mac"))
//		{
//			// to set the name of the app in the Mac App menu:
//			System.setProperty("apple.awt.application.name", "Small Body Mapping Tool");
//			//to show the menu bar at the top of the screen:
//			System.setProperty("apple.laf.useScreenMenuBar", "true");
//		}
//
//		Configuration.setAPLVersion(true);
//
//		SbmtMultiMissionTool.setEnableAuthentication(true);
//
//		// Call the standard client main function
//		SbmtMultiMissionTool.main(args);
//	}
//}
