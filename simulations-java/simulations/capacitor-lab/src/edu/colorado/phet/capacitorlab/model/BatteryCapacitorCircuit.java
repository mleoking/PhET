/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

import edu.colorado.phet.capacitorlab.CLConstants;

/**
 * Physical model of a circuit with a battery connected to a capacitor.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BatteryCapacitorCircuit {
    
    private static final double E0 = 8.854E-12; // vacuum permittivity, aka electric constant (Farads/meter)
    private static final double E_AIR = CLConstants.AIR_DIELECTRIC_CONSTANT;
    
    private final Battery battery;
    private final Capacitor capacitor;

    public BatteryCapacitorCircuit( Battery battery, Capacitor capacitor ) {
        this.battery = battery;
        this.capacitor = capacitor;
    }
    
    public Battery getBattery() {
        return battery;
    }
    
    public Capacitor getCapacitor() {
        return capacitor;
    }
    
    /**
     * Gets the capacitance.
     * Takes into account how much of the dielectric is between the plates.
     * @return capacitance, in Farads (F)
     */
    public double getCapacitance() {
        double Er = capacitor.getDielectricMaterial().getDielectricConstant(); // dimensionless
        double A = capacitor.getPlateArea(); // meters^2
        double Ad = capacitor.getDielectricInsideArea(); // meters^2
        double d = capacitor.getPlateSeparation(); // meters
        double C = ( ( Ad * Er / A ) + ( ( A - Ad ) * E_AIR / A ) ) * ( E0 * A / d ); // Farads
        return C;
    }
    
    /**
     * Gets the plate charge.
     * @return charge, in Coulombs (C)
     */
    public double getPlateCharge() {
        double C = getCapacitance(); // Farads
        double V = battery.getVoltage(); // volts
        double Q = C * V; // Coulombs (1C = 1F * 1V)
        return Q;
    }
    
    /**
     * Gets the effective E-field.
     * @return volts/meter
     */
    public double getEffectiveEfield() {
        double sigma = getSurfaceDensityCharge(); // Coulombs/meters^2
        double Er = capacitor.getDielectricMaterial().getDielectricConstant(); // dimensionless
        double E = sigma / ( Er * E0 ); // volts/meter
        return E;
    }
    
    /**
     * Gets the E-field due to the plates.
     * @return volts/meter
     */
    public double getPlatesEField() {
        double sigma = getSurfaceDensityCharge(); // Coulombs/meters^2
        double Eplates = sigma / E0; // volts/meter
        return Eplates;
    }
    
    /**
     * Gets the E-field due to the dielectric.
     * @return volts/meter
     */
    public double getDielectricEField() {
        return getPlatesEField() - getEffectiveEfield();
    }
    
    /*
     * Gets the surface density charge on the plates.
     * @return Coulombs/meters^2
     */
    private double getSurfaceDensityCharge() {
        double Q = getPlateCharge(); // Coulombs
        double A = capacitor.getPlateArea(); // meters^2
        double sigma = Q / A; // Colulombs/meters^2
        return sigma;
    }
    
    /**
     * Gets the energy stored in the capacitor.
     * @return energy, in joules (J)
     */
    public double getEnergyStored() {
        double C = getCapacitance(); // F
        double V = battery.getVoltage(); // V
        double U = 0.5 * C * V * V; // Joules (J)
        return U;
    }
}
