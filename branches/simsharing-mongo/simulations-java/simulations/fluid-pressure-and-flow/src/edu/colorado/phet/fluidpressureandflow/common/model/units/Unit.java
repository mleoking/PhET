// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.model.units;

import java.text.DecimalFormat;

/**
 * A unit can be used to convert to/from SI, such as feet/meters.
 *
 * @author Sam Reid
 */
public interface Unit {

    //Convert an SI value to this unit
    double siToUnit( double value );

    //Convert this value to SI units
    double toSI( double value );

    //The abbreviation of the unit, to be used in control panels
    String getAbbreviation();

    //Format to be used for displaying values of this unit
    public DecimalFormat getDecimalFormat();
}
