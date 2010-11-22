/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit.BatteryCapacitorCircuitChangeAdapter;
import edu.colorado.phet.common.phetcommon.math.Point3D;

/**
 * Model element for the Plate Charge meter.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PlateChargeMeter extends BarMeter {

    public PlateChargeMeter( final BatteryCapacitorCircuit circuit, World world, Point3D location, boolean visible ) {
        super( world, location, visible, circuit.getCapacitor().getTotalCapacitance() );
        circuit.addBatteryCapacitorCircuitChangeListener( new BatteryCapacitorCircuitChangeAdapter() {
            @Override
            public void chargeChanged() {
                setValue( Math.abs( circuit.getTotalPlateCharge() ) );
            }
        });
    }
}
