/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck.mna.tests;

import edu.colorado.phet.cck.mna.MNACircuit;

/**
 * User: Sam Reid
 * Date: Jun 19, 2006
 * Time: 12:26:27 AM
 * Copyright (c) Jun 19, 2006 by Sam Reid
 */

public class TestRC {
    double v = 0;
    double i = 0.0;
    double dt = 0.01;
    private int numSteps = 10000;

    public TestRC() {
    }

    public static void main( String[] args ) {
        new TestRC().start();
    }

    private void start() {
        for( int i = 0; i < numSteps; i++ ) {
            step();
        }
    }

    private void step() {
        MNACircuit circuit = new MNACircuit();
        circuit.addComponent( new MNACircuit.MNAVoltageSource( "v0", 0, 1, 9.0 ) );
        circuit.addComponent( new MNACircuit.MNAResistor( "r1", 1, 2, 3.0 ) );
        circuit.addComponent( new MNACircuit.MNACapacitor( "c1", 2, 0, 3.0, v, i ) );
        MNACircuit companion = circuit.getCompanionModel( dt );
        MNACircuit.MNASolution solution = companion.getSolution();
        this.i = solution.getCurrent( 1 );//should be same everywhere
        this.v = solution.getVoltage( 2 ) - solution.getVoltage( 0 );
        System.out.println( v );
//        System.out.println( i );
    }
}
