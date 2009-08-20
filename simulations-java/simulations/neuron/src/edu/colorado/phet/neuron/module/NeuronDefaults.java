/* Copyright 2007, University of Colorado */

package edu.colorado.phet.neuron.module;

import java.awt.Dimension;

import edu.umd.cs.piccolo.util.PDimension;


/**
 * GlobalDefaults contains default settings that are common to 2 or more modules.
 * 
 * NOTE! This class is package private, and values herein should only be referenced
 * by the "defaults" classes for each module.  Classes that are module-specific should
 * use the class that corresponds to their module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NeuronDefaults {
    public static final double CROSS_SECTION_RADIUS = 20;//micrometers

    /* Not intended for instantiation */
    private NeuronDefaults() {}
    
    // Clock
    public static final boolean CLOCK_RUNNING = true;
    public static final int CLOCK_FRAME_RATE = 25; // fps, frames per second (wall time)
    public static final double CLOCK_DT = 1;
    public static final int CLOCK_TIME_COLUMNS = 10;

    // Model-view transform
    public static final PDimension INTERMEDIATE_RENDERING_SIZE = new PDimension( 600,600);

}
