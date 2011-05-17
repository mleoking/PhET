// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.EventListener;

import edu.colorado.phet.common.phetcommon.math.Point3D;

/**
 * Interface implemented by all circuits.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface ICircuit {

    Battery getBattery();

    ArrayList<Capacitor> getCapacitors();

    /**
     * Gets the name of the circuit that is visible to the user.
     * This string should be localized.
     *
     * @return
     */
    String getDisplayName();

    /**
     * Gets the total capacitance of the circuit.
     * (design doc symbol: C_total)
     *
     * @return capacitance, in Farads
     */
    double getTotalCapacitance();

    /**
     * Gets the total charge in the circuit.
     * (design doc symbol: Q_total)
     *
     * @return charge, in Coulombs
     */
    double getTotalCharge();

    /**
     * Gets the energy stored in the circuit.
     * (design doc symbol: U)
     *
     * @return energy, in Joules (J)
     */
    double getStoredEnergy();


    double getTotalVoltage();

    /**
     * Gets the voltage between 2 Shapes. The shapes are in world coordinates.
     *
     * @param positiveShape
     * @param negativeShape
     * @return voltage, Double.NaN if the 2 Shape are not both connected to the circuit
     */
    double getVoltageBetween( Shape positiveShape, Shape negativeShape );

    /**
     * Gets the voltage at a shape, with respect to ground.
     *
     * @param shape
     * @return voltage, Double.NaN if the Shape is not connected to the circuit
     */
    double getVoltageAt( Shape shape );

    /**
     * Gets the effective E-field at a specified location.
     * Inside the plates, this is E_effective.
     * Outside the plates, it is zero.
     *
     * @param location
     * @return E-Field, in Volts/meter
     */
    double getEffectiveEFieldAt( Point3D location );

    /**
     * Field due to the plate, at a specific location.
     * Between the plates, the field is either E_plate_dielectric or E_plate_air, depending on whether the probe intersects the dielectric.
     * Outside the plates, the field is zero.
     *
     * @param location
     * @return E-Field, in Volts/meter
     */
    double getPlatesDielectricEFieldAt( Point3D location );

    /**
     * Gets the field due to dielectric polarization, at a specific location.
     * Between the plates, the field is either E_dielectric or E_air, depending on whether the probe intersects the dielectric.
     * Outside the plates, the field is zero.
     *
     * @param location
     * @return E-Field, in Volts/meter
     */
    double getDielectricEFieldAt( Point3D location );

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

    void reset();
}
