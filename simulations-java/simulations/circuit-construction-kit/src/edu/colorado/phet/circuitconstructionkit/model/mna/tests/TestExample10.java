/*  */
package edu.colorado.phet.circuitconstructionkit.model.mna.tests;

/**
 * User: Sam Reid
 * Date: Jun 16, 2006
 * Time: 5:36:54 AM
 */

public class TestExample10 {
    public static void main( String[] args ) {
        for ( double s = 0; s < 100; s += 0.1 ) {
            double Vin = Math.cos( s );
            double val = -5 * s / ( 1 + 25 * s ) * Vin;
            System.out.println( val );
        }
    }
}
