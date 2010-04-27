/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab;

import java.awt.Dimension;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * A collection of constants that configure global properties.
 * If you change something here, it will change *everywhere* in this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CLConstants {

    /* Not intended for instantiation. */
    private CLConstants() {}
    
    //----------------------------------------------------------------------------
    // Debugging
    //----------------------------------------------------------------------------
    
    // enable debug output for canvas layout updates
    public static final boolean DEBUG_CANVAS_UPDATE_LAYOUT = false;
    
    //----------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------
    
    public static final String PROJECT_NAME = "capacitor-lab";
    
    public static final String FLAVOR_MAGNIFYING_GLASS_PROTOTYPE = "magnifying-glass-prototype";

    //----------------------------------------------------------------------------
    // Model
    //----------------------------------------------------------------------------
    
    public static final DoubleRange BATTERY_VOLTAGE_RANGE = new DoubleRange( -10, 10, 0 );
    
    //----------------------------------------------------------------------------
    // View
    //----------------------------------------------------------------------------
    
    // reference coordinate frame size for world nodes
    public static final Dimension CANVAS_RENDERING_SIZE = new Dimension( 1024, 768 );
    
    //----------------------------------------------------------------------------
    // Control
    //----------------------------------------------------------------------------
    
}
