/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.module;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JButton;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.control.FluidControlPanel;
import edu.colorado.phet.opticaltweezers.control.OTClockControlPanel;
import edu.colorado.phet.opticaltweezers.control.PhysicsControlPanel;
import edu.colorado.phet.opticaltweezers.defaults.PhysicsDefaults;
import edu.colorado.phet.opticaltweezers.help.OTWiggleMe;
import edu.colorado.phet.opticaltweezers.model.*;
import edu.colorado.phet.opticaltweezers.persistence.OTConfig;
import edu.colorado.phet.opticaltweezers.view.*;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.help.HelpBalloon;
import edu.colorado.phet.piccolo.help.HelpPane;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * PhysicsModule is the "Physics of Tweezers" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PhysicsModule extends AbstractModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final boolean HAS_WIGGLE_ME = false;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private OTClock _clock;
    private OTModel _model;
    private Laser _laser;
    private Bead _bead;
    private Fluid _fluid;
    
    // View
    private PhetPCanvas _canvas;
    private PNode _rootNode;
    private GlassSlideNode _glassSlideNode;
    private LaserNode _laserNode;
    private BeadNode _beadNode;
    private ModelViewTransform _modelViewTransform;
    private PPath _beadDragBoundsNode;
    private PPath _laserDragBoundsNode;
    private OTRulerNode _rulerNode;
    private PPath _rulerDragBoundsNode;
    
    // Control
    private OTModelViewManager _modelViewManager;
    private PhysicsControlPanel _controlPanel;
    private OTClockControlPanel _clockControlPanel;
    private PhetPNode _returnBeadButtonWrapper;
    private FluidControlPanel _fluidControlPanel;
    private PhetPNode _fluidControlPanelWrapper;
    
    // Help
    private OTWiggleMe _wiggleMe;
    private boolean _wiggleMeInitialized = false;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public PhysicsModule() {
        super( SimStrings.get( "PhysicsModule.title" ), PhysicsDefaults.CLOCK, PhysicsDefaults.CLOCK_PAUSED );

        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------

        _clock = (OTClock) getClock();
        
        _model = new OTModel( _clock );
        
        _laser = new Laser( PhysicsDefaults.LASER_POSITION, 
                PhysicsDefaults.LASER_ORIENTATION, 
                PhysicsDefaults.LASER_DIAMETER, 
                PhysicsDefaults.LASER_WAVELENGTH, 
                PhysicsDefaults.LASER_POWER_RANGE.getDefault() );
        
        _bead = new Bead( PhysicsDefaults.BEAD_POSITION, 
                PhysicsDefaults.BEAD_ORIENTATION, 
                PhysicsDefaults.BEAD_DIAMETER );
        
        _fluid = new Fluid( PhysicsDefaults.FLUID_POSITION,
                PhysicsDefaults.FLUID_ORIENTATION,
                PhysicsDefaults.FLUID_WIDTH,
                PhysicsDefaults.FLUID_SPEED_RANGE, 
                PhysicsDefaults.FLUID_VISCOSITY_RANGE, 
                PhysicsDefaults.FLUID_TEMPERATURE_RANGE );

        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Piccolo canvas
        {
            _canvas = new PhetPCanvas( OTConstants.CANVAS_RENDERING_SIZE );
            _canvas.setBackground( OTConstants.CANVAS_BACKGROUND );
            setSimulationPanel( _canvas );

            _canvas.addComponentListener( new ComponentAdapter() {
                public void componentResized( ComponentEvent e ) {
                    // update the layout when the canvas is resized
                    updateCanvasLayout();
                }
            } );
        }

        // Root of our scene graph
        _rootNode = new PNode();
        _canvas.addWorldChild( _rootNode );

        // Model View transform
        _modelViewTransform = new ModelViewTransform( 0.5, 0, 0 );
        
        // Glass Slide
        _glassSlideNode = new GlassSlideNode( _fluid, _modelViewTransform );
        
        // Laser
        _laserDragBoundsNode = new PPath();
        _laserDragBoundsNode.setStroke( null );
        _laserNode = new LaserNode( _canvas, _laser, _modelViewTransform, PhysicsDefaults.LASER_POWER_RANGE, _laserDragBoundsNode );
        
        // Bead
        _beadDragBoundsNode = new PPath();
        _beadDragBoundsNode.setStroke( null );
        _beadNode = new BeadNode( _bead, _modelViewTransform, _beadDragBoundsNode );
        
        // Ruler
        _rulerDragBoundsNode = new PPath();
        _rulerDragBoundsNode.setStroke( new BasicStroke() );//XXX
        _rulerDragBoundsNode.setStrokePaint( Color.RED );//XXX
        _rulerNode = new OTRulerNode( 600 /* width */, _laser,_modelViewTransform, _rulerDragBoundsNode );
        
        // Layering order on the canvas (back-to-front)
        {
            _rootNode.addChild( _glassSlideNode );
            _rootNode.addChild( _laserNode );
            _rootNode.addChild( _laserDragBoundsNode );
            _rootNode.addChild( _beadNode );
            _rootNode.addChild( _beadDragBoundsNode );
            _rootNode.addChild( _rulerNode );
            _rootNode.addChild( _rulerDragBoundsNode );
        }
        
        //----------------------------------------------------------------------------
        // Model-View management
        //----------------------------------------------------------------------------
        
        _modelViewManager = new OTModelViewManager( _model );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------

        // Control Panel
        _controlPanel = new PhysicsControlPanel( this );
        setControlPanel( _controlPanel );
        
        // Clock controls
        _clockControlPanel = new OTClockControlPanel( (OTClock) getClock() );
        setClockControlPanel( _clockControlPanel );
        
        // Fluid controls
        _fluidControlPanel = new FluidControlPanel( _fluid, OTConstants.PLAY_AREA_CONTROL_FONT );
        _fluidControlPanelWrapper = new PhetPNode( new PSwing( _canvas, _fluidControlPanel ) );
        
        // "Return Bead" button
        JButton returnBeadButton = new JButton( SimStrings.get( "label.returnBead" ) );
        returnBeadButton.setFont( OTConstants.PLAY_AREA_CONTROL_FONT );
        returnBeadButton.setOpaque( false );
        returnBeadButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleReturnBeadButton();
            }
        } );
        _returnBeadButtonWrapper = new PhetPNode( new PSwing( _canvas, returnBeadButton ) );
        
        // Layering of controls on the canvas (back-to-front)
        {
            _rootNode.addChild( _fluidControlPanelWrapper );
            _rootNode.addChild( _returnBeadButtonWrapper );
        }

        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------

        HelpPane helpPane = getDefaultHelpPane();

        HelpBalloon configureHelp = new HelpBalloon( helpPane, "help item", HelpBalloon.RIGHT_CENTER, 20 );
        helpPane.add( configureHelp );
        configureHelp.pointAt( 300, 300 );
        
        // See initWiggleMe for Wiggle Me initialization.

        //----------------------------------------------------------------------------
        // Initialize the module state
        //----------------------------------------------------------------------------

        reset();
        updateCanvasLayout();
    }
    
    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------
    
    /**
     * Indicates that this module has help.
     * 
     * @return true
     */
    public boolean hasHelp() {
        return true;
    }
    
    //----------------------------------------------------------------------------
    // Canvas layout
    //----------------------------------------------------------------------------
    
    /*
     * Updates the layout of stuff on the canvas.
     */
    public void updateCanvasLayout() {

        Dimension2D worldSize = _canvas.getWorldSize();
        System.out.println( "PhysicsModule.updateCanvasLayout worldSize=" + worldSize );//XXX
        if ( worldSize.getWidth() == 0 || worldSize.getHeight() == 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        
        // Glass Slide, width fills the canvas
        _glassSlideNode.setCanvasWidth( worldSize.getWidth() );
        
        // Ruler width fills the canvas. adjust drag bounds for vertical dragging
        {
            _rulerNode.setCanvasWidth( worldSize.getWidth() );
            Rectangle2D globalDragBounds = new Rectangle2D.Double( 0, 0, worldSize.getWidth(), worldSize.getHeight() );
            Rectangle2D localDragBounds = _rulerDragBoundsNode.globalToLocal( globalDragBounds );
            _rulerDragBoundsNode.setPathTo( localDragBounds );
        }
        
        // Adjust drag bounds of bead, so it stays on the glass slide
        {
            // This percentage of the bead must remain visible
            final double m = 0.15;
            
            Rectangle2D sb = _glassSlideNode.getCenterGlobalBounds();
            Rectangle2D bb = _beadNode.getGlobalFullBounds();
            double x = sb.getX() - ( ( 1 - m ) * bb.getWidth() );
            double y = sb.getY();
            double w = sb.getWidth() + ( 2 * ( 1 - m ) * bb.getWidth() );
            double h = sb.getHeight();
            Rectangle2D globalDragBounds = new Rectangle2D.Double( x, y, w, h );
            Rectangle2D localDragBounds = _beadDragBoundsNode.globalToLocal( globalDragBounds );
            _beadDragBoundsNode.setPathTo( localDragBounds );
        }
        
        // Adjust drag bounds of laser, so it stays in canvas
        {
            // This percentage of the laser must remain visible
            final double m = 0.15;
            
            Rectangle2D sb = _glassSlideNode.getCenterGlobalBounds();
            Rectangle2D lb = _laserNode.getGlobalFullBounds();
            double xAdjust = ( 1 - m ) * lb.getWidth();
            double x = -xAdjust;
            double y = lb.getY();
            double w = sb.getWidth() + ( 2 * xAdjust );
            double h = lb.getHeight();
            Rectangle2D globalDragBounds = new Rectangle2D.Double( x, y, w, h );
            Rectangle2D localDragBounds = _laserDragBoundsNode.globalToLocal( globalDragBounds );
            _laserDragBoundsNode.setPathTo( localDragBounds );
            
            //XXX if laser is not visible, move it onto canvas?
        }
        
        // Fluid controls
        _fluidControlPanelWrapper.setOffset( 10, 280 ); //XXX
        
        // "Return Bead" button
        _returnBeadButtonWrapper.setOffset( 50, 230 );//XXX
        //XXX determine if bead is no longer visible

        if ( HAS_WIGGLE_ME ) {
            initWiggleMe();
        }
    }
    
    //----------------------------------------------------------------------------
    // Wiggle Me
    //----------------------------------------------------------------------------
    
    /*
     * Initializes a wiggle me
     */
    private void initWiggleMe() {
        if ( !_wiggleMeInitialized ) {
            
            // Create wiggle me, add to root node.
            String wiggleMeString = SimStrings.get( "wiggleMe.XXX" );  
            _wiggleMe = new OTWiggleMe( _canvas, wiggleMeString );
            _rootNode.addChild( _wiggleMe );
            
            // Animate from the upper-left to some point
            double x = 300;//XXX
            double y = 300;//XXX
            _wiggleMe.setOffset( 0, -100 );
            _wiggleMe.animateTo( x, y );
            
            // Clicking on the canvas makes the wiggle me go away.
            _canvas.addInputEventListener( new PBasicInputEventHandler() {
                public void mousePressed( PInputEvent event ) {
                    _wiggleMe.setEnabled( false );
                    _rootNode.removeChild( _wiggleMe );
                    _canvas.removeInputEventListener( this );
                    _wiggleMe = null;
                }
            } );
            
            _wiggleMeInitialized = true;
        }
    }

    //----------------------------------------------------------------------------
    // AbstractModule implementation
    //----------------------------------------------------------------------------
    
    public void reset() {
        
        // Model
        {
            // Clock
            if ( PhysicsDefaults.CLOCK_PAUSED ) {
                _clock.pause();
            }
            else {
                _clock.start();
            }
            _clock.setDt( PhysicsDefaults.CLOCK_DT_RANGE.getDefault() );
            
            // Bead
            _bead.setPosition( PhysicsDefaults.BEAD_POSITION );
            _bead.setOrientation( PhysicsDefaults.BEAD_ORIENTATION );
            
            // Laser
            _laser.setPosition( PhysicsDefaults.LASER_POSITION );
            _laser.setPower( PhysicsDefaults.LASER_POWER_RANGE.getDefault() );
            
            // Fluid
            _fluid.setSpeed( PhysicsDefaults.FLUID_SPEED_RANGE.getDefault() );
            _fluid.setViscosity( PhysicsDefaults.FLUID_VISCOSITY_RANGE.getDefault() );
            _fluid.setTemperature( PhysicsDefaults.FLUID_TEMPERATURE_RANGE.getDefault() );
        }
        
        // Control panel settings that are view-related
        {
            _controlPanel.setClockSpeed( PhysicsDefaults.CLOCK_DT_RANGE.getDefault() );
            _controlPanel.setElectricFieldSelected( PhysicsDefaults.ELECTRIC_FIELD_SELECTED );
            _controlPanel.setBeadChargesSelected( PhysicsDefaults.BEAD_CHARGES_SELECTED );
            _controlPanel.setAllChargesSelected( PhysicsDefaults.ALL_BEAD_CHARGES_SELECTED );
            _controlPanel.setTrapForceSelected( PhysicsDefaults.TRAP_FORCE_SELECTED );
            _controlPanel.setWholeBeadSelected( PhysicsDefaults.WHOLE_BEAD_SELECTED );
            _controlPanel.setFluidDragSelected( PhysicsDefaults.FLUID_DRAG_FORCE_SELECTED );
            _controlPanel.setBrownianForceSelected( PhysicsDefaults.BROWNIAN_FORCE_SELECTED );
            _controlPanel.setRulerSelected( PhysicsDefaults.RULER_SELECTED );
            _controlPanel.setPositionHistogramSelected( PhysicsDefaults.POSITION_HISTOGRAM_SELECTED );
            _controlPanel.setAdvancedVisible( PhysicsDefaults.ADVANCED_VISIBLE );
            _controlPanel.setFluidControlSelected( PhysicsDefaults.FLUID_CONTROLS_SELECTED );
            _controlPanel.setMomentumChangeSelected( PhysicsDefaults.MOMENTUM_CHANGE_MODEL_SELECTED );
            _controlPanel.setPotentialChartSelected( PhysicsDefaults.POTENTIAL_ENERGY_CHART_SELECTED );
            
            // Fluid controls
            _fluidControlPanelWrapper.setVisible( PhysicsDefaults.FLUID_CONTROLS_SELECTED );
        }
    }

    public void save( OTConfig appConfig ) {
        // TODO Auto-generated method stub
    }

    public void load( OTConfig appConfig ) {
        // TODO Auto-generated method stub
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setFluidControlsVisible( boolean visible ) {
        _fluidControlPanelWrapper.setVisible( visible );
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handleReturnBeadButton() {
        //XXX
        System.out.println( "handleReturnBeadButton" );//XXX
    }
}
