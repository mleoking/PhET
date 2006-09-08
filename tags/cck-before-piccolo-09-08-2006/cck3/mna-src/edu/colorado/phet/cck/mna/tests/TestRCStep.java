/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck.mna.tests;

import edu.colorado.phet.cck.mna.MNACircuit;

/**
 * User: Sam Reid
 * Date: Jun 19, 2006
 * Time: 12:12:34 AM
 * Copyright (c) Jun 19, 2006 by Sam Reid
 */

public class TestRCStep {
    public static void main( String[] args ) {
        String[]netlist = new String[]{
                "v0 0 1 9.0",
                "r1 1 2 3",
                "c2 2 0 3"
        };
        MNACircuit circuit = new MNACircuit();
        circuit.parseNetList( netlist );
        double dt = 0.01;
        MNACircuit companion = circuit.getCompanionModel( dt );
        System.out.println( "companion = " + companion );
        MNACircuit.MNASolution solution = companion.getMNASystem().getSolution();
        System.out.println( "solution = " + solution );
    }
}
