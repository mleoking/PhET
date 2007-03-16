/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck.mna.junit;

import edu.colorado.phet.cck.mna.MNACircuit;
import junit.framework.TestCase;

/**
 * User: Sam Reid
 * Date: Jun 18, 2006
 * Time: 11:26:05 PM
 * Copyright (c) Jun 18, 2006 by Sam Reid
 */

public class TestLadder3_1_1 extends TestCase {
    public void testLadder() {
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
        assertEquals( "numVoltages", 5, solution.getNumVoltages() );
        assertEquals( "numCurrents", 0, solution.getNumCurrents() );
        double tol = 0.000001;
        assertEquals( "v0", solution.getVoltage( 0 ), 0, tol );
        assertEquals( "v1", solution.getVoltage( 1 ), 2.0 / 3.0, tol );
        assertEquals( "v2", solution.getVoltage( 2 ), 1.0 / 3.0, tol );
        assertEquals( "v3", solution.getVoltage( 3 ), 1.0 / 3.0, tol );
        assertEquals( "v4", solution.getVoltage( 4 ), 2.0 / 3.0, tol );
    }
}
