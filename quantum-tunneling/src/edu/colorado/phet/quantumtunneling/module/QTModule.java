/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.module;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;

import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.control.*;
import edu.colorado.phet.quantumtunneling.enum.Direction;
import edu.colorado.phet.quantumtunneling.enum.IRView;
import edu.colorado.phet.quantumtunneling.enum.PotentialType;
import edu.colorado.phet.quantumtunneling.enum.WaveType;
import edu.colorado.phet.quantumtunneling.model.*;
import edu.colorado.phet.quantumtunneling.persistence.QTConfig;
import edu.colorado.phet.quantumtunneling.view.EnergyLegend;
import edu.colorado.phet.quantumtunneling.view.QTCombinedChart;
import edu.colorado.phet.quantumtunneling.view.QTCombinedChartNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;


/**
 * QTModule is the sole module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTModule extends AbstractModule implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Font LEGEND_FONT = new Font( QTConstants.FONT_NAME, Font.PLAIN, 12 );
    
    // All of these values are in local coordinates
    private static final int X_MARGIN = 10; // space at left & right edges of canvas
    private static final int Y_MARGIN = 10; // space at top & bottom edges of canvas
    private static final int X_SPACING = 10; // horizontal space between nodes
    private static final int Y_SPACING = 10; // vertical space between nodes
    private static final int ZOOM_X_OFFSET = 2; // X offset of zoom buttons from edge of plot
    private static final int ZOOM_Y_OFFSET = 2; // Y offset of zoom buttons from edge of plot
    private static final Dimension CANVAS_RENDERING_SIZE = new Dimension( 1000, 1000 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private TotalEnergy _totalEnergy;
    private AbstractPotential _potentialEnergy;
    private ConstantPotential _constantPotential;
    private StepPotential _stepPotential;
    private SingleBarrierPotential _singleBarrierPotential;
    private DoubleBarrierPotential _doubleBarrierPotential;
    private PlaneWave _planeWave;
    private WavePacket _wavePacket;
    
    // View
    private PhetPCanvas _canvas;
    private PNode _parentNode;
    private EnergyLegend _legend;
    private QTCombinedChartNode _chartNode;
    private QTCombinedChart _chart;
    
    // Control
    private PSwing _configureButton;
    private PSwing _measureButton;
    private QTControlPanel _controlPanel;
    private ConfigureEnergyDialog _configureEnergyDialog;
    private TotalEnergyDragHandle _totalEnergyControl;
    private PotentialEnergyControls _potentialEnergyControls;
    private PSwing _waveFunctionZoomControl;
    private PSwing _probabilityDensityZoomControl;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param clock
     */
    public QTModule() {
        super( SimStrings.get( "title.quantumTunneling" ), new QTClock(), true /* startsPaused */ );

        setLogoPanel( null );
        
        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------
        
        // Plane wave
        _planeWave = new PlaneWave();
        _planeWave.setEnabled( false );
        addClockListener( _planeWave );
        
        // Packet wave
        _wavePacket = new WavePacket();
        _wavePacket.setEnabled( false );
        addClockListener( _wavePacket );
        
        // Energy model is created in reset method!
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        EventListener listener = new EventListener();
        
        // Piccolo canvas
        {
            _canvas = new PhetPCanvas( CANVAS_RENDERING_SIZE );
            _canvas.setBackground( QTConstants.CANVAS_BACKGROUND );
            setCanvas( _canvas );
        }
        
        // Root of our scene graph
        {
            _parentNode = new PNode();
            _canvas.addScreenChild( _parentNode );
        }
        
        // Energy graph legend
        { 
            _legend = new EnergyLegend();
        }
        
        // Combined chart
        {
            _chart = new QTCombinedChart();
            _chart.getEnergyPlot().setWavePacket( _wavePacket );
            _chart.setBackgroundPaint( QTConstants.CANVAS_BACKGROUND );
            _chartNode = new QTCombinedChartNode( _chart );
        }
        
        // Add view nodes to the scene graph
        {
            _parentNode.addChild( _legend );
            _parentNode.addChild( _chartNode );
        }       
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------
        
        // Control Panel
        _controlPanel = new QTControlPanel( this );
        setControlPanel( _controlPanel );
        
        // Clock Controls
        QTClockControls clockControls = new QTClockControls( getClock() );
        clockControls.setLoopVisible( false );
        clockControls.setTimeFormat( QTConstants.TIME_FORMAT );
        setClockControlPanel( clockControls );
        addClockListener( new ClockAdapter() {
            public void simulationTimeReset( ClockEvent clockEvent ) {
               handleClockReset();
            }
        } ); 
        
        // Configure button
        {
            JButton jButton = new JButton( SimStrings.get( "button.configureEnergy" ) );
            jButton.setOpaque( false );
            jButton.addActionListener( listener );
            _configureButton = new PSwing( _canvas, jButton );
        }
        
        // Measure button
        {
            JButton jButton = new JButton( SimStrings.get( "button.measure" ) );
            jButton.setOpaque( false );
            jButton.addActionListener( listener );
            _measureButton = new PSwing( _canvas, jButton );
        }
        
        // Energy drag handles
        {
            _totalEnergyControl = new TotalEnergyDragHandle( _chartNode );
            _totalEnergyControl.setValueVisible( QTConstants.SHOW_ENERGY_VALUES );
            _totalEnergyControl.setXAxisPosition( QTConstants.POSITION_RANGE.getUpperBound() - 1 );
            
            _potentialEnergyControls = new PotentialEnergyControls( _chartNode );
            _potentialEnergyControls.setValuesVisible( QTConstants.SHOW_ENERGY_VALUES );
        }
        
        // Zoom buttons
        {
            ZoomControl z1 = new ZoomControl( ZoomControl.VERTICAL, _chart.getWaveFunctionPlot() );
            _waveFunctionZoomControl = new PSwing( _canvas, z1 );
            
            ZoomControl z2 = new ZoomControl( ZoomControl.VERTICAL, _chart.getProbabilityDensityPlot() );
            _probabilityDensityZoomControl = new PSwing( _canvas, z2 );
        }
        
        // Add control nodes to the scene graph
        {
            _parentNode.addChild( _configureButton );
            _parentNode.addChild( _measureButton );
            _parentNode.addChild( _totalEnergyControl );
            _parentNode.addChild( _potentialEnergyControls );
            _parentNode.addChild( _waveFunctionZoomControl );
            _parentNode.addChild( _probabilityDensityZoomControl );
        }
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
        
        //----------------------------------------------------------------------------
        // Initialze the module state
        //----------------------------------------------------------------------------
       
        reset();
        layoutCanvas();
        _canvas.addComponentListener( listener );
        getClock().start();
    }
    
    //----------------------------------------------------------------------------
    // Canvas layout
    //----------------------------------------------------------------------------
    
    /*
     * Lays out nodes on the canvas.
     * This is called whenever the canvas size changes.
     */
    private void layoutCanvas() {
        
        // Height of the legend
        double legendHeight = _legend.getFullBounds().getHeight();
        
        // Location and dimensions of combined chart
        final double chartWidth = _canvas.getWidth() - ( 2 * X_MARGIN );
        final double chartHeight = _canvas.getHeight() - ( legendHeight  + ( 2 * Y_MARGIN ) + Y_SPACING );
        
        // Charts
        {
            _chartNode.setBounds( 0, 0, chartWidth, chartHeight );
            AffineTransform chartTransform = new AffineTransform();
            chartTransform.translate( X_MARGIN, Y_MARGIN + legendHeight + Y_SPACING );
            chartTransform.translate( 0, 0 ); // registration point @ upper left
            _chartNode.setTransform( chartTransform );
            _chartNode.updateChartRenderingInfo();
        }

        // Bounds of plots, in global coordinates -- get these after transforming the chart!
        Rectangle2D energyPlotBounds = _chartNode.localToGlobal( _chartNode.getEnergyPlotBounds() );
        Rectangle2D waveFunctionPlotBounds = _chartNode.localToGlobal( _chartNode.getWaveFunctionPlotBounds() );
        Rectangle2D probabilityDensityPlotBounds = _chartNode.localToGlobal( _chartNode.getProbabilityDensityPlotBounds() );
        
        // Drag handles
        {
            _totalEnergyControl.updateDragBounds();
            _potentialEnergyControls.updateDragBounds();
        }
        
        // dx (sample point spacing)
        {
            // All charts have the same x axis, so just use the Energy chart.
            Point2D p1 = _chartNode.nodeToEnergy( new Point2D.Double( 0, 0 ) );
            Point2D p2 = _chartNode.nodeToEnergy( new Point2D.Double( QTConstants.PIXELS_PER_SAMPLE_POINT, 0 ) );
            double dx = p2.getX() - p1.getX();
            _wavePacket.getSolver().setDx( dx );
            _chart.getWaveFunctionPlot().setDx( dx );
        }
       
        // Legend
        {
            // Aligned with left edge of energy plot, centered in space about energy plot
            AffineTransform legendTransform = new AffineTransform();
            legendTransform.translate( energyPlotBounds.getX(), Y_MARGIN );
            legendTransform.translate( 0, 0 ); // upper left
            _legend.setTransform( legendTransform );
        }
        
        // Configure button
        {
            // Aligned with right edge of energy plot, centered in space about energy plot
            AffineTransform configureTransform = new AffineTransform();
            configureTransform.translate( energyPlotBounds.getX() + energyPlotBounds.getWidth(), Y_MARGIN );
            configureTransform.translate( -_configureButton.getWidth(), 0 ); // registration point = upper right
            _configureButton.setTransform( configureTransform );
        }
        
        // Measure button
        {
            AffineTransform measureTransform = new AffineTransform();
            measureTransform.translate( probabilityDensityPlotBounds.getX(), _canvas.getHeight() - _measureButton.getHeight() - Y_MARGIN );
            measureTransform.translate( 0, 0 ); // registration point = upper left
            _measureButton.setTransform( measureTransform );
        }
        
        // Zoom control for "Wave Function" plot
        {
            AffineTransform transform = new AffineTransform();
            transform.translate( waveFunctionPlotBounds.getX() + ZOOM_X_OFFSET, waveFunctionPlotBounds.getY() + ZOOM_Y_OFFSET );
            transform.translate( 0, 0 ); // registration point = upper left
            _waveFunctionZoomControl.setTransform( transform );
        }
        
        // Zoom control for "Probability Density" plot
        {
            AffineTransform transform = new AffineTransform();
            transform.translate( probabilityDensityPlotBounds.getX() + ZOOM_X_OFFSET, probabilityDensityPlotBounds.getY() + ZOOM_Y_OFFSET );
            transform.translate( 0, 0 ); // registration point = upper left
            _probabilityDensityZoomControl.setTransform( transform );
        }
    }
    
    //----------------------------------------------------------------------------
    // AbstractModule implementation
    //----------------------------------------------------------------------------
    
    /**
     * Resets the module to its initial state.
     */
    public void reset() {
        
        // Model
        {
            TotalEnergy totalEnergy = new TotalEnergy( QTConstants.DEFAULT_TOTAL_ENERGY );
            setTotalEnergy( totalEnergy );
            
            _constantPotential = new ConstantPotential();
            _stepPotential = new StepPotential();
            _singleBarrierPotential = new SingleBarrierPotential();
            _doubleBarrierPotential = new DoubleBarrierPotential();
        }
        
        // Controls
        {
            _controlPanel.setPotentialType( PotentialType.SINGLE_BARRIER );
            _controlPanel.setRealSelected( true );
            _controlPanel.setImaginarySelected( false );
            _controlPanel.setMagnitudeSelected( false );
            _controlPanel.setPhaseSelected( false );
            _controlPanel.setIRView( IRView.SUM );
            _controlPanel.setDirection( Direction.LEFT_TO_RIGHT );
            _controlPanel.setWaveType( WaveType.PACKET );
            _controlPanel.setPacketWidth( QTConstants.DEFAULT_PACKET_WIDTH );
            _controlPanel.setPacketCenter( QTConstants.DEFAULT_PACKET_CENTER );
        }
    }
    
    /**
     * Saves the module's configuration by writing it to a provided configuration object.
     * 
     * @param appConfig
     */
    public void save( QTConfig appConfig ) {
        QTConfig.ModuleConfig config = appConfig.getModuleConfig();
        
        // Model
        config.saveTotalEnergy( _totalEnergy );
        config.setMinRegionWidth( _constantPotential.getMinRegionWidth() ); // assume the same for all potentials!
        config.saveConstantPotential( _constantPotential );
        config.saveStepPotential( _stepPotential );
        config.saveSingleBarrierPotential( _singleBarrierPotential );
        config.saveDoubleBarrierPotential( _doubleBarrierPotential );
        
        // Control panel
        config.savePotentialType( _controlPanel.getPotentialType() );
        config.setRealSelected( _controlPanel.isRealSelected() );
        config.setImaginarySelected( _controlPanel.isImaginarySelected() );
        config.setMagnitudeSelected( _controlPanel.isMagnitudeSelected( ) );
        config.setPhaseSelected( _controlPanel.isPhaseSelected() );
        config.saveIRView( _controlPanel.getIRView() );
        config.saveDirection( _controlPanel.getDirection() );
        config.saveWaveType( _controlPanel.getWaveType() );
        // Make sure we get width & center from the control panel -- the "Measure" feature temporarily changes the model.
        config.setPacketWidth( _controlPanel.getPacketWidth() );
        config.setPacketCenter( _controlPanel.getPacketCenter() );
    }
    
    /**
     * Loads the module's configuration by reading it from a provided configuration object.
     * 
     * @param appConfig
     */
    public void load( QTConfig appConfig ) {
        QTConfig.ModuleConfig config = appConfig.getModuleConfig();
        
        // Model
        setTotalEnergy( config.loadTotalEnergy() );
        setConstantPotential( config.loadConstantPotential() );
        setStepPotential( config.loadStepPotential() );
        setSingleBarrierPotential( config.loadSingleBarrierPotential() );
        setDoubleBarrierPotential( config.loadDoubleBarrierPotential() );
        
        // Control panel
        _controlPanel.setPotentialType( config.loadPotentialType() );
        _controlPanel.setRealSelected( config.isRealSelected() );
        _controlPanel.setImaginarySelected( config.isImaginarySelected() );
        _controlPanel.setMagnitudeSelected( config.isMagnitudeSelected() );
        _controlPanel.setPhaseSelected( config.isPhaseSelected() );
        _controlPanel.setIRView( config.loadIRView() );
        _controlPanel.setDirection( config.loadDirection() );
        _controlPanel.setWaveType( config.loadWaveType() );
        _controlPanel.setPacketWidth( config.getPacketWidth() );
        _controlPanel.setPacketCenter( config.getPacketCenter() );
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    private class EventListener extends ComponentAdapter implements ActionListener {

        public void componentResized( ComponentEvent event ) {
            if ( event.getSource() == _canvas ) {
                layoutCanvas();
            }
        }
        
        public void actionPerformed( ActionEvent event ) {
            if ( event.getSource() == _configureButton.getComponent() ) {
                handleConfigureButton();
            }
            else if ( event.getSource() == _measureButton.getComponent() ) {
                setMeasureEnabled( true );
            }
        }
    }
    
    /*
     * When the "Configure Energy" button is pressed, open a dialog.
     * The clock is temporarily paused while the dialog is open,
     * so that the user interface response is "snappy".
     */
    private void handleConfigureButton() {
        if ( _configureEnergyDialog == null ) {
            boolean clockRunning = getClock().isRunning();
            getClock().pause();
            WaveType waveType = _controlPanel.getWaveType();
            _configureEnergyDialog = new ConfigureEnergyDialog( getFrame(), this, _totalEnergy, _potentialEnergy, _wavePacket, waveType );
            _configureEnergyDialog.show();
            // Dialog is model, so we stop here until the dialog is closed.
            _configureEnergyDialog.cleanup();
            _configureEnergyDialog = null;
            if ( clockRunning ) {
                getClock().start();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setPotentialType( PotentialType potentialType ) {
        AbstractPotential pe = null;
        if ( potentialType == PotentialType.CONSTANT ) {
            pe = _constantPotential;
        }
        else if (  potentialType == PotentialType.STEP ) {
            pe = _stepPotential;
        }
        else if (  potentialType == PotentialType.SINGLE_BARRIER ) {
            pe = _singleBarrierPotential;
        }
        else if (  potentialType == PotentialType.DOUBLE_BARRIER ) {
            pe = _doubleBarrierPotential;
        }
        else {
            throw new IllegalStateException( "unsupported potential type: " + potentialType );
        }
        setPotentialEnergy( pe );
    }
    
    public void setPotentialEnergy( AbstractPotential potentialEnergy ) {
        
        // Replace one of the model elements.
        if ( potentialEnergy instanceof ConstantPotential ) {
            _constantPotential = (ConstantPotential) potentialEnergy;
        }
        else if ( potentialEnergy instanceof StepPotential ) {
            _stepPotential = (StepPotential) potentialEnergy;
        }
        else if ( potentialEnergy instanceof SingleBarrierPotential ) {
            _singleBarrierPotential = (SingleBarrierPotential) potentialEnergy;
        }
        else if ( potentialEnergy instanceof DoubleBarrierPotential ) {
            _doubleBarrierPotential = (DoubleBarrierPotential) potentialEnergy;
        }
        else {
            throw new IllegalArgumentException( "unsupported potential type: " + potentialEnergy.getClass().getName() );
        }
            
        // Wire up the potential so that it's the one displayed.
        {
            if ( _potentialEnergy != null ) {
                _potentialEnergy.deleteObserver( this );
            }
            _potentialEnergy = potentialEnergy;
            _potentialEnergy.addObserver( this );

            _chart.setPotentialEnergy( _potentialEnergy );
            _controlPanel.setPotentialEnergy( _potentialEnergy );
            _potentialEnergyControls.setPotentialEnergy( _potentialEnergy );
            _planeWave.setPotentialEnergy( _potentialEnergy );
            _wavePacket.setPotentialEnergy( potentialEnergy );
        }
        
        resetClock();
    }
    
    public void setConstantPotential( ConstantPotential potential ) {
        if ( _potentialEnergy == _constantPotential ) {
            setPotentialEnergy( potential );
        }
        else {
            _constantPotential = potential;
        }
    }
    
    public void setStepPotential( StepPotential potential ) {
        if ( _potentialEnergy == _stepPotential ) {
            setPotentialEnergy( potential );
        }
        else {
            _stepPotential = potential;
        }
    }
    
    public void setSingleBarrierPotential( SingleBarrierPotential potential ) {
        if ( _potentialEnergy == _singleBarrierPotential ) {
            setPotentialEnergy( potential );
        }
        else {
            _singleBarrierPotential = potential;
        }
    }
    
    public void setDoubleBarrierPotential( DoubleBarrierPotential potential ) {
        if ( _potentialEnergy == _doubleBarrierPotential ) {
            setPotentialEnergy( potential );
        }
        else {
            _doubleBarrierPotential = potential;
        }
    }
    
    public void setTotalEnergy( TotalEnergy totalEnergy ) {
        
        if ( _totalEnergy != null ) {
            _totalEnergy.deleteObserver( this );
        }
        _totalEnergy = totalEnergy;
        _totalEnergy.addObserver( this );
        
        resetClock();

        _chart.setTotalEnergy( _totalEnergy );
        _totalEnergyControl.setTotalEnergy( _totalEnergy );
        _planeWave.setTotalEnergy( _totalEnergy );
        _wavePacket.setTotalEnergy( totalEnergy );
    }
    
    public void setValuesVisible( boolean visible ) {
        _totalEnergyControl.setValueVisible( visible );
        _potentialEnergyControls.setValuesVisible( visible );
    }
    
    public boolean isValuesVisible() {
        return _totalEnergyControl.isValueVisible();
    }
    
    public void setWaveType( WaveType waveType ) {
        resetClock();
        _chart.getEnergyPlot().setWaveType( waveType );
        if ( waveType == WaveType.PLANE ) {
            _planeWave.setEnabled( true );
            _wavePacket.setEnabled( false );
            _chart.getWaveFunctionPlot().setWave( _planeWave );
        }
        else {
            _planeWave.setEnabled( false );
            _wavePacket.setEnabled( true );
            _chart.getWaveFunctionPlot().setWave( _wavePacket );
        }
    }
    
    public void setRealVisible( boolean visible ) {
        _chart.getWaveFunctionPlot().setRealVisible( visible );
    }
    
    public void setImaginaryVisible( boolean visible ) {
        _chart.getWaveFunctionPlot().setImaginaryVisible( visible );
    }
    
    public void setMagnitudeVisible( boolean visible ) {
        _chart.getWaveFunctionPlot().setMagnitudeVisible( visible );
    }
    
    public void setPhaseVisible( boolean visible ) {
        _chart.getWaveFunctionPlot().setPhaseVisible( visible );
    }
    
    public void setDirection( Direction direction ) {
        _planeWave.setNotifyEnabled( false );
        _wavePacket.setNotifyEnabled( false );
        _planeWave.setDirection( direction );
        _wavePacket.setDirection( direction );
        resetClock();
        _planeWave.setNotifyEnabled( true );
        _wavePacket.setNotifyEnabled( true );
    }
    
    public void setIRView( IRView irView ) {
        _chart.getWaveFunctionPlot().setIRView( irView );
    }
    
    public void setWavePacketWidth( double width ) {
        resetClock();
        _wavePacket.setWidth( width );
    }
    
    public void setWavePacketCenter( double center ) {
        resetClock();
        _wavePacket.setCenter( center );
    }
    
    public void setMeasureEnabled( boolean enabled ) {
        _planeWave.setMeasureEnabled( enabled );
        _wavePacket.setMeasureEnabled( enabled );
    }
    
    private void resetClock() {
        getClock().resetSimulationTime();
    }
    
    private void handleClockReset() {
        setMeasureEnabled( false );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Restarts the clock when energies change.
     */
    public void update( Observable o, Object arg ) {
        if ( o == _potentialEnergy || o == _totalEnergy ) {
            resetClock();
        }
    }

}
