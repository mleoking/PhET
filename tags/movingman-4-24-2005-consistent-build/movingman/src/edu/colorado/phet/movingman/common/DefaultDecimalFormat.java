/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.common;

import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Apr 24, 2005
 * Time: 3:53:12 PM
 * Copyright (c) Apr 24, 2005 by Sam Reid
 */

public class DefaultDecimalFormat {
    private DecimalFormat decimalFormat;

    public DefaultDecimalFormat( String str ) {
        this( new DecimalFormat( str ) );
    }

    public DefaultDecimalFormat( DecimalFormat decimalFormat ) {
        this.decimalFormat = decimalFormat;
    }

    public String format( double val ) {
        String str = decimalFormat.format( val );
        if( Double.parseDouble( str ) == 0 && str.indexOf( "-" ) >= 0 ) {
            return format( 0 );
        }
        return str;
    }
}
