package edu.colorado.phet.circuitconstructionkit.model.mna2;

import edu.colorado.phet.circuitconstructionkit.model.Circuit;
import edu.colorado.phet.circuitconstructionkit.model.CompositeCircuitChangeListener;
import edu.colorado.phet.circuitconstructionkit.model.Junction;
import edu.colorado.phet.circuitconstructionkit.model.components.Battery;
import edu.colorado.phet.circuitconstructionkit.model.components.Capacitor;
import edu.colorado.phet.circuitconstructionkit.model.components.Switch;
import edu.colorado.phet.circuitconstructionkit.model.components.Wire;
import edu.colorado.phet.common.phetcommon.math.Vector2D;

import java.util.Arrays;

public class TestScripts {
    public static class TestMacroCircuit {
        public static void main(String[] args) {
            Junction[] j = new Junction[]{new Junction(0, 0), new Junction(1, 1), new Junction(2, 2), new Junction(3, 3), new Junction(4, 4)};
            Circuit circuit = new Circuit();

            Battery battery = new Battery(9.0, 1E-9);
            battery.setStartJunction(j[0]);
            battery.setEndJunction(j[1]);

            Capacitor capacitor = new Capacitor(1E-6);
            capacitor.setCapacitance(1.0);
            capacitor.setStartJunction(j[1]);
            capacitor.setEndJunction(j[2]);

            Switch myswitch = new Switch(j[2].getPosition(), new Vector2D.Double(0, 1), 1, 1, new CompositeCircuitChangeListener());
            myswitch.setStartJunction(j[2]);
            myswitch.setEndJunction(j[3]);

            Wire wire = new Wire(new CompositeCircuitChangeListener(), j[3], j[4]);

            circuit.addBranch(battery);
            circuit.addBranch(capacitor);
            circuit.addBranch(myswitch);
            circuit.addBranch(wire);

            new PureJavaSolver().apply(circuit);
        }

    }

    public static class TestMNACircuit {
        public static void main(String[] args) {
//Debugging circuit: Circuit{
// batteries=
// [Battery{[1->5], v=1.851759060670426E-10}, Battery{[0->6], v=9.0}],
// resistors=
// [Resistor{[2->4], r=1.0E11}, Resistor{[4->3], r=1.840352680379584E-8},
// Resistor{[5->2], r=0.1499999999999968},Resistor{[6->1], r=1.0E-4}]

            MNA.Circuit circuit = new MNA.Circuit(Arrays.asList(new MNA.Battery(1, 5, 1.851759060670426E-10), new MNA.Battery(0, 6, 9.0)),
                    Arrays.asList(new MNA.Resistor(2, 4, 1E11),new MNA.Resistor(4,3,1.840352680379584E-8),
                            new MNA.Resistor(5,2,0.1499999999999968),new MNA.Resistor(6,1,1E-4)));
            MNA.Solution solution=circuit.solve();
            System.out.println("solution = " + solution);
        }
    }
}
