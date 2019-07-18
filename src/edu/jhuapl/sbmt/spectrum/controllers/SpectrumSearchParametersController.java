package edu.jhuapl.sbmt.spectrum.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.jidesoft.swing.CheckBoxTree;

import edu.jhuapl.saavtk.model.ModelNames;
import edu.jhuapl.saavtk.model.structure.AbstractEllipsePolygonModel;
import edu.jhuapl.saavtk.pick.PickManager;
import edu.jhuapl.saavtk.pick.PickManager.PickMode;
import edu.jhuapl.sbmt.model.bennu.otes.SpectraHierarchicalSearchSpecification;
import edu.jhuapl.sbmt.spectrum.model.core.AbstractSpectrumSearchModel;
import edu.jhuapl.sbmt.spectrum.ui.SpectrumSearchParametersPanel;

public class SpectrumSearchParametersController
{
    protected SpectrumSearchParametersPanel panel;
    protected AbstractSpectrumSearchModel model;
    private JPanel auxPanel;
    protected PickManager pickManager;
//    protected SmallBodyViewConfig smallBodyConfig;
    protected SpectraHierarchicalSearchSpecification spectraSpec;
    private boolean hasHierarchicalSpectraSearch;
    private double imageSearchDefaultMaxSpacecraftDistance;
    private Date imageSearchDefaultStartDate;
    private Date imageSearchDefaultEndDate;

    public SpectrumSearchParametersController(Date imageSearchDefaultStartDate, Date imageSearchDefaultEndDate, boolean hasHierarchicalSpectraSearch, double imageSearchDefaultMaxSpacecraftDistance, AbstractSpectrumSearchModel model, PickManager pickManager)
    {
        this.model = model;
        this.spectraSpec = model.getSpectraSpec();
        this.panel = new SpectrumSearchParametersPanel(hasHierarchicalSpectraSearch);
        this.pickManager = pickManager;
        this.hasHierarchicalSpectraSearch = hasHierarchicalSpectraSearch;
        this.imageSearchDefaultMaxSpacecraftDistance = imageSearchDefaultMaxSpacecraftDistance;
        this.imageSearchDefaultEndDate = imageSearchDefaultEndDate;
        this.imageSearchDefaultStartDate = imageSearchDefaultStartDate;
//        this.smallBodyConfig = model.getSmallBodyConfig();
    }

