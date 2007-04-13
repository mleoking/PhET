package edu.colorado.phet.rotation.tests;

import edu.colorado.phet.rotation.model.RotationMath;
import edu.colorado.phet.rotation.model.TimeData;

/**
 * User: Sam Reid
 * Date: Dec 30, 2006
 * Time: 12:30:09 AM
 * Copyright (c) Dec 30, 2006 by Sam Reid
 */

public class TestTimeDerivative {

    /**
     * public static AbstractDoubleVector linearRegression(double[][] data)
     * <p/>
     * Fits a line to multi-dimensional data using the method of least squares.
     * <p/>
     * Parameters:
     * data - [0...n-1][] contains the x-series' (they must be linearly uncorrelated), [n][] contains the y-series.
     * Returns:
     * a vector containing the coefficients (zero component is the intercept, the rest are gradient components). E.g. y(x1, x2, ...) = coeffs(0) + coeffs(1) * x1 + coeffs(2) * x2 + ...
     *
     * @param args
     */
    public static void main( String[] args ) {
        double[] x = new double[]{0, 1, 2, 3, 4};
        double[] y = new double[]{1, 3, 5, 7, 9};

        TimeData[] timeData = new TimeData[x.length];
        for( int i = 0; i < timeData.length; i++ ) {
            timeData[i] = new TimeData( y[i], x[i] );
        }
        double slope = RotationMath.estimateDerivative( timeData );
        System.out.println( "slope = " + slope );
    }
}
