// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.motion.tests;

import junit.framework.TestCase;

import edu.colorado.phet.common.motion.model.SingleBodyMotionModel;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * Author: Sam Reid
 * Jun 26, 2007, 4:30:50 PM
 */
public class TestRecordTime extends TestCase {
    public void testRecordTime() {
        ConstantDtClock swingClock = new ConstantDtClock( 30, 1.0 );
        SingleBodyMotionModel motionModel = new SingleBodyMotionModel( swingClock );
        motionModel.getTimeSeriesModel().setRecordMode();
        for ( int i = 0; i < 100; i++ ) {
            swingClock.stepClockWhilePaused();
        }
        System.out.println( "Time=" + motionModel.getTime() );
        motionModel.getTimeSeriesModel().setPlaybackMode();
        motionModel.getTimeSeriesModel().setPlaybackTime( motionModel.getTime() / 2.0 );

        motionModel.getTimeSeriesModel().setRecordMode();
        for ( int i = 0; i < 100; i++ ) {
            swingClock.stepClockWhilePaused();
        }
        System.out.println( "Time=" + motionModel.getTime() );
    }
}
