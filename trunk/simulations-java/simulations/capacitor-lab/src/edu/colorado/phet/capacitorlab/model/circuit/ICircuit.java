// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model.circuit;

import java.awt.*;
import java.util.ArrayList;
import java.util.EventListener;

import edu.colorado.phet.capacitorlab.model.Battery;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.wire.Wire;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Interface implemented by all circuits.
 * Units are meters, Farad, Coulombs, Volts and Joules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface ICircuit {

    // Circuits have a single battery.
    Battery getBattery();

    // The battery may or may not be connected to the circuit.
    boolean isBatteryConnected();

    /*
     * Gets an ordered list of capacitors in the circuit.
     * The capacitors are ordered starting at the battery's top terminal, and proceeding clockwise.
     */
    ArrayList<Capacitor> getCapacitors();

    /*
     * Gets an ordered list of wires in the circuit.
     * The wires are ordered starting at the battery's top terminal, and proceeding clockwise.
     */
    ArrayList<Wire> getWires();

    // Gets the localized name of the circuit that is visible to the user.
    String getDisplayName();

    // Gets the total capacitance of the circuit. (design doc symbol: C_total)
    double getTotalCapacitance();

    // Gets the total charge in the circuit.(design doc symbol: Q_total)
    double getTotalCharge();

    // Gets the energy stored in the circuit. (design doc symbol: U)
    double getStoredEnergy();

    // Gets the total voltage seen by the capacitors.
    double getTotalVoltage();

    /*
     * Gets the voltage between 2 Shapes. The shapes are in world coordinates.
     * Returns Double.NaN if the 2 Shape are not both connected to the circuit
     */
    double getVoltageBetween( Shape positiveShape, Shape negativeShape );

    /*
     * Gets the voltage at a shape, with respect to ground.
     * Returns Double.NaN if the Shape is not connected to the circuit
     */
    double getVoltageAt( Shape shape );

    /*
     * Gets the effective E-field at a specified location.
     * Inside the plates, this is E_effective.
     * Outside the plates, it is zero.
     */
    double getEffectiveEFieldAt( Point3D location );

    /*
     * Field due to the plate, at a specific location.
     * Between the plates, the field is either E_plate_dielectric or E_plate_air, depending on whether the probe intersects the dielectric.
     * Outside the plates, the field is zero.
     */
    double getPlatesDielectricEFieldAt( Point3D location );

    /*
     * Gets the field due to dielectric polarization, at a specific location.
     * Between the plates, the field is either E_dielectric or E_air, depending on whether the probe intersects the dielectric.
     * Outside the plates, the field is zero.
     */
    double getDielectricEFieldAt( Point3D location );

    // Gets the current amplitude, a number that is proportional to dQ/dt, the change in total charge over time.
    double getCurrentAmplitude();

    // Adds an observer of current amplitude, notified when it changes.
    void addCurrentAmplitudeObserver( SimpleObserver o );

    /**
     * Listener for circuit change notifications.
     * Any change to the circuit fires the circuitChanged callback, with
     * no information about what has changed.  This may seem wasteful, but in
     * practice most things need to be changed when anything changes.  So this
     * simplifies the programming at the expense of some unneeded computation.
     * No performance problems have been noted, but this would be a prime place
     * to start optimizing if performance becomes an issue.
     */
    interface CircuitChangeListener extends EventListener {
        public void circuitChanged();
    }

    void addCircuitChangeListener( CircuitChangeListener listener );

    void removeCircuitChangeListener( CircuitChangeListener listener );

    // Resets the circuit to its initial state.
    void reset();
}
