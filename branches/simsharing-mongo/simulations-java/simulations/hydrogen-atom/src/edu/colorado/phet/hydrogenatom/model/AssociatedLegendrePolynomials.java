// Copyright 2002-2011, University of Colorado

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
     * When l > 6, this implemention starts to differ from Wolfram and "Numerical Recipes in C".
     * To compare with Mathematica online, use: x^2*(3)*( LegendreP[7,3,-0.99])
     * as the input to The Integrator at http://integrals.wolfram.com/index.jsp
     * <p>
     * For a description of why this doesn't work for l > 6,
     * see Section 6.8 of "Numerical Recipes in C, Second Edition" (1992) at
     * http://www.nrbook.com/a/bookcpdf/c6-8.pdf
     * 
     * @param l electron's secondary state
     * @param m electron's tertiary state
     * @param x coordinate on horizontal axis
     * @return double
     * @throws IllegalArgumentException if l > 6
     */
    public static double solve( int l, int m, double x ) {

        // validate arguments
        if ( l > 6 ) {
            // For large l, the brute-force solution below encounters instabilities.
            throw new IllegalArgumentException( "unstable for l > 6" );
        }
        if ( l < 0 ) {
            throw new IllegalArgumentException( "l out of bounds: " + l );
        }
        if ( m < 0 || m > l ) {
            throw new IllegalArgumentException( "m out of bounds: " + m );
        }
        if ( Math.abs( x ) > 1.0 ) {
            throw new IllegalArgumentException( "x out of bounds: " + x );
        }

        ArrayList productTerms = new ArrayList();
        productTerms.add( new PolynomialTerm( 0, 1 ) );
        for ( int i = 0; i < l; i++ ) {
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
            productTerms.set( k, t.derive( l + m ) );
        }

        // Wolfram says there is a sign convention difference here
        double legendre = Math.pow( -1, m ) / (Math.pow( 2, l ) * fact( l )) * Math.pow( 1 - x * x, m / 2.0 ) * eval( productTerms, x );
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
