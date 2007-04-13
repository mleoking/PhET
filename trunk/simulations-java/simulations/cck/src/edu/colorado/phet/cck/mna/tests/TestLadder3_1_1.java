/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck.mna.tests;

import edu.colorado.phet.cck.mna.MNACircuit;

/**
 * User: Sam Reid
 * Date: Jun 18, 2006
 * Time: 11:21:33 PM
 * Copyright (c) Jun 18, 2006 by Sam Reid
 */

public class TestLadder3_1_1 {
    public static void main( String[] args ) {
        String[]netlist = new String[]{
                "I1 0 1 1.0",
                "R2 0 1 1.0",
                "R3 1 2 1.0",
                "R4 0 2 1.0",
                "R5 2 3 1.0",
                "R6 0 3 1.0",
                "R7 3 4 1.0",
                "R8 0 4 1.0",
                "I9 0 4 1.0",
        };
        MNACircuit circuit = new MNACircuit();
        circuit.parseNetList( netlist );
        MNACircuit.MNASolution solution = circuit.getMNASystem().getSolution();
        for( int i = 0; i < solution.getNumVoltages(); i++ ) {
            System.out.println( "solution.getVoltage( " + i + ") = " + solution.getVoltage( i ) );
        }
        for( int i = 0; i < solution.getNumCurrents(); i++ ) {
            System.out.println( "solution.getCurrent( " + i + ") = " + solution.getCurrent( i ) );
        }
    }
}
