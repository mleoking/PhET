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

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.hydrogenatom.enums.DeBroglieView;


public class SchrodingerModel extends DeBroglieModel {

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
     * This algorithm is undefined for (x,y,z) = (0,0,0).
     * 
     * @param x coordinate on horizontal axis
     * @param y coordinate on axis the is perpendicular to the screen
     * @param z coordinate on vertical axis
     * @return double
     * @throws IllegalArgumentException if all args are 0
     */
    public double getProbabilityDensity( double x, double y, double z ) {
        if (  ) {
            throw new IllegalArgumentException( "undefined for (x,y,z)=(0,0,0)" );
        }
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
//        System.out.println( "SchrodingerModel.setElectronState " + stateToString( n, _l, _m ) );//XXX
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
     * Wave function.
     */
    private static double getWaveFunction( int n, int l, int m, double r, double cosTheta ) {
        final double t1 = getLaguerrePolynomial( n, l, r );
        final double t2 = getAssociatedLegendrePolynomial( l, Math.abs( m ), cosTheta );
        return ( t1 * t2 );
    }
    
    /*
     * Laguerre Polynomial.
     * Codified from design document.
     */
    private static double getLaguerrePolynomial( int n, int l, double r ) {
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
    
    private static String stateToString( int n, int l, int m ) {
        return "(n,l,m)=(" + n + "," + l + "," + m + ")";
    }
}
