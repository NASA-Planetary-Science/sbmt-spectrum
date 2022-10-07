package edu.jhuapl.sbmt.spectrum.model.hypertree;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import vtk.vtkPolyData;

import edu.jhuapl.saavtk.model.ShapeModelBody;
import edu.jhuapl.saavtk.model.ShapeModelType;
import edu.jhuapl.saavtk.util.Configuration;
import edu.jhuapl.saavtk.util.FileCache;
import edu.jhuapl.saavtk.util.Frustum;
import edu.jhuapl.saavtk.util.MathUtil;
import edu.jhuapl.saavtk.util.NativeLibraryLoader;
import edu.jhuapl.saavtk.util.SafeURLPaths;
import edu.jhuapl.sbmt.client2.SbmtModelFactory;
import edu.jhuapl.sbmt.client2.SbmtMultiMissionTool;
import edu.jhuapl.sbmt.common.client.SmallBodyModel;
import edu.jhuapl.sbmt.common.client.SmallBodyViewConfig;
import edu.jhuapl.sbmt.core.image.ImageSource;
import edu.jhuapl.sbmt.core.image.InfoFileReader;
import edu.jhuapl.sbmt.model.bennu.spectra.otes.OTES;
import edu.jhuapl.sbmt.model.bennu.spectra.otes.OTESSpectrum;
import edu.jhuapl.sbmt.model.bennu.spectra.ovirs.OVIRS;
import edu.jhuapl.sbmt.model.bennu.spectra.ovirs.OVIRSSpectrum;
import edu.jhuapl.sbmt.query.IQueryBase;
import edu.jhuapl.sbmt.query.fixedlist.FixedListQuery;
import edu.jhuapl.sbmt.query.fixedlist.FixedListSearchMetadata;
import edu.jhuapl.sbmt.spectrum.model.core.BasicSpectrum;
import edu.jhuapl.sbmt.spectrum.model.core.BasicSpectrumInstrument;
import edu.jhuapl.sbmt.spectrum.model.core.interfaces.IBasicSpectrumRenderer;
import edu.jhuapl.sbmt.spectrum.model.io.SpectrumInstrumentMetadataIO;
import edu.jhuapl.sbmt.spectrum.model.statistics.SpectrumStatistics;
import edu.jhuapl.sbmt.spectrum.model.statistics.SpectrumStatistics.Sample;
import edu.jhuapl.sbmt.spectrum.rendering.AdvancedSpectrumRenderer;

/**
 * Generate an input file for the Spectrum hypertree search.  This will be a file
 * that has a list of spectrum filenames, and their bounding boxes.
 * spectrum1.spect bbxmin, bbxmax, bbymin, bbymax, bbzmin, bbzmax, tmin, tmax
 * @author osheacm1
 *
 */
public class SpectrumBoundsCalculator
{


