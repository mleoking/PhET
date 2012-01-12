// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.greenhouse.model;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;


public class GreenhouseClock extends ConstantDtClock {

	public static final double DEFAULT_TIME_DELTA_PER_TICK = 10;
	public static final int DEFAULT_DELAY_BETWEEN_TICKS = 30;
	
    public GreenhouseClock() {
        super( DEFAULT_DELAY_BETWEEN_TICKS, DEFAULT_TIME_DELTA_PER_TICK );
    }

}
