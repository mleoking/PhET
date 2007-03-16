/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck.mna.junit;

import Jama.Matrix;
import edu.colorado.phet.cck.mna.JamaUtil;
import edu.colorado.phet.cck.mna.MNACircuit;
import junit.framework.TestCase;

/**
 * User: Sam Reid
 * Date: Jun 18, 2006
 * Time: 10:49:36 PM
 * Copyright (c) Jun 18, 2006 by Sam Reid
 */

public class TestMNA_2_2_5 extends TestCase {
    public void test1() {
        runMatrixTest( 1, 1, 1, 1, 1, 1 );
    }

    public void test2() {
        runMatrixTest( 1, 2, 3, 4, 5, 6 );
    }

    public void test3() {
        runMatrixTest( 3, 5, 2, 7, 9, 13 );
    }

    private void runMatrixTest( int g1, int g2, int g3, int i1, int v1, int v2 ) {
        String netlist = "" +
                         "r1 1 2 " + 1.0 / g1 + "\n" +
                         "v1 1 0 " + v1 + "\n" +
                         "i1 0 3 " + i1 + "\n" +
                         "v2 2 3 " + v2 + "\n" +
                         "r2 3 0 " + 1.0 / g2 + "\n" +
                         "r3 3 0 " + 1.0 / g3 + "\n";
        MNACircuit circuit = new MNACircuit();
        circuit.parseNetList( netlist );
        MNACircuit.MNASystem system = circuit.getMNASystem();
        Matrix reducedAdmittance = system.getReducedAdmittanceMatrix();

        Matrix desiredReducedAdmittanceMatrix = new Matrix( new double[][]{
                {g1, -g1, 0, 1, 0},
                {-g1, g1, 0, 0, 1},
                {0, 0, g2 + g3, 0, -1},
                {1, 0, 0, 0, 0},
                {0, 1, -1, 0, 0},
        } );

        Matrix reducedSourceMatrix = system.getReducedSourceMatrix();
        Matrix desiredReducedSourceMatrix = new Matrix( new double[][]{
                {0, 0, i1, v1, v2}
        } );
        desiredReducedSourceMatrix = desiredReducedSourceMatrix.transpose();

        assertEquals( true, JamaUtil.equals( reducedSourceMatrix, desiredReducedSourceMatrix ) );
        assertEquals( true, JamaUtil.equals( desiredReducedAdmittanceMatrix, reducedAdmittance ) );
    }
}
