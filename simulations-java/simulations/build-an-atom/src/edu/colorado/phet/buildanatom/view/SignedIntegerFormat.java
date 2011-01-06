package edu.colorado.phet.buildanatom.view;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

//DOC move this to phetcommon?
/**
 * In Build an Atom, we need to always show the sign for positive and negative numbers (for the charge indication)
 * But never for zero, some examples: -2, -1, 0, +1, +2, etc.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class SignedIntegerFormat extends DecimalFormat {
    private NumberFormat decimalFormat;

    public SignedIntegerFormat() {
        this.decimalFormat = ( DecimalFormat.getNumberInstance( PhetResources.readLocale() ) );
        if ( decimalFormat instanceof DecimalFormat ) {
            ( (DecimalFormat) decimalFormat ).applyPattern( "0" );
        }
    }

    public StringBuffer format( long number, StringBuffer result, FieldPosition fieldPosition ) {
        fieldPosition.setBeginIndex( 0 );
        fieldPosition.setEndIndex( 0 );

        //Add a '+' for positive numbers
        if ( number > 0 ) {
            return new StringBuffer( "+" + number );
        }
        else {
            return new StringBuffer( "" + number );
        }
    }

    @Override
    public Number parse( String text, ParsePosition pos ) {
        //Strip out '+' if the user had typed it in
        if ( text.startsWith( "+" ) ) {
            return new DecimalFormat( "0" ).parse( text.substring( 1 ), pos );
        }
        else {
            return new DecimalFormat( "0" ).parse( text, pos );
        }
    }

    public static void main( String[] args ) {
        for ( int i = -4; i < 10; i++ ) {
            System.out.println( "new SignedIntegerFormat().format(" + i + ") = " + new SignedIntegerFormat().format( i ) );
        }
    }

}
