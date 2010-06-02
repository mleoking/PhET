/* Copyright 2007, University of Colorado */

package edu.colorado.phet.membranediffusion.module;

import java.awt.Dimension;

import edu.umd.cs.piccolo.util.PDimension;



/**
 * Default settings that are common to 2 or more modules.
 * 
 * NOTE! This class is package private, and values herein should only be referenced
 * by the "defaults" classes for each module.  Classes that are module-specific should
 * use the class that corresponds to their module.
 */
public class MembraneDiffusionDefaults {

    /* Not intended for instantiation */
    private MembraneDiffusionDefaults() {}
    
    // Clock
    public static final int CLOCK_FRAME_RATE = 20; // fps, frames per second (wall time)
    
    // Set up the clock ranges.  Note that for this sim the clock rates are
    // often several orders of magnitude slower than real time.
    
    public static final double MIN_MEMBRANE_DIFFUSION_CLOCK_DT = (1 / (double)CLOCK_FRAME_RATE) / 3000;
    public static final double MAX_MEMBRANE_DIFFUSION_CLOCK_DT = (1 / (double)CLOCK_FRAME_RATE) / 1000;
    public static final double DEFAULT_MEMBRANE_DIFFUSION_CLOCK_DT = 
    	(MIN_MEMBRANE_DIFFUSION_CLOCK_DT + MAX_MEMBRANE_DIFFUSION_CLOCK_DT) / 2; 
    
    // Model-view transform
    public static final PDimension INTERMEDIATE_RENDERING_SIZE = new PDimension( 786, 786 );

}
