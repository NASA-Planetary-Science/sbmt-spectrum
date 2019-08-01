package edu.jhuapl.sbmt.spectrum.model.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import edu.jhuapl.saavtk.util.FileCache;
import edu.jhuapl.sbmt.core.InstrumentMetadata;
import edu.jhuapl.sbmt.spectrum.model.core.SpectrumInstrumentMetadata;
import edu.jhuapl.sbmt.spectrum.model.core.search.SpectraHierarchicalSearchSpecification;
import edu.jhuapl.sbmt.spectrum.model.core.search.SpectrumSearchSpec;

public class SpectrumInstrumentMetadataIO extends SpectraHierarchicalSearchSpecification<SpectrumSearchSpec>
{
    private static Gson gson = null;
    List<SpectrumInstrumentMetadata<SpectrumSearchSpec>> info = null;
    private File path;
    private String pathString;

    public SpectrumInstrumentMetadataIO(String instrumentName)
    {
        super(instrumentName);
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        gson = builder.create();

    }

    @Override
    public SpectraHierarchicalSearchSpecification<SpectrumSearchSpec> clone()
    {
		SpectrumInstrumentMetadataIO specIO = new SpectrumInstrumentMetadataIO(rootName);
		specIO.setPathString(pathString);
		return specIO;
    }

    @Override
    public void loadMetadata() throws FileNotFoundException
    {
        this.path = FileCache.getFileFromServer(pathString);
        readMetadata(path);
    }

    public void setPathString(String path)
    {
        this.pathString = path;
    }

    public void writeJSON(File file, String json) throws IOException
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(json);
        writer.close();
    }

    public void readMetadata(File file) throws FileNotFoundException
    {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        Type collectionType = new TypeToken<List<SpectrumInstrumentMetadata<SpectrumSearchSpec>>>(){}.getType();
        info = gson.fromJson(bufferedReader, collectionType);
    }

    public InstrumentMetadata<SpectrumSearchSpec> readMetadataFromFileForInstrument(File file, String instrumentName) throws FileNotFoundException
    {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        Type collectionType = new TypeToken<List<SpectrumInstrumentMetadata<SpectrumSearchSpec>>>(){}.getType();
        info = gson.fromJson(bufferedReader, collectionType);
        return getInstrumentMetadata(instrumentName);
    }

    /* (non-Javadoc)
     * @see edu.jhuapl.sbmt.model.bennu.InstrumentMetadataIO#getInstrumentMetadata(java.lang.String)
     */
    @Override
    public InstrumentMetadata<SpectrumSearchSpec> getInstrumentMetadata(String instrumentName)
    {
        for (SpectrumInstrumentMetadata<SpectrumSearchSpec> instInfo : info)
        {
            if (instInfo.getInstrumentName().equals(instrumentName))
            {
                return instInfo;
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see edu.jhuapl.sbmt.model.bennu.InstrumentMetadataIO#readHierarchyForInstrument(java.lang.String)
     */
    @Override
    public void readHierarchyForInstrument(String instrumentName)
    {
        InstrumentMetadata<SpectrumSearchSpec> instrumentMetadata = getInstrumentMetadata(instrumentName);
        for (SpectrumSearchSpec spec : instrumentMetadata.getSpecs())
        {
            addHierarchicalSearchPath(new String[] {spec.getDataName()}, instrumentMetadata.getSpecs().indexOf(spec),-1);
        }
    }

    public static void main(String[] args) throws FileNotFoundException
    {
//        OREXSpectrumInstrumentMetadataIO test2 = new OREXSpectrumInstrumentMetadataIO("OTES");
//        List<OREXSpectrumInstrumentMetadata> metadata = test2.readMetadata(new File("/Users/steelrj1/Desktop/metadata.json"));
//        for (OREXSpectrumInstrumentMetadata data : metadata)
//        {
//            System.out.println("MetadataTest2: test: data is " + data);
//            List<OREXSearchSpec> searchSpecs = data.getSpecs();
//            for (OREXSearchSpec spec : searchSpecs)
//            {
//                System.out.println("MetadataTest2: test: " + spec);
//            }
//        }

//        ArrayList<OREXSearchSpec> specs = new ArrayList<OREXSearchSpec>();
//        ArrayList<OREXSpectrumInstrumentMetadata<OREXSearchSpec>> infos = new ArrayList<OREXSpectrumInstrumentMetadata<OREXSearchSpec>>();
//        OREXSpectrumInstrumentMetadata<OREXSearchSpec> otesInfo = new OREXSpectrumInstrumentMetadata<OREXSearchSpec>("OTES");
//        otesInfo.setQueryType("file");
//        specs.add(new OREXSearchSpec("OTES L2 Calibrated Radiance", "/earth/osirisrex/otes/spectra/l2", "spectra", "spectrumlist.txt", ImageSource.CORRECTED_SPICE,
//                                  "Wavenumber (1/cm)", "Radiance", "OTES L2 Calibrated Radiance"));
//        specs.add(new OREXSearchSpec("OTES L3 Spot Emissivity", "/earth/osirisrex/otes/spectra/l3", "spectra", "spectrumlist.txt", ImageSource.CORRECTED_SPICE,
//              "Wavenumber (1/cm)", "Emissivity", "OTES L3 Spot Emissivity"));
//        otesInfo.addSearchSpecs(specs);
//        infos.add(otesInfo);
//
//
//        OREXSpectrumInstrumentMetadata<OREXSearchSpec> ovirsInfo = new OREXSpectrumInstrumentMetadata<OREXSearchSpec>("OVIRS");
//        ovirsInfo.setQueryType("file");
//        specs = new ArrayList<OREXSearchSpec>();
//        specs.add(new OREXSearchSpec("OVIRS L3 I/F Spectra", "/earth/osirisrex/ovirs/spectra/l3/if", "spectra", "spectrumlist.txt", ImageSource.CORRECTED_SPICE,
//              "Wavenumber (1/cm)", "I/F", "OVIRS L3 I/F Spectra"));
//        specs.add(new OREXSearchSpec("OVIRS L3 REFF", "/earth/osirisrex/ovirs/spectra/l3/reff", "spectra", "spectrumlist.txt", ImageSource.CORRECTED_SPICE,
//                "Wavenumber (1/cm)", "REFF", "OVIRS L3 REFF"));
//        ovirsInfo.addSearchSpecs(specs);
//        infos.add(ovirsInfo);
//
//        String je = gson.toJson(infos);
//        System.out.println("MetadataTest2: test: je " + je);
//        Type collectionType = new TypeToken<List<OREXSpectrumInstrumentMetadata<OREXSearchSpec>>>(){}.getType();
//        List<OREXSpectrumInstrumentMetadata<OREXSearchSpec>> info2 = gson.fromJson(je, collectionType);
//        for (InstrumentMetadata<OREXSearchSpec> data : info2)
//        {
//            System.out.println("MetadataTest2: test: data is " + data);
//            List<OREXSearchSpec> searchSpecs = data.getSpecs();
//            for (SearchSpec spec : searchSpecs)
//            {
//                System.out.println("MetadataTest2: test: " + spec);
//            }
//        }
//        try
//        {
//            test2.writeJSON(new File("/Users/steelrj1/Desktop/metadata.json"), je);
//        }
//        catch (IOException e)
//        {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }

}