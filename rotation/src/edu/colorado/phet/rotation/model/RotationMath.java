package edu.colorado.phet.rotation.model;

import JSci.maths.LinearMath;
import JSci.maths.vectors.AbstractDoubleVector;

/**
 * User: Sam Reid
 * Date: Dec 30, 2006
 * Time: 12:04:24 AM
 * Copyright (c) Dec 30, 2006 by Sam Reid
 */

public class RotationMath {
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
}
