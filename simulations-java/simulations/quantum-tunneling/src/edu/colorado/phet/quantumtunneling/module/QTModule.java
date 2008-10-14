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
import java.awt.RenderingHints;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JDialog;

import edu.colorado.phet.common.jfreechartphet.piccolo.XYPlotNode;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.help.HelpBalloon;
import edu.colorado.phet.common.piccolophet.help.HelpPane;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.QTResources;
import edu.colorado.phet.quantumtunneling.color.QTColorScheme;
import edu.colorado.phet.quantumtunneling.control.*;
import edu.colorado.phet.quantumtunneling.enums.Direction;
import edu.colorado.phet.quantumtunneling.enums.IRView;
import edu.colorado.phet.quantumtunneling.enums.PotentialType;
import edu.colorado.phet.quantumtunneling.enums.WaveType;
import edu.colorado.phet.quantumtunneling.model.*;
import edu.colorado.phet.quantumtunneling.persistence.QTModuleConfig;
import edu.colorado.phet.quantumtunneling.view.*;
import edu.colorado.phet.quantumtunneling.view.AbstractProbabilityNode.ReflectionProbabilityNode;
import edu.colorado.phet.quantumtunneling.view.AbstractProbabilityNode.TransmissionProbabilityNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;


/**
 * QTModule is the sole module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTModule extends QTAbstractModule implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // All of these values are in local coordinates
    private static final int X_MARGIN = 10; // space at left & right edges of canvas
    private static final int Y_MARGIN = 10; // space at top & bottom edges of canvas
    private static final int Y_SPACING = 10; // vertical space between nodes
    private static final int ZOOM_X_OFFSET = 3; // X offset of zoom buttons from edge of plot
    private static final int ZOOM_Y_OFFSET = 3; // Y offset of zoom buttons from edge of plot
    private static final Dimension CANVAS_RENDERING_SIZE = new Dimension( 1000, 1000 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private TotalEnergy _totalEnergy;
    private AbstractPotential _potentialEnergy; // the potential that is currently displayed
    
    // One instance of each potential type, so we can switch between them and retain state.
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
    private QTRegionMarkerManager _regionMarkerManager;
    private ReflectionProbabilityNode _reflectionProbabilityNode;
    private TransmissionProbabilityNode _transmissionProbabilityNode;
    
    // Plots
    private EnergyPlot _energyPlot;
    private WaveFunctionPlot _waveFunctionPlot;
    private ProbabilityDensityPlot _probabilityDensityPlot;
    
    // Nodes to plot datasets separately from chart
    private XYPlotNode _energyPlotNode;
    private XYPlotNode _waveFunctionPlotNode;
    private XYPlotNode _probabilityDensityPlotNode;
    
    // Nodes to draw markers separately from chart
    private QTMarkerNode _energyMarkerNode;
    private QTMarkerNode _waveFunctionMarkerNode;
    private QTMarkerNode _probabilityDensityMarkerNode;
    
    // Control
    private PSwing _configureButton;
    private PSwing _measureButton;
    private QTControlPanel _controlPanel;
    private ConfigureEnergyDialog _configureEnergyDialog;
    private TotalEnergyDragHandle _totalEnergyControl;
    private PotentialEnergyControls _potentialEnergyControls;
    private PSwing _waveFunctionZoomControl;
    private PSwing _probabilityDensityZoomControl;
    private PiccoloClockControlPanel _clockControls;
    private JDialog _richardsonDialog;
    
    // Colors 
    private QTColorScheme _colorScheme;
    
    // This flag is set to true when the Measure button is pressed 
    // so that we can make changes to Total Energy without the clock being reset.
    private boolean _isMeasuring;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     */
    public QTModule() {
        super( QTResources.getString( "title.quantumTunneling" ), new QTClock(), false /* startsPaused */ );

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
        
        // Energy models are created in reset method!
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        EventListener listener = new EventListener();
        
        // Piccolo canvas
        {
            _canvas = new PhetPCanvas( CANVAS_RENDERING_SIZE );
            _canvas.setBackground( QTConstants.CANVAS_BACKGROUND );
            _canvas.setName( "QTModule PhetPCanvas" );
            setSimulationPanel( _canvas );
        }
        
        // Root of our scene graph
        {
            _parentNode = new PNode();
            _canvas.addScreenChild( _parentNode );
        }
        
        // Combined chart
        {
            _chart = new QTCombinedChart();
            _chart.setBackgroundPaint( QTConstants.CANVAS_BACKGROUND );
            
            _chartNode = new QTCombinedChartNode( _chart );
            _parentNode.addChild( _chartNode );
        }
        
        // Energy graph legend
        { 
            _legend = new EnergyLegend();
            _parentNode.addChild( _legend );
        }
        
        // This is where we decide how much of the drawing JFreeChart will be doing...
        if ( QTConstants.JFREECHART_DYNAMIC ) {
            /*
             * If JFreeChart is being used to draw all elements (both static and dynamic) 
             * then get references to the plots that are in the combined chart.
             * We'll add our data to those plots so that the chart changes dynamically.
             */
            _energyPlot = _chart.getEnergyPlot();
            _waveFunctionPlot = _chart.getWaveFunctionPlot();
            _probabilityDensityPlot = _chart.getProbabilityDensityPlot();
        }
        else {
            /*
             * If JFreeChart is being used to draw only static elements, then turn on
             * buffering of the chart node.  The chart will be drawn as an image, and
             * the image will be created only when the chart changes.  To limit the 
             * changes that occur to the chart, we won't be adding any data to it.
             * So we'll create our own plots, to which we'll add the data. And we'll
             * draw those plots top of the static chart using XYPlotNodes.  We'll use 
             * some of the chart's rendering hints for the XYPlot nodes so that the 
             * "look" is consistent with the static chart.
             */
            _chartNode.setBuffered( true );
            
            _energyPlot = new EnergyPlot();
            _waveFunctionPlot = new WaveFunctionPlot();
            _probabilityDensityPlot = new ProbabilityDensityPlot( _waveFunctionPlot.getProbabilityDensitySeries() );
            
            RenderingHints renderingHints = _chart.getRenderingHints();
            
            // marker nodes
            {
                _energyMarkerNode = new QTMarkerNode( _energyPlot );
                _energyMarkerNode.setRenderingHints( renderingHints );
                _parentNode.addChild( _energyMarkerNode );
                
                _waveFunctionMarkerNode = new QTMarkerNode( _waveFunctionPlot );
                _waveFunctionMarkerNode.setRenderingHints( renderingHints );
                _parentNode.addChild( _waveFunctionMarkerNode );
                
                _probabilityDensityMarkerNode = new QTMarkerNode( _probabilityDensityPlot );
                _probabilityDensityMarkerNode.setRenderingHints( renderingHints );
                _parentNode.addChild( _probabilityDensityMarkerNode );
            }
            
            // plot nodes
            {
                _energyPlotNode = new XYPlotNode( _energyPlot );
                _energyPlotNode.setRenderingHints( renderingHints );
                _energyPlotNode.setName( "energyPlotNode" ); // debug
                _parentNode.addChild( _energyPlotNode );

                _waveFunctionPlotNode = new XYPlotNode( _waveFunctionPlot );
                _waveFunctionPlotNode.setRenderingHints( renderingHints );
                _waveFunctionPlotNode.setName( "waveFunctionPlotNode" ); // debug
                _parentNode.addChild( _waveFunctionPlotNode );

                _probabilityDensityPlotNode = new XYPlotNode( _probabilityDensityPlot );
                _probabilityDensityPlotNode.setRenderingHints( renderingHints );
                _probabilityDensityPlotNode.setName( "probabilityDensityPlotNode" ); // debug
                _parentNode.addChild( _probabilityDensityPlotNode );
            }
        }
        
        // Wire up the region marker manager after deciding which plots to use (above).
        _regionMarkerManager = new QTRegionMarkerManager();
        _regionMarkerManager.addPlot( _energyPlot );
        _regionMarkerManager.addPlot( _waveFunctionPlot );
        _regionMarkerManager.addPlot( _probabilityDensityPlot );
        
        // Reflection and transmission probability displays
        _reflectionProbabilityNode = new ReflectionProbabilityNode();
        _parentNode.addChild( _reflectionProbabilityNode );
        _transmissionProbabilityNode = new TransmissionProbabilityNode();
        _parentNode.addChild( _transmissionProbabilityNode );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------
        
        // Control Panel
        _controlPanel = new QTControlPanel( this );
        setControlPanel( _controlPanel );
        
        // Clock Controls
        {
            _clockControls = new PiccoloClockControlPanel( getClock() );
            _clockControls.setRestartButtonVisible( true );
            _clockControls.setTimeDisplayVisible( true );
            _clockControls.setTimeFormat( QTConstants.TIME_FORMAT_PATTERN );
            _clockControls.setTimeColumns( QTConstants.TIME_COLUMNS );
            _clockControls.setUnits( QTResources.getString( "units.time" ) );
            setClockControlPanel( _clockControls );
            addClockListener( new ClockAdapter() {

                public void simulationTimeReset( ClockEvent clockEvent ) {
                    handleClockReset();
                }
            } );
        }
        
        // Configure button
        {
            JButton jButton = new JButton( QTResources.getString( "button.configureEnergy" ) );
            jButton.setOpaque( false );
            jButton.addActionListener( listener );
            _configureButton = new PSwing( jButton );
        }
        
        // Measure button
        {
            JButton jButton = new JButton( QTResources.getString( "button.measure" ) );
            jButton.setOpaque( false );
            jButton.addActionListener( listener );
            _measureButton = new PSwing( jButton );
        }
        
        // Energy drag handles
        {
            // This handler pauses the clock while we're dragging.
            PBasicInputEventHandler dragHandler = new PBasicInputEventHandler() {
                private boolean _clockIsRunning = false;
                public void mousePressed( PInputEvent event ) {
                    _clockIsRunning = getClock().isRunning();
                    if ( _clockIsRunning ) {
                        getClock().pause();
                    }
                }
                public void mouseReleased( PInputEvent event ) {
                    if ( _clockIsRunning ) {
                        getClock().start();
                    }
                }
            };
            
            _totalEnergyControl = new TotalEnergyDragHandle( _chartNode );
            _totalEnergyControl.setXAxisPosition( QTConstants.POSITION_RANGE.getUpperBound() - 1 );
            _totalEnergyControl.addInputEventListener( dragHandler );
            
            _potentialEnergyControls = new PotentialEnergyControls( _chartNode );
            _potentialEnergyControls.addInputEventListener( dragHandler );
        }
        
        // Zoom buttons
        {
            // NOTE: Zoom both the chart and the XYPlotNodes!
            ZoomControl z1 = new ZoomControl( ZoomControl.VERTICAL, QTConstants.WAVE_FUNCTION_ZOOM_SPECS, QTConstants.DEFAULT_WAVE_FUNCTION_ZOOM_INDEX );
            z1.addPlot( _chart.getWaveFunctionPlot() );
            z1.addPlot( _waveFunctionPlot );
            _waveFunctionZoomControl = new PSwing( z1 );
            
            // NOTE: Zoom both the chart and the XYPlotNodes!
            ZoomControl z2 = new ZoomControl( ZoomControl.VERTICAL, QTConstants.PROBABILITY_DENSITY_ZOOM_SPECS, QTConstants.DEFAULT_PROBABILTY_DENSITY_ZOOM_INDEX );
            z2.addPlot( _chart.getProbabilityDensityPlot() );
            z2.addPlot( _probabilityDensityPlot );
            _probabilityDensityZoomControl = new PSwing( z2 );
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
        
        HelpPane helpPane = getDefaultHelpPane();

        HelpBalloon configureHelp = new HelpBalloon( helpPane, QTResources.getString( "help.configure" ), HelpBalloon.RIGHT_CENTER, 20 );
        helpPane.add( configureHelp );
        configureHelp.pointAt( _configureButton, _canvas );
        
        HelpBalloon dragHandleHelp = new HelpBalloon( helpPane, QTResources.getString( "help.dragHandle" ), HelpBalloon.RIGHT_TOP, 20 );
        helpPane.add( dragHandleHelp );
        dragHandleHelp.pointAt( _totalEnergyControl, _canvas );
        
        HelpBalloon zoomHelp = new HelpBalloon( helpPane, QTResources.getString( "help.zoom" ), HelpBalloon.LEFT_CENTER, 30 );
        helpPane.add( zoomHelp );
        zoomHelp.pointAt( _waveFunctionZoomControl, _canvas );
        
        HelpBalloon restartHelp = new HelpBalloon( helpPane, QTResources.getString( "help.restart" ), HelpBalloon.BOTTOM_CENTER, 80 );
        helpPane.add(  restartHelp );
        restartHelp.pointAt( _clockControls.getRestartComponent() );
        
        //----------------------------------------------------------------------------
        // Initialze the module state
        //----------------------------------------------------------------------------
       
        reset();
        updateCanvasLayout();
        _canvas.addComponentListener( listener );
    }
    
    //----------------------------------------------------------------------------
    // Canvas layout
    //----------------------------------------------------------------------------
    
    /*
     * Update the layout of nodes on the canvas.
     * This is called whenever the canvas size changes.
     */
    private void updateCanvasLayout() {
        
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
        
        // Plot and marker nodes (these exist if JFreeChart is not drawing dynamic elements)
        {
            if ( _energyPlotNode != null ) {
                _energyPlotNode.setOffset( 0, 0 );
                _energyPlotNode.setDataArea( energyPlotBounds );
            }
            if ( _waveFunctionPlotNode != null ) {
                _waveFunctionPlotNode.setOffset( 0, 0 );
                _waveFunctionPlotNode.setDataArea( waveFunctionPlotBounds );
            }
            if ( _probabilityDensityPlotNode != null ) {
                _probabilityDensityPlotNode.setOffset( 0, 0 );
                _probabilityDensityPlotNode.setDataArea( probabilityDensityPlotBounds );
            }
            if ( _energyMarkerNode != null ) {
                _energyMarkerNode.setOffset( 0, 0 );
                _energyMarkerNode.setDataArea( energyPlotBounds );
            }
            if ( _waveFunctionMarkerNode != null ) {
                _waveFunctionMarkerNode.setOffset( 0, 0 );
                _waveFunctionMarkerNode.setDataArea( waveFunctionPlotBounds );
            }
            if ( _probabilityDensityMarkerNode != null ) {
                _probabilityDensityMarkerNode.setOffset( 0, 0 );
                _probabilityDensityMarkerNode.setDataArea( probabilityDensityPlotBounds );
            }
        }
        
        // Drag handles
        {
            _totalEnergyControl.updateDragBounds();
            _potentialEnergyControls.updateDragBounds();
        }
        
        // dx (sample point spacing) for Plane Wave
        {
            Point2D p1 = _chartNode.nodeToEnergy( new Point2D.Double( 0, 0 ) );
            Point2D p2 = _chartNode.nodeToEnergy( new Point2D.Double( QTConstants.PIXELS_PER_SAMPLE_POINT_PLANE_WAVE, 0 ) );
            double dx = p2.getX() - p1.getX();
            _waveFunctionPlot.setDx( dx );
        }
        
        // dx (sample point spacing) for Wave Packet
        {
            Point2D p1 = _chartNode.nodeToEnergy( new Point2D.Double( 0, 0 ) );
            Point2D p2 = _chartNode.nodeToEnergy( new Point2D.Double( QTConstants.PIXELS_PER_SAMPLE_POINT_WAVE_PACKET, 0 ) );
            double dx = p2.getX() - p1.getX();
            _wavePacket.setDx( dx );
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
        
        // Reflection and transmission probabilities
        updateRtpLayout();
    }
    
    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------
    
    /**
     * Indicates the this module has help.
     * 
     * @return true
     */
    public boolean hasHelp() {
        return true;
    }
    
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
            
            _energyPlot.setWavePacket( _wavePacket );
            _energyPlot.setPlaneWave( _planeWave );
        }
        
        // Controls
        {
            _controlPanel.setPotentialType( PotentialType.SINGLE_BARRIER );
            _controlPanel.setShowValuesSelected( false );
            _controlPanel.setRtpSelected( false );
            _controlPanel.setRealSelected( true );
            _controlPanel.setImaginarySelected( false );
            _controlPanel.setMagnitudeSelected( false );
            _controlPanel.setPhaseSelected( false );
            _controlPanel.setIRView( IRView.SUM );
            _controlPanel.setDirection( Direction.LEFT_TO_RIGHT );
            _controlPanel.setWaveType( WaveType.PACKET );
            _controlPanel.setPacketWidth( QTConstants.DEFAULT_PACKET_WIDTH );
            _controlPanel.setPacketCenter( QTConstants.DEFAULT_PACKET_CENTER );
            ( (ZoomControl) _waveFunctionZoomControl.getComponent() ).setZoomIndex( QTConstants.DEFAULT_WAVE_FUNCTION_ZOOM_INDEX );
            ( (ZoomControl) _probabilityDensityZoomControl.getComponent() ).setZoomIndex( QTConstants.DEFAULT_PROBABILTY_DENSITY_ZOOM_INDEX );
        }
    }
    
    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------
    
    /**
     * Saves the module's configuration.
     * 
     * @return
     */
    public QTModuleConfig save() {
        
        QTModuleConfig config = new QTModuleConfig();
        
        // Clock
        config.setClockRunning( getClock().isRunning() );
        
        // Model
        config.saveTotalEnergy( _totalEnergy );
        config.setMinRegionWidth( _constantPotential.getMinRegionWidth() ); // assume the same for all potentials!
        config.saveConstantPotential( _constantPotential );
        config.saveStepPotential( _stepPotential );
        config.saveSingleBarrierPotential( _singleBarrierPotential );
        config.saveDoubleBarrierPotential( _doubleBarrierPotential );
        
        // Control panel
        config.savePotentialType( _controlPanel.getPotentialType() );
        config.setShowValuesSelected( _controlPanel.isShowValuesSelected() );
        config.setRtpSelected( _controlPanel.isRtpSelected() );
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
        
        // Zoom controls
        config.setWaveFunctionZoomIndex( ( (ZoomControl) _waveFunctionZoomControl.getComponent() ).getZoomIndex() );
        config.setProbabilityDensityZoomIndex( ( (ZoomControl) _probabilityDensityZoomControl.getComponent() ).getZoomIndex() );
    
        return config;
    }
    
    /**
     * Loads the module's configuration.
     * 
     * @param config
     */
    public void load( QTModuleConfig config ) {
        
        // Clock
        if ( isActive() ) {
            if ( config.isClockRunning() ) {
                getClock().start();
            }
            else {
                getClock().pause();
            }
        }
        
        // Model
        setTotalEnergy( config.loadTotalEnergy() );
        setConstantPotential( config.loadConstantPotential() );
        setStepPotential( config.loadStepPotential() );
        setSingleBarrierPotential( config.loadSingleBarrierPotential() );
        setDoubleBarrierPotential( config.loadDoubleBarrierPotential() );
        
        // Control panel
        _controlPanel.setPotentialType( config.loadPotentialType() );
        _controlPanel.setShowValuesSelected( config.isShowValuesSelected() );
        _controlPanel.setRtpSelected( config.isRtpSelected() );
        _controlPanel.setRealSelected( config.isRealSelected() );
        _controlPanel.setImaginarySelected( config.isImaginarySelected() );
        _controlPanel.setMagnitudeSelected( config.isMagnitudeSelected() );
        _controlPanel.setPhaseSelected( config.isPhaseSelected() );
        _controlPanel.setIRView( config.loadIRView() );
        _controlPanel.setDirection( config.loadDirection() );
        _controlPanel.setWaveType( config.loadWaveType() );
        _controlPanel.setPacketWidth( config.getPacketWidth() );
        _controlPanel.setPacketCenter( config.getPacketCenter() );
        
        // Zoom controls
        ( (ZoomControl) _waveFunctionZoomControl.getComponent() ).setZoomIndex( config.getWaveFunctionZoomIndex() );
        ( (ZoomControl) _probabilityDensityZoomControl.getComponent() ).setZoomIndex( config.getProbabilityDensityZoomIndex() );
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    private class EventListener extends ComponentAdapter implements ActionListener {

        /*
         * Redo the "play area" layout when the application frame is resized.
         */
        public void componentResized( ComponentEvent event ) {
            if ( event.getSource() == _canvas ) {
                updateCanvasLayout();
            }
        }
        
        /*
         * Handles buttons on the "play area".
         */
        public void actionPerformed( ActionEvent event ) {
            if ( event.getSource() == _configureButton.getComponent() ) {
                handleConfigureButton();
            }
            else if ( event.getSource() == _measureButton.getComponent() ) {
                handleMeasureButton();
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
            _configureEnergyDialog = new ConfigureEnergyDialog( getFrame(), this, _totalEnergy, _potentialEnergy, _wavePacket, _planeWave, _colorScheme );
            _configureEnergyDialog.show();
            // Dialog is model, so we stop here until the dialog is closed.
            _configureEnergyDialog.cleanup();
            _configureEnergyDialog = null;
            if ( clockRunning ) {
                getClock().start();
            }
        }
    }
    
    /*
     * When the Measure button is pressed, enabled measurement.
     * If we happen to measure in a region when E0<V0, total energy will be changed.
     * Setting the _isMeasuring flag prevent the clock from being reset if we have
     * to change total energy.
     */
    private void handleMeasureButton() {
        _isMeasuring = true;
        setMeasureEnabled( true );
        _isMeasuring = false;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the type of potential.
     * This selects the appropriate potential instance and makes 
     * it the one that is visible.
     * @param potentialType
     */
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
    
    /**
     * Sets the potential energy model.
     * @param potentialEnergy
     */
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

            _energyPlot.setPotentialEnergy( _potentialEnergy );
            _regionMarkerManager.setPotentialEnergy( _potentialEnergy );
            _controlPanel.setPotentialEnergy( _potentialEnergy );
            _potentialEnergyControls.setPotentialEnergy( _potentialEnergy );
            _planeWave.setPotentialEnergy( _potentialEnergy );
            _wavePacket.setPotentialEnergy( _potentialEnergy );
        }
        
        resetClock();
    }
    
    /**
     * Sets the constant potential instance.
     * @param potential
     */
    public void setConstantPotential( ConstantPotential potential ) {
        if ( _potentialEnergy == _constantPotential ) {
            setPotentialEnergy( potential );
        }
        else {
            _constantPotential = potential;
        }
    }
    
    /**
     * Sets the step potential instance.
     * @param potential
     */
    public void setStepPotential( StepPotential potential ) {
        if ( _potentialEnergy == _stepPotential ) {
            setPotentialEnergy( potential );
        }
        else {
            _stepPotential = potential;
        }
    }
    
    /**
     * Sets the single barrier potential instance.
     * @param potential
     */
    public void setSingleBarrierPotential( SingleBarrierPotential potential ) {
        if ( _potentialEnergy == _singleBarrierPotential ) {
            setPotentialEnergy( potential );
        }
        else {
            _singleBarrierPotential = potential;
        }
    }
    
    /**
     * Sets the double barrier potential instance.
     * @param potential
     */
    public void setDoubleBarrierPotential( DoubleBarrierPotential potential ) {
        if ( _potentialEnergy == _doubleBarrierPotential ) {
            setPotentialEnergy( potential );
        }
        else {
            _doubleBarrierPotential = potential;
        }
    }
    
    /**
     * Sets the total energy model.
     * @param totalEnergy
     */
    public void setTotalEnergy( TotalEnergy totalEnergy ) {
        
        if ( _totalEnergy != null ) {
            _totalEnergy.deleteObserver( this );
        }
        _totalEnergy = totalEnergy;
        _totalEnergy.addObserver( this );
        
        resetClock();

        _energyPlot.setTotalEnergy( _totalEnergy );
        _totalEnergyControl.setTotalEnergy( _totalEnergy );
        _planeWave.setTotalEnergy( _totalEnergy );
        _wavePacket.setTotalEnergy( _totalEnergy );
    }
    
    /**
     * Sets visibility of values on the energy drag handles.
     * @param visible
     */
    public void setValuesVisible( boolean visible ) {
        _totalEnergyControl.setValueVisible( visible );
        _potentialEnergyControls.setValuesVisible( visible );
    }
    
    /**
     * Are values visible on the energy drag handles?
     * @return
     */
    public boolean isValuesVisible() {
        return _totalEnergyControl.isValueVisible();
    }
    
    /**
     * Sets the wave type.
     * @param waveType
     */
    public void setWaveType( WaveType waveType ) {
        resetClock();
        
        if ( waveType == WaveType.PLANE ) {
            _planeWave.setEnabled( true );
            _wavePacket.setEnabled( false );
            _energyPlot.showPlaneWave();
            _waveFunctionPlot.setWave( _planeWave );
            _reflectionProbabilityNode.setWave( _planeWave );
            _transmissionProbabilityNode.setWave( _planeWave );
        }
        else {
            _planeWave.setEnabled( false );
            _wavePacket.setEnabled( true );
            _energyPlot.showWavePacket();
            _waveFunctionPlot.setWave( _wavePacket );
            _reflectionProbabilityNode.setWave( _wavePacket );
            _transmissionProbabilityNode.setWave( _wavePacket );
        }
    }
    
    /**
     * Sets visibility of the "real" view.
     * @param visible
     */
    public void setRealVisible( boolean visible ) {
        _waveFunctionPlot.setRealVisible( visible );
    }
   
    /**
     * Sets visibility of the "imaginary" view.
     * @param visible
     */
    public void setImaginaryVisible( boolean visible ) {
        _waveFunctionPlot.setImaginaryVisible( visible );
    }
    
    /**
     * Sets visibility of the "magnitude" view.
     * @param visible
     */
    public void setMagnitudeVisible( boolean visible ) {
        _waveFunctionPlot.setMagnitudeVisible( visible );
    }
    
    /**
     * Sets visibility of the "phase" view.
     * @param visible
     */
    public void setPhaseVisible( boolean visible ) {
        _waveFunctionPlot.setPhaseVisible( visible );
    }
    
    /**
     * Sets the wave direction.
     * @param direction
     */
    public void setDirection( Direction direction ) {
        _planeWave.setNotifyEnabled( false );
        _wavePacket.setNotifyEnabled( false );
        _planeWave.setDirection( direction );
        _wavePacket.setDirection( direction );
        resetClock();
        _planeWave.setNotifyEnabled( true );
        _wavePacket.setNotifyEnabled( true );
        updateRtpLayout();
    }
    
    /**
     * Sets the "incident/refected" view (sum or separate).
     * @param irView
     */
    public void setIRView( IRView irView ) {
        _waveFunctionPlot.setIRView( irView );
    }
    
    /**
     * Sets the wave packet's width.
     * @param width
     */
    public void setWavePacketWidth( double width ) {
        resetClock();
        _wavePacket.setWidth( width );
    }
    
    /**
     * Sets the wave packet's center.
     * @param center
     */
    public void setWavePacketCenter( double center ) {
        resetClock();
        _wavePacket.setCenter( center );
    }
    
    /**
     * Sets "measure" mode for the waves.
     * <p>
     * Temporarily pauses the clock so that we're guaranteed to see
     * the initial state of the wave.  For wave packets there can
     * be a race condition where the wave gets propogated before 
     * it's displayed if the clock is running.
     * 
     * @param enabled
     */
    public void setMeasureEnabled( boolean enabled ) {
        IClock clock = getClock();
        boolean clockWasRunning = false;
        if ( clock.isRunning() ) {
            clockWasRunning = true;
            clock.pause();
        }
        getClock().pause();
        _planeWave.setMeasureEnabled( enabled );
        _wavePacket.setMeasureEnabled( enabled );
        if ( clockWasRunning ) {
            getClock().start();
        }
    }
    
    /*
     * Resets the simulation clock.
     */
    private void resetClock() {
        getClock().resetSimulationTime();
    }
    
    /*
     * Does any house-keeping required when the simulation clock is reset.
     */
    private void handleClockReset() {
        setMeasureEnabled( false );
    }
    
    /**
     * Sets the color scheme used for this module.
     * 
     * @param colorScheme
     */
    public void setColorScheme( QTColorScheme colorScheme ) {
        _colorScheme = colorScheme;
        // Chart
        _chart.setColorScheme( colorScheme );
        // Plots, in case we're drawing them separately from the chart...
        _energyPlot.setColorScheme( colorScheme );
        _waveFunctionPlot.setColorScheme( colorScheme );
        _probabilityDensityPlot.setColorScheme( colorScheme );
        // Control panel legend...
        _controlPanel.setColorScheme( colorScheme );
        // Drag handles...
        _totalEnergyControl.setValueColor( colorScheme.getAnnotationColor() );
        _potentialEnergyControls.setValueColor( colorScheme.getAnnotationColor() );
        // Legend above the charts...
        _legend.setColorScheme( colorScheme );
        // Region markers...
        _regionMarkerManager.setMarkerColor( colorScheme.getRegionMarkerColor() );
        // Reflection and transmission probabilities...
        _reflectionProbabilityNode.setColorScheme( colorScheme );
        _transmissionProbabilityNode.setColorScheme( colorScheme );
    }
    
    /**
     * Makes Richardson algorithm controls visible.
     * The controls appear in a dialog.
     * 
     * @param visible
     */
    public void setRichardsonControlsVisible( boolean visible ) {
        if ( visible ) {
            if ( _richardsonDialog == null ) {
                IWavePacketSolver solver = _wavePacket.getSolver();
                if ( solver instanceof RichardsonSolver ) {
                    _richardsonDialog = new RichardsonControlsDialog( getFrame(), (RichardsonSolver) solver );
                    _richardsonDialog.show();
                    _richardsonDialog.addWindowListener( new WindowAdapter() {
                        public void windowClosing( WindowEvent event ) {
                            _richardsonDialog = null;
                        }
                        public void windowClosed( WindowEvent event ) {
                            _richardsonDialog = null;
                        }
                    } );
                }
            }
        }
        else {
            if ( _richardsonDialog != null ) {
                _richardsonDialog.dispose();
                _richardsonDialog = null;
            }
        }
    }
    
    public RichardsonSolver getRichardsonSolver() {
        RichardsonSolver richardsonSolver = null;
        IWavePacketSolver solver = _wavePacket.getSolver();
        if ( solver instanceof RichardsonSolver ) {
            richardsonSolver = (RichardsonSolver) solver;
        }
        return richardsonSolver;
    }
    
    public void setRtpVisible( boolean selected ) {
        _reflectionProbabilityNode.setVisible( selected );
        _transmissionProbabilityNode.setVisible( selected );
    }
    
    /*
     * Updates the layout (on the canvas) of reflection and transmission probabilities.
     * This happens when either the canvas size or wave direction is changed.
     */
    private void updateRtpLayout()
    {
        Rectangle2D probabilityDensityPlotBounds = _chartNode.localToGlobal( _chartNode.getProbabilityDensityPlotBounds() );
        PBounds pdZoomControlBounds = _probabilityDensityZoomControl.getFullBounds();
        
        Direction direction = _planeWave.getDirection();
        
        AbstractProbabilityNode leftNode = null;
        AbstractProbabilityNode rightNode = null;
        if ( direction == Direction.LEFT_TO_RIGHT ) {
            leftNode = _reflectionProbabilityNode;
            rightNode = _transmissionProbabilityNode;
        }
        else {
            leftNode = _transmissionProbabilityNode; 
            rightNode = _reflectionProbabilityNode;
        }
        
        double x, y;
        final double xmargin = 15;
        final double ymargin = 15;
        final double xFudge = 60;
        
        x = pdZoomControlBounds.getMaxX() + xmargin;
        y = probabilityDensityPlotBounds.getY() + ymargin;
        leftNode.setOffset( x, y );
        
        x = probabilityDensityPlotBounds.getX() + probabilityDensityPlotBounds.getWidth() - xFudge - xmargin;
        rightNode.setOffset( x, y );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Restarts the clock when energies change.
     * Ignore total energy changes when we are making a quantum measurement.
     */
    public void update( Observable o, Object arg ) {
        if ( o == _potentialEnergy || ( o == _totalEnergy && !_isMeasuring ) ) {
            resetClock();
        }
    }

}
