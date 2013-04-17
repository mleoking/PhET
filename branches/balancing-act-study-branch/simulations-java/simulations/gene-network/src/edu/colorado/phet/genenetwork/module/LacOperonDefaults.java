// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.genenetwork.module;

import java.awt.Dimension;



/**
 * Default settings that are common to 2 or more modules.
 * 
 * NOTE! This class is package private, and values herein should only be referenced
 * by the "defaults" classes for each module.  Classes that are module-specific should
 * use the class that corresponds to their module.
 */
public class LacOperonDefaults {

    /* Not intended for instantiation */
    private LacOperonDefaults() {}
    
    // Clock
    public static final int CLOCK_FRAME_RATE = 25; // fps, frames per second (wall time)
    public static final double CLOCK_DT = 1 / (double)CLOCK_FRAME_RATE;

    // Model-view transform
    public static final Dimension VIEW_SIZE = new Dimension( 1500, 1500 );

}
