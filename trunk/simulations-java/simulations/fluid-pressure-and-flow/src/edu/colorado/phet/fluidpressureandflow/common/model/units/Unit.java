// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.model.units;

import java.text.DecimalFormat;

/**
 * @author Sam Reid
 */
public interface Unit {
    double siToUnit( double value );

    double toSI( double value );

    String getAbbreviation();

    public DecimalFormat getDecimalFormat();
}
