/* Copyright 2007, University of Colorado */

package edu.colorado.phet.membranediffusion.module;

import java.awt.Dimension;



/**
 * Default settings that are common to 2 or more modules.
 * 
 * NOTE! This class is package private, and values herein should only be referenced
 * by the "defaults" classes for each module.  Classes that are module-specific should
 * use the class that corresponds to their module.
 */
public class SimTemplateDefaults {

    /* Not intended for instantiation */
    private SimTemplateDefaults() {}
    
    // Clock
    public static final boolean CLOCK_RUNNING = true;
    public static final int CLOCK_FRAME_RATE = 25; // fps, frames per second (wall time)
    public static final double CLOCK_DT = 1;
    public static final int CLOCK_TIME_COLUMNS = 10;

    // Model-view transform
    public static final Dimension VIEW_SIZE = new Dimension( 1500, 1500 );

}
