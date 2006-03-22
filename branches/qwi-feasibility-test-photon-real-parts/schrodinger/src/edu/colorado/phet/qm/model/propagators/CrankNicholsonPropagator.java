package edu.colorado.phet.qm.model.propagators;

import edu.colorado.phet.qm.model.Potential;
import edu.colorado.phet.qm.model.Propagator;
import edu.colorado.phet.qm.model.Wave;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.model.math.Complex;

/* Two-dimensional Time dependent Schrodinger Equation.  */
/* Use Crank-Nicholson/Cayley algorithm...               */
/* Stable, Norm Conserving.     Li Ju. May.3,1995        */

public class CrankNicholsonPropagator extends Propagator {
    private double simulationTime;

    private double deltaTime;
    private int timeStep;

    private static final Complex TWO = new Complex( 2, 0 );
    private static final Complex MINUS_ONE = new Complex( -1, 0 );
    private Wave wave;

    public CrankNicholsonPropagator( double TAU, Wave wave, Potential potential ) {
        super( potential );
        this.deltaTime = TAU;
        this.wave = wave;
        simulationTime = 0.0;
        timeStep = 0;
    }

    private double getPotential( int i, int j ) {
        return getPotential().getPotential( i, j, timeStep );
    }

    public void propagate( Wavefunction w ) {
        simulationTime += deltaTime;
        timeStep++;

        int XMESH = w.getWidth() - 1;
        int YMESH = w.getHeight() - 1;
        Complex[] alpha = new Complex[XMESH + YMESH];
        Complex[] beta = new Complex[XMESH + YMESH];
        Complex[] gamma = new Complex[XMESH + YMESH];
        for( int i = 0; i < alpha.length; i++ ) {
            alpha[i] = new Complex();
            beta[i] = new Complex();
            gamma[i] = new Complex();
        }

        Complex XAP = new Complex( 0, -0.5 * XMESH * XMESH * deltaTime );
        Complex XA0 = new Complex();
        Complex XA00 = new Complex( 1, XMESH * XMESH * deltaTime );
        Complex XA0V = new Complex( 0, 0.5 * deltaTime );
        Complex YAP = new Complex( 0, -0.5 * YMESH * YMESH * deltaTime );
        Complex YA0 = new Complex();
        Complex YA00 = new Complex( 1, YMESH * YMESH * deltaTime );
        Complex YA0V = new Complex( 0, 0.5 * deltaTime );
        Complex bi = new Complex();
        Complex bj = new Complex();

        for( int j = 1; j < YMESH; j++ ) {
            int N = XMESH;
            alpha[N - 1].zero();
            beta[N - 1] = wave.getValue( XMESH, j, simulationTime );
            for( int i = N - 1; i >= 1; i-- ) {
                XA0 = XA00.plus( XA0V.times( getPotential( i, j ) / 2.0 ) );
//                bi = ( ( TWO.minus( XA0 ) ).times( w[i][j] ) ).minus( ( XAP.times( w[i - 1][j].plus( w[i + 1][j] ) ) ) );
                bi = ( ( TWO.minus( XA0 ) ).times( w.valueAt( i, j ) ) ).minus( ( XAP.times( w.valueAt( i - 1, j ).plus( w.valueAt( i + 1, j ) ) ) ) );
                gamma[i] = MINUS_ONE.divideBy( ( XA0.plus( XAP.times( alpha[i] ) ) ) );
                alpha[i - 1] = gamma[i].times( XAP );
                beta[i - 1] = gamma[i].times( ( XAP.times( beta[i] ) ).minus( bi ) );
            }
            fixEdges( w );
            origFixA( w, j );
            for( int i = 0; i <= N - 1; i++ ) {
                w.setValue( i + 1, j, ( alpha[i].times( w.valueAt( i, j ) ) ).plus( beta[i] ) );
            }
        }
        fixEdges( w );
        for( int i = 1; i < XMESH; i++ ) {
            int N = YMESH;
            alpha[N - 1].zero();
            beta[N - 1] = wave.getValue( i, YMESH, simulationTime );

            for( int j = N - 1; j >= 1; j-- ) {
                YA0 = YA00.plus( YA0V.times( getPotential( i, j ) / 2.0 ) );
                bj = ( ( TWO.minus( YA0 ) ).times( w.valueAt( i, j ) ) ).minus( ( YAP.times( w.valueAt( i, j - 1 ).plus( w.valueAt( i, j + 1 ) ) ) ) );
                gamma[j] = MINUS_ONE.divideBy( YA0.plus( YAP.times( alpha[j] ) ) );
                alpha[j - 1] = gamma[j].times( YAP );
                beta[j - 1] = gamma[j].times( ( ( YAP.times( beta[j] ) ).minus( bj ) ) );
            }
            fixEdges( w );
            origFixB( w, i );
            for( int j = 0; j <= N - 1; j++ ) {
                w.setValue( i, j + 1, ( alpha[j].times( w.valueAt( i, j ) ) ).plus( beta[j] ) );
            }
        }
        fixEdges( w );
    }

    private void origFixB( Wavefunction w, int i ) {
        setValue( w, i, 0 );
    }

    private void origFixA( Wavefunction w, int j ) {
        setValue( w, 0, j );
    }

    private void fixEdges( Wavefunction w ) {
        if( true ) {
            return;
        }
        int border = 1;
        int XMESH = w.getWidth() - 1;
        int YMESH = w.getHeight() - 1;
        for( int i = 0; i <= XMESH; i++ ) {
            setValue( w, i, 0 );
            setValue( w, i, YMESH );
            for( int b = 0; b < border; b++ ) {
                setValue( w, i, 0 + 1 + b );
                setValue( w, i, YMESH - 1 - b );
            }
        }
        for( int j = 0; j <= YMESH; j++ ) {
            setValue( w, 0, j );
            setValue( w, XMESH, j );

            for( int b = 0; b < border; b++ ) {
                setValue( w, 0 + 1 + b, j );
                setValue( w, XMESH - 1 - b, j );
            }
        }
    }

    private void setValue( Wavefunction w, int x, int y ) {
        Complex val = wave.getValue( x, y, timeStep * deltaTime );
        w.setValue( x, y, val );
    }

    public void setDeltaTime( double deltaTime ) {
        this.deltaTime = deltaTime;
    }

    public double getSimulationTime() {
        return simulationTime;
    }

    public void reset() {
    }

    public void setBoundaryCondition( int i, int k, Complex value ) {
    }

    public Propagator copy() {
        return new CrankNicholsonPropagator( deltaTime, wave, getPotential() );
    }

    public void normalize() {
    }

    public void setWavefunctionNorm( double norm ) {
    }

    public void setValue( int i, int j, double real, double imag ) {
    }
}