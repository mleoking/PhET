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

public class TestDCRC extends NodeAnalysisTest {

    public static void main( String[] args ) {
        new TestDCRC().start();
    }

    /**
     * Should decay exponentially.
     */
    private void start() {
        double ds = 0.01;
        for( double s = 1; s < 100; s += ds ) {
            Resistor res = newResistor( 3 );
//            System.out.println( "res.getResistance() = " + res.getResistance() );
            Battery bat = newBattery( 13 );
            Capacitor cap = newCapacitor( 7 );
//            System.out.println( "cap.getResistance() = " + cap.getResistance() );
//            cap.setTime( s );
            Circuit circuit = new Circuit( kl );
            circuit.addBranch( bat );
            circuit.addBranch( res );
            circuit.addBranch( cap );
            Junction j1 = combine( circuit, res.getEndJunction(), bat.getStartJunction() );
            Junction j2 = combine( circuit, bat.getEndJunction(), cap.getStartJunction() );
            Junction j3 = combine( circuit, cap.getEndJunction(), res.getStartJunction() );
            while( circuit.numJunctions() > 0 ) {
                circuit.remove( circuit.junctionAt( 0 ) );
            }
            circuit.addJunction( j1 );
            circuit.addJunction( j2 );
            circuit.addJunction( j3 );

//            new ModifiedNodalAnalysis().applyMNA( circuit );
            new ModifiedNodalAnalysis_Orig().apply( circuit );
            System.out.println( res.getCurrent() );
//            System.out.println( circuit.getVoltage( j1,j2));

//            return;
        }


    }
}
