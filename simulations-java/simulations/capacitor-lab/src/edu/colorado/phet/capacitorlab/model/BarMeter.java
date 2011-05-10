// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model;

import edu.colorado.phet.capacitorlab.model.ICircuit.CircuitChangeListener;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;

/**
 * Base class for all bar meter model elements.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class BarMeter {

    // Convenience class for capacitance meter
    public static class CapacitanceMeter extends BarMeter {
        public CapacitanceMeter( ICircuit circuit, WorldBounds worldBounds, Point3D location, boolean visible ) {
            super( circuit, worldBounds, location, visible, new Function1<ICircuit, Double>() {
                public Double apply( ICircuit circuit ) {
                    return circuit.getTotalCapacitance();
                }
            } );
        }
    }

    // Convenience class for plate charge meter
    public static class PlateChargeMeter extends BarMeter {
        public PlateChargeMeter( ICircuit circuit, WorldBounds worldBounds, Point3D location, boolean visible ) {
            super( circuit, worldBounds, location, visible, new Function1<ICircuit, Double>() {
                public Double apply( ICircuit circuit ) {
                    return circuit.getTotalCharge();
                }
            } );
        }
    }

    // Convenience class for stored energy meter
    public static class StoredEnergyMeter extends BarMeter {
        public StoredEnergyMeter( ICircuit circuit, WorldBounds worldBounds, Point3D location, boolean visible ) {
            super( circuit, worldBounds, location, visible, new Function1<ICircuit, Double>() {
                public Double apply( ICircuit circuit ) {
                    return circuit.getStoredEnergy();
                }
            } );
        }
    }

    // public observable properties
    public final WorldLocationProperty locationProperty;
    public final Property<Boolean> visibleProperty;

    // private observable properties
    private final Property<Double> valueProperty;

    // immutable fields
    private final Function1<ICircuit, Double> valueFunction;

    // mutable fields
    private ICircuit circuit;

    public BarMeter( ICircuit circuit, WorldBounds worldBounds, Point3D location, boolean visible, final Function1<ICircuit, Double> valueFunction ) {

        this.circuit = circuit;
        this.locationProperty = new WorldLocationProperty( worldBounds, location );
        this.visibleProperty = new Property<Boolean>( visible );
        this.valueProperty = new Property<Double>( valueFunction.apply( circuit ) );
        this.valueFunction = valueFunction;

        // change the value when the circuit changes
        circuit.addCircuitChangeListener( new CircuitChangeListener() {
            public void circuitChanged() {
                updateValue();
            }
        } );
    }

    public void reset() {
        locationProperty.reset();
        visibleProperty.reset();
        valueProperty.reset();
    }

    public void setCircuit( ICircuit circuit ) {
        if ( circuit != this.circuit ) {
            this.circuit = circuit;
            updateValue();
        }
    }

    public void addValueObserver( SimpleObserver o ) {
        valueProperty.addObserver( o );
    }

    public double getValue() {
        return valueProperty.get();
    }

    private void updateValue() {
        valueProperty.set( valueFunction.apply( circuit ) );
    }
}
