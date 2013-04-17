// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model.circuit;

import java.awt.*;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.CircuitConfig;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Model of a circuit with a battery (B) connected to a single capacitor (C1).
 * This is treated as a special case of a parallel circuit, with some added features
 * that are specific to "Dielectric" module.
 * </p>
 * <code>
 * |-----|
 * |     |
 * B    C1
 * |     |
 * |-----|
 * </code>
 * </p>
 * Unlike other circuits in this simulation, the battery can be disconnected.
 * When the battery is disconnected, plate charge can be controlled directly.
 * </p>
 * This circuit is used in all 3 modules.  In version 1.00, it was the sole circuit
 * in the simulation. It was heavily refactored to support the Multiple Capacitors
 * module introduced in version 2.00.
 * </p>
 * Variable names used in this implementation where chosen to match the specification
 * in the design document, and therefore violate Java naming conventions.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SingleCircuit extends ParallelCircuit {

    private final Capacitor capacitor; // convenience field, since 1.00 only had one capacitor
    private final Property<Boolean> batteryConnectedProperty; // is the battery connected to the circuit?
    private double disconnectedPlateCharge; // charge set manually by the user, used when battery is disconnected

    public SingleCircuit( CircuitConfig config ) {
        this( config, true /* batteryConnected */ );
    }

    public SingleCircuit( final CircuitConfig config, boolean batteryConnected ) {
        super( config, CLStrings.SINGLE, 1 /* numberOfCapacitors */ );

        this.capacitor = getCapacitors().get( 0 );
        this.batteryConnectedProperty = new Property<Boolean>( batteryConnected );
        this.disconnectedPlateCharge = getTotalCharge();

        batteryConnectedProperty.addObserver( new SimpleObserver() {
            public void update() {
                updatePlateVoltages();
                fireCircuitChanged();
            }
        }, false /* notifyOnAdd */ );

        updatePlateVoltages(); // Must call this at end of constructor!
    }

    @Override public void reset() {
        super.reset();
        batteryConnectedProperty.reset();
    }

    public Capacitor getCapacitor() {
        return capacitor;
    }

    /**
     * Determines whether the battery is connected to the capacitor.
     * When the battery is not connected, the plate charge control becomes active.
     *
     * @param batteryConnected
     */
    public void setBatteryConnected( boolean batteryConnected ) {
        if ( batteryConnected != isBatteryConnected() ) {
            /*
             * When disconnecting the battery, set the disconnected plate charge to
             * whatever the total plate charge was with the battery connected.
             * Need to do this before changing the property value.
             */
            if ( !batteryConnected ) {
                disconnectedPlateCharge = getTotalCharge();
            }
            batteryConnectedProperty.set( batteryConnected );
        }
    }

    public boolean isBatteryConnected() {
        return batteryConnectedProperty.get();
    }

    /*
     * Updates the plate voltage, depending on whether the battery is connected.
     * Null check required because superclass calls this method from its constructor.
     * Remember to call this method at the end of this class' constructor.
     */
    @Override protected void updatePlateVoltages() {
        if ( batteryConnectedProperty != null ) {
            double V = getBattery().getVoltage();
            if ( !isBatteryConnected() ) {
                V = disconnectedPlateCharge / capacitor.getTotalCapacitance(); // V = Q/C
            }
            capacitor.setPlatesVoltage( V );
        }
    }

    /*
     * Normally the total voltage is equivalent to the battery voltage.
     * But disconnecting the battery changes how we compute total voltage, so override this method.
     */
    @Override public double getTotalVoltage() {
        if ( isBatteryConnected() ) {
            return super.getTotalVoltage();
        }
        else {
            return capacitor.getPlatesVoltage();
        }
    }

    // @see ICircuit.getVoltageAt
    public double getVoltageAt( Shape shape ) {
        double voltage = Double.NaN;
        if ( isBatteryConnected() ) {
            voltage = super.getVoltageAt( shape );
        }
        else {
            if ( intersectsSomeTopPlate( shape ) ) {
                voltage = getTotalVoltage();
            }
            else if ( intersectsSomeBottomPlate( shape ) ) {
                voltage = 0;
            }
        }
        return voltage;
    }

    /**
     * Sets the value used for plate charge when the battery is disconnected.
     * (design doc symbol: Q_total)
     *
     * @param disconnectedPlateCharge Coulombs
     */
    public void setDisconnectedPlateCharge( double disconnectedPlateCharge ) {
        if ( disconnectedPlateCharge != this.disconnectedPlateCharge ) {
            this.disconnectedPlateCharge = disconnectedPlateCharge;
            if ( !isBatteryConnected() ) {
                updatePlateVoltages();
                fireCircuitChanged();
            }
        }
    }

    /**
     * Gets the value used for plate charge when the battery is disconnected.
     * (design doc symbol: Q_total)
     *
     * @return charge, in Coulombs
     */
    public double getDisconnectedPlateCharge() {
        return disconnectedPlateCharge;
    }

    // @see ICircuit.getTotalCharge
    @Override public double getTotalCharge() {
        return capacitor.getTotalPlateCharge();
    }
}
