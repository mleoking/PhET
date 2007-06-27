package edu.colorado.phet.common.motion.tests;

import edu.colorado.phet.common.motion.model.SingleBodyMotionModel;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import junit.framework.TestCase;

/**
 * Author: Sam Reid
 * Jun 26, 2007, 4:30:50 PM
 */
public class TestRecordTime extends TestCase {
    public void testRecordTime() {
        SwingClock swingClock = new SwingClock( 30, 1.0 );
        SingleBodyMotionModel motionModel = new SingleBodyMotionModel( swingClock );
        motionModel.getTimeSeriesModel().setRecordMode();
        for( int i = 0; i < 100; i++ ) {
            swingClock.stepClockWhilePaused();
        }
        System.out.println( "Time=" + motionModel.getTime() );
        motionModel.getTimeSeriesModel().setPlaybackMode();
        motionModel.getTimeSeriesModel().setPlaybackTime( motionModel.getTime() / 2.0 );

        motionModel.getTimeSeriesModel().setRecordMode();
        for( int i = 0; i < 100; i++ ) {
            swingClock.stepClockWhilePaused();
        }
        System.out.println( "Time=" + motionModel.getTime() );
    }
}
