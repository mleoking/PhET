/* Copyright 2007, University of Colorado */

package edu.colorado.phet.hydrogenatom.model;


import java.util.ArrayList;

/**
 * AssociatedLegendrePolynomials imlpements associated Legendre polymonials.
 * 
 * @author Sam Reid / Chris Malley
 */
public class AssociatedLegendrePolynomials {

    /**
     * Solves the associated Legendre polynomial.
     * 
     * @param l electron's secondary state
     * @param m electron's tertiary state
     * @param x coordinate on horizontal axis
     * @return double
     */
    public static double solve( int l, int m, double x ) {
        return solveWolfram( l, m, x );
    }

    /*
     * Solution that uses Wolfram's definition of the associated Legendre polynomial.
     * See http://mathworld.wolfram.com/LegendrePolynomial.html.
     * When l > 6, this implemention starts to differ from Wolfram and "Numerical Recipes in C".
     * To compare with Mathematica online, use: x^2*(3)*( LegendreP[7,3,-0.99])
     * as the input to The Integrator at http://integrals.wolfram.com/index.jsp
     * 
     * @param l electron's secondary state
     * @param m electron's tertiary state
     * @param x coordinate on horizontal axis
     * @return double
     * @throws IllegalArgumentException if l > 6
     */
    private static double solveWolfram( int l, int m, double x ) {

        // validate arguments
        if ( l > 6 ) {
            throw new IllegalArgumentException( "untested for l > 6" );
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
        productTerms.add( new Term( 0, 1 ) );
        for ( int i = 0; i < l; i++ ) {
            //x^2-1 times each term on left side
            ArrayList terms = new ArrayList();
            for ( int k = 0; k < productTerms.size(); k++ ) {
                Term term = (Term) productTerms.get( k );
                terms.add( new Term( term.getPower() + 2, term.getCoeff() ) );
                terms.add( new Term( term.getPower(), term.getCoeff() * -1 ) );
            }
            productTerms = terms;
        }
        for ( int k = 0; k < productTerms.size(); k++ ) {
            Term t = (Term) productTerms.get( k );
            t.derive( l + m );
        }

        // Wolfram says there is a sign convention difference here
        double legendre = Math.pow( -1, m ) * Math.pow( 2, -l ) / fact( l ) * Math.pow( 1 - x * x, m / 2.0 ) * eval( productTerms, x );
        return legendre;
    }

    private static double eval( ArrayList productTerms, double x ) {
        double sum = 0;
        for ( int i = 0; i < productTerms.size(); i++ ) {
            Term term = (Term) productTerms.get( i );
            sum += term.eval( x );
        }
        return sum;
    }

    private static double fact( int a ) {
        return ( a == 0 || a == 1 ) ? 1 : a * fact( a - 1 );
    }

    private static class Term {

        int power;
        int coeff;

        public Term( int power, int coeff ) {
            this.power = power;
            this.coeff = coeff;
        }

        public int getPower() {
            return power;
        }

        public int getCoeff() {
            return coeff;
        }

        public void derive( int numDer ) {
            for ( int i = 0; i < numDer; i++ ) {
                derive();
            }
        }

        private void derive() {
            if ( power == 0 ) {
                power = 0;
                coeff = 0;
            }
            else {
                int newCoeff = coeff * power;
                int newPower = power - 1;
                this.coeff = newCoeff;
                this.power = newPower;
            }
        }

        public double eval( double x ) {
            return Math.pow( x, power ) * coeff;
        }
    }

    //XXX remove
    /*
     * Solution obtained from ARTS (Atmospheric Radiative Transfer Simulator)
     * at http://www.sat.uni-bremen.de/arts.
     * The original function was written in C, appeared in legendre.cc, 
     * and was named legengre_poly.  The license is GPL, and we plan to 
     * replace it with Wolfram.
     */
    private static double solveARTS( int l, int m, double x ) {

        if ( m < 0 || m > l || Math.abs( x ) > 1.0 ) {
            throw new IllegalArgumentException( "illegal argument" );
        }

        double pmm = 1.0;
        double result = 0;

        if ( m > 0 ) {
            double fact = 1.0;
            final double somx2 = Math.sqrt( ( 1.0 - x ) * ( 1.0 + x ) );
            for ( int i = 1; i <= m; i++ ) {
                pmm *= -fact * somx2;
                fact += 2.0;
            }
        }

        if ( l == m ) {
            result = pmm;
        }
        else {
            double pmmp1 = x * ( 2 * m + 1 ) * pmm;
            if ( l == ( m + 1 ) ) {
                return pmmp1;
            }
            else {
                for ( int ll = ( m + 2 ); ll <= l; ll++ ) {
                    result = ( x * ( 2 * ll - 1 ) * pmmp1 - ( ll + m - 1 ) * pmm ) / ( ll - m );
                    pmm = pmmp1;
                    pmmp1 = result;
                }
            }
        }

        return result;
    }

    //XXX remove
    /**
     * Compares the two solvers for 0 <= l <= 6.
     */
    public static void main( String[] args ) {
        int successCount = 0;
        int totalCount = 0;
        for ( int l = 0; l <= 6; l++ ) {
            for ( int m = 0; m <= l; m++ ) {
                for ( double x = -1.0; x <= 1.0; x += 0.01 ) {
                    double a = solveARTS( l, m, x );
                    double b = solveWolfram( l, m, x );
                    double diff = Math.abs( a - b );
                    totalCount++;
                    if ( diff < 1E-7 ) {
                        successCount++;
                    }
                    else {
                        System.out.println( "fail: l=" + l + " m=" + m + " x=" + x + " a=" + a + " b=" + b );
                    }
                }
            }
        }
        System.out.println( "successCount = " + successCount );
        System.out.println( "totalCount = " + totalCount );
    }
}
