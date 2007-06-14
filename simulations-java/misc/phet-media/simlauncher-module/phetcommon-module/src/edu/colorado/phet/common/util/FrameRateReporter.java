/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/phetcommon/src/edu/colorado/phet/common/util/FrameRateReporter.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.8 $
 * Date modified : $Date: 2006/01/03 23:37:18 $
 */
package edu.colorado.phet.common.util;

import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.IClock;

/**
 * A utility that prints, to the console, the number of rames per second an AbstractClock is ticking off. Useful
 * for monitoring the performance of simulations.
 * <p/>
 * To use, simply instantiate the class.
 *
 * @author Ron LeMaster
 * @version $Revision: 1.8 $
 */
public class FrameRateReporter {

    /**
     * Constructs and starts the FrameRateReporter.
     *
     * @param clock The clock that is driving the simulation you want to monitor
     */
    public FrameRateReporter( IClock clock ) {
        clock.addClockListener( new ClockAdapter() {
            int frameCnt = 0;
            long lastTickTime = System.currentTimeMillis();
            long averagingTime = 1000;

            public void clockTicked( ClockEvent event ) {
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
