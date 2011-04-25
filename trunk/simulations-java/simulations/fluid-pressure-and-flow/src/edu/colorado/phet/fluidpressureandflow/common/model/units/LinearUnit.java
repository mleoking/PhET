// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.model.units;

import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.math.Function;

/**
 * @author Sam Reid
 */
public class LinearUnit implements Unit {
    private final Function.LinearFunction linearFunction;
    private final String name;
    private final String abbreviation;
    private final DecimalFormat decimalFormat;

    public LinearUnit( String name, String abbreviation, double siToUnitScale, DecimalFormat decimalFormat ) {
        this.name = name;
        this.abbreviation = abbreviation;
        this.decimalFormat = decimalFormat;
        linearFunction = new Function.LinearFunction( 0, 1, 0, siToUnitScale );
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public DecimalFormat getDecimalFormat() {
        return decimalFormat;
    }

    public double siToUnit( double value ) {
        return linearFunction.evaluate( value );
    }

    public double toSI( double value ) {
        return linearFunction.createInverse().evaluate( value );
    }
}
