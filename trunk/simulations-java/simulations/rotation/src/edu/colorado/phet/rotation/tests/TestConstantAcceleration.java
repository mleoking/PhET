package edu.colorado.phet.rotation.tests;

import edu.colorado.phet.rotation.model.AccelerationDriven;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.model.TimeData;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:47:31 PM
 */

public class TestConstantAcceleration {
    public static void main( String[] args ) {
        AccelerationDriven accelerationDriven = new AccelerationDriven();
        RotationModel model = new RotationModel();
        model.setAngularAcceleration( 1.0 );
        model.setUpdateStrategy( accelerationDriven );
        System.out.println( "init state=" + model.getLastState() );
        for( int i = 0; i <= 100; i++ ) {
            model.stepInTime( 1.0 );
            System.out.println( "i = " + i + ", state=" + model.getLastState() );
        }

        TimeData[] timeData = model.getAccelerationTimeSeries( 5 );
        for( int i = 0; i < timeData.length; i++ ) {
            TimeData data = timeData[i];
            System.out.println( "i = " + i + ", data=" + data );
        }
    }
}
