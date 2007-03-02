/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering.module;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.rutherfordscattering.model.RSClock;


public class RutherfordAtomModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Default settings
    //----------------------------------------------------------------------------
    
    public static final boolean CLOCK_PAUSED = true;
    public static final boolean GUN_ENABLED = false;
    public static final double GUN_INTENSITY = 1.0; // 0-1 (1=100%)
    public static final double ENERGY_INTENSITY = 0.5; // 0-1 (1=100%)
    public static final int NUMBER_OF_PROTONS = 79;
    public static final int NUMBER_OF_NEUTRONS = 118;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public RutherfordAtomModule() {
        super( SimStrings.get( "RutherfordAtomModule.title" ), new RSClock(), CLOCK_PAUSED );

        // hide the PhET logo
        setLogoPanel( null );
    }
}
