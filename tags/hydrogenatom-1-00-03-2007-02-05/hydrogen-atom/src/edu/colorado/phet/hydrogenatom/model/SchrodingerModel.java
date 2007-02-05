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

/**
 * SchrodingerModel is the Schrodinger model of the hydrogen atom.
 * <p>
 * Physical representation:
 * Electron is a probability density field.
 * Proton is at the center, visible only when the probability density 
 * field strength is below a threshold value.
 * The atom's state has 3 components (n,l,m). See transition rules below.
 * <p>
 * Wavefunction:
 * This implementation solves the 3D Schrodinger wavefunction,
 * used to compute probability density values in 3D space.
 * <p>
 * Collision behavior:
 * Identical to the "brightness" views of deBroglie, which is why this
 * class is an extension of DeBroglieModel.
 * <p>
 * Absorption behavior:
 * Identical to Borh and deBroglie.
 * <p>
 * Emission behavior:
 * Both spontaneous and stimulated emission are similar to Bohr and 
 * deBroglie, but the rules for transitions (see below) are more 
 * complicated.
 * <p>
 * Transition rules:
 * All of the following rules must be obeyed when choosing a transition.
 * <ol>
 * <li>n = [1...6] as in Bohr and deBroglie
 * <li>l = [0...n-1]
 * <li>m = [-l...+l]
 * <li>abs(l-l') = 1
 * <li>abs(m-m') < 1
 * <li>n transitions have varying transition strengths
 * <li>valid l and m transitions have equal probability
 * </ol>
 * Note that transitions from state nlm=(2,0,0) are a special case.
 * The lower state (1,0,0) is not possible since it violates the abs(l-l')=1 rule.
 * The only way to get out of this state (2,0,0) is by going to a higher state.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SchrodingerModel extends DeBroglieModel {

    //----------------------------------------------------------------------------
    // Debug
    //----------------------------------------------------------------------------
    
    private static final boolean DEBUG_STATE_TRANSITIONS = false;
    
    private static final boolean DEBUG_REJECTED_TRANSITIONS = false;
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    /*
     * This table defines the transition strengths for the primary state component (n).
     * Some of the entries in this table are non-sensical, but their strengths are 
     * zero and it helps to have a symmetrical table.  This table was taken from
     * the simulation design document.
     * 
     * Here's an example that shows how the table is indexed:
     * TRANSITION_STRENGTH[5][0] is the transition strength from n=6 to n=1
     */
    private static final double[][] TRANSITION_STRENGTH = {
        { 0, 0, 0, 0, 0 },
        { 12.53, 0, 0, 0, 0 },
        { 3.34, 0.87, 0, 0, 0 },
        { 1.36, 0.24, 0.07, 0, 0 },
        { 0.69, 0.11, 0, 0.04, 0 },
        { 0.39, 0.06, 0.02, 0, 0 }
    };
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    /* primary state component (n) is part of superclass */
    
    // secondary state component, l = 0,...n-1 (n=electron state)
    private int _l;
    // tertiary state component, m = -l,...+l
    private int _m;
    
    // random number generator for selecting l
    private Random _lRandom;
    // random number generator for selecting m
    private Random _mRandom;
    
    private Point2D _spontaneousEmissionPoint;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * @param position the atom's position in 2D space
     */
    public SchrodingerModel( Point2D position ) {
        super( position );
        super.setView( DeBroglieView.BRIGHTNESS_MAGNITUDE ); // use deBroglie "rings" for collision detection
        
        // many assumptions herein that n=1 is the ground state
        assert( getGroundState() == 1 );
        
        _l = 0;
        _m = 0;
        _lRandom = new Random();
        _mRandom = new Random();
        _spontaneousEmissionPoint = new Point2D.Double();
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

    //----------------------------------------------------------------------------
    // Superclass overrides
    //----------------------------------------------------------------------------
    
    /*
     * Probabilistically determines whether to absorb a photon.
     * Typically we defer to the superclass implementation.
     * But if we're in state (2,0,0), the probability is 100%. 
     * This is not physically correct, but we want to make it easier
     * to get out of state (2,0,0).
     * 
     * @return true or false
     */
    protected boolean absorptionIsCertain() {
        if ( getElectronState() == 2 && _l == 0 ) {
            return true;
        }
        return super.absorptionIsCertain();
    }
    
    /*
     * Determines if a proposed state transition caused by stimulated emission is allowed.
     * 
     * @param nOld
     * @param nNew
     */
    protected boolean stimulatedEmissionIsAllowed( final int nOld, final int nNew ) {
        boolean legal = true;
        if ( nNew == nOld ) {
            legal = false;
        }
        else if ( nNew == 1 && _l == 0 ) {
            // transition from (n,0,0) to (1,?,?) cannot satisfy the abs(l-l')=1 rule
            legal = false;
        }
        else if ( nNew == 1 && _l != 1 ) {
            // the only way to get to (1,0,0) is from (n,1,?)
            legal = false;
        }
        
        if ( DEBUG_REJECTED_TRANSITIONS && !legal ) {
            System.out.println( "Schrodinger.stimulatedEmissionIsAllowed: rejecting " + stateToString( nOld, _l, _m ) + " -> (" + nNew + ",?,?)" );
        }
        
        return legal;
    }
    
    /*
     * Chooses a new primary state (n) for the electron.
     * 
     * @return new primary state, -1 if there is no valid transition
     */
    protected int chooseLowerElectronState() {
        int nOld = getElectronState();
        return getLowerPrimaryState( nOld );
    }
    
    /*
     * Sets the electron's primary state.
     * Randomly chooses the values for the secondary and tertiary states,
     * according to state transition rules.
     * 
     * @param nNew
     */
    protected void setElectronState( final int nNew ) {
        
        int lNew = getNewSecondaryState( nNew, _l );
        int mNew = getNewTertiaryState( lNew, _m );
        
        if ( DEBUG_STATE_TRANSITIONS ) {
            System.out.println( "SchrodingerModel.setElectronState " + 
                    stateToString( getElectronState(), _l, _m ) + "->" + stateToString( nNew, lNew, mNew ) );
        }
        
        // Verify that no transition rules have been broken.
        boolean valid = isaValidTransition( getElectronState(), _l, _m, nNew, lNew, mNew );
        if ( valid ) {
            _l = lNew;
            _m = mNew;
            super.setElectronState( nNew );
        }
        else {
            // There's a bug in the implementation of the transition rules.
            // Print a warning and (as a last resort) transition to (1,0,0).
            warnBadTransition( getElectronState(), _l, _m, nNew, lNew, mNew );
            _l = 0;
            _m = 0;
            super.setElectronState( 1 );
        }
    }
    
    /*
     * Our Schrodinger model emits photons from a random point on the first Bohr orbit.
     * This returns a reference to a Point2D -- be careful not to modify the value returned!
     * @return Point2D
     */
    protected Point2D getSpontaneousEmissionPositionRef() {
        // random point on the orbit
        double radius = BohrModel.getOrbitRadius( GROUND_STATE );
        double angle = RandomUtils.nextAngle();
        // convert to Cartesian coordinates, adjust for atom's position
        double x = ( radius * Math.cos( angle ) ) + getX();
        double y = ( radius * Math.sin( angle ) ) + getY();
        _spontaneousEmissionPoint.setLocation( x, y );
        return _spontaneousEmissionPoint;
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
     * Chooses a new lower value for the primary state (n).
     * The possible values of n are limited by the current value of l, since abs(l-l') must be 1.
     * The probability of each possible n transition is determined by its transition strength.
     * 
     * @param nOld the existing primary state
     * @return the new primary state, -1 there is no valid transition
     */
    private int getLowerPrimaryState( final int nOld ) {
        
        int nNew = -1;

        if ( nOld < 2 ) {
            // no state is lower than (1,0,0)
            return -1;
        }
        else if ( nOld == 2 ) {
            if ( _l == 0 ) {
                // transition from (2,0,?) to (1,0,?) cannot satisfy the abs(l-l')=1 rule
                return -1;
            }
            else {
                // the only transition from (2,1,?) is (1,0,0)
                nNew = 1;
            }
        }
        else if ( nOld > 2 ) {
            
            // determine the possible range of n
            final int nMax = nOld - 1;
            int nMin = Math.max( _l, 1 );
            if ( _l == 0 ) {
                // transition from (n,0,0) to (1,?,?) cannot satisfy the abs(l-l')=1 rule
                nMin = 2;
            }
            
            // get the strengths for each possible transition
            ProbabilisticChooser.Entry[] entries = new ProbabilisticChooser.Entry[nMax - nMin + 1];
            double strengthSum = 0;
            for ( int i = 0; i < entries.length; i++ ) {
                int state = nMin + i;
                double transitionStrength = TRANSITION_STRENGTH[nOld-1][state-1];
                entries[i] = new ProbabilisticChooser.Entry( new Integer( state ), transitionStrength );
                strengthSum += transitionStrength;
            }
            if ( strengthSum == 0 ) {
                // all transitions had zero strength, none are possible
                return -1;
            }
            
            // choose a transition
            ProbabilisticChooser chooser = new ProbabilisticChooser( entries );
            Integer value = (Integer) chooser.get();
            if ( value == null ) {
                return -1;
            }
            nNew = value.intValue();
        }
        
        return nNew;
    }
    
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
        else if ( lOld == nNew ) {
            lNew = lOld - 1;
        }
        else if ( lOld == nNew - 1 ) {
            lNew = lOld - 1;
        }
        else {
            if ( _lRandom.nextBoolean() ) {
                lNew = lOld + 1;
            }
            else {
                lNew = lOld - 1;
            }
        }
        
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
        
        return mNew;
    }
    
    /*
     * Checks state transition rules to see if a proposed transition is valid. 
     * 
     * @param nOld
     * @param lOld
     * @param mOld
     * @param nNew 
     * @param lNew
     * @param mNew
     * @return true or false
     */
    private static boolean isaValidTransition( int nOld, int lOld, int mOld, int nNew, int lNew, int mNew ) {
        
        boolean valid = true;

        if ( nOld == nNew ) {
            valid = false;
        }
        else if ( !( nNew >= 1 && nNew <= getNumberOfStates() ) ) {
            valid = false;
        }
        else if ( !( lNew >= 0 && lNew <= nNew - 1 ) ) {
            valid = false;
        }
        else if ( !( Math.abs( lOld - lNew ) == 1 ) ) {
            valid = false;
        }
        else if ( !( mNew >= -lNew && mNew <= +lNew ) ) {
            valid = false;
        }
        else if ( !( Math.abs( mOld - mNew ) <= 1 ) ) {
            valid = false;
        }
        
        return valid;
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
    
    public static void warnBadTransition( int nOld, int lOld, int mOld, int nNew, int lNew, int mNew ) {
        System.err.println( "WARNING! SchrodingerModel: bad transition " + stateToString( nOld, lOld, mOld ) + " -> " + stateToString( nNew, lNew, mNew ) );
    }
    
    public static String stateToString( int n, int l, int m ) {
        return "(" + n + "," + l + "," + m + ")";
    }
}
