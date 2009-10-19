/* Copyright 2007, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers;

import java.awt.Color;
import java.awt.Dimension;


/**
 * A collection of constants that configure global properties.
 * If you change something here, it will change *everywhere* in this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RPALConstants {

    /* Not intended for instantiation. */
    private RPALConstants() {}
    
    //----------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------
    
    public static final String PROJECT_NAME = "reactants-products-and-leftovers";

    public static final Dimension CANVAS_RENDERING_SIZE = new Dimension( 1024, 768 );
    
    //----------------------------------------------------------------------------
    // Paints
    //----------------------------------------------------------------------------
    
    // Color of the "play area"
    public static final Color CANVAS_BACKGROUND = new Color( 210, 210, 255 );
}
