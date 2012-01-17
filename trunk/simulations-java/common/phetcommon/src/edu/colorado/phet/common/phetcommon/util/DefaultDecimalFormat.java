// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.phetcommon.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParseException;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * This formatter never returns zero-values with a minus sign prefix, and rounds to nearest neighbor.
 * See main for examples.
 */
public class DefaultDecimalFormat extends DecimalFormat {
    private NumberFormat decimalFormat;

    public DefaultDecimalFormat( String str ) {
        this( DecimalFormat.getNumberInstance( PhetResources.readLocale() ) );
        if ( decimalFormat instanceof DecimalFormat ) {
            ( (DecimalFormat) decimalFormat ).applyPattern( str );
        }
    }

    public DefaultDecimalFormat( NumberFormat decimalFormat ) {
        this.decimalFormat = decimalFormat;
        this.decimalFormat.setRoundingMode( RoundingMode.HALF_UP ); // Round to nearest neighbor. This is the rounding method that most of us were taught in grade school.
    }

    public StringBuffer format( double number, StringBuffer result, FieldPosition fieldPosition ) {
        StringBuffer formattedText = decimalFormat.format( number, new StringBuffer(), fieldPosition );
        double parsed = 0;
        try {
            parsed = decimalFormat.parse( formattedText.toString() ).doubleValue();
        }
        catch ( NumberFormatException numberFormatException ) {
            return decimalFormat.format( number, result, fieldPosition );
        }
        catch ( ParseException e ) {
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

    // tests
    public static void main( String[] args ) {

        // negative zero
        assert ( new DecimalFormat( "0.00" ).format( -0.00001 ).equals( "-0.00" ) );
        assert ( new DefaultDecimalFormat( "0.00" ).format( -0.00001 ).equals( "0.00" ) );

        // rounding (even neighbor)
        assert ( new DefaultDecimalFormat( "0.00" ).format( 0.014 ).equals( "0.01" ) );
        assert ( new DefaultDecimalFormat( "0.00" ).format( 0.015 ).equals( "0.02" ) );
        assert ( new DefaultDecimalFormat( "0.00" ).format( 0.016 ).equals( "0.02" ) );

        // rounding (odd neighbor)
        assert ( new DefaultDecimalFormat( "0.00" ).format( 0.024 ).equals( "0.02" ) );
        assert ( new DefaultDecimalFormat( "0.00" ).format( 0.025 ).equals( "0.03" ) );
        assert ( new DefaultDecimalFormat( "0.00" ).format( 0.026 ).equals( "0.03" ) );
    }

}