/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.module;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.control.*;
import edu.colorado.phet.hydrogenatom.model.HAClock;
import edu.colorado.phet.hydrogenatom.view.HALightNode;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.piccolo.help.HelpPane;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;


public class HAModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Dimension CANVAS_RENDERING_SIZE = new Dimension( 750, 750 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PhetPCanvas _canvas;
    private PNode _rootNode;
    
    private LightOnOffControl _lightOnOffControl;
    private LightSourceControl _lightSourceControl;
    private LightIntensityControl _lightIntensityControl;
    private HAClockControls _clockControls;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public HAModule() {
        super( SimStrings.get( "HAModule.title" ), new HAClock() );
        
        // hide the PhET logo
        setLogoPanel( null );
        
        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------
        
        // Piccolo canvas
        {
            _canvas = new PhetPCanvas( CANVAS_RENDERING_SIZE );
            _canvas.setBackground( HAConstants.CANVAS_BACKGROUND );
            setSimulationPanel( _canvas );
        }
        
        // Root of our scene graph
        {
            _rootNode = new PNode();
            _canvas.addWorldChild( _rootNode );
        }
        
        // Gun
        {
            // controls
            _lightOnOffControl = new LightOnOffControl();
            _lightSourceControl = new LightSourceControl();
            _lightSourceControl.setButtonFont( HAConstants.CONTROL_FONT );
            _lightIntensityControl = new LightIntensityControl();
            
            // convert Swing controls to PNodes
            PNode lightSourceNode = new PSwing( _canvas, _lightSourceControl );
            PNode lightIntensityNode = new PSwing( _canvas, _lightIntensityControl );
            
            // group everything related to light source
            HALightNode gunNode = new HALightNode( _lightOnOffControl, lightSourceNode, lightIntensityNode );
            gunNode.setOffset( 50, 200 );
            _rootNode.addChild( gunNode );
        }
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------
        
        // Clock controls
        {
            _clockControls = new HAClockControls( (HAClock) getClock() );
            setClockControlPanel( _clockControls );
        }
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
        
        if ( hasHelp() ) {
            HelpPane helpPane = getDefaultHelpPane();
            
            //XXX add help items
        }
        
        //----------------------------------------------------------------------------
        // Initialze the module state
        //----------------------------------------------------------------------------
       
        _canvas.addComponentListener( new LayoutListener() );
        
        reset();
        layoutCanvas();

    }
    
    //----------------------------------------------------------------------------
    //
    //----------------------------------------------------------------------------
    
    private void reset() {
        //XXX
    }
    
    private void layoutCanvas() {
        //XXX
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    /*
     * Fixes the "play area" layout when the window size changes.
     */
    private class LayoutListener extends ComponentAdapter {
        public void componentResized( ComponentEvent event ) {
            if ( event.getSource() == _canvas ) {
                layoutCanvas();
            }
        }
    }
}
