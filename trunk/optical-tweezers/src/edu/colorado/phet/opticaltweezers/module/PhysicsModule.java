/* Copyright 2007, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticaltweezers.module;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Dimension2D;

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
import edu.colorado.phet.piccolo.help.HelpBalloon;
import edu.colorado.phet.piccolo.help.HelpPane;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * PhysicsModule is the "Physics of Tweezers" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PhysicsModule extends AbstractModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final boolean HAS_WIGGLE_ME = false;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private PhetPCanvas _canvas;
    private PNode _rootNode;
    
    // Model
    private OTModel _model;
    private Laser _laser;
    private Bead _bead;
    private Fluid _fluid;
    private GlassSlide _glassSlide;
    
    // View
    private GlassSlideNode _glassSlideNode;
    private LaserNode _laserNode;
    private BeadNode _beadNode;
    private ModelViewTransform _modelViewTransform;
    
    // Control
    private OTModelViewManager _modelViewManager;
    private PhysicsControlPanel _controlPanel;
    private OTClockControlPanel _clockControlPanel;
    private PSwing _returnBeadButtonWrapper;
    private FluidControlPanel _fluidControlPanel;
    private PSwing _fluidControlPanelWrapper;
    
    // Help
    private OTWiggleMe _wiggleMe;
    private boolean _wiggleMeInitialized = false;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public PhysicsModule() {
        super( SimStrings.get( "PhysicsModule.title" ), new OTClock(), !PhysicsDefaults.CLOCK_RUNNING /* startsPaused */ );

        // hide the PhET logo
        setLogoPanel( null );
        
        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------

        IClock clock = getClock();
        
        _model = new OTModel( clock );
        
        _laser = new Laser( PhysicsDefaults.LASER_POSITION, 
                PhysicsDefaults.LASER_ORIENTATION, 
                PhysicsDefaults.LASER_WIDTH, 
                PhysicsDefaults.LASER_WAVELENGTH, 
                PhysicsDefaults.LASER_POWER_RANGE.getDefault() );
        
        _bead = new Bead( PhysicsDefaults.BEAD_POSITION, 
                PhysicsDefaults.BEAD_ORIENTATION, 
                PhysicsDefaults.BEAD_DIAMETER );
        
        _fluid = new Fluid( PhysicsDefaults.FLUID_VELOCITY_RANGE.getDefault(), 
                PhysicsDefaults.FLUID_VISCOSITY_RANGE.getDefault(), 
                PhysicsDefaults.FLUID_TEMPERATURE_RANGE.getDefault() );
        
        _glassSlide = new GlassSlide( PhysicsDefaults.GLASS_SLIDE_POSITION,
                PhysicsDefaults.GLASS_SLIDE_ORIENTATION,
                PhysicsDefaults.GLASS_SLIDE_HEIGHT,
                PhysicsDefaults.GLASS_SLIDE_EDGE_HEIGHT );

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
        _glassSlideNode = new GlassSlideNode( _glassSlide, _modelViewTransform );
        
        // Laser
        _laserNode = new LaserNode( _canvas, _laser, PhysicsDefaults.LASER_POWER_RANGE );
        
        // Bead
        _beadNode = new BeadNode( _bead, _modelViewTransform );
        
        // Layering order on the canvas (back-to-front)
        {
            _rootNode.addChild( _glassSlideNode );
            _rootNode.addChild( _laserNode );
            _rootNode.addChild( _beadNode );
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
        _fluidControlPanel = new FluidControlPanel( OTConstants.PLAY_AREA_CONTROL_FONT,
                PhysicsDefaults.FLUID_VELOCITY_RANGE,
                PhysicsDefaults.FLUID_VISCOSITY_RANGE,
                PhysicsDefaults.FLUID_TEMPERATURE_RANGE );
        _fluidControlPanelWrapper = new PSwing( _canvas, _fluidControlPanel );
        
        // "Return Bead" button
        JButton returnBeadButton = new JButton( SimStrings.get( "label.returnBead" ) );
        returnBeadButton.setFont( OTConstants.PLAY_AREA_CONTROL_FONT );
        returnBeadButton.setOpaque( false );
        returnBeadButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleReturnBeadButton();
            }
        } );
        _returnBeadButtonWrapper = new PSwing( _canvas, returnBeadButton );
        
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
    
    /**
     * Determines the visible bounds of the canvas in world coordinates.
     */ 
    public Dimension getWorldSize() {
        Dimension2D dim = new PDimension( _canvas.getWidth(), _canvas.getHeight() );
        _canvas.getPhetRootNode().screenToWorld( dim ); // this modifies dim!
        Dimension worldSize = new Dimension( (int) dim.getWidth(), (int) dim.getHeight() );
        return worldSize;
    }
    
    /*
     * Updates the layout of stuff on the canvas.
     */
    public void updateCanvasLayout() {

        Dimension worldSize = getWorldSize();
        System.out.println( "HAModule.updateCanvasLayout worldSize=" + worldSize );//XXX
        if ( worldSize.getWidth() == 0 || worldSize.getHeight() == 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        
        // reusable (x,y) coordinates, for setting offsets
        double x, y;
        
        // Glass Slide, width fills the canvas
        _glassSlideNode.setCanvasWidth( worldSize.getWidth() );
        
        // Laser
        _laserNode.setOffset( 200, 600 ); //XXX get postion from model
        
        // Bead
        _beadNode.setOffset( 400, 300 ); //XXX get position from model
        
        // Fluid controls
        _fluidControlPanelWrapper.setOffset( 10, 280 ); //XXX
        
        // "Return Bead" button
        _returnBeadButtonWrapper.setOffset( 50, 230 );//XXX

        if ( HAS_WIGGLE_ME ) {
            initWiggleMe();
        }
    }
    
    //----------------------------------------------------------------------------
    // Wiggle Me
    //----------------------------------------------------------------------------
    
    /*
     * Initializes a wiggle me that points to the gun on/off button.
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
        
        // Control panel
        _controlPanel.setSlowSpeedSelected( PhysicsDefaults.SLOW_SPEED_SELECTED );
        _controlPanel.setSlowSpeed( PhysicsDefaults.SLOW_SPEED );
        _controlPanel.setFastSpeed( PhysicsDefaults.FAST_SPEED );
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
        _fluidControlPanel.getVelocityControl().setValue( PhysicsDefaults.FLUID_VELOCITY_RANGE.getDefault() );
        _fluidControlPanel.getViscosityControl().setValue( PhysicsDefaults.FLUID_VISCOSITY_RANGE.getDefault() );
        _fluidControlPanel.getTemperatureControl().setValue( PhysicsDefaults.FLUID_TEMPERATURE_RANGE.getDefault() );
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
