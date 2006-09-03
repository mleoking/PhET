/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.common;

import edu.colorado.phet.common.math.Function;

/**
 * User: Sam Reid
 * Date: Dec 12, 2004
 * Time: 9:26:59 PM
 * Copyright (c) Dec 12, 2004 by Sam Reid
 */

public class LinearTransform1d extends Function.LinearFunction {
    public LinearTransform1d( double xMin, double xMax, double yMin, double yMax ) {
        super( xMin, xMax, yMin, yMax );
    }

    public LinearTransform1d getInvertedInstance() {
        return new LinearTransform1d( super.getMinOutput(), super.getMaxOutput(), getMinInput(), getMaxInput() );
    }

    public double transform( double x ) {
        return super.evaluate( x );
    }

}
