// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.circuitconstructionkit.model.mna;

import java.util.Arrays;

import edu.colorado.phet.circuitconstructionkit.model.Circuit;
import edu.colorado.phet.circuitconstructionkit.model.CompositeCircuitChangeListener;
import edu.colorado.phet.circuitconstructionkit.model.Junction;
import edu.colorado.phet.circuitconstructionkit.model.components.Battery;
import edu.colorado.phet.circuitconstructionkit.model.components.Capacitor;
import edu.colorado.phet.circuitconstructionkit.model.components.Switch;
import edu.colorado.phet.circuitconstructionkit.model.components.Wire;
import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;

public class TestScripts {
    public static class TestMacroCircuit {
        public static void main( String[] args ) {
            Junction[] j = new Junction[] { new Junction( 0, 0 ), new Junction( 1, 1 ), new Junction( 2, 2 ), new Junction( 3, 3 ), new Junction( 4, 4 ) };
            Circuit circuit = new Circuit();

            Battery battery = new Battery( 9.0, 1E-9 );
            battery.setStartJunction( j[0] );
            battery.setEndJunction( j[1] );

            Capacitor capacitor = new Capacitor( 1E-6 );
            capacitor.setCapacitance( 1.0 );
            capacitor.setStartJunction( j[1] );
            capacitor.setEndJunction( j[2] );

            Switch myswitch = new Switch( j[2].getPosition(), new MutableVector2D( 0, 1 ), 1, 1, new CompositeCircuitChangeListener() );
            myswitch.setStartJunction( j[2] );
            myswitch.setEndJunction( j[3] );

            Wire wire = new Wire( new CompositeCircuitChangeListener(), j[3], j[4] );

            circuit.addBranch( battery );
            circuit.addBranch( capacitor );
            circuit.addBranch( myswitch );
            circuit.addBranch( wire );

            new MNAAdapter().apply( circuit );
        }
    }

    public static class TestMNACircuit {
        public static void main( String[] args ) {
//Debugging circuit: Circuit{
// batteries=
// [Battery{[1->5], v=1.851759060670426E-10}, Battery{[0->6], v=9.0}],
// resistors=
// [Resistor{[2->4], r=1.0E11}, Resistor{[4->3], r=1.840352680379584E-8},
// Resistor{[5->2], r=0.1499999999999968},Resistor{[6->1], r=1.0E-4}]

            LinearCircuitSolver.Circuit circuit = new ObjectOrientedMNA.OOCircuit( Arrays.asList( new LinearCircuitSolver.Battery( 1, 5, 1.851759060670426E-10 ), new LinearCircuitSolver.Battery( 0, 6, 9.0 ) ),
                                                                                   Arrays.asList( new LinearCircuitSolver.Resistor( 2, 4, 1E8 ), new LinearCircuitSolver.Resistor( 4, 3, 1.840352680379584E-8 ),
                                                                                                  new LinearCircuitSolver.Resistor( 5, 2, 0.1499999999999968 ), new LinearCircuitSolver.Resistor( 6, 1, 1E-4 ) ) );
            LinearCircuitSolver.ISolution solution = new ObjectOrientedMNA().solve( circuit );
            System.out.println( "solution = " + solution );
        }
    }
}