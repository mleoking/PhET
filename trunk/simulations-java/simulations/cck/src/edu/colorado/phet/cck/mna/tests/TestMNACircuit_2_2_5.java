/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck.mna.tests;

import Jama.Matrix;
import edu.colorado.phet.cck.mna.JamaUtil;
import edu.colorado.phet.cck.mna.MNACircuit;

/**
 * User: Sam Reid
 * Date: Jun 18, 2006
 * Time: 1:23:55 PM
 * Copyright (c) Jun 18, 2006 by Sam Reid
 */

public class TestMNACircuit_2_2_5 {
    public static void main( String[] args ) {
        double g1 = 1;
        double g2 = 2;
        double g3 = 3;
        double i1 = 4;
        double v1 = 5;
        double v2 = 6;

        String netlist = "" +
                         "r1 1 2 " + 1.0 / g1 + "\n" +
                         "v1 1 0 " + v1 + "\n" +
                         "i1 0 3 " + i1 + "\n" +
                         "v2 2 3 " + v2 + "\n" +
                         "r2 3 0 " + 1.0 / g2 + "\n" +
                         "r3 3 0 " + 1.0 / g3 + "\n";
        MNACircuit circuit = new MNACircuit();
        circuit.parseNetList( netlist );
        System.out.println( "circuit = " + circuit );
        MNACircuit.MNASystem system = circuit.getMNASystem();
        System.out.println( "system = " + system );
        Matrix reducedAdmittance = system.getReducedAdmittanceMatrix();

        Matrix desiredReducedAdmittanceMatrix = new Matrix( new double[][]{
                {g1, -g1, 0, 1, 0},
                {-g1, g1, 0, 0, 1},
                {0, 0, g2 + g3, 0, -1},
                {1, 0, 0, 0, 0},
                {0, 1, -1, 0, 0},
        } );
        System.out.println( "desiredReducedAdmittanceMatrixesiredReducedAdmittanceMatrix = " + desiredReducedAdmittanceMatrix );
        desiredReducedAdmittanceMatrix.print( 3, 3 );

        System.out.println( "reducedAdmittance = " + reducedAdmittance );
        reducedAdmittance.print( 3, 3 );
        System.out.println( "JamaUtil.equals( desiredReducedAdmittanceMatrix,reducedAdmittance ) = " + JamaUtil.equals( desiredReducedAdmittanceMatrix, reducedAdmittance ) );

        Matrix reducedSourceMatrix = system.getReducedSourceMatrix();
        System.out.println( "reducedSourceMatrix = " + reducedSourceMatrix );
        reducedSourceMatrix.print( 3, 3 );
        Matrix desiredReducedSourceMatrix = new Matrix( new double[][]{
                {0, 0, i1, v1, v2}
        } );

        desiredReducedSourceMatrix = desiredReducedSourceMatrix.transpose();
        System.out.println( "JamaUtil.equals( reducedSourceMatrix,desiredReducedSourceMatrix) = " + JamaUtil.equals( reducedSourceMatrix, desiredReducedSourceMatrix ) );


    }
}
