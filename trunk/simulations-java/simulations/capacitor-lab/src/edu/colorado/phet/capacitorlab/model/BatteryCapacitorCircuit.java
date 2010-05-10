/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

/**
 * Physical model of a circuit with a battery connected to a capacitor.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BatteryCapacitorCircuit {
    
    private static final int MILLIMETERS_PER_METER = 1000;
    private static final double E0 = 8.854E-12 / MILLIMETERS_PER_METER ; // vacuum permittivity, aka electric constant (F/mm)
    private static final double E_AIR = 1.0;
    
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
        // ( Ad * Er ) + E_AIR - Ad
        double Ad = capacitor.getDielectricInsideArea(); // mm
        double Er = capacitor.getDielectricMaterial().getDielectricConstant(); // dimensionless
        double C = ( Ad * Er ) + E_AIR - Ad; // F
        return C;
    }
    
    /**
     * Gets the plate charge.
     * @return charge, in Coulombs (C)
     */
    public double getPlateCharge() {
        double C = getCapacitance(); // F
        double V = battery.getVoltage(); // V
        double Q = C * V; // Coulombs (1C = 1F * 1V)
        return Q;
    }
    
    /**
     * Gets the effective E-field.
     * @return
     */
    public double getEffectiveEfield() {
        double sigma = getSurfaceDensityCharge(); // Coulombs/mm^2
        double Er = capacitor.getDielectricMaterial().getDielectricConstant(); // dimensionless
        double E = sigma / ( Er * E0 ); //XXX units?
        return E; //XXX units?
    }
    
    /**
     * Gets the E-field due to the plates.
     * @return
     */
    public double getPlatesEField() {
        double sigma = getSurfaceDensityCharge(); // Coulombs/mm^2
        double Eplates = sigma / E0; //XXX units?
        return Eplates; //XXX units?
    }
    
    /**
     * Gets the E-field due to the dielectric.
     * @return
     */
    public double getDielectricEField() {
        return getPlatesEField() - getEffectiveEfield(); //XXX units?
    }
    
    /*
     * Gets the surface density charge on the plates.
     * @return Coulombs/mm^2
     */
    private double getSurfaceDensityCharge() {
        double Q = getPlateCharge(); // Coulombs
        double A = capacitor.getPlateArea(); // mm^2
        double sigma = Q / A; // Colulombs/mm^2
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
