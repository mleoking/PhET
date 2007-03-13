/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.model;

import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.model.clock.TimingStrategy;
import edu.colorado.phet.opticaltweezers.OTConstants;


/**
 * OTClock is the clock for this simulation.
 * The simulation time change (dt) on each clock tick is constant,
 * regardless of when (in wall time) the ticks actually happen.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OTClock extends SwingClock {

    public OTClock( int framesPerSecond, double dt ) {
        super( 1000 / framesPerSecond, dt );
    }
    
    public void setDt( final double dt ) {
        setTimingStrategy( new TimingStrategy.Constant( dt ) );
    }
}
