/*  */
package edu.colorado.phet.circuitconstructionkit.model.mna.tests;

import edu.colorado.phet.circuitconstructionkit.model.Circuit;
import edu.colorado.phet.circuitconstructionkit.model.Junction;
import edu.colorado.phet.circuitconstructionkit.model.analysis.ModifiedNodalAnalysis_Orig;
import edu.colorado.phet.circuitconstructionkit.model.components.Battery;
import edu.colorado.phet.circuitconstructionkit.model.components.Capacitor;
import edu.colorado.phet.circuitconstructionkit.model.components.Resistor;

/**
 * User: Sam Reid
 * Date: Jun 16, 2006
 * Time: 1:54:22 AM
 */

public class TestDCRCSnapshot extends NodeAnalysisTest {

    public static void main( String[] args ) {
        new TestDCRCSnapshot().start();
    }

    private void start() {
        Resistor res = newResistor( 5 );
        Battery bat = newBattery( 12 );
        Capacitor cap = newCapacitor( 7 );

        Circuit circuit = new Circuit( kl );
        circuit.addBranch( bat );
        circuit.addBranch( res );
        circuit.addBranch( cap );
        Junction j1 = combine( circuit, bat.getEndJunction(), res.getStartJunction() );
        Junction j2 = combine( circuit, res.getEndJunction(), cap.getStartJunction() );
        Junction j3 = combine( circuit, cap.getEndJunction(), bat.getStartJunction() );
        while ( circuit.numJunctions() > 0 ) {
            circuit.removeJunction( circuit.junctionAt( 0 ) );
        }
        circuit.addJunction( j1 );
        circuit.addJunction( j2 );
        circuit.addJunction( j3 );
        circuit.setTime( 6 );
        new ModifiedNodalAnalysis_Orig().applyMNA( circuit );
        System.out.println( "cap.getVoltageDrop() = " + cap.getVoltageDrop() );
        System.out.println( "cap.getCurrent() = " + cap.getCurrent() );

    }
}
