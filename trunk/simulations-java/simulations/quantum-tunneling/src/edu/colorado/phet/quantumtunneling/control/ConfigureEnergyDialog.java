/* Copyright 2005-2008, University of Colorado */

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
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.QTResources;
import edu.colorado.phet.quantumtunneling.color.QTColorScheme;
import edu.colorado.phet.quantumtunneling.enums.PotentialType;
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
    
    private static final String POSITION_FORMAT = QTConstants.POSITION_FORMAT;
    private static final String ENERGY_FORMAT = QTConstants.ENERGY_FORMAT;
    private static final double POSITION_STEP = QTConstants.POSITION_STEP;
    private static final double ENERGY_STEP = QTConstants.ENERGY_STEP;
    private static final double MIN_ENERGY = QTConstants.ENERGY_RANGE.getLowerBound();
    private static final double MAX_ENERGY = QTConstants.ENERGY_RANGE.getUpperBound();
    
    /*
     * All SpinnerNumberModels are given a range that is well outside the range
     * of valid values. We do this so that we can control the validation ourselves,
     * warn the user about invalid values, and reset the values accordingly.
     */
    private static final double SPINNER_MAX = Double.MAX_VALUE;

    private static final Font AXIS_LABEL_FONT = new PhetFont( Font.PLAIN, 16 );
    private static final Font AXIS_TICK_LABEL_FONT = new PhetFont( Font.PLAIN, 12 );
    private static final Font ANNOTATION_FONT = new PhetFont( Font.PLAIN, 14 );
    private static final Dimension SPINNER_SIZE = new Dimension( 65, 25 );
    
    /* How close the annotations are to the top and bottom of the chart */
    private static final double ANNOTATION_MARGIN = 0.15;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private TotalEnergy _totalEnergy;
    private AbstractPotential _potentialEnergy;
    private WavePacket _wavePacket;
    private PlaneWave _planeWave;
    
    // Dialog's "chart area"
    private EnergyPlot _energyPlot;
    
    // Dialog's "input area"
    private JPanel _inputPanel;
    private PotentialComboBox _potentialComboBox;
    private DoubleSpinner _teSpinner;
    private ArrayList _peSpinners; // array of DoubleSpinner
    private DoubleSpinner _stepSpinner;
    private ArrayList _widthSpinners; // array of DoubleSpinner
    private ArrayList _positionSpinners; // array of DoubleSpinner
    
    // Dialog's "action area"
    private JButton _applyButton, _closeButton;
    
    // Misc
    private QTModule _module;
    private EventListener _listener;
    private boolean _peChanged, _teChanged;
    private QTColorScheme _colorScheme;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param parent
     * @param totalEnergy
     * @param potentialEnergy
     * @param wavePacket
     * @param planeWave
     * @param colorScheme
     */
    public ConfigureEnergyDialog( 
            Frame parent, QTModule module, 
            TotalEnergy totalEnergy, AbstractPotential potentialEnergy, 
            WavePacket wavePacket, PlaneWave planeWave,
            QTColorScheme colorScheme ) {
        super( parent );

        // Make copies of the energy models
        _totalEnergy = new TotalEnergy( totalEnergy );
        _potentialEnergy = PotentialFactory.clonePotentialEnergy( potentialEnergy );
        // Keep references to the wave models
        _wavePacket = wavePacket;
        _planeWave = planeWave;
        
        _colorScheme = colorScheme;
        
        setTitle( QTResources.getString( "title.configureEnergy" ) );
        setModal( true );
        setResizable( false );

        _module = module;
        _listener = new EventListener();

        createUI( parent );
        populateValues();
        
        setLocationRelativeTo( parent );
        
        // do this after creating the UI!
        {
            _teChanged = _peChanged = false;
            _applyButton.setEnabled( false ); // disabled until something is changed
        }
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
        _energyPlot.setColorScheme( _colorScheme );
        {
            // Labels & tick marks
            _energyPlot.getDomainAxis().setLabelFont( AXIS_LABEL_FONT );
            _energyPlot.getRangeAxis().setLabelFont( AXIS_LABEL_FONT );
            _energyPlot.getDomainAxis().setTickLabelFont( AXIS_TICK_LABEL_FONT );
            _energyPlot.getRangeAxis().setTickLabelFont( AXIS_TICK_LABEL_FONT );
            _energyPlot.getDomainAxis().setLabelInsets( new RectangleInsets( 10, 0, 0, 0 ) ); // top,left,bottom,right
            _energyPlot.getRangeAxis().setLabelInsets( new RectangleInsets( 0, 0, 0, 10 ) );

            // X axis tick units
            {
                TickUnits tickUnits = new TickUnits();
                tickUnits.add( new NumberTickUnit( QTConstants.POSITION_TICK_SPACING, QTConstants.POSITION_TICK_FORMAT ) );
                _energyPlot.getDomainAxis().setStandardTickUnits( tickUnits );
                _energyPlot.getDomainAxis().setAutoTickUnitSelection( true );
            }

            // Y axis tick units
            {
                TickUnits tickUnits = new TickUnits();
                tickUnits.add( new NumberTickUnit( QTConstants.ENERGY_TICK_SPACING, QTConstants.ENERGY_TICK_FORMAT ) );
                _energyPlot.getRangeAxis().setStandardTickUnits( tickUnits );
                _energyPlot.getRangeAxis().setAutoTickUnitSelection( true );
            }

            // Set the waves
            _energyPlot.setTotalEnergy( _totalEnergy );
            _energyPlot.setPotentialEnergy( _potentialEnergy );
            _energyPlot.setWavePacket( _wavePacket );
            _energyPlot.setPlaneWave( _planeWave );
            if ( _wavePacket.isEnabled() ) {
                _energyPlot.showWavePacket();
            }
            else {
                _energyPlot.showPlaneWave();
            }
        }
        
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
            // Potential
            JLabel potentialLabel = new JLabel( QTResources.getString( "label.potential" ) );
            _potentialComboBox = new PotentialComboBox();
            _potentialComboBox.setPotentialColor( _colorScheme.getPotentialEnergyColor() );
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
            inputPanelLayout.setMinimumWidth( 1, 60 );
            spinnerPanel.setLayout( inputPanelLayout );
            
            JPanel leftColumn = new JPanel();
            EasyGridBagLayout leftLayout = new EasyGridBagLayout( leftColumn );
            leftLayout.setMinimumWidth( 0, 25 );
            leftColumn.setLayout( leftLayout );
            int leftRow = 0;
            inputPanelLayout.addComponent( leftColumn, 0, 0 );
            
            JPanel rightColumn = new JPanel();
            EasyGridBagLayout rightLayout = new EasyGridBagLayout( rightColumn );
            rightLayout.setMinimumWidth( 0, 25 );
            rightColumn.setLayout( rightLayout );
            int rightRow = 1;
            inputPanelLayout.addComponent( rightColumn, 0, 2 );

            // Total Energy
            {
                JLabel teLabel = new JLabel( QTResources.getString( "label.averageTotalEnergy" ) );
                _teSpinner = new DoubleSpinner( 0, -SPINNER_MAX, SPINNER_MAX, ENERGY_STEP, ENERGY_FORMAT, SPINNER_SIZE );
                _teSpinner.addChangeListener( _listener );
                JLabel teUnits = new JLabel( QTResources.getString( "units.energy" ) );
                leftLayout.addAnchoredComponent( teLabel, leftRow, 0, 2, 1, GridBagConstraints.EAST );
                leftLayout.addComponent( _teSpinner, leftRow, 2 );
                leftLayout.addComponent( teUnits, leftRow, 3 );
                leftRow++;
            }

            // Potential Energy for each region...
            char peChar = QTResources.getChar( "char.potentialEnergy", 'P' );
            {
                JLabel peTitle = new JLabel( QTResources.getString( "label.potentialEnergy" ) );
                leftLayout.addAnchoredComponent( peTitle, leftRow, 0, 4, 1, GridBagConstraints.WEST );
                leftRow++;
                int numberOfRegions = _potentialEnergy.getNumberOfRegions();
                _peSpinners = new ArrayList();
                for ( int i = 0; i < numberOfRegions; i++ ) {
                    String s = String.valueOf( peChar ) + String.valueOf( i + 1 ) + ":";
                    JLabel peLabel = new JLabel( s );
                    DoubleSpinner peSpinner = new DoubleSpinner( 0, -SPINNER_MAX, SPINNER_MAX, ENERGY_STEP, ENERGY_FORMAT, SPINNER_SIZE );
                    peSpinner.addChangeListener( _listener );
                    _peSpinners.add( peSpinner );
                    JLabel peUnits = new JLabel( QTResources.getString( "units.energy" ) );
                    leftLayout.addAnchoredComponent( peLabel, leftRow, 1, GridBagConstraints.EAST );
                    leftLayout.addAnchoredComponent( peSpinner, leftRow, 2, GridBagConstraints.EAST );
                    leftLayout.addAnchoredComponent( peUnits, leftRow, 3, GridBagConstraints.WEST );
                    leftRow++;
                }
            }

            // Step...
            _stepSpinner = null;
            if ( _potentialEnergy instanceof StepPotential ) {
                JLabel stepLabel = new JLabel( QTResources.getString( "label.stepPosition" ) );
                _stepSpinner = new DoubleSpinner( 0, -SPINNER_MAX, SPINNER_MAX, POSITION_STEP, POSITION_FORMAT, SPINNER_SIZE );
                _stepSpinner.addChangeListener( _listener );
                JLabel stepUnits = new JLabel( QTResources.getString( "units.position" ) );
                leftLayout.addAnchoredComponent( stepLabel, leftRow, 0, 2, 1, GridBagConstraints.EAST );
                leftLayout.addComponent( _stepSpinner, leftRow, 2 );
                leftLayout.addComponent( stepUnits, leftRow, 3 );
                leftRow++;
            }
            
            // Barriers...
            _widthSpinners = null;
            _positionSpinners = null;
            char barrierChar = QTResources.getChar( "char.barrier", 'B' );
            if ( _potentialEnergy instanceof BarrierPotential ) {

                int numberOfBarriers = ( (BarrierPotential) _potentialEnergy ).getNumberOfBarriers();

                // Barrier Positions...
                _positionSpinners = new ArrayList();
                JLabel positionTitle = new JLabel( QTResources.getString( "label.barrierPosition" ) );
                rightLayout.addAnchoredComponent( positionTitle, rightRow, 0, 4, 1, GridBagConstraints.WEST );
                rightRow++;
                for ( int i = 0; i < numberOfBarriers; i++ ) {
                    String s = String.valueOf( barrierChar ) + String.valueOf( i + 1 ) + ":";
                    JLabel positionLabel = new JLabel( s );
                    DoubleSpinner positionSpinner = new DoubleSpinner( 0, -SPINNER_MAX, SPINNER_MAX, POSITION_STEP, POSITION_FORMAT, SPINNER_SIZE );
                    positionSpinner.addChangeListener( _listener );
                    _positionSpinners.add( positionSpinner );
                    JLabel positionUnits = new JLabel( QTResources.getString( "units.position" ) );
                    rightLayout.addAnchoredComponent( positionLabel, rightRow, 1, GridBagConstraints.EAST );
                    rightLayout.addAnchoredComponent( positionSpinner, rightRow, 2, GridBagConstraints.EAST );
                    rightLayout.addAnchoredComponent( positionUnits, rightRow, 3, GridBagConstraints.WEST );
                    rightRow++;
                }
                
                // Barrier Widths...
                _widthSpinners = new ArrayList();
                JLabel widthTitle = new JLabel( QTResources.getString( "label.barrierWidth" ) );
                rightLayout.addAnchoredComponent( widthTitle, rightRow, 0, 4, 1, GridBagConstraints.WEST );
                rightRow++;
                for ( int i = 0; i < numberOfBarriers; i++ ) {
                    String s = String.valueOf( barrierChar ) + String.valueOf( i + 1 ) + ":";
                    JLabel widthLabel = new JLabel( s );
                    DoubleSpinner widthSpinner = new DoubleSpinner( 0, -SPINNER_MAX, SPINNER_MAX, POSITION_STEP, POSITION_FORMAT, SPINNER_SIZE );
                    widthSpinner.addChangeListener( _listener );
                    _widthSpinners.add( widthSpinner );
                    JLabel widthUnits = new JLabel( QTResources.getString( "units.position" ) );
                    rightLayout.addAnchoredComponent( widthLabel, rightRow, 1, GridBagConstraints.EAST );
                    rightLayout.addAnchoredComponent( widthSpinner, rightRow, 2, GridBagConstraints.EAST );
                    rightLayout.addAnchoredComponent( widthUnits, rightRow, 3, GridBagConstraints.WEST );
                    rightRow++;
                }
            }
        }

        JPanel inputPanel = new JPanel( new BorderLayout() );
        inputPanel.add( menuPanel, BorderLayout.NORTH );
        inputPanel.add( spinnerPanel, BorderLayout.CENTER );

        inputPanel.setFocusTraversalPolicy( new ContainerOrderFocusTraversalPolicy() );
        return inputPanel;
    }

    /*
     * Creates the dialog's actions panel, consisting of Apply and Close buttons.
     * 
     * @return the actions panel
     */
    private JPanel createActionsPanel() {

        _applyButton = new JButton( QTResources.getString( "button.apply" ) );
        _applyButton.addActionListener( _listener );

        _closeButton = new JButton( QTResources.getString( "button.close" ) );
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
        
        // Potential type
        _potentialComboBox.removeItemListener( _listener );
        PotentialType potentialType = PotentialFactory.getPotentialType( _potentialEnergy );
        _potentialComboBox.setSelectedPotentialType( potentialType );
        _potentialComboBox.addItemListener( _listener );
        
        // Total Energy
        double te = _totalEnergy.getEnergy();
        _teSpinner.setValue( new Double( te ) );
        
        // Potential Energy per region
        for ( int i = 0; i < _peSpinners.size(); i++ ) {
            double pe = _potentialEnergy.getEnergy( i );
            DoubleSpinner peSpinner = (DoubleSpinner) _peSpinners.get( i );
            peSpinner.setValue( new Double( pe ) );
        }
        
        // Step 
        if ( _stepSpinner != null ) {
            double position = _potentialEnergy.getStart( 1 );
            _stepSpinner.setValue( new Double( position ) );
        }
        
        // Barrier Width
        if ( _widthSpinners != null ) {
            for ( int i = 0; i < _widthSpinners.size(); i++ ) {
                DoubleSpinner widthSpinner = (DoubleSpinner) _widthSpinners.get( i );
                int regionIndex = BarrierPotential.toRegionIndex( i );
                double width = ( (BarrierPotential) _potentialEnergy ).getWidth( regionIndex );
                widthSpinner.setValue( new Double( width ) );
            }
        }
        
        // Barrier Positions
        if ( _positionSpinners != null ) {
            for ( int i = 0; i < _positionSpinners.size(); i++ ) {
                DoubleSpinner positionSpinner = (DoubleSpinner) _positionSpinners.get( i );
                int regionIndex = BarrierPotential.toRegionIndex( i );
                double position = ( (BarrierPotential) _potentialEnergy ).getStart( regionIndex );
                positionSpinner.setValue( new Double( position ) );
            }
        }
    }
    
    /*
     * Rebuilds the input panel when the type of potential changes.
     */
    private void rebuildInputPanel() {
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

        char peChar = QTResources.getChar( "char.potentialEnergy", 'P' );
        char barrierChar = QTResources.getChar( "char.barrier", 'B' );
        
        boolean hasBarriers = ( _potentialEnergy instanceof BarrierPotential );
        
        double minY = _energyPlot.getRangeAxis().getRange().getLowerBound();
        double maxY = _energyPlot.getRangeAxis().getRange().getUpperBound();
        
        XYItemRenderer renderer = _energyPlot.getRenderer();
        renderer.removeAnnotations();
        _energyPlot.clearDomainMarkers();
        
        int numberOfRegions = _potentialEnergy.getNumberOfRegions();
        for ( int i = 0; i < numberOfRegions; i++ ) {
            
            // Marker
            if ( i != 0 ) {
                double x = _potentialEnergy.getStart( i );
                Marker marker = new ValueMarker( x );
                marker.setPaint( _colorScheme.getRegionMarkerColor() );
                marker.setStroke( QTConstants.REGION_MARKER_STROKE );
                _energyPlot.addDomainMarker( marker );
            }
            
            // Annotation
            {
                // Region annotation
                String text = String.valueOf( peChar ) + String.valueOf( i + 1 );
                double x = _potentialEnergy.getMiddle( i );
                double y = maxY - ANNOTATION_MARGIN;
                XYTextAnnotation annotation = new XYTextAnnotation( text, x, y );
                annotation.setFont( ANNOTATION_FONT );
                annotation.setPaint( _colorScheme.getAnnotationColor() );
                annotation.setTextAnchor( TextAnchor.TOP_CENTER );
                renderer.addAnnotation( annotation );
                
                // Barrier annotation
                if ( hasBarriers && BarrierPotential.isaBarrier( i ) ) {
                    int barrierIndex = BarrierPotential.toBarrierIndex( i );
                    text = String.valueOf( barrierChar ) + String.valueOf( barrierIndex + 1 );
                    y = minY + ANNOTATION_MARGIN;
                    annotation = new XYTextAnnotation( text, x, y );
                    annotation.setFont( ANNOTATION_FONT );
                    annotation.setPaint( _colorScheme.getAnnotationColor() );
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
            else if ( _peSpinners.contains( event.getSource() ) ) { /* inefficient! */
                handlePotentialEnergyChange( _peSpinners.indexOf( event.getSource() ) );
            }
            else if ( event.getSource() == _stepSpinner ) {
                handleStepPositionChange();
            }
            else if ( _widthSpinners.contains( event.getSource() ) ) { /* inefficient! */
                handleBarrierWidthChange( _widthSpinners.indexOf( event.getSource() ) );
            }
            else if ( _positionSpinners.contains( event.getSource() ) ) { /* inefficient! */
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
     * Sends copies of the energies to the module.
     */
    private void handleApply() {
        if ( _teChanged ) {
            _module.setTotalEnergy( new TotalEnergy( _totalEnergy ) );
            _teChanged = false;
        }
        if ( _peChanged ) {
            _module.setPotentialEnergy( PotentialFactory.clonePotentialEnergy( _potentialEnergy ) ); 
            _peChanged = false;
        }
        _applyButton.setEnabled( false );
    }

    /*
     * Handles the "Close" button, checks for unsaved changes.
     */
    private void handleClose() {
        if ( _teChanged || _peChanged ) {
            String message = QTResources.getString( "message.unsavedChanges" );
            String title = PhetCommonResources.getInstance().getLocalizedString( "Common.title.confirm" );
            int reply = JOptionPane.showConfirmDialog( this, message, title, JOptionPane.YES_NO_CANCEL_OPTION );
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
        PotentialType potentialType = _potentialComboBox.getSelectedPotentialType();
        _potentialEnergy = PotentialFactory.createPotentialEnergy( potentialType );
        _energyPlot.setPotentialEnergy( _potentialEnergy );
        _peChanged = true;
        _applyButton.setEnabled( true );
        rebuildInputPanel();
    }
    
    /*
     * Handles a change in total energy.
     */
    private void handleTotalEnergyChange() {
        double energy = _teSpinner.getDoubleValue();
        if ( energy >= MIN_ENERGY && energy <= MAX_ENERGY ) {
            _totalEnergy.setEnergy( energy );
            _teChanged = true;
            _applyButton.setEnabled( true );
        }
        else {
            warnInvalidInput();
            energy = _totalEnergy.getEnergy();
            _teSpinner.setDoubleValue( energy );
        }
    }
    
    /*
     * Handles a change in the potential energy of a region.
     */
    private void handlePotentialEnergyChange( int regionIndex ) {
        DoubleSpinner peSpinner = (DoubleSpinner) _peSpinners.get( regionIndex );
        double energy = peSpinner.getDoubleValue();
        if ( energy >= MIN_ENERGY && energy <= MAX_ENERGY ) {
            _potentialEnergy.setEnergy( regionIndex, energy );
            updateMarkersAndAnnotations();
            _peChanged = true;
            _applyButton.setEnabled( true );
        }
        else {
            warnInvalidInput();
            energy = _potentialEnergy.getEnergy( regionIndex );
            peSpinner.setDoubleValue( energy );
        }
    }
    
    /*
     * Handles a change in the position of a step.
     */
    private void handleStepPositionChange() {
        if ( _potentialEnergy instanceof StepPotential ) {
            StepPotential step = (StepPotential) _potentialEnergy;
            double position = _stepSpinner.getDoubleValue();
            boolean success = step.setStepPosition( position );
            if ( success ) {
                updateMarkersAndAnnotations();
                _peChanged = true;
                _applyButton.setEnabled( true );
            }
            else {
                warnInvalidInput();
                position = step.getStepPosition();
                _stepSpinner.setDoubleValue( position );
            }
        }
    }
    
    /*
     * Handles a change in the width of a barrier.
     */
    private void handleBarrierWidthChange( int barrierIndex ) {
        if ( _potentialEnergy instanceof BarrierPotential ) {
            BarrierPotential barrier = (BarrierPotential) _potentialEnergy;
            DoubleSpinner widthSpinner = (DoubleSpinner) _widthSpinners.get( barrierIndex );
            double width = widthSpinner.getDoubleValue();
            boolean success = barrier.setBarrierWidth( barrierIndex, width );
            if ( success ) {
                updateMarkersAndAnnotations();
                _peChanged = true;
                _applyButton.setEnabled( true );
            }
            else {
                warnInvalidInput();
                widthSpinner.setDoubleValue( width );
            }
        }
    }
    
    /*
     * Handles a change in the position of a barrier.
     */
    private void handleBarrierPositionChange( int barrierIndex ) {
        if ( _potentialEnergy instanceof BarrierPotential ) {
            BarrierPotential bp = (BarrierPotential) _potentialEnergy;
            DoubleSpinner positionSpinner = (DoubleSpinner) _positionSpinners.get( barrierIndex );
            double position = positionSpinner.getDoubleValue();
            boolean success = bp.setBarrierPosition( barrierIndex, position );
            if ( success ) {
                updateMarkersAndAnnotations();
                _peChanged = true;
                _applyButton.setEnabled( true );
            }
            else {
                warnInvalidInput();
                position = bp.getBarrierPosition( barrierIndex );
                positionSpinner.setDoubleValue( position );
            }
        }
    }
    
    /*
     * Warns the user about invalid input.
     */
    private void warnInvalidInput() {
        Toolkit.getDefaultToolkit().beep();
    }
}
