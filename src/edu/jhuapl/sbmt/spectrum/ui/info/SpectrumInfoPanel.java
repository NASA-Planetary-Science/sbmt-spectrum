package edu.jhuapl.sbmt.spectrum.ui.info;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.jhuapl.saavtk.gui.ModelInfoWindow;
import edu.jhuapl.saavtk.model.Model;
import edu.jhuapl.saavtk.model.ModelManager;
import edu.jhuapl.saavtk.model.ModelNames;
import edu.jhuapl.sbmt.spectrum.model.core.BasicSpectrum;
import edu.jhuapl.sbmt.spectrum.rendering.SpectraCollection;
import edu.jhuapl.sbmt.spectrum.ui.SpectrumPopupMenu;

public class SpectrumInfoPanel extends ModelInfoWindow implements PropertyChangeListener
{
    private ModelManager modelManager;
    private BasicSpectrum spectrum;

    public SpectrumInfoPanel(BasicSpectrum spectrum, ModelManager modelManager)
    {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.modelManager = modelManager;
        this.spectrum = spectrum;

        JPanel panel = new JPanel(new BorderLayout());


        // add the jfreechart graph
        XYSeries series = new XYSeries(spectrum.getDataName());
        Double[] wavelengths = spectrum.getxData();
        double[] spect = spectrum.getSpectrum();
        for (int i=0; i<wavelengths.length; ++i)
            series.add((double)wavelengths[i], spect[i]);
        XYDataset xyDataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYLineChart
                (spectrum.getDataName(), spectrum.getxAxisUnits(), spectrum.getyAxisUnits(),
                        xyDataset, PlotOrientation.VERTICAL, true, true, false);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setDomainPannable(true);
        plot.setRangePannable(true);

        XYItemRenderer r = plot.getRenderer();
        if (r instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
            renderer.setBaseShapesVisible(true);
            renderer.setBaseShapesFilled(true);
            renderer.setDrawSeriesLineAsPath(true);
        }

        panel.add(chartPanel, BorderLayout.CENTER);


        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel,
                BoxLayout.PAGE_AXIS));

        // Add a text box for showing information about the image
        String[] columnNames = {"Property",
                "Value"};

        HashMap<String, String> properties = null;
        Object[][] data = {    {"", ""} };

/*        try
        {

            properties = this.spectrum.getProperties();
            int size = properties.size();
            data = new Object[size][2];

            int i=0;
            for (String key : properties.keySet())
            {
                data[i][0] = key;
                data[i][1] = properties.get(key);

                ++i;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }*/



        JTable table = new JTable(data, columnNames)
        {
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };

        table.setBorder(BorderFactory.createTitledBorder(""));
        table.setPreferredScrollableViewportSize(new Dimension(500, 130));

        JScrollPane scrollPane = new JScrollPane(table);

        bottomPanel.add(Box.createVerticalStrut(10));
        bottomPanel.add(scrollPane);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        add(panel, BorderLayout.CENTER);

        createMenus();

        // Finally make the frame visible
        setTitle(spectrum.getDataName());

        pack();
        setVisible(true);
    }


    public Model getModel()
    {
    	SpectraCollection collection = (SpectraCollection)getCollectionModel();
    	return collection.getSpectrumForName(spectrum.getFullPath());
        //return spectrum;
    }

    public Model getCollectionModel()
    {
        return modelManager.getModel(ModelNames.SPECTRA);
    }


    /**
     * The following function is a bit of a hack. We want to reuse the MSIPopupMenu
     * class, but instead of having a right-click popup menu, we want instead to use
     * it as an actual menu in a menu bar. Therefore we simply grab the menu items
     * from that class and put these in our new JMenu.
     */
    private void createMenus()
    {
        SpectrumPopupMenu msiImagesPopupMenu =
            new SpectrumPopupMenu((SpectraCollection)getCollectionModel(), modelManager, null, null );

        msiImagesPopupMenu.setCurrentSpectrum(spectrum.getSpectrumPathOnServer());

        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("Options");
        menu.setMnemonic('O');

        Component[] components = msiImagesPopupMenu.getComponents();
        for (Component item : components)
        {
            if (item instanceof JMenuItem)
                menu.add(item);
        }

        menuBar.add(menu);

        setJMenuBar(menuBar);
    }

    public void propertyChange(PropertyChangeEvent arg0)
    {
    }
}
