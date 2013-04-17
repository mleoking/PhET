// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.motion.tests;

import junit.framework.TestCase;

import edu.colorado.phet.common.motion.model.SingleBodyMotionModel;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * Author: Sam Reid
 * Jun 26, 2007, 8:48:19 PM
 */
public class TestClear extends TestCase {
    public void testClear() {
        ConstantDtClock swingClock = new ConstantDtClock( 30, 1 );
        SingleBodyMotionModel motionModel = new SingleBodyMotionModel( swingClock );
        int NUM_INIT_SAMPLES = 3;
        for ( int i = 0; i < NUM_INIT_SAMPLES; i++ ) {
            swingClock.stepClockWhilePaused();
        }
        assertEquals( "Data should have " + NUM_INIT_SAMPLES + " samples", motionModel.getXTimeSeries().getSampleCount(), NUM_INIT_SAMPLES );
        motionModel.clear();
        assertEquals( "Data should have cleared", motionModel.getXTimeSeries().getSampleCount(), 0 );
        swingClock.stepClockWhilePaused();
        assertEquals( "Data should have a single sample", motionModel.getXTimeSeries().getSampleCount(), 1 );
    }
}
