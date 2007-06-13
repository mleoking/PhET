/* Copyright 2007, University of Colorado */

package edu.colorado.phet.hydrogenatom.model;


import edu.colorado.phet.common.phetcommon.math.PolynomialTerm;

import java.util.ArrayList;

/**
 * AssociatedLegendrePolynomials implements associated Legendre polymonials.
 * 
 * @author Sam Reid
 */
public class AssociatedLegendrePolynomials {

    /**
     * Solves the associated Legendre polynomial.
     * <p>
     * This solution uses Wolfram's definition of the associated Legendre polynomial.
     * See http://mathworld.wolfram.com/LegendrePolynomial.html.
     * When L > 6, this implemention starts to differ from Wolfram and "Numerical Recipes in C".
     * To compare with Mathematica online, use: x^2*(3)*( LegendreP[7,3,-0.99])
     * as the input to The Integrator at http://integrals.wolfram.com/index.jsp
     * 
     * @param L electron's secondary state
     * @param M electron's tertiary state
     * @param x coordinate on horizontal axis
     * @return double
     * @throws IllegalArgumentException if L > 6
     */
    public static double solve( int L, int M, double x ) {

        // validate arguments
        if ( L > 6 ) {
            throw new IllegalArgumentException( "untested for L > 6" );
        }
        if ( L < 0 ) {
            throw new IllegalArgumentException( "L out of bounds: " + L );
        }
        if ( M < 0 || M > L ) {
            throw new IllegalArgumentException( "M out of bounds: " + M );
        }
        if ( Math.abs( x ) > 1.0 ) {
            throw new IllegalArgumentException( "x out of bounds: " + x );
        }

        ArrayList productTerms = new ArrayList();
        productTerms.add( new PolynomialTerm( 0, 1 ) );
        for ( int i = 0; i < L; i++ ) {
            //x^2-1 times each term on left side
            ArrayList terms = new ArrayList();
            for ( int k = 0; k < productTerms.size(); k++ ) {
                PolynomialTerm term = (PolynomialTerm) productTerms.get( k );
                terms.add( new PolynomialTerm( term.getPower() + 2, term.getCoeff() ) );
                terms.add( new PolynomialTerm( term.getPower(), term.getCoeff() * -1 ) );
            }
            productTerms = terms;
        }
        for ( int k = 0; k < productTerms.size(); k++ ) {
            PolynomialTerm t = (PolynomialTerm) productTerms.get( k );
            productTerms.set( k, t.derive( L + M ) );
        }

        // Wolfram says there is a sign convention difference here
        double legendre = Math.pow( -1, M ) / (Math.pow( 2, L ) * fact( L )) * Math.pow( 1 - x * x, M / 2.0 ) * eval( productTerms, x );
        return legendre;
    }

    private static double eval( ArrayList productTerms, double x ) {
        double sum = 0;
        for ( int i = 0; i < productTerms.size(); i++ ) {
            PolynomialTerm term = (PolynomialTerm) productTerms.get( i );
            sum += term.eval( x );
        }
        return sum;
    }

    private static double fact( int a ) {
        return ( a == 0 || a == 1 ) ? 1 : a * fact( a - 1 );
    }

}
