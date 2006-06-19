/** Sam Reid*/
package edu.colorado.phet.cck3.tests.mna;

import edu.colorado.phet.cck3.circuit.Circuit;
import edu.colorado.phet.cck3.circuit.Junction;
import edu.colorado.phet.cck3.circuit.analysis.ModifiedNodalAnalysis_Orig;
import edu.colorado.phet.cck3.circuit.components.Battery;
import edu.colorado.phet.cck3.circuit.components.Resistor;

/**
 * User: Sam Reid
 * Date: Oct 1, 2004
 * Time: 12:03:21 AM
 * Copyright (c) Oct 1, 2004 by Sam Reid
 */
public class TestNodeAnalysis extends NodeAnalysisTest {

    public static void main( String[] args ) {
        new TestNodeAnalysis().start();
    }

    private void start() {
        Resistor r1 = newResistor( 2 );
        Resistor r2 = newResistor( 4 );
        Resistor r3 = newResistor( 8 );
        Battery v1 = newBattery( 32 );
        Battery v2 = newBattery( 20 );
        Circuit circuit = new Circuit( kl );
        circuit.addBranch( r1 );
        circuit.addBranch( r2 );
        circuit.addBranch( r3 );
        circuit.addBranch( v1 );
        circuit.addBranch( v2 );
        Junction topleft = combine( circuit, r1.getStartJunction(), v1.getEndJunction() );
        Junction topright = combine( circuit, r2.getEndJunction(), v2.getStartJunction() );
        Junction topCenter1 = combine( circuit, r3.getStartJunction(), v1.getStartJunction() );
        Junction topCenter = combine( circuit, topCenter1, r2.getStartJunction() );
        Junction bottomCenter1 = combine( circuit, r1.getEndJunction(), r3.getEndJunction() );
        Junction bottomCenter = combine( circuit, bottomCenter1, v2.getEndJunction() );
        while( circuit.numJunctions() > 0 ) {
            circuit.remove( circuit.junctionAt( 0 ) );
        }
        circuit.addJunction( bottomCenter );
        circuit.addJunction( topleft );
        circuit.addJunction( topCenter );
        circuit.addJunction( topright );

        new ModifiedNodalAnalysis_Orig().apply( circuit );
        System.out.println( "r1.getCurrent() = " + r1.getCurrent() );
    }
}
