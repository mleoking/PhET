/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view.meters;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit.BatteryCapacitorCircuitChangeAdapter;
import edu.umd.cs.piccolo.PNode;

/**
 * Meter that displays capacitance. 
 * Origin is at the upper-left corner of the "track" that the bar moves in.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CapacitanceMeterNode extends BarMeterNode {

    private static final String VALUE_MANTISSA_PATTERN = "0.00";
    private static final int VALUE_EXPONENT = CLConstants.CAPACITANCE_METER_VALUE_EXPONENT;
    private static final String UNITS = CLStrings.FARADS;
    
    private final BatteryCapacitorCircuit circuit;
    
    public CapacitanceMeterNode( BatteryCapacitorCircuit circuit, PNode dragBoundsNode ) {
        super( dragBoundsNode, CLPaints.CAPACITANCE, CLStrings.CAPACITANCE, VALUE_MANTISSA_PATTERN, VALUE_EXPONENT, UNITS, 0 ); 
        
        this.circuit = circuit;
        circuit.addBatteryCapacitorCircuitChangeListener( new  BatteryCapacitorCircuitChangeAdapter() {
            @Override
            public void chargeChanged() {
                update();
            }
        });
        
        update();
    }
    
    private void update() {
        setValue( circuit.getCapacitor().getTotalCapacitance() );
    }
}
