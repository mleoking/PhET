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
 * RichardsonSolver solves the wave function for a wave packet,
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
public class RichardsonSolver {

    //----------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------
    
    private static final double HBAR = QTConstants.HBAR;
    private static final double MASS = QTConstants.MASS;
    
    // Damping factors, to prevent periodic boundary condition.
    private static double[] DAMPING_FACTORS = 
        new double[] { 0.001, 0.005, 0.01, 0.025, 0.05, 0.075, 0.1, 0.15, 0.3, 0.5, 0.7, 0.85, 0.9, 0.925, 0.95, 0.975, 0.99, 0.995, 0.999 };
    
    //----------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------
    
    private WavePacket _wavePacket;
    
    private double _dx;
    private double _dt;
    private double _positions[]; // position at each sample point
    private RComplex _Psi[]; // wave function values at each sample point
    private RComplex _EtoV[]; // potential energy propagator = exp(-i*V(x)*dt/hbar)
    
    private RComplex _alpha; // special parameter for Richardson algorithm
    private RComplex _beta; // special parameter for Richardson algorithm
    
    // Reusable complex numbers
    private RComplex _c1;
    private RComplex _c2;
    private RComplex _c3;
    private RComplex _c4;
    
    //----------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param wavePacket
     */
    public RichardsonSolver( WavePacket wavePacket ) {
        
        _wavePacket = wavePacket;
        _dx = 1;
        _dt = QTConstants.CLOCK_STEP;
                
        _c1 = new RComplex();
        _c2 = new RComplex();
        _c3 = new RComplex();
        _c4 = new RComplex();
        
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
    public RComplex[] getEnergies() {
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
        
        _dt = 0.8 * MASS * _dx * _dx / HBAR; //XXX Richardson algorithm doesn't work without this specific value
        
        // Get the wave packet and energy settings.
        final double width = _wavePacket.getWidth();
        final double center = _wavePacket.getCenter();
        final double E = _wavePacket.getTotalEnergy().getEnergy();
        final double V = _wavePacket.getPotentialEnergy().getEnergyAt( center );
        double vx = Math.sqrt( 2 * ( E - V ) / MASS );
        if ( _wavePacket.getDirection() == Direction.RIGHT_TO_LEFT ) {
            vx *= -1;
        }
        
        // Determine the position range, including extra "damping" points that won't be visible.
        AbstractPotential pe = _wavePacket.getPotentialEnergy();
        final int numberOfRegions = pe.getNumberOfRegions();
        final double minX = pe.getStart( 0 ) - DAMPING_FACTORS.length;
        final double maxX = pe.getEnd( numberOfRegions - 1 ) + DAMPING_FACTORS.length;
        
        // Calculate the number of sample points.
        final int numberOfPoints = (int)( ( maxX - minX ) / _dx ) + 1;
        
        // Initialize constants used by the propagator.
        final double epsilon = HBAR * _dt / ( MASS * _dx * _dx );
        _alpha = new RComplex( 0.5 * ( 1.0 + Math.cos( epsilon / 2 ) ), -0.5 * Math.sin( epsilon / 2 ) );
        _beta = new RComplex( ( Math.sin( epsilon / 4 ) ) * Math.sin( epsilon / 4 ), 0.5 * Math.sin( epsilon / 2 ) );

        // Initialize the data arrays used by the propagator.
        _positions = new double[numberOfPoints];
        _Psi = new RComplex[numberOfPoints];
        _EtoV = new RComplex[numberOfPoints];
        RComplex A = new RComplex( 1 / ( Math.pow( Math.PI, 0.25 ) * Math.sqrt( width ) ), 0 ); // normalization constant
        for ( int i = 0; i < numberOfPoints; i++ ) {
            final double position = minX + ( i * _dx );
            _positions[i] = position;
            if ( zero ) {
                _Psi[i] = new RComplex( 0, 0 );
                _EtoV[i] = new RComplex( 0, 0 );
            }
            else {
                final double r = Math.exp( -( ( position - center ) / width ) * ( ( position - center ) / width ) / 2 );
                _Psi[i] = new RComplex( r * Math.cos( MASS * vx * ( position - center ) / HBAR ), r * Math.sin( MASS * vx * ( position - center ) / HBAR ) );
                _Psi[i].multiply( A );
                final double s = getPotentialEnergy( position ) * _dt / HBAR;
                _EtoV[i] = new RComplex( Math.cos( s ), -Math.sin( s ) );
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
     * Propagates the solution by 1 step.
     * <p>
     * Note that thre's a lot of duplicate code here.
     * The body of each for loop could easily be made into a method; ditto for the damping code.
     * (In fact, that's the way the I originally implemented this.)
     * But this method is such a bottleneck, that reducing method calls results
     * in big performance savings.  So 9sadly) we're trading off good code design for performance.
     */
    public void propagate() {
        int numberOfPoints = _Psi.length;

        // Starting at 0...
        for ( int i = 0; i < numberOfPoints - 1; i += 2 ) {
            // A = Psi[i]
            _c1.setValue( _Psi[i] );
            
            // B = Psi[i+1]
            _c2.setValue( _Psi[i+1] );
            
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
            _Psi[i+1].setValue( _c3 );
            _Psi[i+1].add( _c4 );
        }

        // Starting at 1...
        for ( int i = 1; i < numberOfPoints - 1; i += 2 ) {
            // A = Psi[i]
            _c1.setValue( _Psi[i] );
            
            // B = Psi[i+1]
            _c2.setValue( _Psi[i+1] );
            
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
            _Psi[i+1].setValue( _c3 );
            _Psi[i+1].add( _c4 );
        }
        
        // Wrap around...
        {
            int i = numberOfPoints - 1;
            int j = 0;
            
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
        
        // Apply propagator values...
        for ( int i = 0; i < numberOfPoints; i++ ) {
            _Psi[i].multiply( _EtoV[i] );
        }

        // Now do what we did above, but in the reverse order...
        
        // Wrap around...
        {
            int i = numberOfPoints - 1;
            int j = 0;
            
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

        // Starting at 1...
        for ( int i = 1; i < numberOfPoints - 1; i += 2 ) {
            // A = Psi[i]
            _c1.setValue( _Psi[i] );
            
            // B = Psi[i+1]
            _c2.setValue( _Psi[i+1] );
            
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
            _Psi[i+1].setValue( _c3 );
            _Psi[i+1].add( _c4 );
        }

        // Starting at 0...
        for ( int i = 0; i < numberOfPoints - 1; i += 2 ) {
            // A = Psi[i]
            _c1.setValue( _Psi[i] );
            
            // B = Psi[i+1]
            _c2.setValue( _Psi[i+1] );
            
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
            _Psi[i+1].setValue( _c3 );
            _Psi[i+1].add( _c4 );
        }

        /*
         * Damps the values near the min and max positions
         * to prevent periodic boundary conditions.
         * Otherwise, the wave will appear to exit from one
         * edge of the display and enter on the other edge.
         */
        if ( _Psi.length < DAMPING_FACTORS.length ) {
            return;
        }
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
     
    /**
     * RComplex provides the bare minimum of complex number functionality
     * needed by the Richardson algorithm. The goal is to minimize method
     * calls that occur in the Richardson propagate method.
     */
    public static class RComplex {
        
        private double _real;
        private double _imaginary;
        
        public RComplex() {
            this( 0, 0 );
        }
        
        public RComplex( double real, double imaginary ) {
            _real = real;
            _imaginary = imaginary;
        }
        
        public double getReal() {
            return _real;
        }
        
        public double getImaginary() {
            return _imaginary;
        }
        
        public void setValue( RComplex c ) {
            _real = c._real;
            _imaginary = c._imaginary;
        }
        
        public void add( RComplex c ) {
            _real += c._real;
            _imaginary += c._imaginary;
        }
        
        public void multiply( RComplex c ) {
            double newReal = _real * c._real - _imaginary * c._imaginary;
            double newImaginary = _real * c._imaginary + _imaginary * c._real;
            _real = newReal;
            _imaginary = newImaginary;
        }
        
        public void scale( double scale ) {
            _real *= scale;
            _imaginary *= scale;
        }
        
        public double getAbs() {
            return ( Math.sqrt( ( _real * _real ) + ( _imaginary * _imaginary ) ) );
        }
        
        public double getPhase() {
            return Math.atan2( _imaginary, _real );
        }
    }
}