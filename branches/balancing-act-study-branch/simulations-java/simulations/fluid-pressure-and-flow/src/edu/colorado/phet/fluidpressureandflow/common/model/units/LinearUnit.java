// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.model.units;

import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.math.Function;

/**
 * Linear model for converting values to/from SI, such as feet/meters.
 *
 * @author Sam Reid
 */
public class LinearUnit implements Unit {
    private final Function.LinearFunction linearFunction;
    private final String abbreviation;
    private final DecimalFormat decimalFormat;

    public LinearUnit( String abbreviation, double siToUnitScale, DecimalFormat decimalFormat ) {
        this.abbreviation = abbreviation;
        this.decimalFormat = decimalFormat;
        linearFunction = new Function.LinearFunction( 0, 1, 0, siToUnitScale );
    }

    //See parent docs
    public String getAbbreviation() {
        return abbreviation;
    }

    //See parent docs
    public DecimalFormat getDecimalFormat() {
        return decimalFormat;
    }

    //See parent docs
    public double siToUnit( double value ) {
        return linearFunction.evaluate( value );
    }

    //See parent docs
    public double toSI( double value ) {
        return linearFunction.createInverse().evaluate( value );
    }
}
