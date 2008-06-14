/*  */
package edu.colorado.phet.circuitconstructionkit.model.mna.tests;

/**
 * User: Sam Reid
 * Date: Jun 15, 2006
 * Time: 3:37:10 PM
 */

public class TestExample7 {
    public static void main( String[] args ) {
        double Vin = 5;
        double ds = 0.1;
        for ( double s = 0; s < 10; s += ds ) {
            double value = s * ( s + 1000000 ) * Vin / ( 1000100000 * s + 2100 * Math.pow( s, 2 ) + Math.pow( s, 3 ) + 100000000000.0 );
            System.out.println( value );
        }
    }
}
