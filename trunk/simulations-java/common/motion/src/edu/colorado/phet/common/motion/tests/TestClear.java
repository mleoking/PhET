package edu.colorado.phet.common.motion.tests;

import edu.colorado.phet.common.motion.model.MotionModel;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import junit.framework.TestCase;

/**
 * Author: Sam Reid
 * Jun 26, 2007, 8:48:19 PM
 */
public class TestClear extends TestCase {
    public void testClear() {
        SwingClock swingClock = new SwingClock( 30, 1 );
        MotionModel motionModel = new MotionModel( swingClock );
        int NUM_INIT_SAMPLES = 3;
        for( int i = 0; i < NUM_INIT_SAMPLES; i++ ) {
            swingClock.stepClockWhilePaused();
        }
        assertEquals( "Data should have " + NUM_INIT_SAMPLES + " samples", motionModel.getMotionBodySeries().getXTimeSeries().getSampleCount(), NUM_INIT_SAMPLES );
        motionModel.clear();
        assertEquals( "Data should have cleared", motionModel.getMotionBodySeries().getXTimeSeries().getSampleCount(), 0 );
        swingClock.stepClockWhilePaused();
        assertEquals( "Data should have a single sample", motionModel.getMotionBodySeries().getXTimeSeries().getSampleCount(), 1 );
    }
}
