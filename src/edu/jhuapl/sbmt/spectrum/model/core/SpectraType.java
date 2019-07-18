package edu.jhuapl.sbmt.spectrum.model.core;

import edu.jhuapl.sbmt.query.QueryBase;

public class SpectraType implements ISpectraType
{
//    OTES_SPECTRA("OTES", OTESQuery.getInstance(), OTESSpectrumMath.getInstance(), "cm^-1", new OTES().getBandCenters()),
//    OVIRS_SPECTRA("OVIRS", OVIRSQuery.getInstance(), OVIRSSpectrumMath.getInstance(), "um", new OVIRS().getBandCenters()),
//    NIS_SPECTRA("NIS", NisQuery.getInstance(), NISSpectrumMath.getSpectrumMath(), "cm^-1", new NIS().getBandCenters()),
//    NIRS3_SPECTRA("NIRS3", NIRS3Query.getInstance(), NIRS3SpectrumMath.getInstance(), "cm^-1", new NIRS3().getBandCenters());

    private QueryBase queryBase;
    private SpectrumMath spectrumMath;
    private String displayName;
    private Double[] bandCenters;
    private String bandCenterUnit;

    public SpectraType(String displayName, QueryBase queryBase, SpectrumMath spectrumMath, String bandCenterUnit, Double[] bandCenters)
    {
        this.displayName = displayName;
        this.queryBase = queryBase;
        this.spectrumMath = spectrumMath;
        this.bandCenterUnit = bandCenterUnit;
        this.bandCenters = bandCenters;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    @Override
    public QueryBase getQueryBase()
    {
        return queryBase;
    }

    @Override
    public SpectrumMath getSpectrumMath()
    {
        return spectrumMath;
    }

    @Override
    public Double[] getBandCenters()
    {
        return bandCenters;
    }

    @Override
    public String getBandCenterUnit()
    {
        return bandCenterUnit;
    }

//    public static SpectraType findSpectraTypeForDisplayName(String displayName)
//    {
//        SpectraType type = null;
//        for (SpectraType spectra : values())
//        {
//            if (spectra.getDisplayName().equals(displayName))
//            {
//                return spectra;
//            }
//        }
//        return type;
//    }


}
