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

import javax.swing.JFrame;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.control.RichardsonControlsDialog;
import edu.colorado.phet.quantumtunneling.enums.Direction;
import edu.colorado.phet.quantumtunneling.util.LightweightComplex;


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
public class RichardsonSolver implements IWavePacketSolver {

    //----------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------
    
    /* Enables debugging output */
    private static boolean PRINT_DEBUG = false;
    
    /* Number of propagator steps per tick of the simulation clock */
    private static final int STEPS_PER_CLOCK_TICK = 40;
    
    /* Each damping coefficient is applied to this many adjacent samples */
    private static final int SAMPLES_PER_DAMPING_COEFFICIENT = 10;

    /* Damping coefficients, in order of application, 
     * starting from the boundaries of the sample space and working inward */
    private static final double[] DAMPING_COEFFICIENTS = 
        new double[] { 0.001, 0.005, 0.01, 0.025, 0.05, 0.075, 0.1, 0.15, 0.3, 0.5, 0.7, 0.85, 0.9, 0.925, 0.95, 0.975, 0.99, 0.995, 0.999 };
    
    /* If all visible Psi[] values are below this threshold, then all values of Psi[] are set to zero. */
    private static final double ZERO_THRESHOLD = 0.02;
    
    /* Multiply the number of visible samples by this number, 
     * and add that many undamped samples to each end of the range. */
    private static final double EXTRA_SAMPLES_MULTIPLIER = 2.5;
    
    /*
     * dt for this algorithm must be calibrated based on dx, which is
     * in turn dependent on the width (in pixels) of the Wave Function chart
     * and the number of pixels-per-sample desired.  Too large a value of dt
     * will cause the algorithm to fail, and an incorrect dt will cause the
     * algorithm to be out of sync with the time display.
     */
    private static final double DEFAULT_DT = 0.0025; // value chosen by Sam McKagan, version 0.00.18
    
    //----------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------

    private WavePacket _wavePacket;

    private double _mass;
    private double _hbar;
    private double _dx;
    private double _dt;
    private int _steps;
    
    private double _positions[]; // position at each sample point
    private LightweightComplex _Psi[]; // wave function values at each sample point
    private LightweightComplex _EtoV[]; // potential energy propagator = exp(-i*V(x)*dt/hbar)

    private LightweightComplex _alpha; // special parameter for Richardson algorithm
    private LightweightComplex _beta; // special parameter for Richardson algorithm
    
    // Indicies into Psi[] where "zero thresholding" is applied.
    private int _thresholdStart, _thresholding;
    
    private boolean _isZero; // true if all values of Psi[] are zero
    
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
        
        _mass = QTConstants.MASS;
        _hbar = QTConstants.HBAR;
        _steps = STEPS_PER_CLOCK_TICK;
        _dx = 1;
        _dt = DEFAULT_DT;
        
