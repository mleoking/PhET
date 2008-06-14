/*  */
package edu.colorado.phet.circuitconstructionkit.model.mna.tests;

/**
 * User: Sam Reid
 * Date: Jun 15, 2006
 * Time: 3:37:10 PM
 */

public class TestExample9 {
    public static void main( String[] args ) {
        double ds = 0.1;
        for ( double s = 0; s < 1000; s += ds ) {
            double Vin = Math.sin( s / 10.0 );
//            System.out.println( "Vin = " + Vin );
            double value = -5 * s * Vin / ( 1 + 25 * s );
            System.out.println( value );
        }
    }
}
