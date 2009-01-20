/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.solutions;

import java.awt.geom.Point2D;

/**
 * SolutionsDefaults contains default settings for SolutionsModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SolutionsDefaults {

    /* Not intended for instantiation */
    private SolutionsDefaults() {}
    
    // ExampleModelElement
    public static final double EXAMPLE_MODEL_ELEMENT_WIDTH = 100; // meters
    public static final double EXAMPLE_MODEL_ELEMENT_HEIGHT = 50; // meters
    public static final Point2D EXAMPLE_MODEL_ELEMENT_POSITION = new Point2D.Double( 100, 100 );
    public static final double EXAMPLE_MODEL_ELEMENT_ORIENTATION = 0; // radians

}
