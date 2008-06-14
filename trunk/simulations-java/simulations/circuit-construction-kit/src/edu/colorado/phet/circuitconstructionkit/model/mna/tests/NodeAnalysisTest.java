/*  */
package edu.colorado.phet.circuitconstructionkit.model.mna.tests;

import java.awt.geom.Point2D;

import edu.colorado.phet.circuitconstructionkit.model.Circuit;
import edu.colorado.phet.circuitconstructionkit.model.CircuitChangeListener;
import edu.colorado.phet.circuitconstructionkit.model.Junction;
import edu.colorado.phet.circuitconstructionkit.model.components.*;
import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * User: Sam Reid
 * Date: Jun 11, 2006
 * Time: 8:44:14 PM
 */

public class NodeAnalysisTest {

    public Resistor newResistor( double r ) {
        Resistor rx = new Resistor( new Point2D.Double(), new Vector2D.Double(), 1, 1, kl );
        rx.setResistance( r );
        return rx;
    }

    static CircuitChangeListener kl = new CircuitChangeListener() {
        public void circuitChanged() {
        }
    };

    public Junction combine( Circuit circuit, Junction a, Junction b ) {
        Junction replacement = new Junction( 0, 0 );
        for ( int i = 0; i < circuit.numBranches(); i++ ) {
            Branch brl = circuit.branchAt( i );
            if ( brl.getStartJunction() == a || brl.getStartJunction() == b ) {
                brl.setStartJunction( replacement );
            }
            if ( brl.getEndJunction() == a || brl.getEndJunction() == b ) {
                brl.setEndJunction( replacement );
            }
        }
        circuit.removeJunction( a );
        circuit.removeJunction( b );

        circuit.addJunction( replacement );
        return replacement;
    }

    public Battery newBattery( double volts ) {
        Battery b = new Battery( new Point2D.Double(), new Vector2D.Double(), 1, 1, kl, false );
        b.setVoltageDrop( volts );
        return b;
    }


    public Capacitor newCapacitor( double cap ) {
        Capacitor b = new Capacitor( new Point2D.Double(), new Vector2D.Double(), 1, 1, kl );
        b.setCapacitance( cap );
        return b;
    }

    public Inductor newInductor( double inductance ) {
        Inductor inductor = new Inductor( new Point2D.Double(), new Vector2D.Double(), 1, 1, kl );
        inductor.setInductance( inductance );
        return inductor;
    }


}
