// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.model;

import edu.colorado.phet.common.phetcommon.math.MathUtil;

/**
 * Data structure for a fraction (possibly improper).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Fraction {

    public final int numerator, denominator;

    public Fraction( int numerator, int denominator ) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public double toDecimal() {
        return (double) numerator / (double) denominator;
    }

    public boolean isInteger() {
        return MathUtil.isInteger( toDecimal() );
    }
}
