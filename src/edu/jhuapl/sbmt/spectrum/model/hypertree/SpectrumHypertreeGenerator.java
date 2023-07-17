package edu.jhuapl.sbmt.spectrum.model.hypertree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.jhuapl.sbmt.core.io.DataOutputStreamPool;
import edu.jhuapl.sbmt.query.hyperoctree.HyperBox;
import edu.jhuapl.sbmt.query.hyperoctree.HyperException;
import edu.jhuapl.sbmt.query.hyperoctree.boundedobject.BoundedObjectHyperTreeGenerator;
import edu.jhuapl.sbmt.query.hyperoctree.boundedobject.HyperBoundedObject;

public class SpectrumHypertreeGenerator extends BoundedObjectHyperTreeGenerator
{

    public SpectrumHypertreeGenerator(Path outputDirectory,
            int maxObjectsPerLeaf, HyperBox bbox,
            int maxNumberOfOpenOutputFiles, DataOutputStreamPool pool)
    {
        super(outputDirectory, maxObjectsPerLeaf, bbox, maxNumberOfOpenOutputFiles,
                pool);
    }


    public void addAllObjectsFromFile(String inputPath) throws HyperException, IOException
    {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try (BufferedReader br = new BufferedReader(new FileReader(inputPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] toks = line.split(" ");
                String objName = toks[0];
                try {
                    Date minTime = df.parse(toks[7]);
                    double minT = minTime.getTime();
                    double maxT = df.parse(toks[8]).getTime();
                    double em   = Double.parseDouble(toks[9]);
                    double inc  = Double.parseDouble(toks[11]);
                    double phs  = Double.parseDouble(toks[13]);
                    double dist = Double.parseDouble(toks[15]);

                    HyperBox objBBox = new HyperBox(new double[]{Double.parseDouble(toks[1]), Double.parseDouble(toks[3]), Double.parseDouble(toks[5]), minT, em, inc, phs, dist},
                            new double[]{Double.parseDouble(toks[2]), Double.parseDouble(toks[4]), Double.parseDouble(toks[6]), maxT, em, inc, phs, dist});

                    int objId = objName.hashCode();
                    getFileMap().put(objName, objId);
                    HyperBoundedObject obj = new HyperBoundedObject(objName, objId, objBBox);
                    getRoot().add(obj);
                    setTotalObjectsWritten(getTotalObjectsWritten() + 1);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        }
    }



}
