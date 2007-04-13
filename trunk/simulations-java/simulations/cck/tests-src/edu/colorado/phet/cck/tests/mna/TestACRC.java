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

public class TestACRC extends NodeAnalysisTest {

    public static void main( String[] args ) {
        new TestACRC().start();
    }

    /**
     * should oscillate sinusoidally
     */
    private void start() {
        double ds = 0.01;
        for( double s = 1; s < 100; s += ds ) {
//            System.out.println( "s = " + s );
            Resistor res = newResistor( 3 );
//            System.out.println( "res.getResistance() = " + res.getResistance() );
            Battery bat = newBattery( Math.cos( s ) );
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
                circuit.removeJunction( circuit.junctionAt( 0 ) );
            }
            circuit.addJunction( j1 );
            circuit.addJunction( j2 );
            circuit.addJunction( j3 );

            new ModifiedNodalAnalysis_Orig().applyMNA( circuit );
//            new ModifiedNodalAnalysis().apply( circuit );
            System.out.println( res.getCurrent() );

//            return;
        }


    }
}
