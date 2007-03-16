/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck.mna.junit_sim;

/**
 * User: Sam Reid
 * Date: Jun 19, 2006
 * Time: 2:21:13 PM
 * Copyright (c) Jun 19, 2006 by Sam Reid
 */

import edu.colorado.phet.cck.mna.MNACircuit;
import junit.framework.TestCase;

/**
 * User: Sam Reid
 * Date: Jun 19, 2006
 * Time: 2:04:44 PM
 * Copyright (c) Jun 19, 2006 by Sam Reid
 */

public class TestRCCharge extends TestCase {
    double v = 0;
    double i = 0.0;
    double dt = 0.01;
    private int numSteps = 10000;
    double c1 = 1;
    double r2 = 2;
    double v3 = 3;
    private double time = 0;
    private double i0 = -v3 / r2;
    private double squaredError;

    public TestRCCharge() {
    }

    private void start() {
        for( int i = 0; i < numSteps; i++ ) {
            step();
        }
    }

    private void step() {
        time += dt;
        MNACircuit circuit = new MNACircuit();
        circuit.addComponent( new MNACircuit.MNAVoltageSource( "v0", 0, 1, v3 ) );
        circuit.addComponent( new MNACircuit.MNAResistor( "r1", 1, 2, r2 ) );
        circuit.addComponent( new MNACircuit.MNACapacitor( "c1", 2, 0, c1, v, i ) );
        MNACircuit companion = circuit.getCompanionModel( dt );
        MNACircuit.MNASolution solution = companion.getSolution();
        this.i = solution.getCurrent( 1 );//should be same everywhere
        this.v = solution.getVoltage( 2 ) - solution.getVoltage( 0 );

        double a = i - getDesiredCurrentExp();
        this.squaredError = a * a;
    }

    public double getDesiredCurrent() {
        return c1 * time * v3 / ( 1 + r2 * c1 * time );
    }

    public double getDesiredCurrentExp() {
        return i0 * Math.exp( -time / ( r2 * c1 ) );
    }

    public void testRCCharge() {
        double MSE = new TestRCCharge().getMSE( 1000 );
//        System.out.println( "MSE = " + MSE );
        double MIN_MSE = 1E-5;
        assertTrue( "Low mean square error test.", MSE < MIN_MSE );
//        System.out.println( "MSE = " + MSE );
    }

    private double getMSE( int numSteps ) {
        double se = 0;
        for( int i = 0; i < numSteps; i++ ) {
            step();
            se += squaredError;
        }
        se /= numSteps;
        return se;
    }

    public static void main( String[] args ) {
        new TestRCCharge().start();
    }

}

