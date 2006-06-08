/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.util;

import java.text.DecimalFormat;
import java.text.FieldPosition;

/**
 * This formatter never returns zero-values with a minus
 * sign prefix.  For example,
 * new DecimalFormat("0.0").format(-0)  returns "-0.0";
 * whereas
 * new DefaultDecimalFormat("0.0").format(-0) returns "0.0";
 */
public class DefaultDecimalFormat extends DecimalFormat {
    private DecimalFormat decimalFormat;

    public DefaultDecimalFormat( String str ) {
        this( new DecimalFormat( str ) );
    }

    public DefaultDecimalFormat( DecimalFormat decimalFormat ) {
        this.decimalFormat = decimalFormat;
    }

    public StringBuffer format( double number, StringBuffer result,
                                FieldPosition fieldPosition ) {
        StringBuffer a = decimalFormat.format( number, new StringBuffer(), fieldPosition );
        if( Double.parseDouble( a.toString() ) == 0 && a.indexOf( "-" ) == 0 ) {
            String x = a.toString();
            x = x.substring( 1 );
            result.append( x );
        }
        else {
            result.append( a );
        }
        return result;
    }

}