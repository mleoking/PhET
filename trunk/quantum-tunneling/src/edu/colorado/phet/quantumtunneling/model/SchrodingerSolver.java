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
import edu.colorado.phet.quantumtunneling.enum.Direction;
import edu.colorado.phet.quantumtunneling.util.Complex;
import edu.colorado.phet.quantumtunneling.util.MutableComplex;


/**
 * SchrodingerSolver solves the Schrodinger equation for 1 dimension.
 * <p>
 * This implementation was adapted from the implementation by John Richardson, 
 * found at http://www.neti.no/java/sgi_java/WaveSim.html.
 * <p>
 * The time stepping algorithm used herein is described in:
 * <code>
 * Richardson, John L.,
 * Visualizing quantum scattering on the CM-2 supercomputer,
 * Computer Physics Communications 63 (1991) pp 84-94 
 * </code>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SchrodingerSolver {
    
    //----------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------
    
    private static final double HBAR = QTConstants.PLANCKS_CONSTANT;
    private static final double MASS = QTConstants.ELECTRON_MASS;
    
    //----------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------
    
    private WavePacket _wavePacket;
    
    private double _dx;
    private double _dt;
    private double _positions[]; // position at each sample point
    private MutableComplex _Psi[]; // wave function values at each sample point
    private Complex _EtoV[]; // potential energy propogator = exp(-i*V(x)*dt/hbar)
    
    private Complex _alpha; // special parameter for Richardson algorithm
    private Complex _beta; // special parameter for Richardson algorithm
    
    // Reusable complex numbers
    private MutableComplex _c1;
    private MutableComplex _c2;
    
    //----------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param wavePacket
     * @param dx
     * @param dt
     */
    public SchrodingerSolver( WavePacket wavePacket, double dx, double dt ) {
        
        _wavePacket = wavePacket;
        _dx = dx;
        _dt = dt;
        
        _c1 = new MutableComplex();
        _c2 = new MutableComplex();
        
        update();
    }

    //----------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------
    
    public double[] getPositions() {
        return _positions;
    }
    
    public Complex[] getEnergies() {
        return _Psi;
    }
    
    /**
     * Updates the internal state of the solver.
     */
    public void update() {
        if ( _wavePacket.isInitialized() ) {
            reset();
        }
    }
    
    //----------------------------------------------------------------------
    // Richardson algorithm
    //----------------------------------------------------------------------
    
    /*
     * Sets the initial conditions.
     */
    private void reset() {

        System.out.println( "SchrodingerSolver.reset" );//XXX
        
        final double width = _wavePacket.getWidth();
        final double center = _wavePacket.getCenter();
        final double E = _wavePacket.getTotalEnergy().getEnergy();
        double V = _wavePacket.getPotentialEnergy().getEnergyAt( center );
        double vx = Math.sqrt( 2 * ( E - V ) / MASS );
        if ( _wavePacket.getDirection() == Direction.RIGHT_TO_LEFT ) {
            vx *= -1;
        }
        
        AbstractPotential pe = _wavePacket.getPotentialEnergy();
        final int numberOfRegions = pe.getNumberOfRegions();
        final double minX = pe.getStart( 0 );
        final double maxX = pe.getEnd( numberOfRegions - 1 );
        
        final int numberOfPoints = (int)( ( maxX - minX ) / _dx ) + 1;
        _positions = new double[numberOfPoints];
        _Psi = new MutableComplex[numberOfPoints];
        _EtoV = new Complex[numberOfPoints];
        
        final double epsilon = HBAR * _dt / ( MASS * _dx * _dx );
        _alpha = new Complex( 0.5 * ( 1.0 + Math.cos( epsilon / 2 ) ), -0.5 * Math.sin( epsilon / 2 ) );
        _beta = new Complex( ( Math.sin( epsilon / 4 ) ) * Math.sin( epsilon / 4 ), 0.5 * Math.sin( epsilon / 2 ) );

        for ( int i = 0; i < numberOfPoints; i++ ) {
            final double position = minX + ( i * _dx );
            _positions[i] = position;
            final double r1 = Math.exp( -( ( position - center ) / width ) * ( ( position - center ) / width ) / 2 );
            _Psi[i] = new MutableComplex( r1 * Math.cos( MASS * vx * position / HBAR ), r1 * Math.sin( MASS * vx * position / HBAR ) );
            V = _wavePacket.getPotentialEnergy().getEnergyAt( position );
            final double r2 = V * _dt / HBAR;
            _EtoV[i] = new Complex( Math.cos( r2 ), -Math.sin( r2 ) );
        }
    }
    
    /**
     * Propogates the solution by a specified number of steps.
     * 
     * @param steps
     */
    public void propogate( int steps ) {
        System.out.println( "SchrodingerSolver.propogate " + steps );//XXX
        for ( int i = 0; i < steps; i++ ) {
            propogate1();
            propogate2();
            propogate3();
            propogate4();
            propogate3();
            propogate2();
            propogate1();
        }
    }
    
    /*
     * One of the parts of the propogator.
     */
    private void propogate1() {
        int numberOfPoints = _Psi.length;
        for ( int i = 0; i < numberOfPoints - 1; i += 2 ) {
            // Psi[i] = (alpha * Psi[i]) + (beta * Psi[i+1])
            _c1.setValue( _alpha );
            _c1.multiply( _Psi[i] );
            _c2.setValue( _beta );
            _c2.multiply( _Psi[i + 1] );
            _Psi[i].setValue( _c1 );
            _Psi[i].add( _c2 );
            // Psi[i+1] = (alpha * Psi[i+i]) + (beta * Psi[i])
            _c1.setValue( _alpha );
            _c1.multiply( _Psi[i + 1] );
            _c2.setValue( _beta );
            _c2.multiply( _Psi[i] );
            _Psi[i + 1].setValue( _c1 );
            _Psi[i + 1].add( _c2 );
        }
    }
    
    /*
     * One of the parts of the propogator.
     */
    private void propogate2() {
        int numberOfPoints = _Psi.length;
        for ( int i = 1; i < numberOfPoints - 1; i += 2 ) {
            // Psi[i] = (alpha * Psi[i]) + (beta * Psi[i+1])
            _c1.setValue( _alpha );
            _c1.multiply( _Psi[i] );
            _c2.setValue( _beta );
            _c2.multiply( _Psi[i + 1] );
            _Psi[i].setValue( _c1 );
            _Psi[i].add( _c2 );
            // Psi[i+1] = (alpha * Psi[i+i]) + (beta * Psi[i])
            _c1.setValue( _alpha );
            _c1.multiply( _Psi[i + 1] );
            _c2.setValue( _beta );
            _c2.multiply( _Psi[i] );
            _Psi[i + 1].setValue( _c1 );
            _Psi[i + 1].add( _c2 );
        }
    }
    
    /*
     * One of the parts of the propogator.
     */
    private void propogate3() {
        int numberOfPoints = _Psi.length;
        // Psi[numberOfPoints - 1] = (alpha * Psi[numberOfPoints - 1]) + (beta * Psi[0])
        _c1.setValue( _alpha );
        _c1.multiply( _Psi[numberOfPoints - 1] );
        _c2.setValue( _beta );
        _c2.multiply( _Psi[0] );
        _Psi[numberOfPoints - 1].setValue( _c1 );
        _Psi[numberOfPoints - 1].add( _c2 );
        // Psi[0] = (alpha * Psi[0]) + (beta * Psi[numberOfPoints - 1])
        _c1.setValue( _alpha );
        _c1.multiply( _Psi[0] );
        _c2.setValue( _beta );
        _c2.multiply( _Psi[numberOfPoints - 1] );
        _Psi[0].setValue( _c1 );
        _Psi[0].add( _c2 );
    }
    
    /*
     * One of the parts of the propogator.
     */
    private void propogate4() {
        int numberOfPoints = _Psi.length;
        for ( int i = 0; i < numberOfPoints; i++ ) {
            _Psi[i].multiply( _EtoV[i] );
        }
    }
}
