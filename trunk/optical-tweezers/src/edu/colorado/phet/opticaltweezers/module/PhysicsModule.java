/* Copyright 2006, University of Colorado */

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
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTDefaults;
import edu.colorado.phet.opticaltweezers.control.OTClockControlPanel;
import edu.colorado.phet.opticaltweezers.control.PhysicsControlPanel;
import edu.colorado.phet.opticaltweezers.help.OTWiggleMe;
import edu.colorado.phet.opticaltweezers.model.OTClock;
import edu.colorado.phet.opticaltweezers.model.OTModel;
import edu.colorado.phet.opticaltweezers.persistence.OTConfig;
import edu.colorado.phet.opticaltweezers.view.OTModelViewManager;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.help.HelpBalloon;
import edu.colorado.phet.piccolo.help.HelpPane;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

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

    // Control panels
    private PhysicsControlPanel _controlPanel;
    private OTClockControlPanel _clockControlPanel;

    private OTModel _model;
    
    private OTWiggleMe _wiggleMe;
    private boolean _wiggleMeInitialized = false;
    
    private OTModelViewManager _modelViewManager;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public PhysicsModule() {
        super( SimStrings.get( "PhysicsModule.title" ), new OTClock(), !OTDefaults.CLOCK_RUNNING /* startsPaused */ );

        // hide the PhET logo
        setLogoPanel( null );

        // Fonts
        int jComponentFontSize = SimStrings.getInt( "jcomponent.font.size", OTConstants.DEFAULT_FONT_SIZE );
        Font jComponentFont = new Font( OTConstants.DEFAULT_FONT_NAME, OTConstants.DEFAULT_FONT_STYLE, jComponentFontSize );
        
        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------

        IClock clock = getClock();
        
        // Model
        _model = new OTModel( clock );

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

        
        // Layering order on the canvas (back-to-front)
        {
//            _rootNode.addChild(...);
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
//        System.out.println( "HAModule.updateCanvasLayout worldSize=" + worldSize );//XXX
        if ( worldSize.getWidth() == 0 || worldSize.getHeight() == 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        
        // reusable (x,y) coordinates, for setting offsets
        double x, y;

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
        // TODO Auto-generated method stub
    }

    public void save( OTConfig appConfig ) {
        // TODO Auto-generated method stub
    }

    public void load( OTConfig appConfig ) {
        // TODO Auto-generated method stub
    }
}
