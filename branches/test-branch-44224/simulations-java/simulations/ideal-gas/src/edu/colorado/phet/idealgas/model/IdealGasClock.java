/* Copyright 2003-2008, University of Colorado */

package edu.colorado.phet.idealgas.model;

import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;

/**
 * IdealGasClock
 * <p/>
 * An adapter to provide an older interface for the newer SwingClock class. The older
 * interface is used throughout the simulation
 *
 * @author Ron LeMaster
 */
public class IdealGasClock extends SwingClock {
    private double dt;

    public IdealGasClock( int delay, double dt ) {
        super( delay, dt );
        this.dt = dt;
    }

    public double getDt() {
        return dt;
    }
}
