// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model;

import java.awt.Shape;
import java.util.EventListener;

import edu.colorado.phet.common.phetcommon.math.Point3D;

/**
 * Interface implemented by all circuits.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface ICircuit {

    /**
     * Gets the total capacitance of the circuit.
     * (design doc symbol: C_total)
     *
     * @return capacitance, in Farads
     */
    public double getTotalCapacitance();

    /**
     * Gets the total charge in the circuit.
     * (design doc symbol: Q_total)
     *
     * @return charge, in Coulombs
     */
    public double getTotalPlateCharge();

    /**
     * Gets the energy stored in the circuit.
     * (design doc symbol: U)
     *
     * @return energy, in Joules (J)
     */
    public double getStoredEnergy();

    /**
     * Gets the voltage between 2 Shapes.
     * @param positiveShape
     * @param negativeShape
     *
     * @return voltage, Double.NaN if the 2 Shape are not both connected to the circuit
     */
    public double getVoltageBetween( Shape positiveShape, Shape negativeShape );

    /**
     * Gets the effective E-field at a specified location.
     * Inside the plates, this is E_effective.
     * Outside the plates, it is zero.
     *
     * @param location
     * @return E-Field, in Volts/meter
     */
    public double getEffectiveEFieldAt( Point3D location );

    /**
     * Field due to the plate, at a specific location.
     * Between the plates, the field is either E_plate_dielectric or E_plate_air, depending on whether the probe intersects the dielectric.
     * Outside the plates, the field is zero.
     *
     * @param location
     * @return E-Field, in Volts/meter
     */
    public double getPlatesDielectricEFieldAt( Point3D location );

    /**
     * Gets the field due to dielectric polarization, at a specific location.
     * Between the plates, the field is either E_dielectric or E_air, depending on whether the probe intersects the dielectric.
     * Outside the plates, the field is zero.
     *
     * @param location
     * @return E-Field, in Volts/meter
     */
    public double getDielectricEFieldAt( Point3D location );

    /**
     * Listener for circuit change notifications.
     * Any change to the circuit fires the circuitChanged callback, with
     * no information about what has changed.  This may seem wasteful, but in
     * practice most things need to be changed when anything changes.  So this
     * simplifies the programming at the expense of some unneeded computation.
     * No performance problems have been noted, but this would be the first place
     * to start optimizing if performance becomes an issue.
     */
    public interface CircuitChangeListener extends EventListener {
        public void circuitChanged();
    }

    public void addCircuitChangeListener( CircuitChangeListener listener );

    public void removeCircuitChangeListener( CircuitChangeListener listener );
}
