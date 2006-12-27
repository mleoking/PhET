/** Sam Reid*/
package edu.colorado.phet.cck3.tests;

import edu.colorado.phet.cck3.circuit.Circuit;
import edu.colorado.phet.cck3.circuit.Junction;
import edu.colorado.phet.cck3.circuit.KirkhoffListener;
import edu.colorado.phet.cck3.circuit.components.Battery;
import edu.colorado.phet.cck3.circuit.components.Resistor;
import edu.colorado.phet.cck3.circuit.kirkhoff.KirkhoffSolver;
import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jun 2, 2004
 * Time: 1:05:19 PM
 * Copyright (c) Jun 2, 2004 by Sam Reid
 */
public class TestKirkhoff {
    public static void main( String[] args ) {
//        test1( );
//        test2();
        test3();
    }

    public static void test1() {
        KirkhoffListener kl = new KirkhoffListener() {
            public void circuitChanged() {
            }
        };
        Circuit c = new Circuit( kl );
        Resistor res = new Resistor( new Point2D.Double(), new Vector2D.Double(), 1, 1, kl );
        res.setResistance( 4 );
        Battery batt = new Battery( new Point2D.Double(), new Vector2D.Double(), 1, 1, kl );
        batt.setVoltageDrop( 8 );
        batt.setResistance( .00001 );
        batt.setEndJunction( res.getStartJunction() );
        batt.setStartJunction( res.getEndJunction() );
        c.addBranch( res );
        c.addBranch( batt );

        KirkhoffSolver ks = new KirkhoffSolver();
        ks.apply( c );
    }

    public static void test2() {
        KirkhoffListener kl = new KirkhoffListener() {
            public void circuitChanged() {
            }
        };
        Circuit c = new Circuit( kl );
        Resistor res = new Resistor( new Point2D.Double(), new Vector2D.Double(), 1, 1, kl );
        res.setResistance( 4 );
        Battery batt = new Battery( new Point2D.Double(), new Vector2D.Double(), 1, 1, kl );
        batt.setVoltageDrop( 8 );
        batt.setResistance( .001 );

        batt.setEndJunction( res.getStartJunction() );
        batt.setStartJunction( res.getEndJunction() );
        c.addBranch( res );
        c.addBranch( batt );

        KirkhoffSolver ks = new KirkhoffSolver();
        ks.apply( c );
    }

    public static void test3() {
        KirkhoffListener kl = new KirkhoffListener() {
            public void circuitChanged() {
            }
        };
        Circuit c = new Circuit( kl );
        Resistor r1 = new Resistor( new Point2D.Double(), new Vector2D.Double(), 1, 1, kl );
        r1.setResistance( 4 );

        Resistor r2 = new Resistor( new Point2D.Double(), new Vector2D.Double(), 1, 1, kl );
        r2.setResistance( 4 );

        Battery batt = new Battery( new Point2D.Double(), new Vector2D.Double(), 1, 1, kl );
        batt.setVoltageDrop( 3 );

//        batt.setResistance( .001 );
        Junction j0 = r1.getStartJunction();
        Junction j1 = r1.getEndJunction();

        batt.setEndJunction( j1 );
        batt.setStartJunction( j0 );
        r2.setStartJunction( j0 );
        r2.setEndJunction( j1 );
        c.addBranch( r1 );
        c.addBranch( batt );
        c.addBranch( r2 );

        KirkhoffSolver ks = new KirkhoffSolver();
        ks.apply( c );
    }
}