    public static void main(String[] args) throws IOException {

        Configuration.setAPLVersion(true);
        SbmtMultiMissionTool.configureMission();

        // need password to access OREX data
        Configuration.authenticate();

        SmallBodyViewConfig.initialize();

        System.setProperty("java.awt.headless", "true");
        NativeLibraryLoader.loadHeadlessVtkLibraries();


        // get body model
        SmallBodyViewConfig config;
        String bodyName = args[2];
        if (bodyName.equalsIgnoreCase("EARTH")) {
            config = SmallBodyViewConfig.getSmallBodyConfig(ShapeModelBody.EARTH, ShapeModelType.OREX);
        }
        else if (bodyName.equalsIgnoreCase("BENNU")) {
            config = SmallBodyViewConfig.getSmallBodyConfig(ShapeModelBody.RQ36, ShapeModelType.OREX);
        }
        else {
            System.err.println("No support for body named " + bodyName);
            return;
        }
        SmallBodyModel body = SbmtModelFactory.createSmallBodyModel(config);

        BasicSpectrumInstrument instrument;
        String instName = args[0];
        if (instName.equalsIgnoreCase("OTES")) {
            instrument = new OTES();
        }
        else if (instName.equalsIgnoreCase("OVIRS")) {
            instrument = new OVIRS();
        }
        else {
            System.err.println("No support for spectral instrument named " + instName);
            return;
        }

        String type = args[1];
        String baseDir = bodyName.toLowerCase() + "/osirisrex/" + instName.toLowerCase() + "/" + type.toLowerCase();

        // create a bounds file to write to                                    remove / in case of input such as l3/if or l3/reff
        String boundsFile = "bounds_" + instName.toLowerCase() + "_" + type.toLowerCase().replace("/", "") + ".bounds";
        FileWriter fw;
        try
        {
            fw = new FileWriter(boundsFile);
            BufferedWriter bw = new BufferedWriter(fw);

            IQueryBase queryType = instrument.getQueryBase();
            List<BasicSpectrum> spectrafiles = new ArrayList<BasicSpectrum>();
            if (queryType instanceof FixedListQuery)
            {
                FixedListQuery query = (FixedListQuery)queryType;
                spectrafiles = instrument.getQueryBase().runQuery(FixedListSearchMetadata.of("Spectrum Search", "spectrumlist.txt", "spectra", baseDir, ImageSource.CORRECTED_SPICE)).getResultlist();
            }

            int iFile = 0;
            for (BasicSpectrum spectraFile : spectrafiles) {
                // get filename
                String thisFileName = spectraFile.getFullPath();

                // create spectrum
                IBasicSpectrumRenderer spectrumRenderer;
                if (instrument instanceof OTES) {
                    OTESSpectrum spectrum = new OTESSpectrum(thisFileName, (SpectrumInstrumentMetadataIO)body.getSmallBodyConfig().getHierarchicalSpectraSearchSpecification(), body.getBoundingBoxDiagonalLength(), instrument, true, false);
                    spectrumRenderer = new AdvancedSpectrumRenderer(spectrum, body, false);
                }
                else  { // only 2 options right now, but may change in the future
                    OVIRSSpectrum spectrum = new OVIRSSpectrum(thisFileName, (SpectrumInstrumentMetadataIO)body.getSmallBodyConfig().getHierarchicalSpectraSearchSpecification(), body.getBoundingBoxDiagonalLength(), instrument, true, false);
                    spectrumRenderer = new AdvancedSpectrumRenderer(spectrum, body, false);
                }


                // get x, y, z bounds and time, emission, incidence, phase, s/c distance
                String basePath = FilenameUtils.getPath(thisFileName);
                String fn = FilenameUtils.getBaseName(thisFileName);
                Path infoFile = Paths.get(basePath).resolveSibling("infofiles-corrected/"+fn+".INFO");

                FileCache.setOfflineMode(true, "/project/sbmt2/prod/");

                String createFileURL = SafeURLPaths.instance().getUrl(infoFile.toString());
                System.out.println("SpectrumBoundsCalculator: main: createFileURL " + createFileURL);
                System.out.println("SpectrumBoundsCalculator: main: info file is " + infoFile.toString());
                System.out.println("SpectrumBoundsCalculator: main: getting from server as " + FileCache.getFileFromServer(infoFile.toString()));
                InfoFileReader reader = new InfoFileReader(FileCache.getFileFromServer(infoFile.toString()).getAbsolutePath());
                reader.read();

                Vector3D origin = new Vector3D(reader.getSpacecraftPosition());
                Vector3D fovUnit = new Vector3D(reader.getFrustum2()).normalize(); // for whatever reason, frustum2 contains the vector along the field of view cone
                Vector3D boresightUnit = new Vector3D(reader.getBoresightDirection()).normalize();
                Vector3D lookTarget = origin
                        .add(boresightUnit.scalarMultiply(origin.getNorm()));


                double fovDeg = Math
                        .toDegrees(Vector3D.angle(fovUnit, boresightUnit) * 2.);
                Vector3D toSun = new Vector3D(reader.getSunPosition()).normalize();
                Frustum frustum = new Frustum(origin.toArray(), lookTarget.toArray(),
                        boresightUnit.orthogonal().toArray(), fovDeg, fovDeg);
                double[] frustum1 = frustum.ul;
                double[] frustum2 = frustum.ur;
                double[] frustum3 = frustum.lr;
                double[] frustum4 = frustum.ll;
                double[] spacecraftPosition = reader.getSpacecraftPosition();

                spectrumRenderer.generateFootprint();

                try {
                    List<Sample> sampleEmergenceAngle = SpectrumStatistics.sampleEmergenceAngle(spectrumRenderer, new Vector3D(spacecraftPosition));
                    double em = SpectrumStatistics.getWeightedMean(sampleEmergenceAngle);
                    List<Sample> sampleIncidenceAngle = SpectrumStatistics.sampleIncidenceAngle(spectrumRenderer, toSun);
                    double inc = SpectrumStatistics.getWeightedMean(sampleIncidenceAngle);
                    List<Sample> samplePhaseAngle = SpectrumStatistics.samplePhaseAngle( sampleIncidenceAngle, sampleEmergenceAngle);
                    double ph = SpectrumStatistics.getWeightedMean(samplePhaseAngle);
                    // TODO s/c distance relative to body in info file?
                    double dist = MathUtil.vnorm(spacecraftPosition);


                    vtkPolyData tmp = body.computeFrustumIntersection(
                            spacecraftPosition, frustum1, frustum2, frustum3, frustum4);
                    if (tmp != null) {
                        double[] bbox = tmp.GetBounds();
                        System.out.println("file " + iFile++ + ": " + bbox[0] + ", " + bbox[1] + ", " + bbox[2] + ", " + bbox[3]);
                        // write min and max for x, y, z, time, emission, incidence, phase, distance.
                        bw.write(thisFileName + " " + bbox[0] + " " + bbox[1] + " " + bbox[2] + " " + bbox[3] + " " + bbox[4] + " " + bbox[5] +" " + reader.getStartTime() +
                                " " + reader.getStopTime() +" " + em +" " + em + " "+ inc+" " + inc + " "+ ph +" " + ph + " "+ dist + " " + dist +" \n");

                    }
                } catch (Exception e) {
                    System.err.println("error creating statistics for spectrum " + thisFileName);
                    e.printStackTrace();
                }


            }
            bw.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


    }
}
