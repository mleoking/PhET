/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.boundstates.BSConstants;

/**
 * BSAbstractCoulombSolver is the base class for all Coulomb solvers.
 * It contains both an eigenstate solver and wave function solver.
 * This is an implementation of an analytic solution specified by Sam McKagan.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class BSAbstractCoulombSolver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int DEFAULT_ANGULAR_MOMENTUM_QUANTUM_NUMBER = 0; // !! untested for values other than 0
    
    private static final double SQRT_4_PI = Math.sqrt( 4 * Math.PI );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // These are final since their values should only be set once (in the constructor).
    final private double _offset; // potential offset
    final private double _mass; // particle mass
    final private int _l;  // angular momentum quantum number (L/hbar, where L=angular momentum)
    final private double _a; // a constant used throughout the wave function calculations
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /*
     * Constructor.
     * 
     * @param potential
     * @param particle
     */
    protected BSAbstractCoulombSolver( BSAbstractPotential potential, BSParticle particle ) {
        if ( potential.getNumberOfWells() != 1 ) {
            throw new UnsupportedOperationException( "solver supports only 1 well case" );
        }
        _offset = potential.getOffset();
        _mass = particle.getMass();
        _l = DEFAULT_ANGULAR_MOMENTUM_QUANTUM_NUMBER;
        _a = get_a( particle.getMass() );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    protected double getMass() {
        return _mass;
    }
    
    protected static double get_a( final double mass ) {
        return ( BSConstants.HBAR * BSConstants.HBAR ) / ( mass * BSConstants.KE2 );
    }
    
    //----------------------------------------------------------------------------
    // Eigenstate solver
    //----------------------------------------------------------------------------
    
    /**
     * Gets an eigenstate energy.
     * 
     * @param n eigenstate subscript, n=1,2,3,...
     * @throws IllegalArgumentException if n < 1
     * @return energy value
     */
    public double getEnergy( final int n ) {
        if ( n < 1 ) {
            throw new IllegalArgumentException( "n < 1" );
        }
        return _offset + ( -( _mass * BSConstants.KE2 * BSConstants.KE2 ) / ( 2 * BSConstants.HBAR * BSConstants.HBAR * n * n ) );
    }
    
    //----------------------------------------------------------------------------
    // Wave Function solver
    //----------------------------------------------------------------------------
    
    /**
     * Gets the points that approximate the time-independent wave function.
     * 
     * @param n eigenstate subscript, n=1,2,3,...
     * @param minX
     * @param maxX
     * @param numberOfPoints
     * @throws IllegalArgumentException if n < 1
     * @return array of points
     */
    public Point2D[] getWaveFunction( final int n, final double minX, final double maxX, final int numberOfPoints ) {
        if ( n < 1 ) {
            throw new IllegalArgumentException( "n < 1" );
        }
        Point2D[] points = new Point2D.Double[ numberOfPoints ];
        final double dx = ( maxX - minX ) / numberOfPoints;
        double x = minX;
        double y = 0;
        for ( int i = 0; i < numberOfPoints; i++ ) {
            y = psiScaled( n, x );
            points[i] = new Point2D.Double( x, y );
            x += dx;
        }
        return points;
    }
    
    /*
     * Calculates the scaled wave function at a position.
     * Scaled means that the maximum amplitude is 1.
     * This must be implemented by the subclass.
     * 
     * @param n eigenstate subscript, n=1,2,3,...
     * @param x position, in nm
     * @param a common constant
     * @return scaled value of psi
     */
    protected abstract double psiScaled( final int n, final double x );
    
    /*
     * Calculates the unscaled wave function at a position.
     * Should be called by subclass' implementation of psiScaled.
     * 
     * @param n eigenstate subscript n=1,2,3,...
     * @param x position, in nm
     * @return psi
     */
    protected double psi( final int n, final double x ) {
        assert ( n >= 1 );
        final double absX = Math.abs( x );
        return ( 1 / SQRT_4_PI ) * Math.pow( absX, _l ) * Math.exp( -absX / ( n * _a ) ) * bxSum( n, x );
    }
    
    /*
     * Calculates the summation term involving b and x.
     * 
     * @param n eigenstate subscript n=1,2,3,...
     * @param x position, in nm
     */
    private double bxSum( final int n, final double x ) {
        assert( n >= 1 );
        final double absX = Math.abs( x );
        double sum = 0;
        double b = 0;
        double bPrevious = 0;
        for ( int j = 0; j <= ( n - _l - 1 ); j++ ) {
            b = b( j, n, bPrevious );
            sum += ( b * Math.pow( absX, j ) );
            bPrevious = b;
        }
        return sum;
    }
    
    /* 
     * Calculates the common term b<sub>j</sub>.
     * 
     * For j = 0, b(0) = 2 * ( (n * a)^(-3/2) )
     * 
     * For j > 0, b(j) = ( 2 / (n * a) ) * ( (j + l - n ) / (j * ( j + ( 2 * l ) + 1 )) ) * b(j-1)
     * 
     * @param j which value of b to calculate
     * @param n eigenstate subscript n=1,2,3,...
     * @param bPrevious b<sub>j-1</sub>
     */
    private double b( final int j, final int n, final double bPrevious ) {
        assert( j >= 0 );
        assert( n >= 1 );
        
        double b = 0;
        if ( j == 0 ) {
            b = 2 * Math.pow( ( n * _a ), -1.5 );
        }
        else {
            b = ( 2 / ( n * _a ) ) * ( ( j + _l - n ) / (double)( j * ( j + ( 2 * _l ) + 1 ) ) ) * bPrevious;
        }
        return b;
    }
}
