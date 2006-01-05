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
    
    // Damping factors, to prevent periodic boundary condition.
    private static double[] DAMPING_FACTORS = new double[] { 0.3, 0.7, 0.85, 0.9, 0.925, 0.95, 0.975, 0.99, 0.995, 0.999 };
    
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
    private MutableComplex _c3;
    private MutableComplex _c4;
    
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
        
        _dx = 0.09;//XXX temporarily decrease the number of sample points
        _dt = 0.8 * MASS * _dx * _dx / HBAR;//XXX Richardson algorithm doesn't work without this specific value
                
        _c1 = new MutableComplex();
        _c2 = new MutableComplex();
        _c3 = new MutableComplex();
        _c4 = new MutableComplex();
        
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
    
    public void setDx( double dx ) {
        _dx = dx;
        update();
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
        
        // Get the wave packet and energy settings.
        final double width = _wavePacket.getWidth();
        final double center = _wavePacket.getCenter();
        final double E = _wavePacket.getTotalEnergy().getEnergy();
        final double V = _wavePacket.getPotentialEnergy().getEnergyAt( center );
        double vx = Math.sqrt( 2 * ( E - V ) / MASS );
        if ( _wavePacket.getDirection() == Direction.RIGHT_TO_LEFT ) {
            vx *= -1;
        }
        
        // Deterine the position range.
        AbstractPotential pe = _wavePacket.getPotentialEnergy();
        final int numberOfRegions = pe.getNumberOfRegions();
        final double minX = pe.getStart( 0 );
        final double maxX = pe.getEnd( numberOfRegions - 1 );
        
        // Calculate the number of sample points.
        final int numberOfPoints = (int)( ( maxX - minX ) / _dx ) + 1;
        
        // Initialize constants used by the propogator.
        final double epsilon = HBAR * _dt / ( MASS * _dx * _dx );
        _alpha = new Complex( 0.5 * ( 1.0 + Math.cos( epsilon / 2 ) ), -0.5 * Math.sin( epsilon / 2 ) );
        _beta = new Complex( ( Math.sin( epsilon / 4 ) ) * Math.sin( epsilon / 4 ), 0.5 * Math.sin( epsilon / 2 ) );

        // Initialize the data arrays used by the propogator.
        _positions = new double[numberOfPoints];
        _Psi = new MutableComplex[numberOfPoints];
        _EtoV = new Complex[numberOfPoints];
        for ( int i = 0; i < numberOfPoints; i++ ) {
            final double position = minX + ( i * _dx );
            _positions[i] = position;
            final double r = Math.exp( -( ( position - center ) / width ) * ( ( position - center ) / width ) / 2 );
            _Psi[i] = new MutableComplex( r * Math.cos( MASS * vx * ( position - center ) / HBAR ), r * Math.sin( MASS * vx * ( position -center ) / HBAR ) );
            final double s = getPotentialEnergy( position ) * _dt / HBAR;
            _EtoV[i] = new Complex( Math.cos( s ), -Math.sin( s ) );
        }
    }
    
    /*
     * Determines the potential energy at a position.
     * This assumes that the barrier is centered at 0, and that 
     * potential energy is 0 everywhere is except within the barrier.
     * 
     * @param position
     */
    private double getPotentialEnergy( double position ) {
        return _wavePacket.getPotentialEnergy().getEnergyAt( position );
    }
    
    /**
     * Propogates the solution by a specified number of steps.
     * 
     * @param steps
     */
    public void propogate( int steps ) {
        for ( int i = 0; i < steps; i++ ) {
            propogate1();
            propogate2();
            propogate3();
            propogate4();
            propogate3(); 
            propogate2();
            propogate1();
            damp();
        }
    }
    
    /*
     * One of the parts of the propogator.
     */
    private void propogate1() {
        int numberOfPoints = _Psi.length;
        for ( int i = 0; i < numberOfPoints - 1; i += 2 ) {

            // A = Psi[i]
            _c1.setValue( _Psi[i] );
            
            // B = Psi[i + 1]
            _c2.setValue( _Psi[i + 1] );
            
            // Psi[i] = (alpha * A) + (beta * B)
            _c3.setValue( _alpha );
            _c3.multiply( _c1 );
            _c4.setValue( _beta );
            _c4.multiply( _c2 );
            _Psi[i].setValue( _c3 );
            _Psi[i].add( _c4 );
            
            // Psi[i+1] = (alpha * B) + (beta * A)
            _c3.setValue( _alpha );
            _c3.multiply( _c2 );
            _c4.setValue( _beta );
            _c4.multiply( _c1 );
            _Psi[i + 1].setValue( _c3 );
            _Psi[i + 1].add( _c4 );
        }
    }
    
    /*
     * One of the parts of the propogator.
     */
    private void propogate2() {
        int numberOfPoints = _Psi.length;
        for ( int i = 1; i < numberOfPoints - 1; i += 2 ) {
            
            // A = Psi[i]
            _c1.setValue( _Psi[i] );
            
            // B = Psi[i + 1]
            _c2.setValue( _Psi[i + 1] );
            
            // Psi[i] = (alpha * A) + (beta * B)
            _c3.setValue( _alpha );
            _c3.multiply( _c1 );
            _c4.setValue( _beta );
            _c4.multiply( _c2 );
            _Psi[i].setValue( _c3 );
            _Psi[i].add( _c4 );
            
            // Psi[i+1] = (alpha * B) + (beta * A)
            _c3.setValue( _alpha );
            _c3.multiply( _c2 );
            _c4.setValue( _beta );
            _c4.multiply( _c1 );
            _Psi[i + 1].setValue( _c3 );
            _Psi[i + 1].add( _c4 );
        }
    }
    
    /*
     * One of the parts of the propogator.
     */
    private void propogate3() {
        int numberOfPoints = _Psi.length;
        
        // A = Psi[numberOfPoints - 1]
        _c1.setValue( _Psi[numberOfPoints - 1] );
        
        // B = Psi[0]
        _c2.setValue( _Psi[0] );
        
        // Psi[numberOfPoints - 1] = (alpha * A) + (beta * B)
        _c3.setValue( _alpha );
        _c3.multiply( _c1 );
        _c4.setValue( _beta );
        _c4.multiply( _c2 );
        _Psi[numberOfPoints - 1].setValue( _c3 );
        _Psi[numberOfPoints - 1].add( _c4 );
        
        // Psi[0] = (alpha * B) + (beta * A)
        _c3.setValue( _alpha );
        _c3.multiply( _c2 );
        _c4.setValue( _beta );
        _c4.multiply( _c1 );
        _Psi[0].setValue( _c3 );
        _Psi[0].add( _c4 );
    }
    
    /*
     * One of the parts of the propogator.
     */
    private void propogate4() {
        int numberOfPoints = _Psi.length;
        for ( int i = 0; i < numberOfPoints; i++ ) {
            // Psi[i= = Psi[i] * EtoV[i]
            _Psi[i].multiply( _EtoV[i] );
        }
    }
    
    /*
     * Damps the values near the min and max positions
     * to prevent periodic boundary conditions.
     * Otherwise, the wave will appear to exit from one
     * edge of the display and enter on the other edge.
     */
    private void damp() {
        for ( int i = 0; i < DAMPING_FACTORS.length; i++ ) {
            double scale = DAMPING_FACTORS[i];
            _Psi[i].scale( scale ); // left edge
            _Psi[ _Psi.length - i - 1 ].scale( scale ); // right edge
        }
    }
}
