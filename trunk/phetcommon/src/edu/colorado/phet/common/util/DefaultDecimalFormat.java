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

    public StringBuffer format( double number, StringBuffer result, FieldPosition fieldPosition ) {
        StringBuffer formattedText = decimalFormat.format( number, new StringBuffer(), fieldPosition );
        double parsed = 0;
        try {
            parsed = Double.parseDouble( formattedText.toString().replace( ',', '.' ) );//to handle european 
        }
        catch( NumberFormatException numberFormatException ) {
            return decimalFormat.format( number, result, fieldPosition );
        }
        if( parsed == 0 && formattedText.indexOf( "-" ) == 0 ) {
            result.append( formattedText.substring( 1 ) );
        }
        else {
            result.append( formattedText );
        }
        return result;
    }

}