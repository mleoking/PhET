// Copyright 2002-2011, University of Colorado

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
    
    public static final String FLAVOR_RPAL = "reactants-products-and-leftovers";

    //----------------------------------------------------------------------------
    // Dimensions
    //----------------------------------------------------------------------------
    
    public static final Dimension CANVAS_RENDERING_SIZE = new Dimension( 1024, 768 );
    public static final PDimension BEFORE_AFTER_BOX_SIZE = new PDimension( 450, 300 );
    public static final PDimension HISTOGRAM_BAR_SIZE = new PDimension( 18, 75 );
    
    //----------------------------------------------------------------------------
    // Scaling
    //----------------------------------------------------------------------------
    
    // scaling for images that appear in equations
    public static final double EQUATION_IMAGE_SCALE = 1;
    // scaling for images that appear in the Before and After boxes
    public static final double BEFORE_AFTER_BOX_IMAGE_SCALE = 1;
    // scaling for images that appear below histogram bars
    public static final double HISTOGRAM_IMAGE_SCALE = 1;
}
