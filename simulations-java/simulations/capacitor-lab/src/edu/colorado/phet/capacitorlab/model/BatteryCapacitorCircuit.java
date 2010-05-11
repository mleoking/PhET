/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

import edu.colorado.phet.capacitorlab.model.DielectricMaterial.Air;

/**
 * Physical model of a circuit with a battery connected to a capacitor.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BatteryCapacitorCircuit {
    
    private static final double E0 = 8.854E-12; // vacuum permittivity, aka electric constant (Farads/meter)
    private static final Air AIR = new Air();  // circuit is assumed to be surrounded with air
    
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
        double Ea = AIR.getDielectricConstant(); // dimensionless
        double A = capacitor.getPlateArea(); // meters^2
        double Ad = capacitor.getDielectricContactArea(); // meters^2
        double d = capacitor.getPlateSeparation(); // meters
        double C = ( ( Ad * Er / A ) + ( ( A - Ad ) * Ea / A ) ) * ( E0 * A / d ); // Farads
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
     * Gets the excess plate charge.
     * To handle the case where the dielectric is offset, the capacitor is treated
     * as 2 capacitors in series, one that uses the actual dielectric material, 
     * and one that has the atmosphere as its dielectric.
     * 
     * @return charge, in Coulombs (C)
     */
    public double getExcessPlateCharge() {
        
        // excess charge on the portion of the plate that contacts the dielectric
        double Er1 = capacitor.getDielectricMaterial().getDielectricConstant();
        double A1 = capacitor.getDielectricContactArea();
        double Q1 = getExcessPlateCharge( Er1, A1 );
        
        // excess charge on the portion of the plate that contacts air
        double Er2 = AIR.getDielectricConstant();
        double A2 = capacitor.getPlateArea() - A1;
        double Q2 = getExcessPlateCharge( Er2, A2 );
        
        // in parallel, so add them
        double Qexcess = Q1 + Q2;
        return Qexcess;
    }
    
    /*
     * @param Er dielectric constant of the dielectric, dimensionless
     * @param A plate area, meters^2
     */
    private double getExcessPlateCharge( double Er, double A ) {
        double Ea = AIR.getDielectricConstant();
        double d = capacitor.getPlateSeparation(); // meters
        double V = battery.getVoltage(); // volts
        double Qexcess = ( Er - Ea ) * E0 * ( A / d ) * V; // Coulombs (1C = 1F * 1V)
        return Qexcess;
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
