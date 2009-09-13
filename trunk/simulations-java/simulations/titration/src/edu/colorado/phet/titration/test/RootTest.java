package edu.colorado.phet.titration.test;

import java.util.Date;

import edu.colorado.phet.common.phetcommon.math.Complex;

/**
 * Test for computing roots of polynomials.
 * Feel free to move stuff to phetcommon, change things, etc.
 *
 * @author Jonathan Olson
 */
public class RootTest {
    public static void main( String[] args ) {
        // roots at 5, i, -i
        Complex[] p1 = new Complex[]{new Complex( 1, 0 ), new Complex( -5, 0 ), new Complex( 1, 0 ), new Complex( -5, 0 )};

        // roots at 3, -2
        Complex[] p2 = new Complex[]{new Complex( 1, 0 ), new Complex( -1, 0 ), new Complex( -6, 0 )};

        // mathematica says: {{x->2.74722},{x->-0.481112-0.506256 i},{x->-0.481112+0.506256 i},{x->0.457504-0.559537 i},{x->0.457504+0.559537 i}}
        Complex[] p3 = new Complex[]{new Complex( 10, 0 ), new Complex( -27, 0 ), new Complex( 0, 0 ), new Complex( -3, 0 ), new Complex( 1, 0 ), new Complex( -7, 0 )};
        
        // Laguerre returns NaN for some roots
        // mathematica says:
        //  -0.788172 +- 0.554086 i
        //  0.266144 +- 0.896483 i
        //  0.91802
        Complex[] p4 = new Complex[]{new Complex( 33.79999999999998,0 ), new Complex( 4.259999999999997, 0),  new Complex( 0.1759999999996617, 0 ), new Complex( -0.007400000000033827, 0 ), new Complex( -0.006620000000003383, 0 ), new Complex( -3.3799999999999987E-16, 0 )};

        System.out.println( "Laguerre roots of p1: " + complexArrayToString( findRootsLaguerre( p1, 30 ) ) );
        System.out.println( "Durand-Kerner roots of p1: " + complexArrayToString( findRootsDurandKerner( p1, 30 ) ) );

        System.out.println( "Laguerre roots of p2: " + complexArrayToString( findRootsLaguerre( p2, 30 ) ) );
        System.out.println( "Durand-Kerner roots of p2: " + complexArrayToString( findRootsDurandKerner( p2, 30 ) ) );

        System.out.println( "Laguerre roots of p3: " + complexArrayToString( findRootsLaguerre( p3, 30 ) ) );
        System.out.println( "Durand-Kerner roots of p3: " + complexArrayToString( findRootsDurandKerner( p3, 30 ) ) );
        
        System.out.println( "Laguerre roots of p4: " + complexArrayToString( findRootsLaguerre( p4, 300 ) ) );
        System.out.println( "Durand-Kerner roots of p4: " + complexArrayToString( findRootsDurandKerner( p4, 300 ) ) );

        Date a = new Date();
        for ( int i = 0; i < 100000; i++ ) {
            findRootsLaguerre( p3, 30 );
        }
        Date b = new Date();
        for ( int i = 0; i < 100000; i++ ) {
            findRootsDurandKerner( p3, 30 );
        }
        Date c = new Date();
        for ( int i = 0; i < 100000; i++ ) {
            findRootsOptimizedDurandKerner( p3, 30, 0.00001 );
        }
        Date d = new Date();

        // without thresholding, neither has a huge advantage with 30 iterations

        System.out.println( "Laguerre: " + ( b.getTime() - a.getTime() ) );
        System.out.println( "Durnand-Kerner: " + ( c.getTime() - b.getTime() ) );
        System.out.println( "Optimized Durnand-Kerner: " + ( d.getTime() - c.getTime() ) );

    }

