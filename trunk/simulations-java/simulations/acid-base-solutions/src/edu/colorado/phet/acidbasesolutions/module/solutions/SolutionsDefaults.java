/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.solutions;

import edu.umd.cs.piccolo.util.PDimension;

/**
 * SolutionsDefaults contains default settings for SolutionsModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SolutionsDefaults {

    /* Not intended for instantiation */
    private SolutionsDefaults() {}
    
    public static final PDimension BEAKER_SIZE = new PDimension( 400, 400 ); // screen coordinates
    public static final double BEAKER_CAPACITY = 1; // L
    
    public static final double PH_PROBE_HEIGHT = BEAKER_SIZE.getHeight() + 55;
}
