package edu.colorado.phet.common.motion.tests;

import edu.colorado.phet.common.motion.model.SingleBodyMotionModel;
import edu.colorado.phet.common.motion.model.PositionDriven;
import edu.colorado.phet.common.motion.model.TimeData;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:47:31 PM
 */

public class TestConstantVelocity {
    public static void main( String[] args ) {
        PositionDriven updateRule = new PositionDriven();
        SingleBodyMotionModel model = new SingleBodyMotionModel( new SwingClock( 30, 1 ) );
        model.setUpdateStrategy( updateRule );
        System.out.println( "init state=" + model );
        for( int i = 0; i <= 100; i++ ) {
            model.stepInTime( 1.0 );
            System.out.println( "i = " + i + ", state=" + model );
        }

        TimeData[] timeData = model.getMotionBodySeries().getRecentAccelerationTimeSeries( 5 );
        for( int i = 0; i < timeData.length; i++ ) {
            TimeData data = timeData[i];
            System.out.println( "i = " + i + ", data=" + data );
        }
    }
}
