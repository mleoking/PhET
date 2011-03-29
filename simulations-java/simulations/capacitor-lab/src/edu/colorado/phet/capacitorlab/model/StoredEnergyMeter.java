// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model;

import edu.colorado.phet.common.phetcommon.math.Point3D;

/**
 * Model element for the Stored Energy meter.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class StoredEnergyMeter extends BarMeter {

    public StoredEnergyMeter( final BatteryCapacitorCircuit circuit, World world, Point3D location, boolean visible ) {
        super( circuit, world, location, visible );
    }
    
    @Override
    protected double getCircuitValue() {
        return getCircuit().getStoredEnergy();
    }
}
