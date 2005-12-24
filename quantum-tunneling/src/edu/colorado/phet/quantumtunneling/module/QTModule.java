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
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;

import com.sun.corba.se.ActivationIDL._InitialNameServiceImplBase;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.control.ConfigureEnergyDialog;
import edu.colorado.phet.quantumtunneling.control.QTClockControls;
import edu.colorado.phet.quantumtunneling.control.QTControlPanel;
import edu.colorado.phet.quantumtunneling.enum.Direction;
import edu.colorado.phet.quantumtunneling.enum.IRView;
import edu.colorado.phet.quantumtunneling.enum.PotentialType;
import edu.colorado.phet.quantumtunneling.enum.WaveType;
import edu.colorado.phet.quantumtunneling.model.*;
import edu.colorado.phet.quantumtunneling.persistence.QTConfig;
import edu.colorado.phet.quantumtunneling.view.*;
import edu.umd.cs.piccolo.PNode;


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
    private static final Dimension CANVAS_RENDERING_SIZE = new Dimension( 1000, 1000 );
    private static final int CANVAS_BOUNDARY_STROKE_WIDTH = 1;
    private static final double LEGEND_SCALE = 1;
    private static final double CONFIGURE_BUTTON_SCALE = 1;
    
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
    private QTControlPanel _controlPanel;
    private ConfigureEnergyDialog _configureEnergyDialog;
    private TotalEnergyDragHandle _totalEnergyControl;
    private PotentialEnergyControls _potentialEnergyControls;
    
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
        
        // Configure button
        {
            JButton jButton = new JButton( SimStrings.get( "button.configureEnergy" ) );
            jButton.setOpaque( false );
            jButton.addActionListener( listener );
            _configureButton = new PSwing( _canvas, jButton );
        }
        
        // Energy graph legend
        { 
            _legend = new EnergyLegend();
        }
        
        // Combined chart
        {
            _chart = new QTCombinedChart();
            _chartNode = new QTCombinedChartNode( _chart );
        }

        // Drag handles
        {
            _totalEnergyControl = new TotalEnergyDragHandle( _chartNode );
            _totalEnergyControl.setValueVisible( QTConstants.SHOW_ENERGY_VALUES );
            _totalEnergyControl.setXAxisPosition( QTConstants.POSITION_RANGE.getUpperBound() - 1 );
            
            _potentialEnergyControls = new PotentialEnergyControls( _chartNode );
            _potentialEnergyControls.setValuesVisible( QTConstants.SHOW_ENERGY_VALUES );
        }
        
        // Add all the nodes to one parent node.
        {
            _parentNode = new PNode();
            
            _parentNode.addChild( _configureButton );
            _parentNode.addChild( _legend );
            _parentNode.addChild( _chartNode );
            _parentNode.addChild( _totalEnergyControl );
            _parentNode.addChild( _potentialEnergyControls );

            _canvas.addScreenChild( _parentNode );
        }       
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------
        
        // Control Panel
        _controlPanel = new QTControlPanel( this );
        setControlPanel( _controlPanel );
        
        // Clock Controls
        QTClockControls clockControls = new QTClockControls( getClock() );
        clockControls.setTimeScale( QTConstants.TIME_SCALE );
        clockControls.setTimeFormat( QTConstants.TIME_FORMAT );
        setClockControlPanel( clockControls );
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
        
        //----------------------------------------------------------------------------
        // Initialze the module state
        //----------------------------------------------------------------------------
       
        reset();
        layoutCanvas();
        _canvas.addComponentListener( listener );
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
        
        // Location and dimensions of charts
        final double chartWidth = _canvas.getWidth() - ( 2 * X_MARGIN );
        final double chartHeight = _canvas.getHeight() - ( legendHeight  + ( 2 * Y_MARGIN ) + Y_SPACING );
        
        // Legend
        {
            AffineTransform legendTransform = new AffineTransform();
            legendTransform.translate( X_MARGIN + 55, Y_MARGIN );
            legendTransform.scale( LEGEND_SCALE, LEGEND_SCALE );
            legendTransform.translate( 0, 0 ); // upper left
            _legend.setTransform( legendTransform );
        }
        
        // Configure button
        {
            AffineTransform configureTransform = new AffineTransform();
            configureTransform.translate( X_MARGIN + chartWidth, ( Y_MARGIN + legendHeight + Y_SPACING ) / 2 );
            configureTransform.scale( CONFIGURE_BUTTON_SCALE, CONFIGURE_BUTTON_SCALE );
            configureTransform.translate( -_configureButton.getWidth(), -_configureButton.getHeight() / 2 ); // registration point = right center
            _configureButton.setTransform( configureTransform );
        }
        
        // Charts
        {
            _chartNode.setBounds( 0, 0, chartWidth, chartHeight );
            AffineTransform chartTransform = new AffineTransform();
            chartTransform.translate( X_MARGIN, Y_MARGIN + legendHeight + Y_SPACING );
            chartTransform.translate( 0, 0 ); // registration point @ upper left
            _chartNode.setTransform( chartTransform );
            _chartNode.updateChartRenderingInfo();
        }

        // Drag handles
        {
            _totalEnergyControl.updateDragBounds();
            _potentialEnergyControls.updateDragBounds();
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
            _controlPanel.setWaveType( WaveType.PLANE );
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
        }
    }
    
    /*
     * When the "Configure Energy" button is pressed, open a dialog.
     */
    private void handleConfigureButton() {
        if ( _configureEnergyDialog == null ) {
//            setWaitCursorEnabled( true );
            WaveType waveType = _controlPanel.getWaveType();
            _configureEnergyDialog = new ConfigureEnergyDialog( getFrame(), this, _totalEnergy, _potentialEnergy, waveType );
            _configureEnergyDialog.addWindowListener( new WindowAdapter() {
                // User pressed the closed button in the window dressing
                public void windowClosing( WindowEvent event ) {
                    _configureEnergyDialog.cleanup();
                    _configureEnergyDialog = null;
                }
                // User pressed the close button in the dialog's action area
                public void windowClosed( WindowEvent event ) {
                    _configureEnergyDialog.cleanup();
                    _configureEnergyDialog = null;
                }
            } );
            _configureEnergyDialog.show();
//            setWaitCursorEnabled( false );
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
        
        restartClock();
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
        
        restartClock();

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
        restartClock();
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
        restartClock();
        _planeWave.setNotifyEnabled( true );
        _wavePacket.setNotifyEnabled( true );
    }
    
    public void setIRView( IRView irView ) {
        _chart.getWaveFunctionPlot().setIRView( irView );
    }
    
    public void setWavePacketWidth( double width ) {
        restartClock();
        _wavePacket.setWidth( width );
    }
    
    public void setWavePacketCenter( double center ) {
        restartClock();
        _wavePacket.setCenter( center );
    }
    
    public void measure() {
        //XXX
    }
    
    private void restartClock() {
        getClock().resetSimulationTime();
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Restarts the clock when energies change.
     */
    public void update( Observable o, Object arg ) {
        if ( o == _potentialEnergy || o == _totalEnergy ) {
            restartClock();
        }     
    }

}
