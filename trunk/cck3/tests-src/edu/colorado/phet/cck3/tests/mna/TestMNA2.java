/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck3.tests.mna;

import edu.colorado.phet.cck3.circuit.Circuit;
import edu.colorado.phet.cck3.circuit.Junction;
import edu.colorado.phet.cck3.circuit.components.Battery;
import edu.colorado.phet.cck3.circuit.components.Capacitor;
import edu.colorado.phet.cck3.circuit.components.Resistor;
import edu.colorado.phet.cck3.circuit.kirkhoff.ModifiedNodalAnalysis;

/**
 * User: Sam Reid
 * Date: Jun 16, 2006
 * Time: 1:54:22 AM
 * Copyright (c) Jun 16, 2006 by Sam Reid
 */

public class TestMNA2 extends NodeAnalysisTest {

    public static void main( String[] args ) {
        new TestMNA2().start();
    }

    private void start() {
        double ds = 1;
        for( double s = 1; s < 100; s += ds ) {
            System.out.println( "s = " + s );
            Resistor res = newResistor( 3 );
            Battery bat = newBattery( Math.cos( s ) );
            Capacitor cap = newCapacitor( 7 );
            cap.setTime( s );
            Circuit circuit = new Circuit( kl );
            circuit.addBranch( res );
            circuit.addBranch( bat );
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

            new ModifiedNodalAnalysis().apply( circuit );
            System.out.println( res.getCurrent() );

            return;
        }


    }
}
