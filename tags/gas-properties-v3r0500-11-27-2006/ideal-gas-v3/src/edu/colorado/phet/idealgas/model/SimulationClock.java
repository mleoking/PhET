/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.idealgas.model;

import edu.colorado.phet.common.model.clock.SwingClock;

/**
 * SimulationClock
 * <p>
 * An adapter to provide an older interface for the newer SwingClock class. The older
 * interface is used throughout the simulation
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimulationClock extends SwingClock {
    private double dt;
    private int delay;

    public SimulationClock( int delay, double dt ) {
        super( delay, dt );
        this.dt = dt;
    }

    public double getDt() {
        return dt;
    }
}
