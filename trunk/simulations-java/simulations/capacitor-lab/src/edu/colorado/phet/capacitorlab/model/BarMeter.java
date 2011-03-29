// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model;

import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit.BatteryCapacitorCircuitChangeListener;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Base class for all bar meter model elements.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class BarMeter {

    private final BatteryCapacitorCircuit circuit;

    // directly observable properties
    public final WorldLocationProperty location;
    public final Property<Boolean> visible;
    public final Property<Double> value;

    public BarMeter( final BatteryCapacitorCircuit circuit, final World world, Point3D location, boolean visible ) {

        this.circuit = circuit;
        this.location = new WorldLocationProperty( world, location );
        this.visible = new Property<Boolean>( visible );
        this.value = new Property<Double>( getCircuitValue() );

        // change the value when the circuit changes
        circuit.addBatteryCapacitorCircuitChangeListener( new BatteryCapacitorCircuitChangeListener() {
            public void circuitChanged() {
                value.setValue( getCircuitValue() );
            }
        } );
    }

    /**
     * Subclasses implement this to get the value being measured from the circuit.
     * @return
     */
    protected abstract double getCircuitValue();

    protected BatteryCapacitorCircuit getCircuit() {
        return circuit;
    }

    public void reset() {
        location.reset();
        visible.reset();
        value.reset();
    }
}
