/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck.tests.mna;

import edu.colorado.phet.cck.model.Circuit;
import edu.colorado.phet.cck.model.Junction;
import edu.colorado.phet.cck.model.analysis.ModifiedNodalAnalysis_Orig;
import edu.colorado.phet.cck.model.components.Battery;
import edu.colorado.phet.cck.model.components.Capacitor;
import edu.colorado.phet.cck.model.components.Resistor;

/**
 * User: Sam Reid
 * Date: Jun 16, 2006
 * Time: 1:54:22 AM
 * Copyright (c) Jun 16, 2006 by Sam Reid
 */

public class TestDCRCSnapshotSeq extends NodeAnalysisTest {

    public static void main( String[] args ) {
        new TestDCRCSnapshotSeq().start();
    }

    private void start() {
        for( double s = 1; s < 100; s += 0.01 ) {
            runOnce( s );
        }
    }

    private void runOnce( double s ) {
        Resistor res = newResistor( 5 );
        Battery bat = newBattery( Math.cos( s ) );
        Capacitor cap = newCapacitor( 7 );

        Circuit circuit = new Circuit( kl );
        circuit.addBranch( bat );
        circuit.addBranch( res );
        circuit.addBranch( cap );
        Junction j1 = combine( circuit, bat.getEndJunction(), res.getStartJunction() );
        Junction j2 = combine( circuit, res.getEndJunction(), cap.getStartJunction() );
        Junction j3 = combine( circuit, cap.getEndJunction(), bat.getStartJunction() );
        while( circuit.numJunctions() > 0 ) {
            circuit.removeJunction( circuit.junctionAt( 0 ) );
        }
        circuit.addJunction( j1 );
        circuit.addJunction( j2 );
        circuit.addJunction( j3 );
        circuit.setTime( s );
//        new ModifiedNodalAnalysis().applyMNA( circuit );
        new ModifiedNodalAnalysis_Orig().apply( circuit );
//        System.out.println( "cap.getVoltageDrop() = \t" + cap.getVoltageDrop() );

//        System.out.println( cap.getVoltageDrop() + "\t\t" + cap.getCurrent() );
        System.out.println( cap.getCurrent() );
//        System.out.println( res.getVoltageDrop() );

    }
}
