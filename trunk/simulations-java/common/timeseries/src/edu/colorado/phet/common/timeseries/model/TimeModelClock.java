package edu.colorado.phet.common.timeseries.model;

import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.model.clock.TimingStrategy;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

import java.util.ArrayList;

/**
 * Author: Sam Reid
 * Jun 2, 2007, 4:06:24 AM
 */
public class TimeModelClock extends ConstantDtClock {

    /**
     * Constructor.
     *
     * @param delay desired wall time change between ticks
     * @param dt    constant simulation time change between ticks
     */
    public TimeModelClock( int delay, double dt ) {
        super( delay, dt );
    }

}
