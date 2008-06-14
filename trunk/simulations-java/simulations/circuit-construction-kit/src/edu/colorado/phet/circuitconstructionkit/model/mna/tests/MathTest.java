/*  */
package edu.colorado.phet.circuitconstructionkit.model.mna.tests;

/**
 * User: Sam Reid
 * Date: Jun 16, 2006
 * Time: 11:19:26 AM
 */

public class MathTest {
    public static void main( String[] args ) {
        double alpha = 1;
        for ( double t = 0; t < 10; t += 0.1 ) {
            double lhs = alpha * t / ( 1 + alpha * t );
            double rhs = Math.exp( -t / alpha );
            System.out.println( "lhs = " + lhs + ", rhs=" + rhs );
        }
    }
}