    public void setupSearchParametersPanel()
    {
        initHierarchicalImageSearch();

        if(hasHierarchicalSpectraSearch)
        {
//            model.getSmallBodyConfig().hierarchicalSpectraSearchSpecification.processTreeSelections(
//                    panel.getCheckBoxTree().getCheckBoxTreeSelectionModel().getSelectionPaths());
            model.getSpectraSpec().processTreeSelections(
                    panel.getCheckBoxTree().getCheckBoxTreeSelectionModel().getSelectionPaths());
            panel.getSelectRegionButton().setVisible(false);
            panel.getClearRegionButton().setVisible(false);
        }
        else
        {
            panel.getSelectRegionButton().setVisible(true);
            panel.getClearRegionButton().setVisible(true);

            panel.getClearRegionButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    clearRegionButtonActionPerformed(evt);
                }
            });

            JSpinner startSpinner = panel.getStartSpinner();
            startSpinner.setModel(new javax.swing.SpinnerDateModel(model.getStartDate(), null, null, java.util.Calendar.DAY_OF_MONTH));
            startSpinner.setEditor(new javax.swing.JSpinner.DateEditor(startSpinner, "yyyy-MMM-dd HH:mm:ss"));
            startSpinner.setMinimumSize(new java.awt.Dimension(36, 22));
            startSpinner.setPreferredSize(new java.awt.Dimension(180, 22));
            startSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent evt) {
                    startSpinnerStateChanged(evt);
                }
            });


            panel.getEndDateLabel().setText("End Date:");
            JSpinner endSpinner = panel.getEndSpinner();
            endSpinner.setModel(new javax.swing.SpinnerDateModel(model.getEndDate(), null, null, java.util.Calendar.DAY_OF_MONTH));
            endSpinner.setEditor(new javax.swing.JSpinner.DateEditor(endSpinner, "yyyy-MMM-dd HH:mm:ss"));
            endSpinner.setMinimumSize(new java.awt.Dimension(36, 22));
            endSpinner.setPreferredSize(new java.awt.Dimension(180, 22));
            endSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent evt) {
                    endSpinnerStateChanged(evt);
                }
            });

            panel.addComponentListener(new java.awt.event.ComponentAdapter() {
                public void componentHidden(java.awt.event.ComponentEvent evt) {
                    formComponentHidden(evt);
                }
            });

            JFormattedTextField toPhaseTextField = panel.getToPhaseTextField();
            toPhaseTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.###"))));
            toPhaseTextField.setText("180");
            toPhaseTextField.setPreferredSize(new java.awt.Dimension(0, 22));

            JFormattedTextField fromPhaseTextField = panel.getFromPhaseTextField();
            fromPhaseTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.###"))));
            fromPhaseTextField.setText("0");
            fromPhaseTextField.setPreferredSize(new java.awt.Dimension(0, 22));

            JFormattedTextField toEmissionTextField = panel.getToEmissionTextField();
            toEmissionTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.###"))));
            toEmissionTextField.setText("180");
            toEmissionTextField.setPreferredSize(new java.awt.Dimension(0, 22));

            JFormattedTextField fromEmissionTextField = panel.getFromEmissionTextField();
            fromEmissionTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.###"))));
            fromEmissionTextField.setText("0");
            fromEmissionTextField.setPreferredSize(new java.awt.Dimension(0, 22));

            JFormattedTextField toIncidenceTextField = panel.getToIncidenceTextField();
            toIncidenceTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.###"))));
            toIncidenceTextField.setText("180");
            toIncidenceTextField.setPreferredSize(new java.awt.Dimension(0, 22));

            JFormattedTextField fromIncidenceTextField = panel.getFromIncidenceTextField();
            fromIncidenceTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.###"))));
            fromIncidenceTextField.setText("0");
            fromIncidenceTextField.setPreferredSize(new java.awt.Dimension(0, 22));

            JFormattedTextField toDistanceTextField = panel.getToDistanceTextField();
            toDistanceTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.###"))));
            toDistanceTextField.setText("1000");
            toDistanceTextField.setPreferredSize(new java.awt.Dimension(0, 22));

            JFormattedTextField fromDistanceTextField = panel.getFromDistanceTextField();
            fromDistanceTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.###"))));
            fromDistanceTextField.setText("0");
            fromDistanceTextField.setPreferredSize(new java.awt.Dimension(0, 22));


            model.setStartDate(imageSearchDefaultStartDate);
            ((SpinnerDateModel)startSpinner.getModel()).setValue(model.getStartDate());
            model.setEndDate(imageSearchDefaultEndDate);
            ((SpinnerDateModel)endSpinner.getModel()).setValue(model.getEndDate());

            panel.getFullCheckBox().addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    model.addToPolygonsSelected(0);
                }
            });

            panel.getPartialCheckBox().addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    model.addToPolygonsSelected(0);
                }
            });

            panel.getDegenerateCheckBox().addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    model.addToPolygonsSelected(0);
                }
            });

            panel.getFromDistanceTextField().getDocument().addDocumentListener(new DocumentListener()
            {

                @Override
                public void removeUpdate(DocumentEvent e)
                {
                    if (!panel.getFromDistanceTextField().getText().equals(""))
                        model.setMinDistanceQuery(Integer.parseInt(panel.getFromDistanceTextField().getText()));
                }

                @Override
                public void insertUpdate(DocumentEvent e)
                {
                    if (!panel.getFromDistanceTextField().getText().equals(""))
                        model.setMinDistanceQuery(Integer.parseInt(panel.getFromDistanceTextField().getText()));
                }

                @Override
                public void changedUpdate(DocumentEvent e)
                {
                    if (!panel.getFromDistanceTextField().getText().equals(""))
                        model.setMinDistanceQuery(Integer.parseInt(panel.getFromDistanceTextField().getText()));

                }
            });

            panel.getToDistanceTextField().getDocument().addDocumentListener(new DocumentListener()
            {

                @Override
                public void removeUpdate(DocumentEvent e)
                {
                    if (!panel.getToDistanceTextField().getText().equals(""))
                        model.setMaxDistanceQuery(Integer.parseInt(panel.getToDistanceTextField().getText()));            }

                @Override
                public void insertUpdate(DocumentEvent e)
                {
                    if (!panel.getToDistanceTextField().getText().equals(""))
                        model.setMaxDistanceQuery(Integer.parseInt(panel.getToDistanceTextField().getText()));            }

                @Override
                public void changedUpdate(DocumentEvent e)
                {
                    if (!panel.getToDistanceTextField().getText().equals(""))
                        model.setMaxDistanceQuery(Integer.parseInt(panel.getToDistanceTextField().getText()));
                }
            });

            panel.getFromIncidenceTextField().addActionListener(new ActionListener()
            {

                @Override
                public void actionPerformed(ActionEvent e)
                {
                    model.setMinIncidenceQuery(Integer.parseInt(panel.getFromIncidenceTextField().getText()));
                }
            });

            panel.getToIncidenceTextField().addActionListener(new ActionListener()
            {

                @Override
                public void actionPerformed(ActionEvent e)
                {
                    model.setMaxIncidenceQuery(Integer.parseInt(panel.getToIncidenceTextField().getText()));
                }
            });

            panel.getFromEmissionTextField().addActionListener(new ActionListener()
            {

                @Override
                public void actionPerformed(ActionEvent e)
                {
                    model.setMinEmissionQuery(Integer.parseInt(panel.getFromEmissionTextField().getText()));
                }
            });

            panel.getToEmissionTextField().addActionListener(new ActionListener()
            {

                @Override
                public void actionPerformed(ActionEvent e)
                {
                    model.setMaxEmissionQuery(Integer.parseInt(panel.getToEmissionTextField().getText()));
                }
            });

            panel.getFromPhaseTextField().addActionListener(new ActionListener()
            {

                @Override
                public void actionPerformed(ActionEvent e)
                {
                    model.setMinPhaseQuery(Integer.parseInt(panel.getFromPhaseTextField().getText()));
                }
            });

            panel.getToPhaseTextField().addActionListener(new ActionListener()
            {

                @Override
                public void actionPerformed(ActionEvent e)
                {
                    model.setMaxPhaseQuery(Integer.parseInt(panel.getToPhaseTextField().getText()));
                }
            });

            toDistanceTextField.setValue(imageSearchDefaultMaxSpacecraftDistance);

            model.setStartDate((Date)panel.getStartSpinner().getValue());
            model.setEndDate((Date)panel.getEndSpinner().getValue());
            model.setMinDistanceQuery(Integer.parseInt(panel.getFromDistanceTextField().getText()));
            model.setMaxDistanceQuery(Integer.parseInt(panel.getToDistanceTextField().getText()));
            model.setMinIncidenceQuery(Integer.parseInt(panel.getFromIncidenceTextField().getText()));
            model.setMaxIncidenceQuery(Integer.parseInt(panel.getToIncidenceTextField().getText()));
            model.setMinEmissionQuery(Integer.parseInt(panel.getFromEmissionTextField().getText()));
            model.setMaxEmissionQuery(Integer.parseInt(panel.getToEmissionTextField().getText()));
            model.setMinPhaseQuery(Integer.parseInt(panel.getFromPhaseTextField().getText()));
            model.setMaxPhaseQuery(Integer.parseInt(panel.getToPhaseTextField().getText()));

        }

        panel.getClearRegionButton().setText("Clear Region");
        panel.getClearRegionButton().addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearRegionButtonActionPerformed(evt);
            }
        });


        panel.getSubmitButton().setText("Search");
        panel.getSubmitButton().addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (hasHierarchicalSpectraSearch)
                    model.setSelectedPath(panel.getCheckBoxTree().getCheckBoxTreeSelectionModel().getSelectionPaths());
                panel.getSelectRegionButton().setSelected(false);
                model.clearSpectraFromDisplay();
                model.performSearch();
            }
        });


        panel.getSelectRegionButton().setText("Select Region");
        panel.getSelectRegionButton().addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectRegionButtonActionPerformed(evt);
            }
        });




    }

    // Sets up everything related to hierarchical image searches
    protected void initHierarchicalImageSearch()
    {
        // Show/hide panels depending on whether this body has hierarchical image search capabilities
        if(hasHierarchicalSpectraSearch)
        {
            // Has hierarchical search capabilities, these replace the camera and filter checkboxes so hide them
//            panel.getFilterCheckBoxPanel().setVisible(false);
//            panel.getUserDefinedCheckBoxPanel().setVisible(false);
//            panel.getAuxPanel().setVisible(false);

            // Create the tree
            spectraSpec.clearTreeLeaves();
            spectraSpec.readHierarchyForInstrument(model.getInstrument().getDisplayName());
//            panel.setCheckBoxTree(new CheckBoxTree(model.getSmallBodyConfig().hierarchicalSpectraSearchSpecification.getTreeModel()));
            panel.setCheckBoxTree(new CheckBoxTree(model.getSpectraSpec().getTreeModel()));

            // Place the tree in the panel
                panel.getHierarchicalSearchScrollPane().setViewportView(panel.getCheckBoxTree());
        }
//        else
//        {
//            // No hierarchical search capabilities, hide the scroll pane
//            panel.getHierarchicalSearchScrollPane().setVisible(false);
//        }
    }

    public void formComponentHidden(java.awt.event.ComponentEvent evt)
    {
        panel.getSelectRegionButton().setSelected(false);
        pickManager.setPickMode(PickMode.DEFAULT);
    }

    public void startSpinnerStateChanged(javax.swing.event.ChangeEvent evt)
    {
        Date date =
                ((SpinnerDateModel)panel.getStartSpinner().getModel()).getDate();
        if (date != null)
            model.setStartDate(date);
    }

    public void endSpinnerStateChanged(javax.swing.event.ChangeEvent evt)
    {
        Date date =
                ((SpinnerDateModel)panel.getEndSpinner().getModel()).getDate();
        if (date != null)
            model.setEndDate(date);

    }

    public void selectRegionButtonActionPerformed(ActionEvent evt)
    {
        if (panel.getSelectRegionButton().isSelected())
            pickManager.setPickMode(PickMode.CIRCLE_SELECTION);
        else
            pickManager.setPickMode(PickMode.DEFAULT);
    }

    public void clearRegionButtonActionPerformed(ActionEvent evt)
    {
        AbstractEllipsePolygonModel selectionModel = (AbstractEllipsePolygonModel)model.getModelManager().getModel(ModelNames.CIRCLE_SELECTION);
        selectionModel.removeAllStructures();
    }

    public SpectrumSearchParametersPanel getPanel()
    {
        return panel;
    }

    public void setPanel(SpectrumSearchParametersPanel panel)
    {
        this.panel = panel;
    }

    public JPanel getAuxPanel()
    {
        return auxPanel;
    }

    public void setAuxPanel(JPanel auxPanel)
    {
        this.auxPanel = auxPanel;
        panel.setAuxPanel(auxPanel);
    }
}
