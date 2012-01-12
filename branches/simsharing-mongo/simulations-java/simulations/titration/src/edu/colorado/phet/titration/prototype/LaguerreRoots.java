// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.titration.prototype;

import edu.colorado.phet.common.phetcommon.math.Complex;

/**
 * Finds the complex roots of a polynomial using the Laguerre method.
 * Original code and comments by Jonathan Olson in class RootTest.
 * This class factored out of RootTest by Chris Malley.
 *
 * @author Jonathan Olson
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LaguerreRoots {

    /**
     * Finds all of the complex roots of a polynomial (in no particular order)
     * Currently uses iterations, but it could be changed to handle a threshold of how fast the guesses are changing
     * (the variable 'a' in laguerreSingleRoot)
     * <p/>
     * This is accomplished by repeatedly finding some root with Laguerre's Method, factoring that root of the polynomial,
     * and then repeating with the new (lower-degree) polynomial
     * <p/>
     * Additionally, it doesn't check for convergence. This could also be done in laguerreSingleRoot, and could throw an
     * exception if it hasn't "converged" in a certain number of iterations. Apparently Laguerre's Method hardly ever
     * fails to converge.
     *
     * @param polynomial The complex coefficients of the polynomial, from high-to-low. So to specify the polynomial
     *                   3x^3+x-5, pass in (in complex form) {3, 0, 1, -5}. Do not pass 0 as the first coefficient.
     * @param iterations The number of iterations to find each root
     * @return An array of the complex roots of the polynomial, in no particular order
     */
    public static Complex[] findRoots( Complex[] polynomial, int iterations ) {
        Complex[] roots = new Complex[polynomial.length - 1];

        Complex[] poly = new Complex[polynomial.length];
        for ( int i = 0; i < poly.length; i++ ) {
            poly[i] = polynomial[i];
        }
        for ( int i = 0; i < roots.length; i++ ) {
            if ( poly.length > 2 ) {
                roots[i] = findSingleRoot( poly, Complex.ZERO, iterations ); // for now, just use 0
            }
            else if ( poly.length == 2 ) {
                roots[i] = ( new Complex( -1, 0 ) ).getMultiply( poly[1] ).getDivide( poly[0] );
            }
            else {
                throw new RuntimeException( "Can't find the root of a non-zero constant" );
            }
            poly = factorOutRoot( poly, roots[i] );
        }

        return roots;
    }
    
    /**
     * Uses Laguerre's Method to find some root of the polynomial
     *
     * @param poly       The polynomial
     * @param x0         The first guess (convergence doesn't really matter on this)
     * @param iterations Number of iterations to try
     * @return The root
     */
    private static Complex findSingleRoot( Complex[] poly, Complex x0, int iterations ) {
        // order of the polynomial
        Complex n = new Complex( poly.length - 1, 0 );

        Complex[] d1 = differentiate( poly );
        Complex[] d2 = differentiate( d1 );

        Complex x = x0;
        for ( int i = 0; i < iterations; i++ ) {
            Complex px = evaluate( poly, x );

            if ( px.getAbs() == 0 ) {
                break;
            }

            // g = p'(x)/p(x)
            Complex g = evaluate( d1, x ).getDivide( px );

            // h = g^2 - p''(x)/p(x)
            Complex h = g.getMultiply( g ).getSubtract( evaluate( d2, x ).getDivide( px ) );

            // k = sqrt( (n - 1) * (n * h - g^2) )
            Complex k = sqrt( n.getSubtract( 1.0 ).getMultiply( n.getMultiply( h ).getSubtract( g.getMultiply( g ) ) ) );

            // a = n / (g +- k)
            Complex a;
            if ( Double.isInfinite( k.getReal() ) || Double.isInfinite( k.getImaginary() ) ) {
                a = Complex.ZERO;
            }
            else if ( g.getAdd( k ).getAbs() > g.getSubtract( k ).getAbs() ) {
                a = n.getDivide( g.getAdd( k ) );
            }
            else {
                a = n.getDivide( g.getSubtract( k ) );
            }

            // our new guess
            x = x.getSubtract( a );

        }

        return x;
    }
    
    /**
     * Returns a differentiated polynomial
     *
     * @param poly A polynomial
     * @return The derivative of the polynomial
     */
    private static Complex[] differentiate( Complex[] poly ) {
        if ( poly.length < 2 ) {
            return new Complex[]{Complex.ZERO};
        }
        Complex ret[] = new Complex[poly.length - 1];
        for ( int i = 0; i < ret.length; i++ ) {
            ret[i] = poly[i].getMultiply( ret.length - i );
        }
        return ret;
    }
    
    /**
     * Evaluates a polynomial at a specific value using Horner's method.
     *
     * @param poly The polynomial, as specified above
     * @param x    The variable to evaluate at
     * @return p(x)
     */
    private static Complex evaluate( Complex[] poly, Complex x ) {
        //System.out.println( "Evaluating " + complexArrayToString( poly ) + " at " + x );
        Complex cur = new Complex( 0, 0 );
        for ( Complex coeff : poly ) {
            cur = cur.getMultiply( x );
            cur = cur.getAdd( coeff );
        }

        return cur;
    }
    
    /**
     * Implementation of the complex square root. 
     * Maybe should be added to the Complex class, and moved to phetcommon?
     *
     * @param a The complex number
     * @return Its square root (the variety where the real coefficient is positive)
     */
    private static Complex sqrt( Complex a ) {
        if ( a.getImaginary() == 0 && a.getReal() < 0 ) {
            return new Complex( 0, Math.sqrt( -a.getReal() ) );
        }
        else {
            return new Complex(
                    Math.sqrt( ( a.getAbs() + a.getReal() ) / 2.0 ),
                    a.getImaginary() / ( Math.sqrt( 2 * ( a.getAbs() + a.getReal() ) ) )
            );
        }
    }
    
    /**
     * For a polynomial of degree n, it returns a polynomial of degree n-1 where (x - root) has been factored out.
     * Basically, it does synthetic division
     *
     * @param poly The polynomial
     * @param root The root
     * @return Lower degree polynomial
     */
    private static Complex[] factorOutRoot( Complex[] poly, Complex root ) {
        normalizePoly( poly );
        Complex[] ret = new Complex[poly.length - 1];

        ret[0] = poly[0]; // should be a 1
        for ( int i = 1; i < ret.length; i++ ) {
            ret[i] = ret[i - 1].getMultiply( root ).getAdd( poly[i] );
        }

        // remainder should be either 0 or very close.ay off, then the root wasn't really a root
        //Complex remainder = ret[ret.length - 1].getMultiply( root ).getAdd( poly[poly.length - 1] );

        return ret;
    }
    
    /**
     * Normalizes a polynomial (in place). This modifies the polynomial so that its leading coefficient is 1.
     * This will change the evaluation of the polynomial, but it will NOT change the roots
     *
     * @param poly A polynomial
     */
    private static void normalizePoly( Complex[] poly ) {
        Complex lead = poly[0];
        for ( int i = 0; i < poly.length; i++ ) {
            poly[i] = poly[i].getDivide( lead );
        }
    }
}
