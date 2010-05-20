/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import java.awt.Color;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit.BatteryCapacitorCircuitChangeAdapter;
import edu.umd.cs.piccolo.PNode;

/**
 * Meter that displays charge on the capacitor plates. 
 * Origin is at the upper-left corner of the "track" that the bar moves in.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PlateChargeMeterNode extends BarMeterNode {

    private static final Color POSITIVE_BAR_COLOR = CLPaints.POSITIVE_CHARGE;
    private static final Color NEGATIVE_BAR_COLOR = CLPaints.NEGATIVE_CHARGE;
    private static final String TITLE = CLStrings.METER_PLATE_CHARGE;
    private static final String VALUE_MANTISSA_PATTERN = "0.000";
    private static final int VALUE_EXPONENT = CLConstants.PLATE_CHARGE_METER_MAX_EXPONENT;
    private static final String UNITS = CLStrings.UNITS_COULOMBS;
    
    private final BatteryCapacitorCircuit circuit;
    
    public PlateChargeMeterNode( BatteryCapacitorCircuit circuit, PNode dragBoundsNode ) {
        super( dragBoundsNode, POSITIVE_BAR_COLOR, TITLE, VALUE_MANTISSA_PATTERN, VALUE_EXPONENT, UNITS ); 
        
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
        double value = circuit.getTotalPlateCharge();
        setValue( Math.abs( value ) );
        setBarColor( ( value < 0 ) ? NEGATIVE_BAR_COLOR : POSITIVE_BAR_COLOR );
    }
}
