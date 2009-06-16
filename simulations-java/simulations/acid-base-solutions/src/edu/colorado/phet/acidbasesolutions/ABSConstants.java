/* Copyright 2009, University of Colorado */

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

    //----------------------------------------------------------------------------
    // View
    //----------------------------------------------------------------------------
    
    // reference coordinate frame size for world nodes
    public static final Dimension CANVAS_RENDERING_SIZE = new Dimension( 1024, 768 );
    
    // how much to scale Swing components that are wrapped in PSwing
    public static final double PSWING_SCALE = 1.25;
    
    //----------------------------------------------------------------------------
    // Model
    //----------------------------------------------------------------------------
    
    public static final double AVOGADROS_NUMBER = 6.022E23;
    
    public static final DoubleRange CONCENTRATION_RANGE = new DoubleRange( 1E-3, 1 );
    public static final DoubleRange WEAK_STRENGTH_RANGE = new DoubleRange( 1E-10, 1 );
    public static final DoubleRange STRONG_STRENGTH_RANGE = new DoubleRange( 20, 1E7 );
    public static final DoubleRange INTERMEDIATE_STRENGTH_RANGE = new DoubleRange( WEAK_STRENGTH_RANGE.getMax(), STRONG_STRENGTH_RANGE.getMin() ); // exclusive
    public static final DoubleRange CUSTOM_STRENGTH_RANGE = new DoubleRange( WEAK_STRENGTH_RANGE.getMin(), STRONG_STRENGTH_RANGE.getMax() );
    
}
