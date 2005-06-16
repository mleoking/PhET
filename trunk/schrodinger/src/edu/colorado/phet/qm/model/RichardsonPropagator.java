package edu.colorado.phet.qm.model;

import edu.colorado.phet.qm.model.operators.ProbabilityValue;


/*********************************************************/
/* Two-dimensional Time dependent Schrodinger Equation.  */
/* Use Crank-Nicholson/Cayley algorithm...               */
/* Stable, Norm Conserving.     Li Ju. May.3,1995        */

/**
 * *****************************************************
 */

public class RichardsonPropagator implements Propagator {
    private double simulationTime;

    private double deltaTime;
    private int timeStep;

    private BoundaryCondition boundaryCondition;
    private Potential potential;

    double hbar, mass, epsilon;
    private Complex alpha;
    private Complex beta;
    private Complex[][] betaeven;
    private Complex[][] betaodd;

    public RichardsonPropagator( double TAU, BoundaryCondition boundaryCondition, Potential potential ) {
        this.deltaTime = TAU;
        this.boundaryCondition = boundaryCondition;
        this.potential = potential;
        simulationTime = 0.0;
        timeStep = 0;
        hbar = 1;
        mass = 50;

        double dx = 1.0;
        double dt = 0.8 * mass * dx * dx / hbar;
        epsilon = hbar * dt / ( mass * dx * dx );

        alpha = new Complex( ( 1 + Math.cos( epsilon ) ) / 2.0, -Math.sin( epsilon ) / 2.0 );
        beta = new Complex( ( 1 - Math.cos( epsilon ) ) / 2.0, Math.sin( epsilon ) / 2.0 );
    }

    public void propagate( Complex[][] w ) {
        int nx = w.length;
        double norm = new ProbabilityValue().compute( w );
        System.out.println( "pre: norm = " + norm );
        betaeven = new Complex[nx][nx];
        betaodd = new Complex[nx][nx];
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
        prop2D( w );
        norm = new ProbabilityValue().compute( w );
        System.out.println( "post: norm = " + norm );
        simulationTime += deltaTime;
    }

    private void prop2D( Complex[][] w ) {
        applyPotential( w );
//

        stepX0( w, 1, 0 );
        stepX0( w, -1, 0 );
        stepX0( w, 0, 1 );
        stepX0( w, 0, -1 );

//        copy = Wavefunction.copy( w );
//        for( int i = 1; i < w.length - 1; i++ ) {
//            for( int j = 1; j < w[0].length - 1; j++ ) {
//                Complex alphaTerm = alpha.times( copy[i][j] );
//                Complex evenTerm = betaeven[i][j].times( copy[i + 1][j] );
//                Complex oddTerm = betaodd[i][j].times( copy[i - 1][j] );
//                w[i][j].setValue( alphaTerm.plus( evenTerm ).plus( oddTerm ) );
//            }
//        }
//
//        copy = Wavefunction.copy( w );
//        for( int i = 1; i < w.length - 1; i++ ) {
//            for( int j = 1; j < w[0].length - 1; j++ ) {
//                Complex alphaTerm = alpha.times( copy[i][j] );
//                Complex evenTerm = betaeven[i][j].times( copy[i][j - 1] );
//                Complex oddTerm = betaodd[i][j].times( copy[i][j + 1] );
//                w[i][j].setValue( alphaTerm.plus( evenTerm ).plus( oddTerm ) );
//            }
//        }
//
//        copy = Wavefunction.copy( w );
//        for( int i = 1; i < w.length - 1; i++ ) {
//            for( int j = 1; j < w[0].length - 1; j++ ) {
//                Complex alphaTerm = alpha.times( copy[i][j] );
//                Complex evenTerm = betaeven[i][j].times( copy[i][j + 1] );
//                Complex oddTerm = betaodd[i][j].times( copy[i][j - 1] );
//                w[i][j].setValue( alphaTerm.plus( evenTerm ).plus( oddTerm ) );
//            }
//        }
    }

    private void stepX0( Complex[][] w, int dv ) {
        Complex[][] copy = Wavefunction.copy( w );
        for( int i = 1; i < w.length - 1; i++ ) {
            for( int j = 1; j < w[0].length - 1; j++ ) {
                Complex alphaTerm = alpha.times( copy[i][j] );
                Complex evenTerm = betaeven[i][j].times( copy[i - dv][j] );
                Complex oddTerm = betaodd[i][j].times( copy[i + dv][j] );
                w[i][j].setValue( alphaTerm.plus( evenTerm ).plus( oddTerm ) );
            }
        }
    }

//    private void stepX0( Complex[][] w, int dv ) {
//        Complex[][] copy = Wavefunction.copy( w );
//        for( int i = 1; i < w.length - 1; i++ ) {
//            for( int j = 1; j < w[0].length - 1; j++ ) {
//                Complex alphaTerm = alpha.times( copy[i][j] );
//                Complex evenTerm = betaeven[i][j].times( copy[i - dv][j] );
//                Complex oddTerm = betaodd[i][j].times( copy[i + dv][j] );
//                w[i][j].setValue( alphaTerm.plus( evenTerm ).plus( oddTerm ) );
//            }
//        }
//    }

//
//    private void stepY0( Complex[][] w, int dv ) {
//        Complex[][] copy = Wavefunction.copy( w );
//        for( int i = 1; i < w.length - 1; i++ ) {
//            for( int j = 1; j < w[0].length - 1; j++ ) {
//                Complex alphaTerm = alpha.times( copy[i][j] );
//                Complex evenTerm = betaeven[i][j].times( copy[i][j - dv] );
//                Complex oddTerm = betaodd[i][j].times( copy[i][j + dv] );
//                w[i][j].setValue( alphaTerm.plus( evenTerm ).plus( oddTerm ) );
//            }
//        }
//    }

//    private void stepX1( Complex[][] w ) {
//        Complex[][] copy = Wavefunction.copy( w );
//        for( int i = 1; i < w.length - 1; i++ ) {
//            for( int j = 1; j < w[0].length - 1; j++ ) {
//                Complex alphaTerm = alpha.times( copy[i][j] );
//                Complex evenTerm = betaeven[i][j].times( copy[i - 1][j] );
//                Complex oddTerm = betaodd[i][j].times( copy[i + 1][j] );
//                w[i][j].setValue( alphaTerm.plus( evenTerm ).plus( oddTerm ) );
//            }
//        }
//    }

    private void applyPotential( Complex[][] w ) {
    }

    public void setDeltaTime( double deltaTime ) {
        this.deltaTime = deltaTime;
    }

    public double getSimulationTime() {
        return simulationTime;
    }
}