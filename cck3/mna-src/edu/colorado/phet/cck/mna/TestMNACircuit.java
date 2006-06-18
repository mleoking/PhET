/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck.mna;

/**
 * User: Sam Reid
 * Date: Jun 18, 2006
 * Time: 1:23:55 PM
 * Copyright (c) Jun 18, 2006 by Sam Reid
 */

public class TestMNACircuit {
    public static void main( String[] args ) {
        String[]netList = new String[]{
                "i1 0 1 1.0",
                "r2 1 0 1.0",
                "r3 1 2 1.0",
                "r4 2 0 1.0",
                "r5 2 3 1.0",
                "r6 3 0 1.0",
                "r7 3 4 1.0",
                "r8 4 0 1.0",
                "i9 0 4 1.0",
        };
        MNACircuit circuit = new MNACircuit();
        circuit.parseNetList( netList );
        System.out.println( "circuit = " + circuit );
    }
}
