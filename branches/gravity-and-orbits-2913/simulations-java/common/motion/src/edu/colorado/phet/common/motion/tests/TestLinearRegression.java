// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.motion.tests;

import JSci.maths.LinearMath;
import JSci.maths.polynomials.RealPolynomial;
import JSci.maths.vectors.AbstractDoubleVector;

/**
 * User: Sam Reid
 * Date: Dec 30, 2006
 * Time: 12:30:09 AM
 */

public class TestLinearRegression {

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
        double[] y = new double[]{1, 3, 5, 7, 9.01};
        int n = 2;
        double[][] data = new double[n][x.length];
        data[0] = x;
        data[1] = y;
        AbstractDoubleVector out = LinearMath.linearRegression( data );
        System.out.println( "out = " + out );

        RealPolynomial lsf = LinearMath.leastSquaresFit( 2, data );
        System.out.println( "lsf = " + lsf );
    }
}
