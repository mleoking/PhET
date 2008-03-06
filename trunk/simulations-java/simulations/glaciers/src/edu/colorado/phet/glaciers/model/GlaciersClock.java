/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

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
    
    public GlaciersClock( int frameRate, double dt ) {
        super( 1000 / frameRate, dt );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setFrameRate( int frameRate ) {
        setDelay( 1000 / frameRate );
    }
    
    public int getFrameRate() {
        return 1000 / getDelay();
    }
}
