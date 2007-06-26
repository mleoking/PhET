package edu.colorado.phet.common.motion;

import JSci.maths.LinearMath;
import JSci.maths.vectors.AbstractDoubleVector;
import edu.colorado.phet.common.motion.model.TimeData;

/**
 * User: Sam Reid
 * Date: Dec 30, 2006
 * Time: 12:04:24 AM
 */

public class MotionMath {
    public static double estimateDerivative( TimeData[] timeSeries ) {
        double[] x = new double[timeSeries.length];
        double[] y = new double[timeSeries.length];
        for( int i = 0; i < y.length; i++ ) {
            x[i] = timeSeries[i].getTime();
            y[i] = timeSeries[i].getValue();
        }
        double[][] data = new double[2][timeSeries.length];
        data[0] = x;
        data[1] = y;
        AbstractDoubleVector out = LinearMath.linearRegression( data );
        if( Double.isNaN( out.getComponent( 1 ) ) || Double.isInfinite( out.getComponent( 1 ) ) ) {
            //todo handle this error elsewhere
            return 0.0;
        }
        return out.getComponent( 1 );
    }

    public static double averageTime( TimeData[] datas ) {
        double a = 0.0;
        for( int i = 0; i < datas.length; i++ ) {
            a += datas[i].getTime();
        }
        return a / datas.length;
    }

    public static TimeData getDerivative( TimeData[] recentPositionTimeSeries ) {
        if( recentPositionTimeSeries.length == 0 ) {
//            System.out.println( "MotionMath.getDerivative, returning zero" );
            return new TimeData( 0, 0 );
        }
        return new TimeData( estimateDerivative( recentPositionTimeSeries ), averageTime( recentPositionTimeSeries ) );
    }
}
