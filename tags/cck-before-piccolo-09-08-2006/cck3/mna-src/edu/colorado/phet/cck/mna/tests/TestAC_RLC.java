/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck.mna.tests;

import edu.colorado.phet.cck.mna.MNACircuit;

/**
 * User: Sam Reid
 * Date: Jun 19, 2006
 * Time: 12:26:27 AM
 * Copyright (c) Jun 19, 2006 by Sam Reid
 */

public class TestAC_RLC {
    double capV = 5.0;
    double capI = 2.0;

    double indV = 1.0;
    double indI = 2.0;

    double dt = 0.01;
    private int numSteps = 10000;
    private double time = 0;

    public static void main( String[] args ) {
        new TestAC_RLC().start();
    }

    private void start() {
        for( int i = 0; i < numSteps; i++ ) {
            step();
        }
    }

    private void step() {
        //r*r*c<4L underdamped
        double V = 10;
        double R = 1;
        double L = 0.001;
        double C = 0.001;
        time += dt;
        MNACircuit circuit = new MNACircuit();
        double voltage = V * Math.sin( time / 2 );
//        System.out.println( "voltage = " + voltage );
        circuit.addComponent( new MNACircuit.MNAVoltageSource( "v0", 0, 1, voltage ) );
        circuit.addComponent( new MNACircuit.MNAResistor( "r1", 1, 2, R ) );
        circuit.addComponent( new MNACircuit.MNAInductor( "L1", 2, 3, L, indV, indI ) );
        circuit.addComponent( new MNACircuit.MNACapacitor( "c1", 3, 0, C, capV, capI ) );
        MNACircuit companion = circuit.getCompanionModel( dt );
        MNACircuit.MNASolution solution = companion.getSolution();
        this.capI = solution.getCurrent( 1 );//should be same everywhere
        this.indI = solution.getCurrent( 1 );
        this.capV = -( solution.getVoltage( 0 ) - solution.getVoltage( 3 ) );
        this.indV = -( solution.getVoltage( 3 ) - solution.getVoltage( 2 ) );
        System.out.println( capV );
//        System.out.println( i );
    }
}
