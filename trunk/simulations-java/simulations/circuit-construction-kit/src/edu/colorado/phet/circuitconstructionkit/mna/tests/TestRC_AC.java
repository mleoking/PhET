/*  */
package edu.colorado.phet.circuitconstructionkit.mna.tests;

import edu.colorado.phet.circuitconstructionkit.mna.MNACircuit;

/**
 * User: Sam Reid
 * Date: Jun 19, 2006
 * Time: 12:26:27 AM
 */

public class TestRC_AC {
    double v = 0;
    double i = 0.0;
    double dt = 0.01;
    private int numSteps = 10000;
    private double time = 0;

    public TestRC_AC() {
    }

    public static void main( String[] args ) {
        new TestRC_AC().start();
    }

    private void start() {
        for( int i = 0; i < numSteps; i++ ) {
            step();
        }
    }

    private void step() {
        time += dt;
        MNACircuit circuit = new MNACircuit();
        circuit.addComponent( new MNACircuit.MNAVoltageSource( "v0", 0, 1, 9.0 * Math.sin( time ) ) );
        circuit.addComponent( new MNACircuit.MNAResistor( "r1", 1, 2, 3.0 ) );
        circuit.addComponent( new MNACircuit.MNACapacitor( "c1", 2, 0, 3.0, v, i ) );
        MNACircuit companion = circuit.getCompanionModel( dt );
        MNACircuit.MNASolution solution = companion.getSolution();
        this.i = solution.getCurrent( 1 );//should be same everywhere
        this.v = solution.getVoltage( 2 ) - solution.getVoltage( 0 );
        System.out.println( v );
    }
}
