/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.test.schmidt_lee;

import java.text.DecimalFormat;

import edu.colorado.phet.boundstates.test.benfold.Function;
import edu.colorado.phet.boundstates.test.benfold.TestBenfold;


/**
 * TestSchmidtLeeHarmonicOscillator demonstrates that the Schmidt-Lee algorithm
 * has problems with finding the eigenstates for a Harmonic Oscillator potential.
 * With a position range of [-4,+4], the higher energy values are incorrect.
 * As the position range is increased (eg, [-8,+8]), the error is reduced.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TestSchmidtLeeHarmonicOscillator {

    private static final double MASS = 1;
    private static final double OMEGA = 1; // angular frequency
    private static final double HBAR = 0.658;
    private static final double MIN_X = -10;
    private static final double MAX_X = +10;
    
    private static final double ACCEPTABLE_ERROR = 0.1;
    
    private static final int NUMBER_OF_POINTS = 1000;
    private static final int NUMBER_OF_EIGENSTATES = 40;
    
    private static class HarmonicOscillator implements PotentialFunction {
        public double evaluate( double x ) {
            return ( 0.5 * MASS * OMEGA * OMEGA * x * x );
        }
    }
    
    public static void main( String[] args ) {
        
        DecimalFormat format = new DecimalFormat( "0.000" );
        double[] analyticSolution = new double[NUMBER_OF_EIGENSTATES];
        double[] schmidtLeeSolution = new double[NUMBER_OF_EIGENSTATES];
        
        // Analytic solution...
        for ( int n = 0; n < NUMBER_OF_EIGENSTATES; n++ ) {
            analyticSolution[n] = HBAR * OMEGA * ( n + 0.5 );
        }
        
        // Schmidt-Lee solution...
        final double hb = ( HBAR * HBAR ) / ( 2 * MASS );
        final PotentialFunction harmonicOscillator = new HarmonicOscillator();
        for ( int node = 0; node < NUMBER_OF_EIGENSTATES; node++ ) {
            try {
                Wavefunction solver = new Wavefunction( hb, MIN_X, MAX_X, NUMBER_OF_POINTS, node, harmonicOscillator );
                schmidtLeeSolution[node] = solver.getE();
            }
            catch ( Exception e ) {
                e.printStackTrace();
            }
        }
        
        // Print a comparison...
        System.out.println( "En      Analytic   Schmidt-Lee     Benfold" );
        for ( int i = 0; i < NUMBER_OF_EIGENSTATES; i++ ) {
            System.out.print( "E" + i );
            System.out.print( "      " + format.format( analyticSolution[i] ) );
            System.out.print( "      " + format.format( schmidtLeeSolution[i] ) );
            if ( Math.abs( analyticSolution[i] - schmidtLeeSolution[i] ) > ACCEPTABLE_ERROR ) {
                System.out.print( "      *" );
            }
            System.out.println();
        }
    }
}
