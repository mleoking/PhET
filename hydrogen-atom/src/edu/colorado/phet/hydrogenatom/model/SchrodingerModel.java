/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.model;

import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.hydrogenatom.enums.DeBroglieView;


public class SchrodingerModel extends DeBroglieModel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double EULER_MASCHERONI_CONSTANT = 0.57721566490153286060;

    // Limit for the Associated Legendre Polynomial, which contains a sum to infinity.
    private static final int LEGENDRE_POLYNOMIAL_LIMIT = 100;
    
    // Limit for the Gamma Function, which contains a product to infinity.
    private static final int GAMMA_FUNCTION_LIMIT = 100;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // secondary state component, l = 0,...n-1 (n=electron state)
    private int _l;
    // tertiary state component, m = -l,...+l
    private int _m;
    // random number generator for selecting l
    private Random _lRandom;
    // random number generator for selecting m
    private Random _mRandom;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public SchrodingerModel( Point2D position ) {
        super( position );
        super.setView( DeBroglieView.BRIGHTNESS_MAGNITUDE ); // use deBroglie collision detection
        _l = 0;
        _m = 0;
        _lRandom = new Random();
        _mRandom = new Random();
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the electron's secondary state (l).
     * @return int
     */
    public int getSecondaryElectronState() {
        return _l;
    }
    
    /**
     * Gets the electron's tertiary state (m).
     * @return int
     */
    public int getTertiaryElectronState() {
        return _m;
    }
    
    /**
     * Gets the probability density at a point in 3D space.
     * 
     * @param x coordinate on horizontal axis
     * @param y coordinate on axis the is perpendicular to the screen
     * @param z coordinate on vertical axis
     */
    public double getProbabilityDensity( double x, double y, double z ) {
        // convert to Polar coordinates
        double r = Math.sqrt( ( x * x ) + ( y * y ) + ( z * z ) );
        double cosTheta = Math.abs( z ) / r;
        // calculate wave function
        int n = getElectronState();
        double w = getWaveFunction( n, _l, _m, r, cosTheta );
        // square the wave function
        return ( w * w );
    }
    
    //----------------------------------------------------------------------------
    // Superclass overrides
    //----------------------------------------------------------------------------
    
    /*
     * Sets the electron's primary state.
     * Randomly chooses the values for the secondary and tertiary states.
     */
    protected void setElectronState( int n ) {
        _l = getNewSecondaryState( n, _l );
        _m = getNewTertiaryState( _l, _m );
        super.setElectronState( n );
        System.out.println( "SchrodingerModel.setElectronState " + stateToString( n, _l, _m ) );//XXX
    }
    
    /**
     * Unfortunately inherited from deBroglie, irrelevant to Schrodinger.
     */
    public void setView( DeBroglieView view ) {
        throw new UnsupportedOperationException( "SchrodingerModel.setView is not supported" );
    }
    
    //----------------------------------------------------------------------------
    // State change
    //----------------------------------------------------------------------------
    
    /*
     * Chooses a value for the secondary state (l) based on the primary state (n).
     * The new value l' must be in [0,...n-1], and l-l' must be in [-1,1].
     */
    private int getNewSecondaryState( int n, int l ) {

        int lNew = _lRandom.nextInt( n );
        assert( lNew >= 0 && lNew <= n-1 );
        
        //XXX how to apply transition rule A ?
//        assert( Math.abs( l - lNew ) == 1 );
        
        return lNew;
    }
    
    /*
     * Chooses a value for the tertiary state (m) based on the primary state (l).
     * The new value m' must be in [-1,...,+l], and m-m' must be in [-1,0,1].
     */
    private int getNewTertiaryState( int l, int m ) {
        
        int mNew = _mRandom.nextInt( ( 2 * l ) + 1 ) - l;
        assert( Math.abs( mNew ) <= l );
        
        //XXX how to apply transition rule B ?
//        assert( Math.abs( mNew - m ) <= 1 );
        
        return mNew;
    }
    
    //----------------------------------------------------------------------------
    // Wave function
    //----------------------------------------------------------------------------
    
    /*
     * Calculates the wave function.
     */
    private static double getWaveFunction( int n, int l, int m, double r, double cosTheta ) {
        return getLaguerrePolynomial( n, l, r ) * getLegendrePolynomial( l, m, cosTheta );
    }
    
    /*
     * Calculates the Laguerre Polynomial.
     */
    private static double getLaguerrePolynomial( int n, int l, double r ) {
        final double a = BohrModel.getOrbitRadius( n ) / ( n * n );
        final double multiplier = Math.pow( r, l ) * Math.exp( -r / ( n * a ) );
        double b = 2.0 * Math.pow( ( n * a ), ( -1.5 ) ); // b0
        double sum = 0;
        final int limit = n - l - 1;
        for ( int j = 1; j <= limit; j++ ) {
            b = ( 2.0 / ( n * a ) ) * ( ( j + l + n ) / ( j * ( j + ( 2.0 * l ) + 1.0 ) ) ) * b;
            sum += ( b * Math.pow( r, j ) );
        }
        return ( multiplier * sum );
    }
    
    /*
     * Calculates the Legendre Polynomial.
     */
    private static double getLegendrePolynomial( int l, int m, double cosTheta ) {
        final double t1 = 1.0 / ( gamma( -l ) * gamma( l + 1 ) );
        final double t2 = Math.pow( ( ( 1 + cosTheta ) / ( 1 - cosTheta ) ), ( 0.5 * m ) );
        double sum = 0;
        for ( int n = 0; n <= LEGENDRE_POLYNOMIAL_LIMIT; n++ ) {
            double t3 = ( gamma( n -l ) * gamma( n + l + 1 ) ) / (double)( gamma( n + 1 - m ) * factorial( n ) );
            double t4 = Math.pow( ( 1.0 - cosTheta ) / 2.0, n );
            sum += ( t3 * t4 );
        }
        return ( t1 * t2 * sum );
    }
    
    /*
     * Gamma function, works only if x is an integer.
     */
    private static int gamma( int x ) {
        int gamma = 1;
        if ( x > 0 ) {
            gamma = factorial( x - 1 );
        }
        return gamma;
    }
    
    /*
     * Gamma function, works for non-integer values.
     */
    private static double gamma( double x ) {
        final double multiplier = ( Math.exp( -EULER_MASCHERONI_CONSTANT * x ) );
        double product = 1;
        for ( int n = 1; n <= GAMMA_FUNCTION_LIMIT; n++ ) {
            product *= Math.pow( 1 + ( x / n ), - 1 ) * Math.exp( x / n );
        }
        return ( multiplier * product );
    }
    
    /*
     * Factorial method.
     */
    private static int factorial( int n ) {
        if ( n < 0 ) {
            throw new IllegalArgumentException( "factorial is undefined for negative integers: " + n );
        }
        int factorial = 1; // 0! = 1, 1! = 1
        if ( n > 1 ) {
            for ( int i = n; i > 0; i-- ) {
                factorial *= i;
            }
        }
        return factorial;
    }
    
    //----------------------------------------------------------------------------
    // Debug
    //----------------------------------------------------------------------------
    
    private static String stateToString( int n, int l, int m ) {
        return "(n,l,m)=(" + n + "," + l + "," + m + ")";
    }
}
