/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

import java.awt.geom.Point2D;
import java.util.EventListener;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.Battery.BatteryChangeAdapter;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeListener;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.Air;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.CustomDielectricMaterial;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.CustomDielectricMaterial.CustomDielectricChangeListener;

/**
 * Physical model of a circuit with a battery connected to a capacitor.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BatteryCapacitorCircuit {
    
    private static final double E0 = CLConstants.E0;
    private static final double Ea = new Air().getDielectricConstant();  // circuit is surrounded with air
    
    private final EventListenerList listeners;
    private final Battery battery;
    private final Capacitor capacitor;
    private boolean batteryConnected;
    private double plateCharge;
    
    private CustomDielectricMaterial customDielectric;
    private CustomDielectricChangeListener customDielectricChangeListener;

    public BatteryCapacitorCircuit( Battery battery, Capacitor capacitor ) {
        
        listeners = new EventListenerList();
        batteryConnected = true;
        
        this.battery = battery;
        battery.addBatteryChangeListener( new BatteryChangeAdapter() {

            @Override
            public void voltageChanged() {
                handleBatteryChanged();
            }
            
            private void handleBatteryChanged() {
                fireVoltageChanged();
                fireChargeChanged();
                fireEfieldChanged();
                fireEnergyChanged();
            }
        });
        
        this.capacitor = capacitor;
        capacitor.addCapacitorChangeListener( new CapacitorChangeListener() {

            public void dielectricMaterialChanged() {
                updateDielectricListener();
                handleCapacitorChanged();
            }

            public void dielectricOffsetChanged() {
                handleCapacitorChanged();
            }

            public void plateSeparationChanged() {
                handleCapacitorChanged();
            }

            public void plateSizeChanged() {
                handleCapacitorChanged();
            }
            
            private void handleCapacitorChanged() {
                fireCapacitanceChanged();
                fireChargeChanged();
                fireEfieldChanged();
                fireEnergyChanged();
            }
            
        });
        
        customDielectricChangeListener = new CustomDielectricChangeListener() {
            public void dielectricConstantChanged() {
                fireCapacitanceChanged();
                fireChargeChanged();
                fireEfieldChanged();
                fireEnergyChanged();
            }
        };
        
        updateDielectricListener();
    }
    
    public Battery getBattery() {
        return battery;
    }
    
    public Capacitor getCapacitor() {
        return capacitor;
    }
    
    public void setBatteryConnected( boolean connected ) {
        if ( connected != this.batteryConnected ) {
            this.batteryConnected = connected;
            fireBatteryConnectedChanged();
        }
    }
    
    public boolean isBatteryConnected() {
        return batteryConnected;
    }

    /**
     * Gets the voltage.
     * If the battery is connected, then the battery voltage is used.
     * If the battery is not connected, then the plate charge is set directly
     * by the user, and voltage is computed.
     * 
     * @return voltage, in volts (V)
     */
    public double getVoltage() {
        double voltage = 0;
        if ( isBatteryConnected() ) {
            voltage = battery.getVoltage();
        }
        else {
            voltage = getPlateCharge() / getCapacitance();
        }
        return voltage;
    }
    
    /**
     * Gets the capacitance.
     * Takes into account how much of the dielectric is between the plates.
     * @return capacitance, in Farads (F)
     */
    public double getCapacitance() {
        double Er = capacitor.getDielectricMaterial().getDielectricConstant(); // dimensionless
        double A = capacitor.getPlateArea(); // meters^2
        double Ad = capacitor.getDielectricContactArea(); // meters^2
        double d = capacitor.getPlateSeparation(); // meters
        double C = ( ( Ad * Er / A ) + ( ( A - Ad ) * Ea / A ) ) * ( E0 * A / d ); // Farads
        return C;
    }
    
    /**
     * Gets the plate charge.
     * If the battery is connected, then charge is computed.
     * If the battery is not connected, then charge is set directly by the user.
     * 
     * @return charge, in Coulombs (C)
     */
    public double getPlateCharge() {
        double Q = 0;
        if ( isBatteryConnected() ) {
            double C = getCapacitance(); // Farads
            double V = getVoltage(); // volts
            Q = C * V; // Coulombs (1C = 1F * 1V)
        }
        else {
            Q = plateCharge;
        }
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
        double Er = capacitor.getDielectricMaterial().getDielectricConstant();
        double A = capacitor.getDielectricContactArea();
        double d = capacitor.getPlateSeparation(); // meters
        double V = getVoltage(); // volts
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
    
    /**
     * Gets the surface density charge on the plates.
     * @return Coulombs/meters^2
     */
    public double getSurfaceDensityCharge() {
        double Q = getPlateCharge(); // Coulombs
        double A = capacitor.getPlateArea(); // meters^2
        double sigma = Q / A; // Colulombs/meters^2
        return sigma;
    }
    
    /**
     * Gets the energy stored in the capacitor.
     * @return energy, in joules (J)
     */
    public double getStoredEnergy() {
        double C = getCapacitance(); // F
        double V = getVoltage(); // V
        double U = 0.5 * C * V * V; // Joules (J)
        return U;
    }
    
    public static double getMaxPlateCharge() {
        DielectricMaterial material = new CustomDielectricMaterial( CLConstants.DIELECTRIC_CONSTANT_RANGE.getMax() );
        Capacitor capacitor = new Capacitor( new Point2D.Double(), 
                CLConstants.PLATE_SIZE_RANGE.getMax(), CLConstants.PLATE_SEPARATION_RANGE.getMin(), material, 0 /* dielectricOffset */ );
        Battery battery = new Battery( new Point2D.Double(), CLConstants.BATTERY_VOLTAGE_RANGE.getMax(), true );
        BatteryCapacitorCircuit circuit = new BatteryCapacitorCircuit( battery, capacitor );
        return circuit.getPlateCharge();
    }
    
    private void updateDielectricListener() {
        if ( customDielectric != null ) {
            customDielectric.removeCustomDielectricChangeListener( customDielectricChangeListener );
            customDielectric = null;
        }
        DielectricMaterial material = capacitor.getDielectricMaterial();
        if ( material instanceof CustomDielectricMaterial ) {
            customDielectric = (CustomDielectricMaterial) material;
            customDielectric.addCustomDielectricChangeListener( customDielectricChangeListener );
        }
    }
    
    public interface BatteryCapacitorCircuitChangeListener extends EventListener {
        public void voltageChanged();
        public void capacitanceChanged();
        public void chargeChanged();
        public void efieldChanged();
        public void energyChanged();
        public void batteryConnectedChanged();
    }
    
    public static class BatteryCapacitorCircuitChangeAdapter implements BatteryCapacitorCircuitChangeListener {
        public void voltageChanged() {}
        public void capacitanceChanged() {}
        public void chargeChanged() {}
        public void efieldChanged() {}
        public void energyChanged() {}
        public void batteryConnectedChanged() {}
    }
    
    public void addBatteryCapacitorCircuitChangeListener( BatteryCapacitorCircuitChangeListener listener ) {
        listeners.add(  BatteryCapacitorCircuitChangeListener.class, listener );
    }
    
    public void removeBatteryCapacitorCircuitChangeListener( BatteryCapacitorCircuitChangeListener listener ) {
        listeners.remove(  BatteryCapacitorCircuitChangeListener.class, listener );
    }
    
    public void fireVoltageChanged() {
        for ( BatteryCapacitorCircuitChangeListener listener : listeners.getListeners( BatteryCapacitorCircuitChangeListener.class ) ) {
            listener.voltageChanged();
        }
    }
    
    public void fireCapacitanceChanged() {
        for ( BatteryCapacitorCircuitChangeListener listener : listeners.getListeners( BatteryCapacitorCircuitChangeListener.class ) ) {
            listener.capacitanceChanged();
        }
    }
    
    public void fireChargeChanged() {
        for ( BatteryCapacitorCircuitChangeListener listener : listeners.getListeners( BatteryCapacitorCircuitChangeListener.class ) ) {
            listener.chargeChanged();
        }
    }
    
    public void fireEfieldChanged() {
        for ( BatteryCapacitorCircuitChangeListener listener : listeners.getListeners( BatteryCapacitorCircuitChangeListener.class ) ) {
            listener.efieldChanged();
        }
    }
    
    public void fireEnergyChanged() {
        for ( BatteryCapacitorCircuitChangeListener listener : listeners.getListeners( BatteryCapacitorCircuitChangeListener.class ) ) {
            listener.energyChanged();
        }
    }
    
    private void fireBatteryConnectedChanged() {
        for ( BatteryCapacitorCircuitChangeListener listener : listeners.getListeners( BatteryCapacitorCircuitChangeListener.class ) ) {
            listener.batteryConnectedChanged();
        }
    }
}
