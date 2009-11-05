/* Copyright 2007, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers;

import java.awt.Color;
import java.awt.Dimension;

import edu.umd.cs.piccolo.util.PDimension;


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

    //----------------------------------------------------------------------------
    // Dimensions
    //----------------------------------------------------------------------------
    
    public static final Dimension CANVAS_RENDERING_SIZE = new Dimension( 1024, 768 );
    public static final PDimension BEFORE_AFTER_BOX_SIZE = new PDimension( 450, 300 );
    public static final PDimension HISTOGRAM_BAR_SIZE = new PDimension( 18, 75 );
    
    //----------------------------------------------------------------------------
    // Paints
    //----------------------------------------------------------------------------
    
    public static final Color CANVAS_BACKGROUND = new Color( 210, 210, 255 );
    public static final Color BEFORE_AFTER_BOX_COLOR = new Color( 46, 107, 178 );
    public static final Color PLUS_SIGN_COLOR = BEFORE_AFTER_BOX_COLOR;
    public static final Color ARROW_COLOR = BEFORE_AFTER_BOX_COLOR;
    public static final Color HISTOGRAM_BAR_COLOR = BEFORE_AFTER_BOX_COLOR;
}