        update();
    }

    //----------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------

    /**
     * Gets the position (x) coordinates that were used to compute the wave function solution.
     *
     * @return double[]
     */
    public double[] getPositionValues() {
        return _positions;
    }

    /**
     * Gets the wave function values.
     *
     * @return LightweightComplex[]
     */
    public LightweightComplex[] getWaveFunctionValues() {
        return _Psi;
    }

    /**
     * Sets the dx (position spacing) between sample points.
     *
     * @param dx
     */
    public void setDx( double dx ) {
        _dx = dx;
        update();
    }
    
    public double getDx() {
        return _dx;
    }
    
    public void setDt( double dt ) {
        _dt = dt;
        update();
    }
    
    public double getDt() {
        return _dt;
    }
    
    public void setHbar( double hbar ) {
        _hbar = hbar;
        update();
    }
    
    public double getHbar() {
        return _hbar;
    }
    
    public void setMass( double mass ) {
        _mass = mass;
        update();
    }
    
    public double getMass() {
        return _mass;
    }
    
    public void setSteps( int steps ) {
        _steps = steps;
        update();
    }
    
    public int getSteps() {
        return _steps;
    }

    /*
     * Overrides the value of dt, setting it to something that 
     * doesn't cause this algorithm to fail. In this case, we're
     * setting it to the function of mass, dx and hbar used by 
     * the original Richardson code.
     * 
     * Analysis by Sam Reid:
     * Most ODE (ordinary differential equation) solvers fail to work
     * when you increase dt too high.  In this simulation, we have a set
     * of parameters that pushes dt past the failure point.  In the code
     * below, if s > PI/2 or epsilon > PI, then the propagator terms
     * (alpha, beta, EtoV) have the wrong form, and the solver fails.
     */
    private void overrideDt() {
        _dt = 0.8 * _mass * _dx * _dx / _hbar;
    }
    
    /**
     * Resets the solver.
     */
    public void update() {
        reset();
    }
    
    //----------------------------------------------------------------------
    // Richardson algorithm
    //----------------------------------------------------------------------
    
    /*
     * Resets the solver.
     */
    private void reset() {

        if ( !_wavePacket.isInitialized() ) {
            return;
        }
        
        _isZero = isSolutionZero();

        // Get the wave packet and energy settings.
        final double width = _wavePacket.getWidth();
        final double center = _wavePacket.getCenter();
        final double E = _wavePacket.getTotalEnergy().getEnergy();
        final double V = _wavePacket.getPotentialEnergy().getEnergyAt( center );
        double vx = Math.sqrt( 2 * ( E - V ) / _mass );
        if ( _wavePacket.getDirection() == Direction.RIGHT_TO_LEFT ) {
            vx *= -1;
        }

        // Determine the position range, including extra "damping" points that won't be visible.
        AbstractPotential pe = _wavePacket.getPotentialEnergy();
        final int numberOfRegions = pe.getNumberOfRegions();
        final int numberOfVisibleSamples = (int)( ( pe.getEnd( numberOfRegions - 1 ) - pe.getStart( 0 ) ) / _dx ) + 1;
        final int numberOfExtraSamples = (int)( numberOfVisibleSamples * EXTRA_SAMPLES_MULTIPLIER );
        final int numberOfDampedSamples = SAMPLES_PER_DAMPING_COEFFICIENT * DAMPING_COEFFICIENTS.length;
        final double minX = pe.getStart( 0 ) - ( _dx * numberOfExtraSamples ) - ( _dx * numberOfDampedSamples );
        final double maxX = pe.getEnd( numberOfRegions - 1 ) + ( _dx * numberOfExtraSamples ) + ( _dx * numberOfDampedSamples );
        
        // Calculate the number of samples.
        final int numberOfSamples = (int)( ( maxX - minX ) / _dx ) + 1;
        
        if ( PRINT_DEBUG ) {
            System.out.print( "DEBUG: RichardsonSolver.reset " );//XXX
            System.out.print( " samples=" + numberOfSamples );//XXX
            System.out.print( " visible=" + numberOfVisibleSamples );//XXX
            System.out.print( " extra=" + ( 2 * numberOfExtraSamples ) );//XXX
            System.out.print( " damped=" + ( 2 * numberOfDampedSamples ) );//XXX
            System.out.print( " minX=" + minX + " maxX=" + maxX + " dx=" + _dx );//XXX
            System.out.println();
        }

        // Set indicies for applying zero threshold...
        _thresholdStart = numberOfExtraSamples + numberOfDampedSamples - 1;
        _thresholding = numberOfSamples - numberOfExtraSamples - numberOfDampedSamples - 1;
        
        // Initialize constants used by the propagator.
        final double epsilon = _hbar * _dt / ( _mass * _dx * _dx );
        _alpha = new LightweightComplex( 0.5 * ( 1.0 + Math.cos( epsilon / 2 ) ), -0.5 * Math.sin( epsilon / 2 ) );
        _beta = new LightweightComplex( ( Math.sin( epsilon / 4 ) ) * Math.sin( epsilon / 4 ), 0.5 * Math.sin( epsilon / 2 ) );

        // Initialize the data arrays used by the propagator.
        _positions = new double[numberOfSamples];
        _Psi = new LightweightComplex[numberOfSamples];
        _EtoV = new LightweightComplex[numberOfSamples];
        LightweightComplex A = new LightweightComplex( 1 / ( Math.pow( Math.PI, 0.25 ) * Math.sqrt( width ) ), 0 ); // normalization constant
        for ( int i = 0; i < numberOfSamples; i++ ) {
            final double position = minX + ( i * _dx );
            _positions[i] = position;
            if ( _isZero ) {
                _Psi[i] = new LightweightComplex( 0, 0 );
                _EtoV[i] = new LightweightComplex( 0, 0 );
            }
            else {
                final double r = Math.exp( -( ( position - center ) / width ) * ( ( position - center ) / width ) / 2 );
                _Psi[i] = new LightweightComplex( r * Math.cos( _mass * vx * ( position - center ) / _hbar ), r * Math.sin( _mass * vx * ( position - center ) / _hbar ) );
                _Psi[i].multiply( A );
                final double s = getPotentialEnergy( position ) * _dt / _hbar;
                _EtoV[i] = new LightweightComplex( Math.cos( s ), -Math.sin( s ) );
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
    protected double getPotentialEnergy( double position ) {
        return _wavePacket.getPotentialEnergy().getEnergyAt( position );
    }

    /**
     * Propagates the wave function.
     */
    public void propagate() {
        propagate( _steps );
    }
    
    /*
     * Propagates the solution by a specified number of steps.
     * <p>
     * Note that this method contains a lot of duplicate code.
     * The body of each for loop could easily be made into a method; ditto for the damping code.
     * (In fact, that's the way the I originally implemented this.)
     * But this method is such a bottleneck, that reducing method calls results
     * in big performance savings.  So (sadly) we're trading off good code design for performance.
     * <p>
     * I'm also doing my own complex number addition and multiplication.
     * These operations used to be handled by a complex number class,
     * but the method calls were another source of performance problems.
     *
     * @param steps number of steps to propagate
     */
    private void propagate( int steps ) {

        double r1, r2, r3, r4; // reusable real parts
        double i1, i2, i3, i4; // reusable imaginary parts

        final double alphaReal = _alpha._real;
        final double alphaImaginary = _alpha._imaginary;
        final double betaReal = _beta._real;
        final double betaImaginary = _beta._imaginary;

        final int n = _Psi.length; // number of samples

        for ( int step = 0; step < steps; step++ ) {

            // Starting at 0...
            for ( int i = 0; i < n - 1; i += 2 ) {
                // A = Psi[i]
                r1 = _Psi[i]._real;
                i1 = _Psi[i]._imaginary;

                // B = Psi[i+1]
                r2 = _Psi[i + 1]._real;
                i2 = _Psi[i + 1]._imaginary;

                // Psi[i] = (alpha * A) + (beta * B)
                r3 = alphaReal * r1 - alphaImaginary * i1;
                i3 = alphaReal * i1 + alphaImaginary * r1;
                r4 = betaReal * r2 - betaImaginary * i2;
                i4 = betaReal * i2 + betaImaginary * r2;
                _Psi[i]._real = r3 + r4;
                _Psi[i]._imaginary = i3 + i4;

                // Psi[i+1] = (alpha * B) + (beta * A)
                r3 = alphaReal * r2 - alphaImaginary * i2;
                i3 = alphaReal * i2 + alphaImaginary * r2;
                r4 = betaReal * r1 - betaImaginary * i1;
                i4 = betaReal * i1 + betaImaginary * r1;
                _Psi[i + 1]._real = r3 + r4;
                _Psi[i + 1]._imaginary = i3 + i4;
            }

            // Starting at 1...
            for ( int i = 1; i < n - 1; i += 2 ) {
                // A = Psi[i]
                r1 = _Psi[i]._real;
                i1 = _Psi[i]._imaginary;

                // B = Psi[i+1]
                r2 = _Psi[i + 1]._real;
                i2 = _Psi[i + 1]._imaginary;

                // Psi[i] = (alpha * A) + (beta * B)
                r3 = alphaReal * r1 - alphaImaginary * i1;
                i3 = alphaReal * i1 + alphaImaginary * r1;
                r4 = betaReal * r2 - betaImaginary * i2;
                i4 = betaReal * i2 + betaImaginary * r2;
                _Psi[i]._real = r3 + r4;
                _Psi[i]._imaginary = i3 + i4;

                // Psi[i+1] = (alpha * B) + (beta * A)
                r3 = alphaReal * r2 - alphaImaginary * i2;
                i3 = alphaReal * i2 + alphaImaginary * r2;
                r4 = betaReal * r1 - betaImaginary * i1;
                i4 = betaReal * i1 + betaImaginary * r1;
                _Psi[i + 1]._real = r3 + r4;
                _Psi[i + 1]._imaginary = i3 + i4;
            }

            // Wrap around...
            {
                // A = Psi[n-1]
                r1 = _Psi[n - 1]._real;
                i1 = _Psi[n - 1]._imaginary;

                // B = Psi[0]
                r2 = _Psi[0]._real;
                i2 = _Psi[0]._imaginary;

                // Psi[n-1] = (alpha * A) + (beta * B)
                r3 = alphaReal * r1 - alphaImaginary * i1;
                i3 = alphaReal * i1 + alphaImaginary * r1;
                r4 = betaReal * r2 - betaImaginary * i2;
                i4 = betaReal * i2 + betaImaginary * r2;
                _Psi[n - 1]._real = r3 + r4;
                _Psi[n - 1]._imaginary = i3 + i4;

                // Psi[0] = (alpha * B) + (beta * A)
                r3 = alphaReal * r2 - alphaImaginary * i2;
                i3 = alphaReal * i2 + alphaImaginary * r2;
                r4 = betaReal * r1 - betaImaginary * i1;
                i4 = betaReal * i1 + betaImaginary * r1;
                _Psi[0]._real = r3 + r4;
                _Psi[0]._imaginary = i3 + i4;
            }

            // Apply propagator values...
            for ( int i = 0; i < n; i++ ) {
                // Psi[i] = Psi[i] * EtoV[i]
                r1 = _Psi[i]._real;
                i1 = _Psi[i]._imaginary;
                r2 = _EtoV[i]._real;
                i2 = _EtoV[i]._imaginary;
                _Psi[i]._real = r1 * r2 - i1 * i2;
                _Psi[i]._imaginary = r1 * i2 + i1 * r2;
            }

            // Now do what we did above, but in the reverse order...

            // Wrap around...
            {
                // A = Psi[n-1]
                r1 = _Psi[n - 1]._real;
                i1 = _Psi[n - 1]._imaginary;

                // B = Psi[0]
                r2 = _Psi[0]._real;
                i2 = _Psi[0]._imaginary;

                // Psi[n-1] = (alpha * A) + (beta * B)
                r3 = alphaReal * r1 - alphaImaginary * i1;
                i3 = alphaReal * i1 + alphaImaginary * r1;
                r4 = betaReal * r2 - betaImaginary * i2;
                i4 = betaReal * i2 + betaImaginary * r2;
                _Psi[n - 1]._real = r3 + r4;
                _Psi[n - 1]._imaginary = i3 + i4;

                // Psi[0] = (alpha * B) + (beta * A)
                r3 = alphaReal * r2 - alphaImaginary * i2;
                i3 = alphaReal * i2 + alphaImaginary * r2;
                r4 = betaReal * r1 - betaImaginary * i1;
                i4 = betaReal * i1 + betaImaginary * r1;
                _Psi[0]._real = r3 + r4;
                _Psi[0]._imaginary = i3 + i4;
            }

            // Starting at 1...
            for ( int i = 1; i < n - 1; i += 2 ) {
                // A = Psi[i]
                r1 = _Psi[i]._real;
                i1 = _Psi[i]._imaginary;

                // B = Psi[i+1]
                r2 = _Psi[i + 1]._real;
                i2 = _Psi[i + 1]._imaginary;

                // Psi[i] = (alpha * A) + (beta * B)
                r3 = alphaReal * r1 - alphaImaginary * i1;
                i3 = alphaReal * i1 + alphaImaginary * r1;
                r4 = betaReal * r2 - betaImaginary * i2;
                i4 = betaReal * i2 + betaImaginary * r2;
                _Psi[i]._real = r3 + r4;
                _Psi[i]._imaginary = i3 + i4;

                // Psi[i+1] = (alpha * B) + (beta * A)
                r3 = alphaReal * r2 - alphaImaginary * i2;
                i3 = alphaReal * i2 + alphaImaginary * r2;
                r4 = betaReal * r1 - betaImaginary * i1;
                i4 = betaReal * i1 + betaImaginary * r1;
                _Psi[i + 1]._real = r3 + r4;
                _Psi[i + 1]._imaginary = i3 + i4;
            }

            // Starting at 0...
            for ( int i = 0; i < n - 1; i += 2 ) {
                // A = Psi[i]
                r1 = _Psi[i]._real;
                i1 = _Psi[i]._imaginary;

                // B = Psi[i+1]
                r2 = _Psi[i + 1]._real;
                i2 = _Psi[i + 1]._imaginary;

                // Psi[i] = (alpha * A) + (beta * B)
                r3 = alphaReal * r1 - alphaImaginary * i1;
                i3 = alphaReal * i1 + alphaImaginary * r1;
                r4 = betaReal * r2 - betaImaginary * i2;
                i4 = betaReal * i2 + betaImaginary * r2;
                _Psi[i]._real = r3 + r4;
                _Psi[i]._imaginary = i3 + i4;

                // Psi[i+1] = (alpha * B) + (beta * A)
                r3 = alphaReal * r2 - alphaImaginary * i2;
                i3 = alphaReal * i2 + alphaImaginary * r2;
                r4 = betaReal * r1 - betaImaginary * i1;
                i4 = betaReal * i1 + betaImaginary * r1;
                _Psi[i + 1]._real = r3 + r4;
                _Psi[i + 1]._imaginary = i3 + i4;
            }

            // Apply damping
            applyDamping();
        }
        
        // Apply "zero thresholding".
        applyZeroThresholding();
    }
 
    /*
     * Applies damping to the wave function.
     * Damps the values near the min and max positions to prevent periodic boundary conditions.
     * Otherwise, the wave will appear to exit from one edge of the display and enter on the other edge.
     */
    private void applyDamping() {
        final int numberOfDampedSamples = SAMPLES_PER_DAMPING_COEFFICIENT * DAMPING_COEFFICIENTS.length;
        if ( _Psi.length > numberOfDampedSamples ) {
            for ( int i = 0; i < numberOfDampedSamples; i++ ) {
                final double scale = DAMPING_COEFFICIENTS[i/SAMPLES_PER_DAMPING_COEFFICIENT ];
                // left edge...
                _Psi[i]._real *= scale;
                _Psi[i]._imaginary *= scale;
                // right edge...
                _Psi[_Psi.length - i - 1]._real *= scale;
                _Psi[_Psi.length - i - 1]._imaginary *= scale;
            }
        }
    }
    
    /*
     * Applies "zero thresholding" to the wave function.
     * If all of the Psi[] values in the visible range have real and imaginary
     * parts below the threshold, then set all Psi[] values to zero.
     * If we have enough points outside the visible range, then this will
     * prevent us from ever seeing reflection or circularity at the boundaries. 
     */
    private void applyZeroThresholding() {
        
        // If Psi[] is not already zero...
        if ( !_isZero ) {

            boolean thresholdExceeded = false;
            for ( int i = _thresholdStart; i < _thresholding; i++ ) {
                // Stop as soon as one value exceeds the threshold.
                if ( Math.abs( _Psi[i]._real ) > ZERO_THRESHOLD || Math.abs( _Psi[i]._imaginary ) > ZERO_THRESHOLD ) {
                    thresholdExceeded = true;
                    break;
                }
            }

            if ( ! thresholdExceeded ) {
                for ( int i = 0; i < _Psi.length; i++ ) {
                    _Psi[i]._real = 0;
                    _Psi[i]._imaginary = 0;
                }
                _isZero = true;
                if ( PRINT_DEBUG ) {
                    System.out.println( "DEBUG: zero thresholding has been applied to wave function" );
                }
            }
        }
    }

    /**
     * The wave function solution should be zero if the region that the wave packet
     * is centered in has E < V. (where E=total energy, V=potential energy)
     *
     * @return true or false
     */
    protected boolean isSolutionZero() {
        // E
        final double E = _wavePacket.getTotalEnergy().getEnergy();

        // V at wave packet's center
        AbstractPotential pe = _wavePacket.getPotentialEnergy();
        final int regionIndex = pe.getRegionIndexAt( _wavePacket.getCenter() );
        final double V = pe.getEnergy( regionIndex );

        return E < V;
    }
}