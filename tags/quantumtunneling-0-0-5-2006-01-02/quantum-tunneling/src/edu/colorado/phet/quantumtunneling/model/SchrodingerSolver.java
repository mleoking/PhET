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
    
    private static final double HBAR = QTConstants.PLANCKS_CONSTANT;
    private static final double MASS = QTConstants.ELECTRON_MASS;
    
    private WavePacket _wavePacket;
    
    private double _dx;
    private double _dt;
    private double _energyScale; // ratio of V/E
    private double _positions[]; // position at each sample point
    private MutableComplex _Psi[]; // wave function values at each sample point
    private Complex _EtoV[]; // potential energy propogator = exp(-i*V(x)*dt/hbar)
    
    private double _epsilon; // special parameter for Richardson algorithm
    private Complex _alpha; // special parameter for Richardson algorithm
    private Complex _beta; // special parameter for Richardson algorithm
    
    public SchrodingerSolver( WavePacket wavePacket, double dx, double dt ) {
        _wavePacket = wavePacket;
        _dx = dx;
        _dt = dt;
        update();
    }

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
        System.out.println( "SchrodingerSolver.update" );//XXX
        initPhysics();
    }
    
    public void initPhysics() {
        
        if ( _wavePacket.getTotalEnergy() == null || _wavePacket.getPotentialEnergy() == null ) {
            return;
        }
        
        System.out.println( "SchrodingerSolver.initPhysics" );//XXX
        _energyScale = 1; //XXX ratio of E/V ???
        
        final double packetWidth = _wavePacket.getWidth();
        final double packetCenter = _wavePacket.getCenter();
        final double E = _wavePacket.getTotalEnergy().getEnergy();
        final double vx = Math.sqrt( 2 * E / MASS );
        
        AbstractPotential pe = _wavePacket.getPotentialEnergy();
        final int numberOfRegions = pe.getNumberOfRegions();
        final double minX = pe.getStart( 0 );
        final double maxX = pe.getEnd( numberOfRegions - 1 ) + _dx;
        
        final int numberOfPoints = (int)( ( maxX - minX ) / _dx );
        _positions = new double[numberOfPoints];
        _Psi = new MutableComplex[numberOfPoints];
        _EtoV = new Complex[numberOfPoints];
        
        _epsilon = HBAR * _dt / ( MASS * _dx * _dx );
        _alpha = new Complex( 0.5 * ( 1.0 + Math.cos( _epsilon / 2 ) ), -0.5 * Math.sin( _epsilon / 2 ) );
        _beta = new Complex( ( Math.sin( _epsilon / 4 ) ) * Math.sin( _epsilon / 4 ), 0.5 * Math.sin( _epsilon / 2 ) );

        for ( int i = 0; i < numberOfPoints; i++ ) {
            final double position = minX + ( i * _dx );
            _positions[i] = position;
            double r = Math.exp( -( ( position - packetCenter ) / packetWidth ) * ( ( position - packetCenter ) / packetWidth ) );
            _Psi[i] = new MutableComplex( r * Math.cos( MASS * vx * position / HBAR ), r * Math.sin( MASS * vx * position / HBAR ) );
            r = v( position ) * _dt / HBAR;
            _EtoV[i] = new Complex( Math.cos( r ), -Math.sin( r ) );
        }
    }
    
    private double v( final double x ) {
        return ( Math.abs( x ) < _wavePacket.getWidth() / 2 ) ? ( getTotalEnergy() * _energyScale ) : 0;
    }
    
    public void stepPhysics( int steps ) {
        System.out.println( "SchrodingerSolver.stepPhysics " + steps );//XXX
        for ( int i = 0; i < steps; i++ ) {
            doStep1();
            doStep1();
            doStep2();
            doStep3();
            doStep2();
            doStep1();
            doStep1();
        }
    }
    
    private void doStep1() {
        MutableComplex x = new MutableComplex( 0, 0 );
        MutableComplex y = new MutableComplex( 0, 0 );
        MutableComplex w = new MutableComplex( 0, 0 );
        MutableComplex z = new MutableComplex( 0, 0 );
        int numberOfPoints = _Psi.length;
        for ( int i = 0; i < numberOfPoints - 1; i += 2 ) {
            x.setValue( _Psi[i] );
            y.setValue( _Psi[i + 1] );
            w.setValue( _alpha.getMultiply( x ) );
            z.setValue( _beta.getMultiply( y ) );
            _Psi[i + 0].setValue( w.getAdd( z ) );
            w.setValue( _alpha.getMultiply( y ) );
            z.setValue( _beta.getMultiply( x ) );
            _Psi[i + 1].setValue( w.getAdd( z ) );
        }
    }
    
    private void doStep2() {
        MutableComplex x = new MutableComplex( 0, 0 );
        MutableComplex y = new MutableComplex( 0, 0 );
        MutableComplex w = new MutableComplex( 0, 0 );
        MutableComplex z = new MutableComplex( 0, 0 );
        int numberOfPoints = _Psi.length;
        x.setValue( _Psi[numberOfPoints - 1] );
        y.setValue( _Psi[0] );
        w.setValue( _alpha.getMultiply( x ) );
        z.setValue( _beta.getMultiply( y ) );
        _Psi[numberOfPoints - 1].setValue( w.getAdd( z ) );
        w.setValue( _alpha.getMultiply( y ) );
        z.setValue( _beta.getMultiply( x ) );
        _Psi[0].setValue( w.getAdd( z ) );
    }
    
    private void doStep3() {
        MutableComplex x = new MutableComplex( 0, 0 );
        int numberOfPoints = _Psi.length;
        for ( int i = 0; i < numberOfPoints; i++ ) {
            x.setValue( _Psi[i] );
            _Psi[i].setValue( x.getMultiply( _EtoV[i] ) );
        }
    }
    
    private double getTotalEnergy() {
        return _wavePacket.getTotalEnergy().getEnergy();
    }
    
    private double getPotentialEnergy( double position ) {
        return _wavePacket.getPotentialEnergy().getEnergyAt( position );
    }
}
