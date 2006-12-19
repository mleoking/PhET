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

import edu.colorado.phet.common.math.ProbabilisticChooser;
import edu.colorado.phet.hydrogenatom.enums.DeBroglieView;
import edu.colorado.phet.hydrogenatom.util.RandomUtils;


public class SchrodingerModel extends DeBroglieModel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // example: TRANSITION_STRENGTH[5][0] is the transition strength from n=6 to n=1
    private static final double[][] TRANSITION_STRENGTH = {
        { 0, 0, 0, 0, 0 },
        { 12.53, 0, 0, 0 },
        { 3.34, 0.87, 0, 0, 0 },
        { 1.36, 0.24, 0.07, 0, 0 },
        { 0.69, 0.11, 0, 0.04, 0 },
        { 0.39, 0.06, 0.02, 0, 0 }
    };
    
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
        super.setView( DeBroglieView.BRIGHTNESS_MAGNITUDE ); // use deBroglie "rings" for collision detection
        
        // many assumptions herein that the smallest value of n is 1
        assert( getGroundState() == 1 );
        
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
     * This algorithm is undefined for (x,y,z) = (0,0,0).
     * 
     * @param x coordinate on horizontal axis
     * @param y coordinate on axis the is perpendicular to the screen
     * @param z coordinate on vertical axis
     * @return double
     */
    public double getProbabilityDensity( double x, double y, double z ) {
        int n = getElectronState();
        return getProbabilityDensity( n, _l, _m, x, y, z );
    }
    
    //----------------------------------------------------------------------------
    // Superclass overrides
    //----------------------------------------------------------------------------
    
    /*
     * Determines if a proposed state transition is legal.
     * 
     * @param nOld
     * @param nNew
     */
    protected boolean isaLegalTransition( final int nOld, final int nNew ) {
        boolean legal = true;
        if ( nNew == nOld ) {
            legal = false;
        }
        else if ( nOld == 2 && _l == 0 ) {
            // transition from nlm=(2,0,0) to (1,0,0) would break the abs(l-l')=0 rule
            legal = false;
        }
        return legal;
    }
    
    /*
     * Chooses a new primary state (n) for the electron.
     * The new state is a lower state, and this is called during spontaneous emission.
     * The possible values of n are limited by the current value of l, since abs(l-l') must be 1.
     * The probability of each possible n transition is determined by its transition strength.
     * 
     * @return int positive state number, -1 if there is no state could be chosen
     */
    protected int chooseLowerElectronState() {
        
        final int n = getElectronState();
        int nNew = 1;
        if ( n == 2 && _l == 0 ) {
            // transition from nlm=(2,0,0) to (1,0,0) would break the abs(l-l')=0 rule
            return -1;
        }
        else if ( n > 2 ) {
            final int nMax = n - 1;
            final int nMin = Math.max( _l - 1, 1 );
            ProbabilisticChooser.Entry[] entries = new ProbabilisticChooser.Entry[nMax - nMin + 1];
            for ( int i = 0; i < entries.length; i++ ) {
                int state = nMin + i;
                entries[i] = new ProbabilisticChooser.Entry( new Integer( state ), TRANSITION_STRENGTH[n][state] );
            }
            ProbabilisticChooser chooser = new ProbabilisticChooser( entries );
            nNew = ( (Integer) chooser.get() ).intValue();
        }
        
        assert( nNew >= 1 );
        assert( nNew <= getNumberOfStates() );
        return nNew;
    }
    
    /*
     * Sets the electron's primary state.
     * Randomly chooses the values for the secondary and tertiary states.
     */
    protected void setElectronState( final int nNew ) {
        
        int lNew = getNewSecondaryState( nNew, _l );
        int mNew = getNewTertiaryState( lNew, _m );
        
        // Verify that no transition rules have been broken
        assert( nNew >= 1 && nNew <= 6 );
        assert( lNew >= 0 && lNew <= nNew - 1 );
        assert( Math.abs( _l - lNew ) == 1 );
        assert( mNew >= -lNew && mNew <= +lNew );
        assert( Math.abs( _m - mNew ) <= 1 );
        
        _l = lNew;
        _m = mNew;
        super.setElectronState( nNew );
//        System.out.println( "SchrodingerModel.setElectronState " + stateToString( n, _l, _m ) );//XXX
    }
    
    /*
     * Our Schrodinger model emits photons from a random point on the first Bohr orbit.
     * @return Point2D
     */
    protected Point2D getSpontaneousEmissionPosition() {
        // random point on the orbit
        double radius = BohrModel.getOrbitRadius( GROUND_STATE );
        double angle = RandomUtils.nextAngle();
        // convert to Cartesian coordinates, adjust for atom's position
        double x = ( radius * Math.cos( angle ) ) + getX();
        double y = ( radius * Math.sin( angle ) ) + getY();
        return new Point2D.Double( x, y );
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
     * 
     * @param nNew the new primary state
     * @param lOld the existing secondary state
     * @return the new secondary state
     */
    private int getNewSecondaryState( final int nNew, final int lOld ) {

        int lNew = 0;
        
        if ( lOld == 0 ) {
            lNew = 1;
        }
        else if ( lOld > nNew - 1 ) {
            lNew = nNew - 1;
        }
        else if ( lOld == nNew - 1 ) {
            lNew = nNew - 2;
        }
        else {
            if ( _lRandom.nextBoolean() ) {
                lNew = lOld + 1;
            }
            else {
                lNew = lOld - 1;
            }
        }
        
//        System.out.println( "SchrodingerModel.getSecondaryState n=" + nNew + " l=" + lOld + " lNew=" + lNew );//XXX
        assert( lNew >= 0 );
        assert( lNew <= nNew - 1 );
        assert( Math.abs( lOld - lNew ) == 1 );
        return lNew;
    }
    
    /*
     * Chooses a value for the tertiary state (m) based on the primary state (l).
     * The new value m' must be in [-l,...,+l], and m-m' must be in [-1,0,1].
     * 
     * @param lNew the new secondary state
     * @param mOld the existing tertiary state
     * @param the new tertiary state
     */
    private int getNewTertiaryState( final int lNew, final int mOld ) {
        
        int mNew = 0;
        
        if ( lNew == 0 ) {
            mNew = 0;
        }
        else if ( mOld > lNew ) {
            mNew = lNew;
        }
        else if ( mOld < -lNew ) {
            mNew = -lNew;
        }
        else if ( mOld == lNew ) {
            int a = _mRandom.nextInt( 2 );
            if ( a == 0 ) {
                mNew = mOld;
            }
            else {
                mNew = mOld - 1;
            }
        }
        else if ( mOld == -lNew ) {
            int a = _mRandom.nextInt( 2 );
            if ( a == 0 ) {
                mNew = mOld;
            }
            else {
                mNew = mOld + 1;
            }
        }
        else {
            int a = _mRandom.nextInt( 3 );
            if ( a == 0 ) {
                mNew = mOld + 1;
            }
            else if ( a == 1 ) {
                mNew = mOld - 1;
            }
            else {
                mNew = mOld;
            }
        }
        
//        System.out.println( "SchrodingerModel.getTeritiaryState l=" + lNew + " m=" + mOld + " mNew=" + mNew );//XXX
        assert( mNew >= -lNew );
        assert( mNew <= +lNew );
        assert( Math.abs( mOld - mNew ) <= 1 );
        return mNew;
    }
    
    //----------------------------------------------------------------------------
    // Wave function
    //----------------------------------------------------------------------------
    
    /**
     * Probability Density.
     * This algorithm is undefined for (x,y,z) = (0,0,0).
     * 
     * @param n primary state
     * @param l secondary state
     * @param m tertiary state
     * @param x coordinate on horizontal axis
     * @param y coordinate on axis the is perpendicular to the screen
     * @param z coordinate on vertical axis
     * @return double
     */
    public static double getProbabilityDensity( int n, int l, int m, double x, double y, double z ) {
        if ( n < 1 ) {
            throw new IllegalArgumentException( "violated 1 <= n" );
        }
        if ( l < 0 || l >= n ) {
            throw new IllegalArgumentException( "violated 0 <= l <= n-1" );
        }
        if ( m < -l || m > l ) {
            throw new IllegalArgumentException( "violated -l <= m <= +l" );
        }
        if ( x == 0 && y == 0 && z == 0 ) {
            throw new IllegalArgumentException( "undefined for (x,y,z)=(0,0,0)" );
        }
        // convert to Polar coordinates
        double r = Math.sqrt( ( x * x ) + ( y * y ) + ( z * z ) );
        double cosTheta = Math.abs( z ) / r;
        // calculate wave function
        double w = getWaveFunction( n, l, m, r, cosTheta );
        // square the wave function
        return ( w * w );
    }
    
    /*
     * Wavefunction.
     */
    private static double getWaveFunction( int n, int l, int m, double r, double cosTheta ) {
        final double t1 = getGeneralizedLaguerrePolynomial( n, l, r );
        final double t2 = getAssociatedLegendrePolynomial( l, Math.abs( m ), cosTheta );
        return ( t1 * t2 );
    }
    
    /*
     * Generalized Laguerre Polynomial.
     * Codified from design document.
     */
    private static double getGeneralizedLaguerrePolynomial( int n, int l, double r ) {
        final double a = BohrModel.getOrbitRadius( n ) / ( n * n );
        final double multiplier = Math.pow( r, l ) * Math.exp( -r / ( n * a ) );
        final double b0 = 2.0 * Math.pow( ( n * a ), ( -1.5 ) ); // b0
        final int limit = n - l - 1;
        double bj = b0;
        double sum = b0; // j==0
        for ( int j = 1; j <= limit; j++ ) {
            bj = ( 2.0 / ( n * a ) ) * ( ( j + l - n ) / ( j * ( j + ( 2.0 * l ) + 1.0 ) ) ) * bj;
            sum += ( bj * Math.pow( r, j ) );
        }
        return ( multiplier * sum );
    }
    
    /*
     * Associated Legendre Polynomial.
     * This does not correspond to the design document.
     * 
     * This implementation was obtained from ARTS (Atmospheric Radiative Transfer Simulator)
     * at http://www.sat.uni-bremen.de/arts.
     * The original function was written in C, appeared in legendre.cc, 
     * and was named legengre_poly. Here we have ported it to Java.
     * The original code is GPL, and the original copyright appears below.
     * 
     * =================================================================
     * Copyright (C) 2003 Oliver Lemke  <olemke@uni-bremen.de>
     * This program is free software; you can redistribute it and/or modify it
     * under the terms of the GNU General Public License as published by the
     * Free Software Foundation; either version 2, or (at your option) any
     * later version.
     *
     * This program is distributed in the hope that it will be useful,
     * but WITHOUT ANY WARRANTY; without even the implied warranty of
     * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
     * GNU General Public License for more details.
     *
     * You should have received a copy of the GNU General Public License
     * along with this program; if not, write to the Free Software
     * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
     * =================================================================
     */
    private static double getAssociatedLegendrePolynomial( int l, int m, double x ) {

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
    
    //----------------------------------------------------------------------------
    // Debug
    //----------------------------------------------------------------------------
    
    public static String stateToString( int n, int l, int m ) {
        return "(n,l,m)=(" + n + "," + l + "," + m + ")";
    }
}
