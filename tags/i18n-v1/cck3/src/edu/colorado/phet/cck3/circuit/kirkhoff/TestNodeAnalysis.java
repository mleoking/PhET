/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.kirkhoff;

import edu.colorado.phet.cck3.circuit.Branch;
import edu.colorado.phet.cck3.circuit.Circuit;
import edu.colorado.phet.cck3.circuit.Junction;
import edu.colorado.phet.cck3.circuit.KirkhoffListener;
import edu.colorado.phet.cck3.circuit.components.Battery;
import edu.colorado.phet.cck3.circuit.components.Resistor;
import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Oct 1, 2004
 * Time: 12:03:21 AM
 * Copyright (c) Oct 1, 2004 by Sam Reid
 */
public class TestNodeAnalysis {

    public static Resistor newResistor( double r ) {
        Resistor rx = new Resistor( new Point2D.Double(), new Vector2D.Double(), 1, 1, kl );
        rx.setResistance( r );
        return rx;
    }

    static KirkhoffListener kl = new KirkhoffListener() {
        public void circuitChanged() {
        }
    };

    public static void main( String[] args ) {
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

        new ModifiedNodalAnalysis().apply( circuit );
    }

    private static Junction combine( Circuit circuit, Junction a, Junction b ) {
        Junction replacement = new Junction( 0, 0 );
        for( int i = 0; i < circuit.numBranches(); i++ ) {
            Branch brl = circuit.branchAt( i );
            if( brl.getStartJunction() == a || brl.getStartJunction() == b ) {
                brl.setStartJunction( replacement );
            }
            if( brl.getEndJunction() == a || brl.getEndJunction() == b ) {
                brl.setEndJunction( replacement );
            }
        }
        circuit.remove( a );
        circuit.remove( b );

        circuit.addJunction( replacement );
        return replacement;
    }

    private static Battery newBattery( double volts ) {
        Battery b = new Battery( new Point2D.Double(), new Vector2D.Double(), 1, 1, kl, false );
        b.setVoltageDrop( volts );
        return b;
    }
}
