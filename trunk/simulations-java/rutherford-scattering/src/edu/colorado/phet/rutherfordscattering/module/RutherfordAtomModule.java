/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering.module;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.rutherfordscattering.RSConstants;
import edu.colorado.phet.rutherfordscattering.control.RutherfordAtomControlPanel;
import edu.colorado.phet.rutherfordscattering.model.RSClock;
import edu.umd.cs.piccolo.PNode;


public class RutherfordAtomModule extends AbstractModule {

    //----------------------------------------------------------------------------
    // Default settings
    //----------------------------------------------------------------------------
    
    public static final boolean CLOCK_PAUSED = false;
    public static final boolean GUN_ENABLED = false;
    public static final double GUN_INTENSITY = 1.0; // 0-1 (1=100%)
    public static final double ENERGY_INTENSITY = 0.5; // 0-1 (1=100%)
    public static final int NUMBER_OF_PROTONS = 79;
    public static final int NUMBER_OF_NEUTRONS = 118;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PhetPCanvas _canvas;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public RutherfordAtomModule() {
        super( SimStrings.get( "RutherfordAtomModule.title" ), new RSClock(), CLOCK_PAUSED );

        // hide the PhET logo
        setLogoPanel( null );
        
        // Piccolo canvas
        _canvas = new PhetPCanvas( RSConstants.CANVAS_RENDERING_SIZE );
        _canvas.setBackground( RSConstants.CANVAS_BACKGROUND );
        setSimulationPanel( _canvas );
        
        RutherfordAtomControlPanel controlPanel = new RutherfordAtomControlPanel( this );
        setControlPanel( controlPanel );
    }

    public void reset() {
        // TODO Auto-generated method stub
    }

    protected void updateCanvasLayout() {
        // TODO Auto-generated method stub
    }
}
