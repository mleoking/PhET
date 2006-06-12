/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck3.tests.mna;

import edu.colorado.phet.cck3.circuit.Circuit;
import edu.colorado.phet.cck3.circuit.Junction;
import edu.colorado.phet.cck3.circuit.components.Battery;
import edu.colorado.phet.cck3.circuit.components.Resistor;
import edu.colorado.phet.cck3.circuit.kirkhoff.ModifiedNodalAnalysis;

/**
 * User: Sam Reid
 * Date: Oct 1, 2004
 * Time: 12:03:21 AM
 * Copyright (c) Oct 1, 2004 by Sam Reid
 */
public class TestNodeAnalysis2 extends NodeAnalysisTest {

    public static void main( String[] args ) {
        new TestNodeAnalysis2().start();
    }

    private void start() {
        Resistor res = newResistor( 2 );
        Battery bat = newBattery( 20 );
        Circuit circuit = new Circuit( kl );
        circuit.addBranch( res );
        circuit.addBranch( bat );
        Junction j1 = combine( circuit, res.getEndJunction(), bat.getStartJunction() );
        Junction j2 = combine( circuit, bat.getEndJunction(), res.getStartJunction() );
        while( circuit.numJunctions() > 0 ) {
            circuit.remove( circuit.junctionAt( 0 ) );
        }
        circuit.addJunction( j1 );
        circuit.addJunction( j2 );

        new ModifiedNodalAnalysis().apply( circuit );
        System.out.println( "r1.getCurrent() = " + res.getCurrent() );
    }
}
