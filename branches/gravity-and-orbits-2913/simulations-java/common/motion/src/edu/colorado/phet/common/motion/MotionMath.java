// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.motion;

import JSci.maths.LinearMath;
import JSci.maths.vectors.AbstractDoubleVector;

import edu.colorado.phet.common.motion.model.TimeData;
import edu.colorado.phet.common.phetcommon.math.Function;

/**
 * User: Sam Reid
 * Date: Dec 30, 2006
 * Time: 12:04:24 AM
 */

public class MotionMath {
    public static double estimateDerivative( TimeData[] timeSeries ) {
        AbstractDoubleVector out = getLinearRegressionCoefficients( timeSeries );
        if ( Double.isNaN( out.getComponent( 1 ) ) || Double.isInfinite( out.getComponent( 1 ) ) ) {
            //todo handle this error elsewhere
            return 0.0;
        }
        return out.getComponent( 1 );
    }

    /**
     * @return a vector containing the coefficients (zero component is the intercept, the rest are gradient components). E.g. y(x1, x2, ...) = coeffs(0) + coeffs(1) * x1 + coeffs(2) * x2 + ...
     *         see http://jsci.sourceforge.net/api/JSci/maths/LinearMath.html#linearRegression(double[][])
     */
    public static AbstractDoubleVector getLinearRegressionCoefficients( TimeData[] timeSeries ) {
        double[] x = new double[timeSeries.length];
        double[] y = new double[timeSeries.length];
        for ( int i = 0; i < y.length; i++ ) {
            x[i] = timeSeries[i].getTime();
            y[i] = timeSeries[i].getValue();
        }
        double[][] data = new double[2][timeSeries.length];
        data[0] = x;
        data[1] = y;
        return LinearMath.linearRegression( data );
    }

    public static double averageTime( TimeData[] datas ) {
        double a = 0.0;
        for ( int i = 0; i < datas.length; i++ ) {
            a += datas[i].getTime();
        }
        return a / datas.length;
    }

    public static TimeData getDerivative( TimeData[] recentPositionTimeSeries ) {
        if ( recentPositionTimeSeries.length == 0 ) {
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
        if ( x.length == 0 ) {
//            System.out.println( "MotionMath.getDerivative, returning zero" );
            return new TimeData( 0, 0 );
        }
        double sum = 0.0;
        int count = 0;
        for ( int i = 1; i < x.length - 1; i++ ) {
            sum += getSecondDerivative( x[i - 1], x[i], x[i + 1] );
            count++;
        }
        if ( count == 0 ) {
            return new TimeData( 0.0, averageTime( x ) );
        }
        return new TimeData( sum / count, averageTime( x ) );
    }

    private static double getSecondDerivative( TimeData a, TimeData b, TimeData c ) {
        double num = a.getValue() - 2 * b.getValue() + c.getValue();
        double h1 = c.getTime() - b.getTime();
        double h2 = b.getTime() - a.getTime();
        double h = ( h1 + h2 ) / 2.0;
        if (h==0)
            new RuntimeException("h was zero").printStackTrace();
        return num / ( h * h );
    }


    public static TimeData[] smooth( TimeData[] series, int numSmooth ) {
        for ( int i = 0; i < numSmooth; i++ ) {
            series = smooth( series );
        }
        return series;
    }

    private static TimeData[] smooth( TimeData[] datas ) {
        TimeData[] smooth = new TimeData[datas.length];
        for ( int i = 0; i < smooth.length; i++ ) {
            if ( i > 0 && i < smooth.length - 1 ) {
                smooth[i] = new TimeData( ( datas[i - 1].getValue() + datas[i].getValue() + datas[i + 1].getValue() ) / 3.0, datas[i].getTime() );
            }
            else {
                smooth[i] = new TimeData( datas[i].getValue(), datas[i].getTime() );
            }
        }

        return smooth;
    }

    public static Function.LinearFunction getLinearFit( TimeData[] data ) {
        AbstractDoubleVector model = MotionMath.getLinearRegressionCoefficients( data );
        return new Function.LinearFunction( model.getComponent( 0 ), model.getComponent( 1 ) );
    }
}
