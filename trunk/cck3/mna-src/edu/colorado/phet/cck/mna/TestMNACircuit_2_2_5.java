/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck.mna;

/**
 * User: Sam Reid
 * Date: Jun 18, 2006
 * Time: 1:23:55 PM
 * Copyright (c) Jun 18, 2006 by Sam Reid
 */

public class TestMNACircuit_2_2_5 {
    public static void main( String[] args ) {
        String netlist = "" +
                         "r1 1 2 1.0\n" +
                         "v2 1 0 1.0\n" +
                         "i1 0 3 1.0\n" +
                         "v3 2 3 1.0\n" +
                         "r2 3 0 1.0\n" +
                         "r3 3 0 1.0\n";
        MNACircuit circuit = new MNACircuit();
        circuit.parseNetList( netlist );
        System.out.println( "circuit = " + circuit );
        MNACircuit.MNASystem system = circuit.getMNASystem();
        System.out.println( "system = " + system );
    }
}
