/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck.mna.tests;

import Jama.Matrix;
import edu.colorado.phet.cck.mna.MNACircuit;

/**
 * User: Sam Reid
 * Date: Jun 18, 2006
 * Time: 4:16:22 PM
 * Copyright (c) Jun 18, 2006 by Sam Reid
 */

public class MNATest3 {
    public static void main( String[] args ) {
        String[]netlist = new String[]{
                "V1 0 1 8",
                "R1 1 0 1",
                "R2 1 0 1",
        };
        MNACircuit circuit = new MNACircuit();
        circuit.parseNetList( netlist );
        MNACircuit.MNASystem system = circuit.getMNASystem();
        System.out.println( "system = " + system );

        Matrix solution = system.getSolutionMatrix();
        System.out.println( "solution = " + solution );
        solution.print( 3, 3 );
    }
}
