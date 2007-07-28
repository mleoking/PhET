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

    /**
     * Gets the second derivative of the given time series data using the central difference formula
     * See: http://mathews.ecs.fullerton.edu/n2003/NumericalDiffMod.html
     *
     * @param x
     * @return
     */
    public static TimeData getSecondDerivative( TimeData[] x ) {
        if( x.length == 0 ) {
//            System.out.println( "MotionMath.getDerivative, returning zero" );
            return new TimeData( 0, 0 );
        }
        double sum = 0.0;
        int count = 0;
        for( int i = 1; i < x.length - 1; i++ ) {
            sum += getSecondDerivative( x[i - 1], x[i], x[i + 1] );
            count++;
        }
        if( count == 0 ) {
            return new TimeData( 0.0, averageTime( x ) );
        }
        return new TimeData( sum / count, averageTime( x ) );
    }

    private static double getSecondDerivative( TimeData a, TimeData b, TimeData c ) {
        double num = a.getValue() - 2 * b.getValue() + c.getValue();
        double h1 = c.getTime() - b.getTime();
        double h2 = b.getTime() - a.getTime();
        double h = ( h1 + h2 ) / 2.0;
        return num / ( h * h );
    }


    public static TimeData[] smooth( TimeData[] series, int numSmooth ) {
        for( int i = 0; i < numSmooth; i++ ) {
            series = smooth( series );
        }
        return series;
    }

    private static TimeData[] smooth( TimeData[] datas ) {
        TimeData[] smooth = new TimeData[datas.length];
        for( int i = 0; i < smooth.length; i++ ) {
            if( i > 0 && i < smooth.length - 1 ) {
                smooth[i] = new TimeData( ( datas[i - 1].getValue() + datas[i].getValue() + datas[i + 1].getValue() ) / 3.0, datas[i].getTime() );
            }
            else {
                smooth[i] = new TimeData( datas[i].getValue(), datas[i].getTime() );
            }
        }

        return smooth;
    }


}
