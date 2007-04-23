/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.module;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.charts.*;
import edu.colorado.phet.opticaltweezers.control.OTClockControlPanel;
import edu.colorado.phet.opticaltweezers.control.PhysicsControlPanel;
import edu.colorado.phet.opticaltweezers.defaults.PhysicsDefaults;
import edu.colorado.phet.opticaltweezers.dialog.FluidControlDialog;
import edu.colorado.phet.opticaltweezers.help.OTWiggleMe;
import edu.colorado.phet.opticaltweezers.model.*;
import edu.colorado.phet.opticaltweezers.persistence.OTConfig;
import edu.colorado.phet.opticaltweezers.view.*;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
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
    private PhysicsModel _model;
    
    // View
    private PhetPCanvas _canvas;
    private PNode _rootNode;
    private FluidNode _fluidNode;
    private LaserNode _laserNode;
    private BeadNode _beadNode;
    private PPath _beadDragBoundsNode;
    private PPath _laserDragBoundsNode;
    private OTRulerNode _rulerNode;
    private PPath _rulerDragBoundsNode;
    private PositionHistogramChartNode _positionHistogramChartNode;
    private PotentialEnergyChartNode _potentialEnergyChartNode;
    private TrapForceNode _trapForceNode;
    
    // Control
    private PhysicsControlPanel _controlPanel;
    private OTClockControlPanel _clockControlPanel;
    private PSwing _returnBeadButtonWrapper;
    private FluidControlDialog _fluidControlDialog;
    
    // Help
    private OTWiggleMe _wiggleMe;
    private boolean _wiggleMeInitialized = false;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public PhysicsModule() {
        super( OTResources.getString( "title.physicsOfTweezers" ), PhysicsDefaults.CLOCK, PhysicsDefaults.CLOCK_PAUSED );

        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------

        OTClock clock = (OTClock) getClock();
         
        _model = new PhysicsModel( clock );
        
        Fluid fluid = _model.getFluid();
        Laser laser = _model.getLaser();
        Bead bead = _model.getBead();
        ModelViewTransform modelViewTransform = _model.getModelViewTransform();
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Piccolo canvas
        {
            _canvas = new PhetPCanvas( PhysicsDefaults.VIEW_SIZE );
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
        
        // Fluid
        _fluidNode = new FluidNode( fluid, modelViewTransform );
        
        // Laser
        _laserDragBoundsNode = new PPath();
        _laserDragBoundsNode.setStroke( null );
        _laserNode = new LaserNode( laser, modelViewTransform, _laserDragBoundsNode );
        
        // Bead
        _beadDragBoundsNode = new PPath();
        _beadDragBoundsNode.setStroke( null );
        _beadNode = new BeadNode( bead, modelViewTransform, _beadDragBoundsNode );
        _beadNode.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent event ) {
                if ( event.getPropertyName().equals( PNode.PROPERTY_TRANSFORM ) ) {
                   updateReturnBeadButtonVisibility();
                }
            }
        });
        
        // Trap Force
        _trapForceNode = new TrapForceNode( laser, bead, modelViewTransform );
        
        // Ruler
        _rulerDragBoundsNode = new PPath();
        _rulerDragBoundsNode.setStroke( null );
        _rulerNode = new OTRulerNode( laser, modelViewTransform, _rulerDragBoundsNode );
        
        // Position Histogram chart
        PositionHistogramPlot positionHistogramPlot = new PositionHistogramPlot();
        PositionHistogramChart positionHistogramChart = new PositionHistogramChart( positionHistogramPlot );
        _positionHistogramChartNode = new PositionHistogramChartNode( positionHistogramChart, laser, bead, clock );
        _positionHistogramChartNode.addCloseListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                _positionHistogramChartNode.setVisible( false );
                _controlPanel.setPositionHistogramSelected( false );
            }
        });
        
        // Potential Energy chart
        PotentialEnergyPlot potentialEnergyPlot = new PotentialEnergyPlot();
        PotentialEnergyChart potentialEnergyChart = new PotentialEnergyChart( potentialEnergyPlot );
        _potentialEnergyChartNode = new PotentialEnergyChartNode( _canvas, potentialEnergyChart );
        _potentialEnergyChartNode.addCloseListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                _potentialEnergyChartNode.setVisible( false );
                _controlPanel.setPotentialChartSelected( false );
            }
        });
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------

        // Control Panel
        _controlPanel = new PhysicsControlPanel( this );
        setControlPanel( _controlPanel );
        
        // Clock controls
        _clockControlPanel = new OTClockControlPanel( (OTClock) getClock() );
        _clockControlPanel.setTimeDisplayPattern( PhysicsDefaults.CLOCK_DISPLAY_PATTERN );
        setClockControlPanel( _clockControlPanel );
        
        // "Return Bead" button
        JButton returnBeadButton = new JButton( OTResources.getString( "button.returnBead" ) );
        Font font = new Font( OTConstants.DEFAULT_FONT_NAME, Font.BOLD, 18 );
        returnBeadButton.setFont( font );
        returnBeadButton.setOpaque( false );
        returnBeadButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleReturnBeadButton();
            }
        } );
        _returnBeadButtonWrapper = new PSwing( returnBeadButton );
        
        // Activity that flashes the bead whenever it's visible
        // If the button is made visible, flash it's text red for 10 seconds.
        long duration = -1; // forever
        long step = 500; // every 1/2 second
        long startTime = System.currentTimeMillis(); // now
        PActivity flash = new PActivity( duration, step, startTime ) {
            boolean fRed = true;
            protected void activityStep( long elapsedTime ) {
                if ( _returnBeadButtonWrapper.getVisible() ) {
                    super.activityStep( elapsedTime );
                    if ( fRed ) {
                        _returnBeadButtonWrapper.getComponent().setForeground( Color.RED );
                    }
                    else {
                        _returnBeadButtonWrapper.getComponent().setForeground( Color.BLACK );
                    }
                    fRed = !fRed;
                }
            }
        };
        _canvas.getRoot().addActivity( flash );
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------

        // See initWiggleMe for Wiggle Me initialization.

        //----------------------------------------------------------------------------
        // Layering order of nodes on the canvas
        //----------------------------------------------------------------------------

        _rootNode.addChild( _fluidNode );
        _rootNode.addChild( _laserNode );
        _rootNode.addChild( _laserDragBoundsNode );
        _rootNode.addChild( _beadNode );
        _rootNode.addChild( _beadDragBoundsNode );
        _rootNode.addChild( _trapForceNode );
        _rootNode.addChild( _positionHistogramChartNode );
        _rootNode.addChild( _potentialEnergyChartNode );
        _rootNode.addChild( _rulerNode );
        _rootNode.addChild( _rulerDragBoundsNode );
        _rootNode.addChild( _returnBeadButtonWrapper );

        //----------------------------------------------------------------------------
        // Initialize the module state
        //----------------------------------------------------------------------------

        resetAll();
        updateCanvasLayout();
    }
   
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    public OTClock getOTClock() {
        return _model.getClock();
    }
    
    public void setFluidControlsVisible( boolean visible ) {
        if ( !visible ) {
            if ( _fluidControlDialog != null ) {
                _fluidControlDialog.dispose();
                _fluidControlDialog = null;
            }
        }
        else {
            JFrame parentFrame = getFrame();
            Fluid fluid = _model.getFluid();
            _fluidControlDialog = new FluidControlDialog( parentFrame, OTConstants.CONTROL_PANEL_CONTROL_FONT, fluid );
            _fluidControlDialog.addWindowListener( new WindowAdapter() {
                // called when the close button in the dialog's window dressing is clicked
                public void windowClosing( WindowEvent e ) {
                    _fluidControlDialog.dispose();
                }
                // called by JDialog.dispose
                public void windowClosed( WindowEvent e ) {
                    _fluidControlDialog = null;
                    _controlPanel.setFluidControlSelected( false ); 
                }
            });
            // Position a the left-center of the main frame
            Point p = parentFrame.getLocationOnScreen();
            _fluidControlDialog.setLocation( (int)p.getX() + 10, (int)p.getY() + ( ( parentFrame.getHeight() - _fluidControlDialog.getHeight() ) / 2 ) );
            _fluidControlDialog.show();
        }
    }
    
    public void setRulerVisible( boolean visible ) {
        _rulerNode.setVisible( visible );
    }
    
    public void setPositionHistogramChartVisible( boolean visible ) {
        _positionHistogramChartNode.setVisible( visible );
    }
    
    public void setPotentialEnergyChartVisible( boolean visible ) {
        _potentialEnergyChartNode.setVisible( visible );
    }
    
    public void setTrapForceVisible( boolean visible ) {
        _trapForceNode.setVisible( visible );
    }
    
    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------
    
    /**
     * Indicates whether this module has help.
     * 
     * @return true
     */
    public boolean hasHelp() {
        return false;
    }
    
    //----------------------------------------------------------------------------
    // Canvas layout
    //----------------------------------------------------------------------------
    
    /*
     * Updates the layout of stuff on the canvas.
     */
    public void updateCanvasLayout() {

        double x = 0;
        double y = 0;
        double w = 0;
        double h = 0;
        
        Dimension2D worldSize = _canvas.getWorldSize();
        System.out.println( "PhysicsModule.updateCanvasLayout worldSize=" + worldSize );//XXX
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        
        // Fluid width adjusts to fill the canvas.
        _fluidNode.setWorldSize( worldSize );
        
        // Ruler width adjusts to fill the canvas.
        _rulerNode.setWorldSize( worldSize );
        
        // Position Histogram chart resizes to fill the canvas.
        _positionHistogramChartNode.setChartSize( worldSize.getWidth(), 200 );
        _positionHistogramChartNode.updateChartRenderingInfo();
        
        // Potential Energy chart resizes to fill the canvas.
        _potentialEnergyChartNode.setChartSize( worldSize.getWidth(), 250 );
        _potentialEnergyChartNode.updateChartRenderingInfo();

        // Adjust drag bounds of bead, so it stays in the fluid
        {
            // This percentage of the bead must remain visible
            final double m = 0.15;
            
            Rectangle2D sb = _fluidNode.getCenterGlobalBounds();
            Rectangle2D bb = _beadNode.getGlobalFullBounds();
            x = sb.getX() - ( ( 1 - m ) * bb.getWidth() );
            y = sb.getY();
            w = sb.getWidth() + ( 2 * ( 1 - m ) * bb.getWidth() );
            h = sb.getHeight();
            x -= 500;//XXX test Return Bead button
            w += 1000;//XXX test Return Bead button
            Rectangle2D globalDragBounds = new Rectangle2D.Double( x, y, w, h );
            Rectangle2D localDragBounds = _beadDragBoundsNode.globalToLocal( globalDragBounds );
            _beadDragBoundsNode.setPathTo( localDragBounds );
        }
        
        // "Return Bead" button
        {
            // center on canvas
            x = ( worldSize.getWidth() - _returnBeadButtonWrapper.getFullBounds().getWidth() ) / 2;
            y = ( worldSize.getHeight() - _returnBeadButtonWrapper.getFullBounds().getHeight() ) / 2;
            _returnBeadButtonWrapper.setOffset( x, y );
            
            // If the bead is outside the bounds of the canvas, make the "Return Bead" button visible.
            updateReturnBeadButtonVisibility();
        }
        
        // Adjust drag bounds of laser, so it stays in canvas
        {
            // This percentage of the laser must remain visible
            final double m = 0.15;
            
            Rectangle2D sb = _fluidNode.getCenterGlobalBounds();
            Rectangle2D lb = _laserNode.getGlobalFullBounds();
            double xAdjust = ( 1 - m ) * lb.getWidth();
            x = -xAdjust;
            y = lb.getY();
            w = sb.getWidth() + ( 2 * xAdjust );
            h = lb.getHeight();
            Rectangle2D globalDragBounds = new Rectangle2D.Double( x, y, w, h );
            Rectangle2D localDragBounds = _laserDragBoundsNode.globalToLocal( globalDragBounds );
            _laserDragBoundsNode.setPathTo( localDragBounds );
            
            // If laser is not visible, move it to center of canvas
            Laser laser = _model.getLaser();
            Rectangle2D worldBounds = new Rectangle2D.Double( 0, 0, worldSize.getWidth(), worldSize.getHeight() );
            Rectangle2D laserBounds = _laserNode.getFullBounds();
            if ( !worldBounds.intersects( laserBounds ) ) {
                ModelViewTransform modelViewTransform = _model.getModelViewTransform();
                double xModel = modelViewTransform.viewToModel( worldSize.getWidth() / 2 );
                double yModel = laser.getPositionRef().getY();
                laser.setPosition( xModel, yModel );
            }
        }
        
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
            _wiggleMe = new OTWiggleMe( _canvas, OTResources.getString( "label.wiggleMe" ) );
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
    
    public void resetAll() {
        
        // Model
        {
            // Clock
            OTClock clock = _model.getClock();
            clock.setPaused( PhysicsDefaults.CLOCK_PAUSED );
            clock.setDt( PhysicsDefaults.CLOCK_DT_RANGE.getDefault() );
            
            // Bead
            Bead bead = _model.getBead();
            bead.setPosition( PhysicsDefaults.BEAD_POSITION );
            bead.setOrientation( PhysicsDefaults.BEAD_ORIENTATION );
            
            // Laser
            Laser laser = _model.getLaser();
            laser.setPosition( PhysicsDefaults.LASER_POSITION );
            laser.setPower( PhysicsDefaults.LASER_POWER_RANGE.getDefault() );
            
            // Fluid
            Fluid fluid = _model.getFluid();
            fluid.setSpeed( PhysicsDefaults.FLUID_SPEED_RANGE.getDefault() );
            fluid.setViscosity( PhysicsDefaults.FLUID_VISCOSITY_RANGE.getDefault() );
            fluid.setTemperature( PhysicsDefaults.FLUID_TEMPERATURE_RANGE.getDefault() );
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
            setFluidControlsVisible( PhysicsDefaults.FLUID_CONTROLS_SELECTED );
        }
    }

    public void save( OTConfig appConfig ) {
        // TODO Auto-generated method stub
    }

    public void load( OTConfig appConfig ) {
        // TODO Auto-generated method stub
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handleReturnBeadButton() {
        System.out.println( "handleReturnBeadButton" );//XXX
        Rectangle2D b = _returnBeadButtonWrapper.getFullBounds().getBounds();
        double x = b.getX() + ( b.getWidth() / 2 );
        double y = b.getY() + ( b.getHeight() / 2 );
        ModelViewTransform modelViewTransform = _model.getModelViewTransform();
        Point2D p = modelViewTransform.viewToModel( x, y );
        _model.getBead().setPosition( p );
        _returnBeadButtonWrapper.setVisible( false );
        _returnBeadButtonWrapper.setPickable( false );
        _returnBeadButtonWrapper.setChildrenPickable( false );
    }
    
    private void updateReturnBeadButtonVisibility() {
        
        Dimension2D worldSize = _canvas.getWorldSize();
        Rectangle2D worldBounds = new Rectangle2D.Double( 0, 0, worldSize.getWidth(), worldSize.getHeight() );
        Rectangle2D beadBounds = _beadNode.getFullBounds();
        
        //XXX using intersects is a little dodgy since the bead is a circle
        _returnBeadButtonWrapper.setVisible( !worldBounds.intersects( beadBounds ) );
        _returnBeadButtonWrapper.setPickable( _returnBeadButtonWrapper.getVisible() );
        _returnBeadButtonWrapper.setChildrenPickable( _returnBeadButtonWrapper.getVisible() );
    }
}
