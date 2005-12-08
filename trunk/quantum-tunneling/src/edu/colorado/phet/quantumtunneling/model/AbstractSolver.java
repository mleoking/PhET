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

import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.quantumtunneling.util.Complex;
import edu.colorado.phet.quantumtunneling.util.MutableComplex;


/**
 * AbstractSolver is the base class for a classes that implements 
 * closed-form solutions to Schrodinger's equation for a plane wave.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractSolver implements Observer {
    
    protected static final double PLANCKS_CONSTANT = 0.658;  // Planck's constant, in eV fs
    protected static final double ELECTRON_MASS = 5.7;  // mass, in eV/c^2

    private TotalEnergy _te;
    private AbstractPotentialSpace _pe;
    private Complex[] _k;
    
    /*
     * Constructor.
     */
    public AbstractSolver( TotalEnergy te, AbstractPotentialSpace pe ) {
        _te = te;
        _te.addObserver( this );
        _pe = pe;
        _pe.addObserver( this );
        _k = new Complex[ pe.getNumberOfRegions() ];
        updateK();
    }
    
    public void cleanup() {
        _te.deleteObserver( this );
        _te = null;
        _pe.deleteObserver( this );
        _pe = null;
    }
    
    public TotalEnergy getTotalEnergy() {
        return _te;
    }
    
    public AbstractPotentialSpace getPotentialEnergy() {
        return _pe;
    }
    
    public Complex getK( int regionIndex ) {
        if ( regionIndex > _k.length - 1  ) {
            throw new IndexOutOfBoundsException( "regionIndex out of range: " + regionIndex );
        }
        return _k[ regionIndex ];
    }
    
    public abstract Complex solve( final double x, final double t );
    
    /*
     * Updates k values whenever the total or potential energy changes.
     */
    public void update( Observable o, Object arg ) {
        updateK();
    }
    
    private void updateK() {
        final double E = getTotalEnergy().getEnergy();
        final int numberOfRegions = getPotentialEnergy().getNumberOfRegions();
        for ( int i = 0; i < numberOfRegions; i++ ) {
            _k[i] = solveK( E, getPotentialEnergy().getEnergy( i ) );    
        }
    }
    
    /*
     * Calculate the wave number, in units of 1/nm.
     * 
     * k = sqrt( 2m(E-V) / h^2 )
     * 
     * where:
     * m = mass
     * E = total energy
     * V = potential energy
     * h = Planck's constant
     * 
     * If E < V, the result is imaginary.
     *
     * @param E total energy, in eV
     * @param V potential energy, in eV
     * @return
     */
    private static Complex solveK( final double E, final double V ) {
        final double m = ELECTRON_MASS;
        final double h = PLANCKS_CONSTANT;
        final double value = Math.sqrt( 2 * m * Math.abs( E - V ) / ( h * h ) );
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
    
    
    /*
     * e^(ikx)
     */
    protected Complex commonTerm1( final double x, int kIndex ) {
        Complex k = getK( kIndex );
        MutableComplex result = new MutableComplex( 0, 1 ); // i
        result.multiply( k );
        result.multiply( x );
        result.exp();
        return result;
    }
    
    /*
     * e^(-ikx)
     */
    protected Complex commonTerm2( final double x, int kIndex ) {
        Complex k = getK( kIndex );
        MutableComplex result = new MutableComplex( 0, 1 ); // i
        result.multiply( k );
        result.multiply( -x );
        result.exp();
        return result;
    }
    
    /*
     * Common term used by all solutions.
     * 
     * e^(-i*E*t/h)
     * 
     * where:
     * e = Euler's number
     * i = sqrt(-1)
     * E = total energy
     * t = time
     * h = Planck's constant
     * 
     * @param t 
     * @param E
     */
    protected static Complex commonTerm3( final double t, final double E ) {
        final double h = PLANCKS_CONSTANT;
        MutableComplex c = new MutableComplex( 0, 1 );
        c.multiply( -1 * E * t / h );
        c.exp();
        return c;
    }
}