    /**
     * Finds all of the complex roots of a polynomial (in no particular order)
     * Currently uses iterations, but it could be changed to handle a threshold of how fast the guesses are changing
     * (compare guess to next)
     * <p/>
     * Looks simpler, and possibly just better than the Laguerre-based method. But test this!
     *
     * @param polynomial The complex coefficients of the polynomial, from high-to-low. So to specify the polynomial
     *                   3x^3+x-5, pass in (in complex form) {3, 0, 1, -5}.
     * @param iterations The number of iterations to find each root
     * @return An array of the complex roots of the polynomial, in no particular order
     */
    public static Complex[] findRootsDurandKerner( Complex[] polynomial, int iterations ) {
        Complex[] guess = new Complex[polynomial.length - 1];

        // spread out the initial guesses
        Complex initializer = new Complex( 0.4, 0.9 );
        for ( int i = 0; i < guess.length; i++ ) {
            guess[i] = initializer;
            initializer = initializer.getMultiply( initializer );
        }

        for ( int i = 0; i < iterations; i++ ) {
            Complex[] next = new Complex[guess.length];
            for ( int k = 0; k < guess.length; k++ ) {
                Complex value = evaluate( polynomial, guess[k] );
                for ( int l = 0; l < guess.length; l++ ) {
                    if ( l == k ) { continue;}

                    // don't divide by zero
                    if ( guess[l] == guess[k] ) {continue; }

                    value = value.getDivide( guess[k].getSubtract( guess[l] ) );
                }
                next[k] = guess[k].getSubtract( value );
            }
            guess = next;
        }

        return guess;
    }

    public static Complex[] findRootsOptimizedDurandKerner( Complex[] polynomial, int iterations, double threshold ) {
        Complex[] guess = new Complex[polynomial.length - 1];

        // spread out the initial guesses
        Complex initializer = new Complex( 0.4, 0.9 );
        for ( int i = 0; i < guess.length; i++ ) {
            guess[i] = initializer;
            initializer = initializer.getMultiply( initializer );
        }

        for ( int i = 0; i < iterations; i++ ) {
            Complex[] next = new Complex[guess.length];
            boolean belowThreshold = true;
            for ( int k = 0; k < guess.length; k++ ) {
                Complex value = evaluate( polynomial, guess[k] );
                if ( value.getAbs() > threshold ) {
                    belowThreshold = false;
                }
                for ( int l = 0; l < guess.length; l++ ) {
                    if ( l == k ) { continue;}

                    // don't divide by zero
                    if ( guess[l] == guess[k] ) {continue; }

                    value = value.getDivide( guess[k].getSubtract( guess[l] ) );
                }
                next[k] = guess[k].getSubtract( value );
            }
            guess = next;

            // already did the computation of the next guess, so use that (even though we were below the threshold before)
            if( belowThreshold ) {
                break;
            }
        }

        return guess;
    }


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
    public static Complex[] findRootsLaguerre( Complex[] polynomial, int iterations ) {
        Complex[] roots = new Complex[polynomial.length - 1];

        Complex[] poly = new Complex[polynomial.length];
        for ( int i = 0; i < poly.length; i++ ) {
            poly[i] = polynomial[i];
        }
        for ( int i = 0; i < roots.length; i++ ) {
            if ( poly.length > 2 ) {
                roots[i] = laguerreSingleRoot( poly, Complex.ZERO, iterations ); // for now, just use 0
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
     * Turn an array of Complex numbers into a string
     *
     * @param poly A polynomial (or list of roots)
     * @return A string
     */
    public static String complexArrayToString( Complex[] poly ) {
        String out = "{ ";
        for ( int i = 0; i < poly.length; i++ ) {
            out += String.valueOf( poly[i] ) + " ";
        }
        out += "}";
        return out;
    }

    /**
     * Evaluates a polynomial at a specific value using Horner's method.
     *
     * @param poly The polynomial, as specified above
     * @param x    The variable to evaluate at
     * @return p(x)
     */
    private static Complex evaluate( Complex[] poly, Complex x ) {
        Complex cur = new Complex( 0, 0 );
        for ( Complex coeff : poly ) {
            cur = cur.getMultiply( x );
            cur = cur.getAdd( coeff );
        }

        return cur;
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

    /**
     * Implementation of the complex square root. Maybe should be added to the Complex class, and moved to phetcommon?
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
     * Uses Laguerre's Method to find some root of the polynomial
     *
     * @param poly       The polynomial
     * @param x0         The first guess (convergence doesn't really matter on this)
     * @param iterations Number of iterations to try
     * @return The root
     */
    private static Complex laguerreSingleRoot( Complex[] poly, Complex x0, int iterations ) {
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
            //System.out.println( "h: " + h );

            // k = sqrt( (n - 1) * (n * h - g^2) )
            Complex k = sqrt( n.getSubtract( 1.0 ).getMultiply( n.getMultiply( h ).getSubtract( g.getMultiply( g ) ) ) );

            // a = n / (g +- k)
            Complex a;
            if ( g.getAdd( k ).getAbs() > g.getSubtract( k ).getAbs() ) {
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

}
