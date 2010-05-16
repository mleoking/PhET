package edu.colorado.phet.capacitorlab.control;

import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit.BatteryCapacitorCircuitChangeAdapter;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolox.pswing.PSwing;


public class ChargeControlNode extends PhetPNode {

    private final BatteryCapacitorCircuit circuit;
    
    public ChargeControlNode( BatteryCapacitorCircuit circuit ) {
        this.circuit = circuit;
        circuit.addBatteryCapacitorCircuitChangeListener( new BatteryCapacitorCircuitChangeAdapter() {
            //XXX
        });
        
        double min = 0;
        double max = BatteryCapacitorCircuit.getMaxPlateCharge(); 
        LinearValueControl lvc = new LinearValueControl(min, max, "Q (top)", "0.000E00", "C" ); //XXX i18n
        
        addChild( new PSwing( lvc ) );
    }
}
