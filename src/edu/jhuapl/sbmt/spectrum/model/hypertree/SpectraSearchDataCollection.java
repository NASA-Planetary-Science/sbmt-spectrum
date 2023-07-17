package edu.jhuapl.sbmt.spectrum.model.hypertree;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JComponent;

import vtk.vtkProp;

import edu.jhuapl.saavtk.model.PolyhedralModel;
import edu.jhuapl.saavtk.util.BoundingBox;
import edu.jhuapl.sbmt.core.body.BodyViewConfig;
import edu.jhuapl.sbmt.lidar.hyperoctree.FSHyperTreeSkeleton;
import edu.jhuapl.sbmt.model.boundedobject.BoundedObjectSearchDataCollection;
import edu.jhuapl.sbmt.model.boundedobject.hyperoctree.BoundedObjectHyperTreeSkeleton;

public class SpectraSearchDataCollection
        extends BoundedObjectSearchDataCollection
{

    private Map<String, FSHyperTreeSkeleton> skeletons = new HashMap<String, FSHyperTreeSkeleton>();
    private FSHyperTreeSkeleton currentSkeleton;
    private JComponent parentForProgressMonitor;
    private boolean loading=false;
    private BodyViewConfig polyhedralModelConfig;
    private PolyhedralModel smallBodyModel;
    private List<vtkProp> actors = new ArrayList<vtkProp>();


    public boolean isLoading()
    {
        return loading;
    }

    public SpectraSearchDataCollection(PolyhedralModel smallBodyModel)
    {
        super(smallBodyModel);
        this.polyhedralModelConfig = (BodyViewConfig)smallBodyModel.getConfig();
    }

    public void clearDatasourceSkeletons()
    {
        skeletons.clear();
    }

    /**
     * Creates a skeleton for the specified datasource name, assumes the data source path for the name
     * is already added to the lidarDatasourceMap
     * @param datasourceName
     */
    public void addDatasourceSkeleton(String datasourceName, String datasourcePath)
    {
        if (datasourceName != null && datasourceName.length() > 0)
        {
            Path basePath = Paths.get(datasourcePath);
            FSHyperTreeSkeleton skeleton = skeletons.get(datasourceName);
            if (skeleton == null)
            {
                skeleton = new BoundedObjectHyperTreeSkeleton(basePath);
                skeletons.put(datasourceName, skeleton);
            }
        }
    }

    public void setCurrentDatasourceSkeleton(String datasourceName)
    {
        if (datasourceName != null && datasourceName.length() > 0)
        {
            FSHyperTreeSkeleton skeleton = skeletons.get(datasourceName);
            if (skeleton != null)
                currentSkeleton = skeleton;
        }
    }

    private Set<FSHyperTreeSkeleton> readIn = new HashSet<FSHyperTreeSkeleton>();

    public void readSkeleton()
    {
        if (!readIn.contains(currentSkeleton))
        {
            currentSkeleton.read();
            readIn.add(currentSkeleton);
        }
    }

    public FSHyperTreeSkeleton getCurrentSkeleton() {
        return currentSkeleton;
    }

    public TreeSet<Integer> getLeavesIntersectingBoundingBox(BoundingBox bbox, double[] tlims)
    {
        double[] bounds=new double[]{bbox.xmin,bbox.xmax,bbox.ymin,bbox.ymax,bbox.zmin,bbox.zmax,tlims[0],tlims[1]};
        return currentSkeleton.getLeavesIntersectingBoundingBox(bounds);
    }

    public TreeSet<Integer> getLeavesIntersectingBoundingBox(BoundingBox bbox, double[] tlims, double[] spectraLims)
    {
        // assumes this is a spectra search with phase, emission, incidence angles, and spacecraft distance
        double[] bounds=new double[]{bbox.xmin,bbox.xmax,bbox.ymin,bbox.ymax,bbox.zmin,bbox.zmax,tlims[0],tlims[1],
                spectraLims[0], spectraLims[1],spectraLims[2], spectraLims[3],spectraLims[4], spectraLims[5],spectraLims[6], spectraLims[7]};
        return currentSkeleton.getLeavesIntersectingBoundingBox(bounds);
    }


    public Map<String, String> getSpectraDataSourceMap()
    {
        return polyhedralModelConfig.spectraSearchDataSourceMap;
    }

}
