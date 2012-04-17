// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.phetcommon.util;

import java.math.BigDecimal;
import java.math.MathContext;
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
        // #3303, When we move to Java 1.6, replace roundNearestNeighbor with this.decimalFormat.setRoundingMode( RoundingMode.HALF_UP );
    }

    // #3303, Java 1.5 workaround for "nearest neighbor" rounding.
    private double roundNearestNeighbor( double number ) {
        final int numDigitsToShow = decimalFormat.getMaximumFractionDigits();
        BigDecimal bigDecimal = new BigDecimal( number, new MathContext( numDigitsToShow, RoundingMode.HALF_UP ) );
        BigDecimal roundedBigDecimal = bigDecimal.setScale( numDigitsToShow, RoundingMode.HALF_UP );
        return roundedBigDecimal.doubleValue();
    }

    public StringBuffer format( double number, StringBuffer result, FieldPosition fieldPosition ) {
        StringBuffer formattedText = decimalFormat.format( roundNearestNeighbor( number ), new StringBuffer(), fieldPosition );
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

    // Creates a format with at least one integer place and a specified number of decimal places.
    public static DefaultDecimalFormat createFormat( int numberOfDecimalPlaces ) {
        String pattern = "0.";
        for ( int i = 0; i < numberOfDecimalPlaces; i++ ) {
            pattern += "0";
        }
        return new DefaultDecimalFormat( pattern );
    }

    // tests
    public static void main( String[] args ) {

        // negative zero
        assert ( new DecimalFormat( "0.00" ).format( -0.00001 ).equals( "-0.00" ) );
        assert ( new DefaultDecimalFormat( "0.00" ).format( -0.00001 ).equals( "0.00" ) );

        // positive rounding (even neighbor)
        assert ( new DefaultDecimalFormat( "0.00" ).format( 0.014 ).equals( "0.01" ) );
        assert ( new DefaultDecimalFormat( "0.00" ).format( 0.015 ).equals( "0.02" ) );
        assert ( new DefaultDecimalFormat( "0.00" ).format( 0.016 ).equals( "0.02" ) );

        // positive rounding (odd neighbor)
        assert ( new DefaultDecimalFormat( "0.00" ).format( 0.024 ).equals( "0.02" ) );
        assert ( new DefaultDecimalFormat( "0.00" ).format( 0.025 ).equals( "0.03" ) );
        assert ( new DefaultDecimalFormat( "0.00" ).format( 0.026 ).equals( "0.03" ) );

         // negative rounding (even neighbor)
        assert ( new DefaultDecimalFormat( "0.00" ).format( -0.014 ).equals( "-0.01" ) );
        assert ( new DefaultDecimalFormat( "0.00" ).format( -0.015 ).equals( "-0.02" ) );
        assert ( new DefaultDecimalFormat( "0.00" ).format( -0.016 ).equals( "-0.02" ) );

        // negative rounding (odd neighbor)
        assert ( new DefaultDecimalFormat( "0.00" ).format( -0.024 ).equals( "-0.02" ) );
        assert ( new DefaultDecimalFormat( "0.00" ).format( -0.025 ).equals( "-0.03" ) );
        assert ( new DefaultDecimalFormat( "0.00" ).format( -0.026 ).equals( "-0.03" ) );

        // try other numbers of decimal places, with odd and even neighbors
        assert ( new DefaultDecimalFormat( "0.000" ).format( 0.0015 ).equals( "0.002" ) );
        assert ( new DefaultDecimalFormat( "0.000" ).format( 0.0025 ).equals( "0.003" ) );
        assert ( new DefaultDecimalFormat( "0.0000" ).format( 0.00015 ).equals( "0.0002" ) );
        assert ( new DefaultDecimalFormat( "0.0000" ).format( 0.00025 ).equals( "0.0003" ) );
    }

}