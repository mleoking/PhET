/*  */
package edu.colorado.phet.circuitconstructionkit.model.mna.tests;

import edu.colorado.phet.circuitconstructionkit.model.mna.MNACircuit;

/**
 * User: Sam Reid
 * Date: Jun 18, 2006
 * Time: 1:23:55 PM
 */

public class TestMNACircuit_1_4_6 {
    public static void main( String[] args ) {
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
        System.out.println( "circuit = " + circuit );
        MNACircuit.MNASystem system = circuit.getMNASystem();
        System.out.println( "system = " + system );
    }
}
