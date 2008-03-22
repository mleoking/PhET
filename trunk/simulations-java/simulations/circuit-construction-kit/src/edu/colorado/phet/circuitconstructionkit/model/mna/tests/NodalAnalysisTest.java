/*  */
package edu.colorado.phet.circuitconstructionkit.model.mna.tests;

import edu.colorado.phet.circuitconstructionkit.model.mna.MNACircuit;

/**
 * User: Sam Reid
 * Date: Jun 18, 2006
 * Time: 4:16:22 PM
 */

public class NodalAnalysisTest {
    public static void main( String[] args ) {
        String[] netlist = new String[]{
                "I1 0 1 10",
                "R1 1 0 1"
        };
        MNACircuit circuit = new MNACircuit();
        circuit.parseNetList( netlist );
        MNACircuit.MNASystem system = circuit.getMNASystem();
        System.out.println( "system = " + system );

    }
}
