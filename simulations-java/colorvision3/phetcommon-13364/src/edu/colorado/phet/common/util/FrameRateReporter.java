/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.util;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickListener;

/**
 * FrameRateReporter
 * A utility that prints, to the console, the number of rames per second an AbstractClock is ticking off. Useful
 * for monitoring the performance of simulations.
 * <p/>
 * To use, simply instantiate the class.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class FrameRateReporter {

    public FrameRateReporter( AbstractClock clock ) {
        clock.addClockTickListener( new ClockTickListener() {
            int frameAve = 25;
            int tickCnt = 0;
            long lastTickTime = System.currentTimeMillis();

            public void clockTicked( AbstractClock c, double dt ) {
                if( ++tickCnt % frameAve == 0 ) {
                    long currTime = System.currentTimeMillis();
                    long elapsedTime = currTime - lastTickTime;
                    lastTickTime = currTime;
                    double rate = frameAve * 1000 / elapsedTime;
                    System.out.println( "rate = " + rate );
                }
            }
        } );
    }
}
