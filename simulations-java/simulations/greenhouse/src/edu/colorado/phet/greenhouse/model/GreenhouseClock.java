package edu.colorado.phet.greenhouse.model;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;


public class GreenhouseClock extends ConstantDtClock {

	public static final double DEFAULT_TIME_DELTA_PER_TICK = 10;
	
    public GreenhouseClock() {
        super( 30, DEFAULT_TIME_DELTA_PER_TICK );
    }

}
