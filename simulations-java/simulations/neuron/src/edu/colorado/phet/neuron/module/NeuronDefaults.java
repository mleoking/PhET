/* Copyright 2007, University of Colorado */

package edu.colorado.phet.neuron.module;

import edu.umd.cs.piccolo.util.PDimension;


/**
 * GlobalDefaults contains default settings that are common to 2 or more modules.
 *
 * @author John Blanco
 */
public class NeuronDefaults {

    /* Not intended for instantiation */
    private NeuronDefaults() {}
    
    // Clock
    public static final int CLOCK_FRAME_RATE = 15; // fps, frames per second (wall time)
    
    // Set up the clock ranges for the various modules.  Note that for this
    // sim the clock rates are often several orders of magnitude slower than
    // real time.
    
    public static final double MIN_ACTION_POTENTIAL_CLOCK_DT = (1 / (double)CLOCK_FRAME_RATE) / 3000;
    public static final double MAX_ACTION_POTENTIAL_CLOCK_DT = (1 / (double)CLOCK_FRAME_RATE) / 1000;
    public static final double DEFAULT_ACTION_POTENTIAL_CLOCK_DT = 
    	(MIN_ACTION_POTENTIAL_CLOCK_DT + MAX_ACTION_POTENTIAL_CLOCK_DT) * 0.55;
    
    public static final double MIN_MEMBRANE_DIFFUSION_CLOCK_DT = (1 / (double)CLOCK_FRAME_RATE) / 3000;
    public static final double MAX_MEMBRANE_DIFFUSION_CLOCK_DT = (1 / (double)CLOCK_FRAME_RATE) / 1000;
    public static final double DEFAULT_MEMBRANE_DIFFUSION_CLOCK_DT = 
    	(MIN_ACTION_POTENTIAL_CLOCK_DT + MAX_MEMBRANE_DIFFUSION_CLOCK_DT) / 2; 
    
    // Model-view transform
    public static final PDimension INTERMEDIATE_RENDERING_SIZE = new PDimension( 786, 786 );

}
