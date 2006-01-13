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
 * WavePacketSolver solves the wave function for a wave packet,
 * by solving Schrodinger's equation for 1 dimension.
 * <p>
 * The propagation algorithm used herein was adapted from the implementation
 * by John Richardson, found at http://www.neti.no/java/sgi_java/WaveSim.html,
 * and described in:<br>
 * <code>
 * Richardson, John L.,
 * Visualizing quantum scattering on the CM-2 supercomputer,
 * Computer Physics Communications 63 (1991) pp 84-94 
 * </code>
 * <p>
 * Richardson's algorithm has period boundary conditions, which was not 
 * desired for this simulation. The dampling algoritm used herein was adapted
 * from the PhET "Quantum Wave Interference" simulation, by Sam Reid.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class WavePacketSolver {
    
    //----------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------
    
    private static final double HBAR = QTConstants.HBAR;
    private static final double MASS = QTConstants.MASS;
    
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
    public WavePacketSolver( WavePacket wavePacket ) {
        
        _wavePacket = wavePacket;
        _dx = 1;
        _dt = QTConstants.CLOCK_STEP;
                
        _c1 = new MutableComplex();
        _c2 = new MutableComplex();
        _c3 = new MutableComplex();
        _c4 = new MutableComplex();
        
        update();
    }

    //----------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------
    
    /**
     * Gets the position (x) coordinates of the wave function solution.
     * 
     * @return double[]
     */
    public double[] getPositions() {
        return _positions;
    }
    
    /**
     * Gets the energy (y) coordinates of the wave function solution.
     * 
     * @return Complex[]
     */
    public Complex[] getEnergies() {
        return _Psi;
    }
    
    /**
     * Sets the dx (position spacing) between sample points in the 
     * wave function solution.
     * 
     * @param dx
     */
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
        
        boolean zero = isSolutionZero();
        
        _dt = 0.8 * MASS * _dx * _dx / HBAR;//XXX Richardson algorithm doesn't work without this specific value
        
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
        final double A = 1 / ( Math.pow( Math.PI, 0.25 ) * Math.sqrt( width ) ); // normalization constant
        for ( int i = 0; i < numberOfPoints; i++ ) {
            final double position = minX + ( i * _dx );
            _positions[i] = position;
            if ( zero ) {
                _Psi[i] = new MutableComplex( 0, 0 );
                _EtoV[i] = Complex.ZERO;
            }
            else {
                final double r = Math.exp( -( ( position - center ) / width ) * ( ( position - center ) / width ) / 2 );
                _Psi[i] = new MutableComplex( r * Math.cos( MASS * vx * ( position - center ) / HBAR ), r * Math.sin( MASS * vx * ( position - center ) / HBAR ) );
                _Psi[i].multiply( A );
                final double s = getPotentialEnergy( position ) * _dt / HBAR;
                _EtoV[i] = new Complex( Math.cos( s ), -Math.sin( s ) );
            }
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
     * Propogates the solution by 1 step.
     */
    public void propogate() {
        int numberOfPoints = _Psi.length;

        for ( int i = 0; i < numberOfPoints - 1; i += 2 ) {
            propogate( i, i + 1 );
        }

        for ( int i = 1; i < numberOfPoints - 1; i += 2 ) {
            propogate( i, i + 1 );
        }

        propogate( numberOfPoints - 1, 0 );

        for ( int i = 0; i < numberOfPoints; i++ ) {
            _Psi[i].multiply( _EtoV[i] );
        }

        // now do what we did above, but in the reverse order...
        
        propogate( numberOfPoints - 1, 0 );

        for ( int i = 1; i < numberOfPoints - 1; i += 2 ) {
            propogate( i, i + 1 );
        }

        for ( int i = 0; i < numberOfPoints - 1; i += 2 ) {
            propogate( i, i + 1 );
        }

        // apply damping to prevent periodic boundary condition...
        damp();
    }
    
    /*
     * Performs propogation on two of the Psi entries.
     * 
     * @param i
     * @param j
     */
    private void propogate( int i, int j ) {
        // A = Psi[i]
        _c1.setValue( _Psi[i] );
        
        // B = Psi[j]
        _c2.setValue( _Psi[j] );
        
        // Psi[i] = (alpha * A) + (beta * B)
        _c3.setValue( _alpha );
        _c3.multiply( _c1 );
        _c4.setValue( _beta );
        _c4.multiply( _c2 );
        _Psi[i].setValue( _c3 );
        _Psi[i].add( _c4 );
        
        // Psi[j] = (alpha * B) + (beta * A)
        _c3.setValue( _alpha );
        _c3.multiply( _c2 );
        _c4.setValue( _beta );
        _c4.multiply( _c1 );
        _Psi[j].setValue( _c3 );
        _Psi[j].add( _c4 );
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
    
    /**
     * The wave function solution should be zero if the first 
     * region encountered by the wave (dependent on direction)
     * has E < V. (where E=total energy, V=potential energy)
     * 
     * @return true or false
     */
    protected boolean isSolutionZero() {
        
        boolean isZero = false;
        AbstractPotential pe = _wavePacket.getPotentialEnergy();
        
        final double E = _wavePacket.getTotalEnergy().getEnergy();
        final int firstRegionIndex = 0;
        final int lastRegionIndex = pe.getNumberOfRegions() - 1;
        
        if ( _wavePacket.isLeftToRight() && E < pe.getEnergy( firstRegionIndex ) ) {
            isZero = true;
        }
        else if ( _wavePacket.isRightToLeft() && E < pe.getEnergy( lastRegionIndex ) ) {
            isZero = true;
        }
        
        return isZero;
    }
}
