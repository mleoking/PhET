/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck.mna.junit;

import Jama.Matrix;
import edu.colorado.phet.cck.mna.JamaUtil;
import edu.colorado.phet.cck.mna.MNACircuit;
import junit.framework.TestCase;

/**
 * User: Sam Reid
 * Date: Jun 18, 2006
 * Time: 9:46:11 PM
 * Copyright (c) Jun 18, 2006 by Sam Reid
 */

public class Test_1_4_6_NA_Generation extends TestCase {
    public void test_1_4_6() {
        String netlist = "i1 0 1 1.0\n" +
                         "r2 1 0 1.0\n" +
                         "r3 1 2 1.0\n" +
                         "r4 2 0 1.0\n" +
                         "r5 2 3 1.0\n" +
                         "r6 3 0 1.0\n" +
                         "r7 3 4 1.0\n" +
                         "r8 4 0 1.0\n" +
                         "i9 0 4 1.0";
        MNACircuit circuit = new MNACircuit();
        circuit.parseNetList( netlist );

        MNACircuit.MNASystem system = circuit.getMNASystem();


        double[][]values = new double[][]{
                {4, -1, -1, -1, -1},
                {-1, 2, -1, 0, 0},
                {-1, -1, 3, -1, 0},
                {-1, 0, -1, 3, -1},
                {-1, 0, 0, -1, 2}
        };
        Matrix admittance = new Matrix( values );

        Matrix source = new Matrix( new double[][]{{
                -2, 1, 0, 0, 1
        }} );
        source = source.transpose();
        assertEquals( true, JamaUtil.equals( system.getAdmittanceMatrix(), admittance ) );
        assertEquals( true, JamaUtil.equals( system.getSourceMatrix(), source ) );
    }
}
