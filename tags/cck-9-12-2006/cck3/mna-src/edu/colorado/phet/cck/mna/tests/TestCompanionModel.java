/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck.mna.tests;

import edu.colorado.phet.cck.mna.MNACircuit;

/**
 * User: Sam Reid
 * Date: Jun 18, 2006
 * Time: 11:40:40 PM
 * Copyright (c) Jun 18, 2006 by Sam Reid
 */

public class TestCompanionModel {
    public static void main( String[] args ) {
        String[]netlist = new String[]{
                "C1 0 1 1.0"
        };
        MNACircuit circuit = new MNACircuit();
        circuit.parseNetList( netlist );
        System.out.println( "circuit = " + circuit );

        MNACircuit companionModel = circuit.getCompanionModel( 0.1 );
        System.out.println( "companionModel = " + companionModel );
    }
}
