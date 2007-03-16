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

public class MNATest2 {
    public static void main( String[] args ) {
        String[]netlist = new String[]{
                "V1 0 1 9",
                "R1 1 2 1",
                "R2 2 3 1",
                "R3 3 0 1"
        };
        MNACircuit circuit = new MNACircuit();
        circuit.parseNetList( netlist );
        MNACircuit.MNASystem system = circuit.getMNASystem();
        System.out.println( "system = " + system );

        Matrix solution = system.getSolutionMatrix();
        System.out.println( "solution = " + solution );
        solution.print( 3, 3 );
        MNACircuit.MNASolution mnaSolution = system.getSolution();
        double voltageAtNode0 = mnaSolution.getVoltage( 0 );
        System.out.println( "voltageAtNode0 = " + voltageAtNode0 );
        for( int i = 0; i < mnaSolution.getNumVoltages(); i++ ) {
            System.out.println( "mnaSolution.getVoltage(" + i + ") = " + mnaSolution.getVoltage( i ) );
        }
        for( int i = 0; i < mnaSolution.getNumCurrents(); i++ ) {
            System.out.println( "mnaSolution.getCurrent( " + i + ") = " + mnaSolution.getCurrent( i ) );
        }
    }
}
