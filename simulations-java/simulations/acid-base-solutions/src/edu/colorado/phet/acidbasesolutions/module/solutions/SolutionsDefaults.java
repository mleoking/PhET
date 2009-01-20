/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.solutions;

import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.util.PDimension;

/**
 * SolutionsDefaults contains default settings for SolutionsModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SolutionsDefaults {

    /* Not intended for instantiation */
    private SolutionsDefaults() {}
    
    //XXX ExampleModelElement
    public static final double EXAMPLE_MODEL_ELEMENT_WIDTH = 100; // meters
    public static final double EXAMPLE_MODEL_ELEMENT_HEIGHT = 50; // meters
    public static final Point2D EXAMPLE_MODEL_ELEMENT_POSITION = new Point2D.Double( 100, 100 );
    public static final double EXAMPLE_MODEL_ELEMENT_ORIENTATION = 0; // radians

    public static final PDimension BEAKER_SIZE = new PDimension( 350, 375 ); // screen coordinates
    public static final double BEAKER_CAPACITY = 1; // L
    
    public static final double PH_PROBE_HEIGHT = BEAKER_SIZE.getHeight() + 75;
}
