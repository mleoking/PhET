/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions;

import java.awt.Dimension;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;

/**
 * A collection of constants that configure global properties.
 * If you change something here, it will change *everywhere* in this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ABSConstants {

    /* Not intended for instantiation. */
    private ABSConstants() {}
    
    //----------------------------------------------------------------------------
    // Debugging
    //----------------------------------------------------------------------------
    
    // enable debug output for canvas layout updates
    public static final boolean DEBUG_CANVAS_UPDATE_LAYOUT = false;
    
    //----------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------
    
    public static final String PROJECT_NAME = "acid-base-solutions";
    
    public static final String FLAVOR_MAGNIFYING_GLASS_PROTOTYPE = "magnifying-glass-prototype";

    //----------------------------------------------------------------------------
    // Model
    //----------------------------------------------------------------------------
    
    public static final double AVOGADROS_NUMBER = 6.022E23;
    
    public static final DoubleRange ACID_CONCENTRATION_RANGE = new DoubleRange( 0.001, 1, 0.001 );
    public static final DoubleRange WEAK_BASE_CONCENTRATION_RANGE = new DoubleRange( 0.001, 1, 0.001 );
    
    //----------------------------------------------------------------------------
    // View
    //----------------------------------------------------------------------------
    
    // reference coordinate frame size for world nodes
    public static final Dimension CANVAS_RENDERING_SIZE = new Dimension( 1024, 768 );
}
