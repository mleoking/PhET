/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck.mna.tests;

import edu.colorado.phet.cck.mna.MNACircuit;

/**
 * User: Sam Reid
 * Date: Jun 19, 2006
 * Time: 2:04:44 PM
 * Copyright (c) Jun 19, 2006 by Sam Reid
 */

public class TestRC_Time {
    double v = 0;
    double i = 0.0;
    double dt = 0.01;
    private int numSteps = 10000;
    double c1 = 1;
    double r2 = 2;
    double v3 = 3;
    private double time = 0;
    private double i0 = 1;

    public TestRC_Time() {
    }

    public static void main( String[] args ) {
        new TestRC_Time().start();
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

        if( time == dt ) {
            i0 = i / Math.exp( -time / ( r2 * c1 ) );//normalize on the first step
        }
//        System.out.println( "actualCurrent= " + i + ", desiredCurrent=" + getDesiredCurrentExp() );
//        System.out.println( "actualCurrent=\t" + i + "\tdesiredCurrent=\t" + getDesiredCurrentExp() );
        double a = i - getDesiredCurrentExp();
        double squaredDiff = a * a;
        System.out.println( "squaredDiff = " + squaredDiff );
//        System.out.println( getDesiredCurrent() );
//        System.out.println( getDesiredCurrentExp() );
    }

    public double getDesiredCurrent() {
        return c1 * time * v3 / ( 1 + r2 * c1 * time );
    }

    public double getDesiredCurrentExp() {
        return i0 * Math.exp( -time / ( r2 * c1 ) );
    }
}
