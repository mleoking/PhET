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

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.control.*;
import edu.colorado.phet.hydrogenatom.model.HAClock;
import edu.colorado.phet.hydrogenatom.view.HAGunNode;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.piccolo.help.HelpPane;
import edu.umd.cs.piccolo.PNode;


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
    
    private HAClockControlPanel _clockControlPanel;
    
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
            GunOnOffControl gunOnOffControl = new GunOnOffControl();
            GunTypeControlPanel gunTypeControlPanel = new GunTypeControlPanel();
            LightControlPanel lightControlPanel = new LightControlPanel( _canvas );
            AlphaParticleControlPanel alphaParticleControlPanel = new AlphaParticleControlPanel( _canvas );
            
            // group everything related to the gun
            HAGunNode gunNode = new HAGunNode( _canvas, gunOnOffControl, gunTypeControlPanel, 
                    lightControlPanel, alphaParticleControlPanel );
            gunNode.setOffset( 50, 200 );
            _rootNode.addChild( gunNode );
        }
        
        //XXX test
        {
            final WavelengthControl wavelengthControl = new WavelengthControl( _canvas, 90, 900, Color.GRAY, Color.LIGHT_GRAY );
            wavelengthControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    System.out.println( "wavelength=" + wavelengthControl.getWavelength() );
                }
            } );
            wavelengthControl.setOffset( 100, 100 );
            _rootNode.addChild( wavelengthControl );
        }
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------
        
        // Clock controls
        {
            _clockControlPanel = new HAClockControlPanel( (HAClock) getClock() );
            setClockControlPanel( _clockControlPanel );
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
