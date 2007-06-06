/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: C:/Java/cvs/root/SelfDrivenParticles/phetcommon/src/edu/colorado/phet/common/util/FrameRateReporter.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: Sam Reid $
 * Revision : $Revision: 1.1.1.1 $
 * Date modified : $Date: 2005/08/10 08:22:02 $
 */
package edu.colorado.phet.common.util;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;

/**
 * FrameRateReporter
 * A utility that prints, to the console, the number of rames per second an AbstractClock is ticking off. Useful
 * for monitoring the performance of simulations.
 * <p/>
 * To use, simply instantiate the class.
 *
 * @author Ron LeMaster
 * @version $Revision: 1.1.1.1 $
 */
public class FrameRateReporter {

    /**
     * @param clock The clock that is driving the simulation you want to monitor
     */
    public FrameRateReporter( AbstractClock clock ) {
        clock.addClockTickListener( new ClockTickListener() {
            int frameCnt = 0;
            long lastTickTime = System.currentTimeMillis();
            long averagingTime = 1000;

            public void clockTicked( ClockTickEvent event ) {
                frameCnt++;
                long currTime = System.currentTimeMillis();
                if( currTime - lastTickTime > averagingTime ) {
                    double rate = frameCnt * 1000 / ( currTime - lastTickTime );
                    lastTickTime = currTime;
                    frameCnt = 0;
                    System.out.println( "rate = " + rate );
                }
            }
        } );
    }
}
