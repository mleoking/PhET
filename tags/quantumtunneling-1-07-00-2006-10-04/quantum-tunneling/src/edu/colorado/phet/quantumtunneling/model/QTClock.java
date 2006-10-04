/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.model;

import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.model.clock.TimingStrategy;
import edu.colorado.phet.quantumtunneling.QTConstants;


/**
 * QTClock is the clock for this simulation.
 * The simulation time change (dt) on each clock tick is constant,
 * regardless of when (in wall time) the ticks actually happen.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTClock extends SwingClock {

    /* Wall time between clock ticks (milliseconds) */
    private static final int WALL_DT = ( 1000 / QTConstants.CLOCK_FRAME_RATE );
    
    /* Simulation time between clock ticks (units defined by the simulation) */
    private static final double SIM_DT = QTConstants.CLOCK_STEP;
    
    public QTClock() {
        super( WALL_DT, new TimingStrategy.Constant( SIM_DT ) );
    }
}
