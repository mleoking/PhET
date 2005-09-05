package edu.colorado.phet.qm.model.propagators;

import edu.colorado.phet.qm.model.*;


/*********************************************************/
/* Two-dimensional Time dependent Schrodinger Equation.  */
/* Use Crank-Nicholson/Cayley algorithm...               */
/* Stable, Norm Conserving.     Li Ju. May.3,1995        */

/**
 * *****************************************************
 */

public class RichardsonPropagator extends Propagator {
    private double simulationTime;

    private double deltaTime;
    private int timeStep;

    private Wave wave;

    private double hbar, mass, epsilon;
    private Complex alpha;
    private Complex beta;
    private Complex[][] betaeven;
    private Complex[][] betaodd;

    protected Wavefunction copy;

    public RichardsonPropagator( DiscreteModel discreteModel, double TAU, Wave wave, Potential potential ) {
        super( discreteModel, potential );
        this.deltaTime = TAU;
        this.wave = wave;
        setPotential( potential );
        simulationTime = 0.0;
        timeStep = 0;
        hbar = 1;
        mass = 1;//0.0020;

        deltaTime = 0.8 * mass / hbar;
        betaeven = new Complex[0][0];
        betaodd = new Complex[0][0];
        update();
    }

    public void update() {

        epsilon = toEpsilon( deltaTime );

        alpha = createAlpha();
        beta = createBeta();
        for( int i = 0; i < betaeven.length; i++ ) {
            for( int j = 0; j < betaeven[i].length; j++ ) {
                betaeven[i][j] = new Complex();
                betaodd[i][j] = new Complex();
                if( ( i + j ) % 2 == 0 ) {
                    betaeven[i][j] = beta;
                }
                else {
                    betaodd[i][j] = beta;
                }
            }
        }
//        System.out.println( "deltaTime= " + deltaTime );
//        System.out.println( "epsilon = " + epsilon );
//        System.out.println( "alpha = " + alpha );
//        System.out.println( "beta = " + beta );
    }

    protected Complex createAlpha() {
        return new Complex( ( 1 + Math.cos( epsilon ) ) / 2.0, -Math.sin( epsilon ) / 2.0 );//from the paper
    }

    protected Complex createBeta() {
        return new Complex( ( 1 - Math.cos( epsilon ) ) / 2.0, Math.sin( epsilon ) / 2.0 );
    }

    private double toEpsilon( double dt ) {
        return hbar * dt / ( mass );
    }

    public void propagate( Wavefunction w ) {
        int nx = w.getWidth();
        if( betaeven.length != w.getWidth() ) {
            betaeven = new Complex[nx][nx];
            betaodd = new Complex[nx][nx];

            update();
        }

        prop2D( w );
        simulationTime += deltaTime;
        timeStep++;
    }

    protected void prop2D( Wavefunction w ) {
        copy = new Wavefunction( w.getWidth(), w.getHeight() );
        applyPotential( w );
        stepIt( w, 0, -1 );
        stepIt( w, 0, 1 );
        stepIt( w, 1, 0 );
        stepIt( w, -1, 0 );
    }

    Complex aTemp = new Complex();
    Complex bTemp = new Complex();
    Complex cTemp = new Complex();

    protected void stepIt( Wavefunction w, int dx, int dy ) {
        w.copyTo( copy );
        for( int i = 1; i < w.getWidth() - 1; i++ ) {
            for( int j = 1; j < w.getHeight() - 1; j++ ) {
                stepIt( w, i, j, dx, dy );
            }
        }
        for( int i = 0; i < w.getWidth(); i++ ) {
            stepItConstrained( w, i, 0, dx, dy );
            stepItConstrained( w, i, w.getHeight() - 1, dx, dy );
        }
        for( int j = 1; j < w.getHeight(); j++ ) {//todo should this start at 0?
            stepItConstrained( w, 0, j, dx, dy );
            stepItConstrained( w, w.getWidth() - 1, j, dx, dy );
        }
    }

    private void stepItConstrained( Wavefunction w, int i, int j, int dx, int dy ) {
        int nxPlus = ( i + dx + w.getWidth() ) % w.getWidth();
        int nyPlus = ( j + dy + w.getHeight() ) % w.getHeight();

        int nxMinus = ( i - dx + w.getWidth() ) % w.getWidth();
        int nyMinus = ( j - dy + w.getHeight() ) % w.getHeight();

        aTemp.setToProduct( alpha, copy.valueAt( i, j ) );
        bTemp.setToProduct( betaeven[i][j], copy.valueAt( nxPlus, nyPlus ) );
        cTemp.setToProduct( betaodd[i][j], copy.valueAt( nxMinus, nyMinus ) );
        w.valueAt( i, j ).setToSum( aTemp, bTemp, cTemp );
    }

    private void stepIt( Wavefunction w, int i, int j, int dx, int dy ) {
        boolean even = ( i + j ) % 2 == 0;
        if( even ) {
            aTemp.setToProduct( alpha, copy.valueAt( i, j ) );
            bTemp.setToProduct( betaeven[i][j], copy.valueAt( i + dx, j + dy ) );
//            cTemp.setToProduct( betaodd[i][j], copy.valueAt( i - dx, j - dy ) );
            w.valueAt( i, j ).setToSum( aTemp, bTemp );
        }
        else {
            aTemp.setToProduct( alpha, copy.valueAt( i, j ) );
//            bTemp.setToProduct( betaeven[i][j], copy.valueAt( i + dx, j + dy ) );
            cTemp.setToProduct( betaodd[i][j], copy.valueAt( i - dx, j - dy ) );
            w.valueAt( i, j ).setToSum( aTemp, cTemp );
        }

    }

    Complex potTemp = new Complex();
    Complex waveTemp = new Complex();

    protected void applyPotential( Wavefunction w ) {//todo ignore damping region
        for( int i = 1; i < w.getWidth() - 1; i++ ) {
            for( int j = 1; j < w.getHeight() - 1; j++ ) {
                double pot = getPotential().getPotential( i, j, timeStep );
                potTemp.setValue( Math.cos( pot * deltaTime / hbar ), -Math.sin( pot * deltaTime / hbar ) );
                waveTemp.setValue( w.valueAt( i, j ) );
                w.valueAt( i, j ).setToProduct( waveTemp, potTemp );
            }
        }
    }

    public void setDeltaTime( double deltaTime ) {
        this.deltaTime = deltaTime;
        update();
    }

    public double getSimulationTime() {
        return simulationTime;
    }

    public void reset() {
    }

    public void setBoundaryCondition( int i, int k, Complex value ) {
    }

    public Propagator copy() {
        return new RichardsonPropagator( getDiscreteModel(), deltaTime, wave, getPotential() );
    }

    public void normalize() {
    }

    public void setWavefunctionNorm( double norm ) {
    }

    public double getEpsilon() {
        return epsilon;
    }

    public Wave getWave() {
        return wave;
    }

}