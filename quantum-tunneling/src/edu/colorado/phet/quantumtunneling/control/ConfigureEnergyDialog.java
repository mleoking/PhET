/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.ui.TextAnchor;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.model.*;
import edu.colorado.phet.quantumtunneling.module.QTModule;
import edu.colorado.phet.quantumtunneling.view.EnergyPlot;


/**
 * ConfigureEnergyDialog is the "Configure Energy" dialog.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ConfigureEnergyDialog extends JDialog {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static final Dimension CHART_SIZE = new Dimension( 450, 150 );
    
    private static final double POSITION_STEP = 0.1;
    private static final double ENERGY_STEP = 0.1;
    
    private static final double MIN_REGION_WIDTH = 0.5;
    
    private static final Font AXES_FONT = new Font( QTConstants.FONT_NAME, Font.PLAIN, 12 );
    private static final Font ANNOTATION_FONT = new Font( QTConstants.FONT_NAME, Font.PLAIN, 12 );
    private static final Color BARRIER_PROPERTIES_COLOR = Color.RED;
    private static final Dimension SPINNER_SIZE = new Dimension( 65, 25 );
    
    /* How close the annotations are to the top and bottom of the chart */
    private static final double ANNOTATION_MARGIN = 0.25;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private TotalEnergy _totalEnergy;
    private AbstractPotentialEnergy _potentialEnergy;
    
    // Chart area
    private EnergyPlot _energyPlot;
    
    // Input area
    private JPanel _inputPanel;
    private JComboBox _potentialComboBox;
    private Object _constantItem, _stepItem, _barrierItem, _doubleBarrierItem; // potential choices
    private JSpinner _teSpinner;
    private ArrayList _peSpinners; // array of JSpinner
    private JSpinner _stepSpinner;
    private ArrayList _widthSpinners; // array of JSpinner
    private ArrayList _positionSpinners; // array of JSpinner
    
    // Action area
    private JButton _applyButton, _closeButton;
    
    // Misc
    private Frame _parent;
    private QTModule _module;
    private EventListener _listener;
    private boolean _peChanged, _teChanged;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param parent
     * @param totalEnergy
     * @param potentialEnergy
     */
    public ConfigureEnergyDialog( Frame parent, QTModule module, TotalEnergy totalEnergy, AbstractPotentialEnergy potentialEnergy ) {
        super( parent );

        setTitle( SimStrings.get( "title.configureEnergy" ) );
        setModal( true );
        setResizable( false );

        _parent = parent;
        _module = module;
        _listener = new EventListener();
        
        // Make copies of the model
        _totalEnergy = new TotalEnergy( totalEnergy );
        if ( potentialEnergy instanceof ConstantPotential ) {
            _potentialEnergy = new ConstantPotential( (ConstantPotential) potentialEnergy );
        }
        else if ( potentialEnergy instanceof StepPotential ) {
            _potentialEnergy = new StepPotential( (StepPotential) potentialEnergy );
        }
        else if ( potentialEnergy instanceof BarrierPotential ) {
            _potentialEnergy = new BarrierPotential( (BarrierPotential) potentialEnergy );
        }
        else {
            throw new IllegalStateException( "unsupported potential type: " + potentialEnergy.getClass().getName() );
        }

        createUI( parent );
        populateValues();
        
        setLocationRelativeTo( parent );
        
        _teChanged = _peChanged = false; // do this after creating the UI!
    }

    /**
     * Clients should call this before releasing references to this object.
     */
    public void cleanup() {
        // Nothing to do
    }

    //----------------------------------------------------------------------------
    // Private initializers
    //----------------------------------------------------------------------------

    /*
     * Creates the user interface for the dialog.
     * 
     * @param parent the parent Frame
     */
    private void createUI( Frame parent ) {
        
        JPanel chartPanel = createChartPanel();
        _inputPanel = new JPanel();
        _inputPanel.add( createInputPanel() );
        JPanel actionsPanel = createActionsPanel();

        JPanel p1 = new JPanel( new BorderLayout() );
        p1.add( chartPanel, BorderLayout.NORTH );
        p1.add( new JSeparator(), BorderLayout.CENTER );
        
        JPanel p2 = new JPanel( new BorderLayout() );
        p2.add( p1, BorderLayout.NORTH );
        p2.add( _inputPanel, BorderLayout.SOUTH );
        
        JPanel p3 = new JPanel( new BorderLayout() );
        p3.add( p2, BorderLayout.NORTH );
        p3.add( new JSeparator(), BorderLayout.CENTER );
        
        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.add( p3, BorderLayout.NORTH );
        mainPanel.add( actionsPanel, BorderLayout.SOUTH );

        mainPanel.setBorder( new EmptyBorder( 10, 10, 0, 10 ) );
        chartPanel.setBorder( new EmptyBorder( 0, 0, 5, 0 ) );
        _inputPanel.setBorder( new EmptyBorder( 0, 0, 5, 0 ) );

        getContentPane().add( mainPanel );
        pack();
    }

    /*
     * Creates the dialog's chart panel.
     * 
     * @return the chart panel
     */
    private JPanel createChartPanel() {

        // Plot
        _energyPlot = new EnergyPlot();
        _energyPlot.setAxesFont( AXES_FONT );
        TickUnits units = (TickUnits) NumberAxis.createIntegerTickUnits();
        _energyPlot.getDomainAxis().setStandardTickUnits( units );
        
        // Chart
        JFreeChart chart = new JFreeChart( null /*title*/, null /*font*/, _energyPlot, false /* createLegend */);
        
        // Chart panel
        ChartPanel chartPanel = new ChartPanel( chart );
        chartPanel.setPopupMenu( null ); // disable popup menu, on by default
        chartPanel.setMouseZoomable( false ); // disable zooming, on by default
        chartPanel.setMinimumDrawWidth( (int) CHART_SIZE.getWidth() - 1 );
        chartPanel.setMinimumDrawHeight( (int) CHART_SIZE.getHeight() - 1 );
        chartPanel.setPreferredSize( CHART_SIZE );

        return chartPanel;
    }

    /*
     * Creates the dialog's input panel.
     * 
     * @return the input panel
     */
    private JPanel createInputPanel() {
        
        // Menu panel...
        JPanel menuPanel = new JPanel();
        {
            // Potential choices...
            JLabel potentialLabel = new JLabel( SimStrings.get( "label.potential" ) );
            _constantItem = SimStrings.get( "choice.potential.constant" );
            _stepItem = SimStrings.get( "choice.potential.step" );
            _barrierItem = SimStrings.get( "choice.potential.barrier" );
            _doubleBarrierItem = SimStrings.get( "choice.potential.double" );
            
            // Potential menu...
            Object[] items = { _constantItem, _stepItem, _barrierItem, _doubleBarrierItem };
            _potentialComboBox = new JComboBox( items );
            _potentialComboBox.addItemListener( _listener );

            // Layout
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.addAnchoredComponent( potentialLabel, 0, 1, GridBagConstraints.EAST );
            layout.addAnchoredComponent( _potentialComboBox, 0, 2, GridBagConstraints.WEST );
            menuPanel.setLayout( new BorderLayout() );
            menuPanel.add( innerPanel, BorderLayout.WEST );
        }

        // Spinner panel...
        JPanel spinnerPanel = new JPanel();
        {
            EasyGridBagLayout inputPanelLayout = new EasyGridBagLayout( spinnerPanel );
            inputPanelLayout.setMinimumWidth( 0, 25 );
            inputPanelLayout.setMinimumWidth( 4, 60 );
            inputPanelLayout.setMinimumWidth( 5, 25 );

            spinnerPanel.setLayout( inputPanelLayout );
            int row = 0;

            // Total Energy
            {
                JLabel teLabel = new JLabel( SimStrings.get( "label.totalEnergy" ) );
                teLabel.setForeground( QTConstants.TOTAL_ENERGY_COLOR );
                SpinnerModel model = new SpinnerNumberModel( 
                        QTConstants.ENERGY_RANGE.getLowerBound(), 
                        QTConstants.ENERGY_RANGE.getLowerBound(), QTConstants.ENERGY_RANGE.getUpperBound(), 
                        ENERGY_STEP );
                _teSpinner = new JSpinner( model );
                _teSpinner.addChangeListener( _listener );
                _teSpinner.setPreferredSize( SPINNER_SIZE );
                _teSpinner.setMinimumSize( SPINNER_SIZE );
                JLabel teUnits = new JLabel( SimStrings.get( "units.energy" ) );
                inputPanelLayout.addAnchoredComponent( teLabel, row, 0, 2, 1, GridBagConstraints.EAST );
                inputPanelLayout.addComponent( _teSpinner, row, 2 );
                inputPanelLayout.addComponent( teUnits, row, 3 );
                row++;
            }

            // Potential Energy for each region...
            {
                JLabel peTitle = new JLabel( SimStrings.get( "label.potentialEnergy" ) );
                peTitle.setForeground( QTConstants.POTENTIAL_ENERGY_COLOR );
                inputPanelLayout.addAnchoredComponent( peTitle, row, 0, 4, 1, GridBagConstraints.WEST );
                row++;
                int numberOfRegions = _potentialEnergy.getNumberOfRegions();
                _peSpinners = new ArrayList();
                for ( int i = 0; i < numberOfRegions; i++ ) {
                    JLabel peLabel = new JLabel( "R" + ( i + 1 ) + ":" );
                    peLabel.setForeground( QTConstants.POTENTIAL_ENERGY_COLOR );
                    SpinnerModel model = new SpinnerNumberModel( QTConstants.ENERGY_RANGE.getLowerBound(), 
                            QTConstants.ENERGY_RANGE.getLowerBound(), QTConstants.ENERGY_RANGE.getUpperBound(), 
                            ENERGY_STEP );
                    JSpinner peSpinner = new JSpinner( model );
                    peSpinner.addChangeListener( _listener );
                    peSpinner.setPreferredSize( SPINNER_SIZE );
                    peSpinner.setMinimumSize( SPINNER_SIZE );
                    _peSpinners.add( peSpinner );
                    JLabel peUnits = new JLabel( SimStrings.get( "units.energy" ) );
                    inputPanelLayout.addAnchoredComponent( peLabel, row, 1, GridBagConstraints.EAST );
                    inputPanelLayout.addAnchoredComponent( peSpinner, row, 2, GridBagConstraints.EAST );
                    inputPanelLayout.addAnchoredComponent( peUnits, row, 3, GridBagConstraints.WEST );
                    row++;
                }
            }

            // Step...
            _stepSpinner = null;
            if ( _potentialEnergy instanceof StepPotential ) {
                JLabel stepLabel = new JLabel( SimStrings.get( "label.stepPosition" ) );
                stepLabel.setForeground( BARRIER_PROPERTIES_COLOR );
                SpinnerModel model = new SpinnerNumberModel( 
                        QTConstants.POSITION_RANGE.getLowerBound() + MIN_REGION_WIDTH,
                        QTConstants.POSITION_RANGE.getLowerBound() + MIN_REGION_WIDTH, 
                        QTConstants.POSITION_RANGE.getUpperBound() - MIN_REGION_WIDTH,
                        POSITION_STEP );
                _stepSpinner = new JSpinner( model );
                _stepSpinner.addChangeListener( _listener );
                _stepSpinner.setPreferredSize( SPINNER_SIZE );
                _stepSpinner.setMinimumSize( SPINNER_SIZE );
                JLabel stepUnits = new JLabel( SimStrings.get( "units.position" ) );
                inputPanelLayout.addAnchoredComponent( stepLabel, row, 0, 2, 1, GridBagConstraints.EAST );
                inputPanelLayout.addComponent( _stepSpinner, row, 2 );
                inputPanelLayout.addComponent( stepUnits, row, 3 );
                row++;
            }
            
            // Barriers...
            _widthSpinners = null;
            _positionSpinners = null;
            if ( _potentialEnergy instanceof BarrierPotential ) {

                row = 1;
                int column = 5;

                int numberOfBarriers = ( (BarrierPotential) _potentialEnergy ).getNumberOfBarriers();

                // Barrier Widths...
                _widthSpinners = new ArrayList();
                JLabel widthTitle = new JLabel( SimStrings.get( "label.barrierWidth" ) );
                widthTitle.setForeground( BARRIER_PROPERTIES_COLOR );
                inputPanelLayout.addAnchoredComponent( widthTitle, row, column, 4, 1, GridBagConstraints.WEST );
                row++;
                column++;
                for ( int i = 0; i < numberOfBarriers; i++ ) {
                    JLabel widthLabel = new JLabel( "B" + ( i + 1 ) + ":" );
                    widthLabel.setForeground( BARRIER_PROPERTIES_COLOR );
                    SpinnerModel widthModel = new SpinnerNumberModel( 
                            0, 0, 
                            QTConstants.POSITION_RANGE.getUpperBound() - QTConstants.POSITION_RANGE.getLowerBound(), 
                            POSITION_STEP );
                    JSpinner widthSpinner = new JSpinner( widthModel );
                    widthSpinner.addChangeListener( _listener );
                    widthSpinner.setPreferredSize( SPINNER_SIZE );
                    widthSpinner.setMinimumSize( SPINNER_SIZE );
                    _widthSpinners.add( widthSpinner );
                    JLabel widthUnits = new JLabel( SimStrings.get( "units.position" ) );
                    inputPanelLayout.addAnchoredComponent( widthLabel, row, column, GridBagConstraints.EAST );
                    inputPanelLayout.addAnchoredComponent( widthSpinner, row, column + 1, GridBagConstraints.EAST );
                    inputPanelLayout.addAnchoredComponent( widthUnits, row, column + 2, GridBagConstraints.WEST );
                    row++;
                }
                column--;

                // Barrier Positions...
                _positionSpinners = new ArrayList();
                JLabel positionTitle = new JLabel( SimStrings.get( "label.barrierPosition" ) );
                positionTitle.setForeground( BARRIER_PROPERTIES_COLOR );
                inputPanelLayout.addAnchoredComponent( positionTitle, row, column, 4, 1, GridBagConstraints.WEST );
                row++;
                column++;
                for ( int i = 0; i < numberOfBarriers; i++ ) {
                    JLabel positionLabel = new JLabel( "B" + ( i + 1 ) + ":" );
                    positionLabel.setForeground( BARRIER_PROPERTIES_COLOR );
                    SpinnerModel positionModel = new SpinnerNumberModel(
                            QTConstants.POSITION_RANGE.getLowerBound(), 
                            QTConstants.POSITION_RANGE.getLowerBound(), QTConstants.POSITION_RANGE.getUpperBound(), 
                            POSITION_STEP );
                    JSpinner positionSpinner = new JSpinner( positionModel );
                    positionSpinner.addChangeListener( _listener );
                    positionSpinner.setPreferredSize( SPINNER_SIZE );
                    positionSpinner.setMinimumSize( SPINNER_SIZE );
                    _positionSpinners.add( positionSpinner );
                    JLabel positionUnits = new JLabel( SimStrings.get( "units.position" ) );
                    inputPanelLayout.addAnchoredComponent( positionLabel, row, column, GridBagConstraints.EAST );
                    inputPanelLayout.addAnchoredComponent( positionSpinner, row, column + 1, GridBagConstraints.EAST );
                    inputPanelLayout.addAnchoredComponent( positionUnits, row, column + 2, GridBagConstraints.WEST );
                    row++;
                }
            }
        }

        JPanel inputPanel = new JPanel( new BorderLayout() );
        inputPanel.add( menuPanel, BorderLayout.NORTH );
        inputPanel.add( spinnerPanel, BorderLayout.CENTER );
        
        return inputPanel;
    }

    /*
     * Creates the dialog's actions panel, consisting of Apply and Close buttons.
     * 
     * @return the actions panel
     */
    private JPanel createActionsPanel() {

        _applyButton = new JButton( SimStrings.get( "button.apply" ) );
        _applyButton.addActionListener( _listener );

        _closeButton = new JButton( SimStrings.get( "button.close" ) );
        _closeButton.addActionListener( _listener );

        JPanel buttonPanel = new JPanel( new GridLayout( 1, 2, 10, 0 ) );
        buttonPanel.add( _applyButton );
        buttonPanel.add( _closeButton );

        JPanel actionPanel = new JPanel( new FlowLayout() );
        actionPanel.add( buttonPanel );

        return actionPanel;
    }

    /*
     * Populates the user interface with values from the model.
     */
    private void populateValues() {
        
        double minX = _energyPlot.getDomainAxis().getRange().getLowerBound();
        double maxX = _energyPlot.getDomainAxis().getRange().getUpperBound();
        
        // Energy plot
        _energyPlot.setTotalEnergy( _totalEnergy );
        _energyPlot.setPotentialEnergy( _potentialEnergy );
        
        // Potential type
        _potentialComboBox.removeItemListener( _listener );
        if ( _potentialEnergy instanceof ConstantPotential ) {
            _potentialComboBox.setSelectedItem( _constantItem );
        }
        else if ( _potentialEnergy instanceof StepPotential ) {
            _potentialComboBox.setSelectedItem( _stepItem );
        }
        else if ( _potentialEnergy instanceof BarrierPotential ) {
            int numberOfBarriers = ( (BarrierPotential) _potentialEnergy).getNumberOfBarriers();
            if ( numberOfBarriers == 1 ) {
                _potentialComboBox.setSelectedItem( _barrierItem );
            }
            else if ( numberOfBarriers == 2 ) {
                _potentialComboBox.setSelectedItem( _doubleBarrierItem );
            }
            else {
                throw new IllegalStateException( "unsupported number of barriers: " + numberOfBarriers );
            }
        }
        else {
            throw new IllegalStateException( "unsupported potential type: " + _potentialEnergy.getClass().getName() );
        }
        _potentialComboBox.addItemListener( _listener );
        
        // Total Energy
        double te = _totalEnergy.getEnergy();
        _teSpinner.setValue( new Double( te ) );
        
        // Potential Energy per region
        for ( int i = 0; i < _peSpinners.size(); i++ ) {
            double pe = _potentialEnergy.getRegion(i).getEnergy();
            JSpinner peSpinner = (JSpinner) _peSpinners.get( i );
            peSpinner.setValue( new Double( pe ) );
        }
        
        // Step 
        if ( _stepSpinner != null ) {
            double position = _potentialEnergy.getRegion( 1 ).getStart();
            _stepSpinner.setValue( new Double( position ) );
        }
        
        // Barrier Width
        if ( _widthSpinners != null ) {
            for ( int i = 0; i < _widthSpinners.size(); i++ ) {
                JSpinner widthSpinner = (JSpinner) _widthSpinners.get( i );
                int regionIndex = BarrierPotential.toRegionIndex( i );
                double width = ( (BarrierPotential) _potentialEnergy ).getRegion( regionIndex ).getWidth();
                widthSpinner.setValue( new Double( width ) );
            }
        }
        
        // Barrier Positions
        if ( _positionSpinners != null ) {
            for ( int i = 0; i < _positionSpinners.size(); i++ ) {
                JSpinner positionSpinner = (JSpinner) _positionSpinners.get( i );
                int regionIndex = BarrierPotential.toRegionIndex( i );
                double position = ( (BarrierPotential) _potentialEnergy ).getRegion( regionIndex ).getStart();
                positionSpinner.setValue( new Double( position ) );
            }
        }
    }
    
    /*
     * Rebuilds the user interface when the type of potential changes.
     */
    private void rebuildUI() {
        boolean visible = isVisible();
        if ( visible ) {
            setVisible( false );
        }
        _inputPanel.removeAll();
        _inputPanel.add( createInputPanel() );
        populateValues();
        pack();
        if ( visible ) {
            setVisible( true );
        }
    }
    
    //----------------------------------------------------------------------------
    // Markers & Annotations
    //----------------------------------------------------------------------------
    
    /*
     * Updates the region/barrier markers and annotations to match the the model.
     */
    private void updateMarkersAndAnnotations() {

        boolean hasBarriers = ( _potentialEnergy instanceof BarrierPotential );
        
        double minY = _energyPlot.getRangeAxis().getRange().getLowerBound();
        double maxY = _energyPlot.getRangeAxis().getRange().getUpperBound();
        
        XYItemRenderer renderer = _energyPlot.getRenderer();
        renderer.removeAnnotations();
        _energyPlot.clearDomainMarkers();
        
        PotentialRegion[] regions = _potentialEnergy.getRegions();
        for ( int i = 0; i < regions.length; i++ ) {
            
            // Marker
            if ( i != 0 ) {
                double x = regions[i].getStart();
                Marker marker = new ValueMarker( x );
                marker.setPaint( QTConstants.REGION_MARKER_COLOR );
                marker.setStroke( QTConstants.REGION_MARKER_STROKE );
                _energyPlot.addDomainMarker( marker );
            }
            
            // Annotation
            {
                // Region annotation
                String text = "R" + ( i + 1 );
                double x = regions[i].getStart() + ( ( regions[i].getEnd() - regions[i].getStart() ) / 2 );
                double y = maxY - ANNOTATION_MARGIN;
                XYTextAnnotation annotation = new XYTextAnnotation( text, x, y );
                annotation.setFont( ANNOTATION_FONT );
                annotation.setPaint( QTConstants.POTENTIAL_ENERGY_COLOR );
                annotation.setTextAnchor( TextAnchor.TOP_CENTER );
                renderer.addAnnotation( annotation );
                
                // Barrier annotation
                if ( hasBarriers && BarrierPotential.isaBarrier( i ) ) {
                    int barrierIndex = BarrierPotential.toBarrierIndex( i );
                    text = "B" + ( barrierIndex + 1 );
                    y = minY + ANNOTATION_MARGIN;
                    annotation = new XYTextAnnotation( text, x, y );
                    annotation.setFont( ANNOTATION_FONT );
                    annotation.setPaint( BARRIER_PROPERTIES_COLOR );
                    annotation.setTextAnchor( TextAnchor.BOTTOM_CENTER );
                    renderer.addAnnotation( annotation );
                }
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------

    /*
     * Dispatches events to the appropriate handler method.
     */
    private class EventListener implements ActionListener, ChangeListener, ItemListener {

        public void actionPerformed( ActionEvent event ) {
            if ( event.getSource() == _applyButton ) {
                handleApply();
            }
            else if ( event.getSource() == _closeButton ) {
                handleClose();
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }

        public void stateChanged( ChangeEvent event ) {
            if ( event.getSource() == _teSpinner ) {
                handleTotalEnergyChange();
            }
            else if ( _peSpinners.contains( event.getSource() ) ) { //XXX inefficient
                handlePotentialEnergyChange( _peSpinners.indexOf( event.getSource() ) );
            }
            else if ( event.getSource() == _stepSpinner ) {
                handleStepPositionChange();
            }
            else if ( _widthSpinners.contains( event.getSource() ) ) { //XXX inefficient
                handleBarrierWidthChange( _widthSpinners.indexOf( event.getSource() ) );
            }
            else if ( _positionSpinners.contains( event.getSource() ) ) { //XXX inefficient
                handleBarrierPositionChange( _positionSpinners.indexOf( event.getSource() ) );
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }

        public void itemStateChanged( ItemEvent event ) {
            if ( event.getStateChange() == ItemEvent.SELECTED ) {
                if ( event.getSource() == _potentialComboBox ) {
                    handlePotentialTypeChange();
                }
                else {
                    throw new IllegalArgumentException( "unexpected event: " + event );
                }
            }
        }
    }

    /*
     * Handles the "Apply" button.
     */
    private void handleApply() {
        if ( _teChanged ) {
            _module.setTotalEnergy( _totalEnergy );
            _teChanged = false;
        }
        if ( _peChanged ) {
            _module.setPotentialEnergy( _potentialEnergy ); 
            _peChanged = false;
        }
    }

    /*
     * Handles the "Close" button, checks for unsaved changes.
     */
    private void handleClose() {
        if ( _teChanged || _peChanged ) {
            String message = SimStrings.get( "message.unsavedChanges" );
            String title = SimStrings.get( "title.confirm" );
            int reply = JOptionPane.showConfirmDialog( this, message, "Confirm", JOptionPane.YES_NO_CANCEL_OPTION );
            if ( reply == JOptionPane.YES_OPTION) {
                handleApply();
                dispose();
            }
            if ( reply == JOptionPane.NO_OPTION) {
                dispose();
            }
            else {
                // Do nothing if canceled.
            }
        }
        else {
            dispose();
        }
    }
    
    /*
     * Handles selection in the "Potential" combo box.
     */
    private void handlePotentialTypeChange() {
        System.out.println( "ConfigureEnergyDialog.handlePotentialTypeChange" );//XXX
        AbstractPotentialEnergy potentialEnergy = null;
        
        Object o = _potentialComboBox.getSelectedItem();
        if ( o == _constantItem ) {
            potentialEnergy = new ConstantPotential();
        }
        else if ( o == _stepItem ) {
            potentialEnergy = new StepPotential();
        }
        else if ( o == _barrierItem ) {
            potentialEnergy = new BarrierPotential();
        }
        else if ( o == _doubleBarrierItem ) {
            potentialEnergy = new BarrierPotential( 2 );
        }
        else {
            throw new IllegalStateException( "unsupported potential selection: " + o );
        }
        
        if ( potentialEnergy != _potentialEnergy ) {
            _potentialEnergy = potentialEnergy;
            _peChanged = true;
            rebuildUI();
        }
    }
    
    /*
     * Handles a change in total energy.
     */
    private void handleTotalEnergyChange() {
        Double value = (Double) _teSpinner.getValue();
        _totalEnergy.setEnergy( value.doubleValue() );
        _teChanged = true;
    }
    
    /*
     * Handles a change in the potential energy of a region.
     */
    private void handlePotentialEnergyChange( int regionIndex ) {
        JSpinner peSpinner = (JSpinner) _peSpinners.get( regionIndex );
        Double value = (Double) peSpinner.getValue();
        _potentialEnergy.setEnergy( regionIndex, value.doubleValue() );
        updateMarkersAndAnnotations();
        _peChanged = true;
    }
    
    /*
     * Handles a change in a step.
     */
    private void handleStepPositionChange() {
        if ( _potentialEnergy instanceof StepPotential ) {
        Double value = (Double) _stepSpinner.getValue();
            ( (StepPotential) _potentialEnergy ).setStepPosition( value.doubleValue() );
            updateMarkersAndAnnotations();
            _peChanged = true;
        }
    }
    
    /*
     * Handles a change in the width of a barrier.
     */
    private void handleBarrierWidthChange( int barrierIndex ) {
        if ( _potentialEnergy instanceof BarrierPotential ) {
            BarrierPotential bp = (BarrierPotential) _potentialEnergy;
            JSpinner widthSpinner = (JSpinner) _widthSpinners.get( barrierIndex );
            Double value = (Double) widthSpinner.getValue();
//            System.out.println( "barrier " + barrierIndex + " width = " + value );//XXX
            boolean success = bp.setBarrierWidth( barrierIndex, value.doubleValue() );
            if ( success ) {
                updateMarkersAndAnnotations();
                _peChanged = true;
            }
            else {
                System.out.println( "WARNING: BarrierPotential.setBarrierWidth returned false: " + value );
                double width = bp.getBarrierWidth( barrierIndex );
                widthSpinner.setValue( new Double( width ) );
            }
        }
    }
    
    /*
     * Handles a change in the position of a barrier.
     */
    private void handleBarrierPositionChange( int barrierIndex ) {
        if ( _potentialEnergy instanceof BarrierPotential ) {
            BarrierPotential bp = (BarrierPotential) _potentialEnergy;
            JSpinner positionSpinner = (JSpinner) _positionSpinners.get( barrierIndex );
            Double value = (Double) positionSpinner.getValue();
//            System.out.println( "barrier " + barrierIndex + " position = " + value );//XXX
            boolean success = bp.setBarrierPosition( barrierIndex, value.doubleValue() );
            if ( success ) {
                updateMarkersAndAnnotations();
                _peChanged = true;
            }
            else {
                System.out.println( "WARNING: BarrierPotential.setBarrierPosition returned false: " + value );
                double position = bp.getBarrierPosition( barrierIndex );
                positionSpinner.setValue( new Double( position ) );
            }
        }
    }
}
