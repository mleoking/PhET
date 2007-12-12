/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.phetcommon.util;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.ParseException;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

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
            parsed = DecimalFormat.getNumberInstance( PhetResources.readLocale() ).parse( formattedText.toString() ).doubleValue();//to handle european
        }
        catch( NumberFormatException numberFormatException ) {
            return decimalFormat.format( number, result, fieldPosition );
        }
        catch( ParseException e ) {
            e.printStackTrace();
        }
        if ( parsed == 0 && formattedText.indexOf( "-" ) == 0 ) {
            result.append( formattedText.substring( 1 ) );
        }
        else {
            result.append( formattedText );
        }
        return result;
    }

    public static void main( String[] args ) {
        System.out.println( "new DecimalFormat( \"0.00\" ).format( -0.00001 )= " + new DecimalFormat( "0.00" ).format( -0.00001 ) );
        System.out.println( "new DefaultDecimalFormat( \"0.00\" ).format( -0.00001 )= " + new DefaultDecimalFormat( "0.00" ).format( -0.00001 ) );
    }

}