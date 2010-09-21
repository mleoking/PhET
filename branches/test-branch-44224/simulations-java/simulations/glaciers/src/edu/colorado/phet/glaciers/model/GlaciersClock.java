/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.glaciers.GlaciersConstants;

/**
 * GlaciersClock is the clock for this simulation.
 * The simulation time change (dt) on each clock tick is constant,
 * regardless of when (in wall time) the ticks actually happen.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlaciersClock extends ConstantDtClock {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public GlaciersClock( ) {
        super( 1000 / GlaciersConstants.CLOCK_FRAME_RATE_RANGE.getDefault(), GlaciersConstants.CLOCK_DT );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    /**
     * Sets the frame rate.
     * Due to integer precision and conversion to clock delay,
     * the clock may actually run at a slightly different frame rate.
     * Whether the clock can actually run at the requested frame rate
     * is dependent on the speed of the host computer.
     * 
     * @param frameRate frames per second
     */
    public void setFrameRate( int frameRate ) {
        setDelay( 1000 / frameRate );
    }
    
    /**
     * Gets the frame rate.
     * Due to integer precision and conversion to clock delay,
     * this may be slightly different than the frame rate set with setFrameRate.
     * 
     * @return frames per second
     */
    public int getFrameRate() {
        return 1000 / getDelay();
    }
}
