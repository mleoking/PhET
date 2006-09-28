/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.model;

import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.enums.Direction;
import edu.colorado.phet.quantumtunneling.util.Complex;
import edu.colorado.phet.quantumtunneling.util.MutableComplex;


/**
 * AbstractPlaneSolver is the base class for all classes that implement
 * closed-form solutions to the wave function equation for a plane wave.
 * <p>
 * Terms are defined as follows:
 * <code>
 * e = Euler's number
 * E = total energy, in eV
 * h = Planck's constant
 * i = sqrt(-1)
 * kn = wave number of region n, in 1/nm
 * m = mass, in eV/c^2
 * t = time, in fs
 * Vn = potential energy of region n, in eV
 * x = position, in nm
 * </code>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractPlaneSolver {
    
    //----------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------
    
    // Static term used to calculate k:  2 * m / h^2
    private static final double K_COMMON = ( 2 * QTConstants.MASS ) / ( QTConstants.HBAR * QTConstants.HBAR );

    //----------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------
    
    private TotalEnergy _te;
    private AbstractPotential _pe;
    private Direction _direction;
    
    private Complex[] _k;
    
    //----------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param te
     * @param pe
     */
    public AbstractPlaneSolver( TotalEnergy te, AbstractPotential pe, Direction direction ) {
        _te = te;
        _pe = pe;
        _direction = direction;
        _k = new Complex[ pe.getNumberOfRegions() ];
        update();
    }
    
    //----------------------------------------------------------------------
    // Wave function solution
    //----------------------------------------------------------------------
    
    /**
     * Solves the wave function.
     * 
     * @param x position, in nm
     * @param t time, in fs
     * @return
     */
    public abstract WaveFunctionSolution solve( final double x, final double t );
    
    //----------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------
    
    /**
     * Gets the total energy model associated with the solver.
     * @return
     */
    protected TotalEnergy getTotalEnergy() {
        return _te;
    }
    
    /**
     * Gets the potential energy model associated with the solver.
     * @return
     */
    protected AbstractPotential getPotentialEnergy() {
        return _pe;
    }
    
    /**
     * Sets the direction used for calculating the solution.
     * @param direction
     */
    public void setDirection( Direction direction ) {
        _direction = direction;
        update();
    }
    
    /**
     * Gets the direction used for calculating the solution.
     * @return direction
     */
    public Direction getDirection() {
        return _direction;
    }
    
    /*
     * Is the direction left to right?
     * @return
     */
    protected boolean isLeftToRight() {
        return ( _direction == Direction.LEFT_TO_RIGHT );
    }
    
    /*
     * Is the direction right to left?
     */
    protected boolean isRightToLeft() {
        return ( _direction == Direction.RIGHT_TO_LEFT );
    }
    
    /**
     * Gets the reflection probability.
     * 
     * @return
     */
    public double getReflectionProbability() {
        Complex B = getB();
        return ( B.getReal() * B.getReal() ) + ( B.getImaginary() * B.getImaginary() );
    }
    
    /**
     * Gets the transmission probability.
     * 
     * @return
     */
    public double getTransmissionProbability() {
        return 1 - getReflectionProbability();
    }
    
    /**
     * Gets the coefficient B, the amplitude of the reflected wave.
     * This is used elsewhere to calculate the relection and transmission probabilities.
     * 
     * @return Complex
     */
    protected abstract Complex getB();
    
    /**
     * Gets the boundary position between two regions.
     * <p>
     * If the wave direction is right-to-left, flip the region indicies.
     * 
     * @param regionIndex1 smaller index
     * @param regionIndex2 larger index
     * @return
     */
    protected double getBoundary( final int regionIndex1, final int regionIndex2 ) {
        if ( regionIndex1 + 1 != regionIndex2 ) {
            throw new IllegalArgumentException( "regionIndex1 + 1 != regionIndex2" );
        }
        if ( isLeftToRight() ) {
            return _pe.getEnd( regionIndex1 );
        }
        else {
            return _pe.getStart( flipRegionIndex( regionIndex1 ) );
        }
    }
    
    /**
     * Gets the k value for a specified region.
     * <p>
     * If the wave direction is right-to-left, then flip the region
     * index and multiply the k value by -1.
     * 
     * @param regionIndex
     * @return
     */
    protected Complex getK( final int regionIndex ) {
        if ( regionIndex < 0 || regionIndex > _k.length - 1  ) {
            throw new IndexOutOfBoundsException( "regionIndex out of range: " + regionIndex );
        }
        if ( isLeftToRight() ) {
            return _k[regionIndex];
        }
        else {
            return _k[flipRegionIndex( regionIndex )].getMultiply( -1 );
        }
    }
    
    /**
     * By default, regions are indexed in a left-to-right order.
     * This method flips the region index so that the regions are 
     * visited in a right-to-left order.
     * <p>
     * This method should be used by subclasses in their 
     * implementation of the <code>solve</code> method.
     * 
     * @param regionIndex
     */
    protected int flipRegionIndex( int regionIndex ) {
        // OK if region index is out of bounds!
        return getPotentialEnergy().getNumberOfRegions() - 1 - regionIndex;
    }
    
    //----------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------
    
    /**
     * Updates the solver to match it's model.
     */
    public void update() {
        updateK();
        updateCoefficients(); // update coefficients after updating k values!
    }
    
    /*
     * Updates the k values. 
     */
    private void updateK() {
        final double E = getTotalEnergy().getEnergy();
        final int numberOfRegions = getPotentialEnergy().getNumberOfRegions();
        for ( int i = 0; i < numberOfRegions; i++ ) {
            double V = getPotentialEnergy().getEnergy( i );
            _k[i] = solveK( E, V );
        }
    }
    
    /**
     * Updates the coeffiecients.
     * The algorithm for doing this varies by subclass.
     */
    protected abstract void updateCoefficients();
    
    /**
     * Is the solution zero for the energies that are assigned to this solver instance?
     * <p>
     * This method should be used by subclasses in their 
     * implementation of the <code>solve</code> method.
     * 
     * @return true or false
     */
    protected boolean isSolutionZero() {
        return isSolutionZero( getTotalEnergy(), getPotentialEnergy(), _direction );
    }
    
    /**
     * The wave function solution should be zero if the first 
     * region encountered by the wave (dependent on direction)
     * has E < V. (where E=total energy, V=potential energy)
     * 
     * @param te the total energy
     * @param pe the potential energy
     * @param the direction of the wave
     * @return true or false
     */
    public static boolean isSolutionZero( TotalEnergy te, AbstractPotential pe, Direction direction ) {
        
        boolean isZero = false;
        
        final double E = te.getEnergy();
        final int firstRegionIndex = 0;
        final int lastRegionIndex = pe.getNumberOfRegions() - 1;
        
        if ( direction == Direction.LEFT_TO_RIGHT && E < pe.getEnergy( firstRegionIndex ) ) {
            isZero = true;
        }
        else if (  direction == Direction.RIGHT_TO_LEFT && E < pe.getEnergy( lastRegionIndex ) ) {
            isZero = true;
        }
        
        return isZero;
    }
    
    /*
     * Calculates the wave number, in units of 1/nm.
     * 
     * k = sqrt( 2 * m * (E-V) / h^2 )
     * 
     * If direction is right-to-left, multiply k by -1.
     * If E < V, the result is imaginary.
     *
     * @param E total energy, in eV
     * @param V potential energy, in eV
     * @return
     */
    private static Complex solveK( final double E, final double V ) {
        final double value = Math.sqrt( K_COMMON * Math.abs( E - V ) );
        double real = 0;
        double imag = 0;
        if ( E >= V  ) {
            real = value;
        }
        else {
            imag = value;
        }
        return new Complex( real, imag );
    }
    
    //----------------------------------------------------------------------
    // Common terms that appear in the solution
    //----------------------------------------------------------------------
    
    /**
     * e^(ikx)
     * 
     * @param x position
     * @param k
     */
    protected static Complex commonTerm1( Complex k, final double x ) {
        MutableComplex c = new MutableComplex();
        c.setValue( Complex.I );
        c.multiply( k );
        c.multiply( x );
        c.exp();
        return c;
    }
    
    /**
     * e^(-ikx)
     * 
     * @param x position
     * @param regionIndex region index
     */
    protected static Complex commonTerm2( Complex k, final double x ) {
        MutableComplex c = new MutableComplex();
        c.setValue( Complex.I );
        c.multiply( k );
        c.multiply( -x );
        c.exp();
        return c;
    }
    
    /**
     * e^(-i*E*t/h)
     * 
     * @param t time 
     * @param E total energy
     */
    protected static Complex commonTerm3( final TotalEnergy te, final double t ) {
        final double h = QTConstants.HBAR;
        final double E = te.getEnergy();
        MutableComplex c = new MutableComplex();
        c.setValue( Complex.I );
        c.multiply( -1 * E * t / h );
        c.exp();
        return c;
    }
}
