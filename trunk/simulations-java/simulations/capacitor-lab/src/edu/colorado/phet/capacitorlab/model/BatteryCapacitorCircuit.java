/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

import java.util.EventListener;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.Battery.BatteryChangeAdapter;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeAdapter;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.CustomDielectricMaterial;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.CustomDielectricMaterial.CustomDielectricChangeListener;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;

/**
 * Physical model of a circuit with a battery connected to a capacitor.
 * <p>
 * Variable names used in this implementation where chosen to match the specification
 * in the design document, and therefore violate Java naming conventions. 
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BatteryCapacitorCircuit {
    
    // constants
    private static final double EPSILON_0 = CLConstants.EPSILON_0;
    private static final double EPSILON_AIR = CLConstants.EPSILON_AIR;
    private static final double EPSILON_VACUUM = CLConstants.EPSILON_VACUUM;
    
    // immutable instance data
    private final CLClock clock;
    private final EventListenerList listeners;
    private final Battery battery;
    private final Capacitor capacitor;
    
    // mutable instance data
    private boolean batteryConnected;
    private double disconnectedPlateCharge; // charge set manually by the user, used when battery is disconnected
    private CustomDielectricMaterial customDielectric;
    private CustomDielectricChangeListener customDielectricChangeListener;
    private double currentAmplitude;
    private double previousVoltage;

    public BatteryCapacitorCircuit( CLClock clock, Battery battery, Capacitor capacitor, boolean batteryConnected, double disconnectedPlateCharge ) {
        
        this.clock = clock;
        this.listeners = new EventListenerList();
        this.battery = battery;
        this.capacitor = capacitor;
        this.batteryConnected = batteryConnected;
        this.disconnectedPlateCharge = disconnectedPlateCharge;
        this.currentAmplitude = 0;
        this.previousVoltage = getPlatesVoltage();
        
        // respond to battery changes
        battery.addBatteryChangeListener( new BatteryChangeAdapter() {
            @Override
            public void voltageChanged() {
                handleBatteryVoltageChanged();
            }
        });
        
        // respond to capacitor changes
        capacitor.addCapacitorChangeListener( new CapacitorChangeAdapter() {

            @Override
            public void capacitanceChanged() {
                handleCapacitanceChanged();
            }
            
            @Override
            public void dielectricMaterialChanged() {
                handleDielectricMaterialChanged();
            }
        });
        
        // change listener to be used with custom dielectric materials
        customDielectric = null;
        customDielectricChangeListener = new CustomDielectricChangeListener() {
            public void dielectricConstantChanged() {
                fireCapacitanceChanged();
                fireChargeChanged();
                fireEfieldChanged();
                fireEnergyChanged();
            }
        };
        updateCustomDielectric();
        
        // update current amplitude on each clock tick
        clock.addClockListener( new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                updateCurrentAmplitude();
            }
        });
    }
    
    //----------------------------------------------------------------------------------
    //
    // Circuit components
    //
    //----------------------------------------------------------------------------------
    
    /**
     * Gets the battery that is associated with this circuit.
     * @return
     */
    public Battery getBattery() {
        return battery;
    }
    
    /**
     * Gets the capacitor that is associated with this circuit.
     * @return
     */
    public Capacitor getCapacitor() {
        return capacitor;
    }
    
    //----------------------------------------------------------------------------------
    //
    // Plate Voltage (V)
    //
    //----------------------------------------------------------------------------------

    /**
     * Determines whether the battery is connected to the capacitor.
     * @param connected
     */
    public void setBatteryConnected( boolean connected ) {
        if ( connected != this.batteryConnected ) {
            this.batteryConnected = connected;
            fireBatteryConnectedChanged();
            fireChargeChanged();
            fireEfieldChanged();
            fireEnergyChanged();
        }
    }
    
    /**
     * Is the battery connected to the capacitor?
     * @return
     */
    public boolean isBatteryConnected() {
        return batteryConnected;
    }
    
    /**
     * Gets the plate voltage.
     * If the battery is connected, then the battery voltage is used.
     * If the battery is not connected, then the plate charge is set directly
     * by the user, and voltage is computed.
     * 
     * @return voltage, in volts (V)
     */
    public double getPlatesVoltage() {
        double V_plates = 0;
        if ( isBatteryConnected() ) {
            V_plates = battery.getVoltage();
        }
        else {
            V_plates = getDisconnectedPlateCharge() / capacitor.getTotalCapacitance();
        }
        return V_plates;
    }
    
    //----------------------------------------------------------------------------------
    //
    // Plate Charge (Q)
    //
    //----------------------------------------------------------------------------------
    
    /**
     * Sets the value used for plate charge when the battery is disconnected.
     * 
     * @param manualPlateCharge Coulombs
     */
    public void setDisconnectedPlateCharge( double manualPlateCharge ) {
        if ( manualPlateCharge != this.disconnectedPlateCharge ) {
            this.disconnectedPlateCharge = manualPlateCharge;
            if ( !isBatteryConnected() ) {
                fireChargeChanged();
                fireVoltageChanged();
                fireEfieldChanged();
                fireEnergyChanged();
            }
        }
    }
    
    /**
     * Gets the value used for plate charge when the battery is disconnected.
     * 
     * @return charge, in Coulombs
     */
    public double getDisconnectedPlateCharge() {
        return disconnectedPlateCharge;
    }
    
    /**
     * Gets the charge for the portion of the top plate contacting the air.
     * @return charge, in Coulombs
     */
    public double getAirPlateCharge() {
        return capacitor.getAirCapacitance() * getPlatesVoltage();
    }
    
    /**
     * Gets the charge for the portion of the top plate contacting the dielectric.
     * @return charge, in Coulombs
     */
    public double getDielectricPlateCharge() {
        return capacitor.getDieletricCapacitance() * getPlatesVoltage();
    }
    
    /**
     * Gets the total charge on the top plate.
     * @return charge, in Coulombs
     */
    public double getTotalPlateCharge() {
        return getDielectricPlateCharge() + getAirPlateCharge();
    }
    
    /**
     * Gets the excess plate charge due to plates contacting air.
     * 
     * @return excess charge, in Coulombs
     */
    public double getExcessAirPlateCharge() {
        return getExcessPlateCharge( EPSILON_AIR, capacitor.getAirCapacitance(), getPlatesVoltage() );
    }
    
    /**
     * Gets the excess plate charge due to plates contacting the dielectric.
     * 
     * @return excess charge, in Coulombs
     */
    public double getExcessDielectricPlateCharge() {
        return getExcessPlateCharge( capacitor.getDielectricConstant(), capacitor.getDieletricCapacitance(), getPlatesVoltage() );
    }
    
    /*
     * General solution for excess plate charge.
     * 
     * @param epsilon_r dielectric constant, dimensionless
     * @param C capacitance due to the dielectric
     * @param V_plates plate voltage, volts
     * @return charge, in Coulombs (C)
     */
    private static double getExcessPlateCharge( double epsilon_r, double C, double V_plates ) {
        if ( !( epsilon_r > 0 ) ) {
            throw new IllegalArgumentException( "model requires epsilon_r > 0 : " + epsilon_r );
        }
        return ( ( epsilon_r - EPSILON_VACUUM ) / epsilon_r ) * C * V_plates; // Coulombs (1C = 1F * 1V)
    }
    
    /**
     * Gets the maximum charge on the top plate.
     * Maximum charge occurs when the battery is connected, 
     * plate separate and dielectric offset are at their minimums,
     * and all other quantities are at their maximums.
     * 
     * @return charge, in Coulombs
     */
    public static double getMaxPlateCharge() {
        DielectricMaterial material = new CustomDielectricMaterial( CLConstants.DIELECTRIC_CONSTANT_RANGE.getMax() );
        Capacitor capacitor = new Capacitor( new Point3D.Double(), CLConstants.PLATE_SIZE_RANGE.getMax(), CLConstants.PLATE_SEPARATION_RANGE.getMin(), material, CLConstants.DIELECTRIC_OFFSET_RANGE.getMin() );
        Battery battery = new Battery( new Point3D.Double(), CLConstants.BATTERY_VOLTAGE_RANGE.getMax() );
        BatteryCapacitorCircuit circuit = new BatteryCapacitorCircuit( new CLClock(), battery, capacitor, true /* batteryConnected */, 0 /* manualPlateCharge, don't care */ );
        return circuit.getTotalPlateCharge();
    }
    
    //----------------------------------------------------------------------------------
    //
    // Surface Charge Density (sigma)
    //
    //----------------------------------------------------------------------------------
    
    /**
     * Gets the surface density charge between the plates and air.
     * @return Coulombs/meters^2
     */
    public double getAirSurfaceChargeDensity() {
        return getSurfaceChargeDensity( EPSILON_AIR, getPlatesVoltage(), capacitor.getPlateSeparation() );
    }
    
    /**
     * Gets the surface density charge between the plates and the dielectric.
     * @return Coulombs/meters^2
     */
    public double getDielectricSurfaceChargeDensity() {
        return getSurfaceChargeDensity( capacitor.getDielectricConstant(), getPlatesVoltage(), capacitor.getPlateSeparation() );
    }
    
    /*
     * General computation of surface charge density.
     * 
     * @param epsilon_r dielectric constant, dimensionless
     * @param V_plate plate voltage, in volts
     * @param d plate separation, meters
     * @return Coulombs/meters^2
     */
    private static double getSurfaceChargeDensity( double epsilon_r, double V_plate, double d ) {
        if ( !( d > 0 ) ) {
            throw new IllegalArgumentException( "model requires d (plate separation) > 0 : " + d );
        }
        return epsilon_r * EPSILON_0 * V_plate / d;
    }
    
    //----------------------------------------------------------------------------------
    //
    // E-Field (E)
    //
    //----------------------------------------------------------------------------------
    
    /**
     * Gets the effective (net) field between the plates.
     * This is uniform everywhere between the plates.
     * 
     * @return volts/meter
     */
    public double getEffectiveEfield() {
        return getPlatesVoltage() / capacitor.getPlateSeparation();
    }
    
    /**
     * Gets the field due to the plates in the capacitor volume that contains air.
     * 
     * @return E-field, in Volts/meter
     */
    public double getPlatesAirEField() {
        return getPlatesEField( EPSILON_AIR,  getPlatesVoltage(), capacitor.getPlateSeparation() );
    }
    
    /**
     * Gets the field due to the plates in the capacitor volume that contains the dielectric.
     * 
     * @return E-field, in Volts/meter
     */
    public double getPlatesDielectricEField() {
        return getPlatesEField( capacitor.getDielectricConstant(), getPlatesVoltage(), capacitor.getPlateSeparation() );
    }
    
    /*
     * General solution for the E-field due to some dielectric.
     * 
     * @param epsilon_r dielectric constant, dimensionless
     * @param V_plates plate voltage, volts
     * @param d plate separation, meters
     * @return E-field, in Volts/meter
     */
    // epsilon_air * V_plates / d
    private static double getPlatesEField( double epsilon_r, double V_plates, double d ) {
        if ( !( d > 0 ) ) {
            throw new IllegalArgumentException( "model requires d (plate separation) > 0 : " + d );
        }
        return epsilon_r * V_plates / d;
    }
    
    /**
     * Gets the field due to air polarization.
     * @return E-field, in Volts/meter
     */
    public double getAirEField() {
        return getPlatesAirEField() - getEffectiveEfield();
    }
    
    /**
     * Gets the field due to dielectric polarization.
     * @return E-field, in Volts/meter
     */
    public double getDielectricEField() {
        return getPlatesDielectricEField() - getEffectiveEfield();
    }
    
    //----------------------------------------------------------------------------------
    //
    // Stored Energy (U)
    //
    //----------------------------------------------------------------------------------
    
    /**
     * Gets the energy stored in the capacitor.
     * @return energy, in joules (J)
     */
    public double getStoredEnergy() {
        double C_total = capacitor.getTotalCapacitance(); // F
        double V_plates = getPlatesVoltage(); // V
        return 0.5 * C_total * V_plates * V_plates; // Joules (J)
    }
    
    //----------------------------------------------------------------------------------
    //
    // Current
    //
    //----------------------------------------------------------------------------------
    
    public double getCurrentAmplitude() {
        return currentAmplitude;
    }
    
    private void setCurrentAmplitude( double currentAmplitude ) {
        if ( currentAmplitude != this.getCurrentAmplitude() ) {
            this.currentAmplitude = currentAmplitude;
            fireCurrentChanged();
        }
    }
    
    /*
     * Current amplitude is proportional to dV/dt, the change in voltage over time.
     */
    private void updateCurrentAmplitude() {
        double V = getPlatesVoltage(); // use plates voltage so that this works when battery is disconnected
        double dV = V - previousVoltage;
        double dt = clock.getDt();
        previousVoltage = V;
        setCurrentAmplitude( Math.min( 1, Math.abs( dV / dt ) ) );
    }
    
    //----------------------------------------------------------------------------------
    //
    // Model update handlers
    //
    //----------------------------------------------------------------------------------
    
    private void handleBatteryVoltageChanged() {
        fireVoltageChanged();
        fireChargeChanged();
        fireEfieldChanged();
        fireEnergyChanged();
    }
    
    private void handleCapacitanceChanged() {
        fireCapacitanceChanged();
        fireChargeChanged();
        fireEfieldChanged();
        fireEnergyChanged();
    }
    
    private void handleDielectricMaterialChanged() {
        updateCustomDielectric();
    }
    
    /*
     * Manages change notification for custom dielectrics.
     */
    private void updateCustomDielectric() {
        // unregister for notification from previous custom dielectric
        if ( customDielectric != null ) {
            customDielectric.removeCustomDielectricChangeListener( customDielectricChangeListener );
            customDielectric = null;
        }
        // register for notification from current custom dielectric
        DielectricMaterial material = capacitor.getDielectricMaterial();
        if ( material instanceof CustomDielectricMaterial ) {
            customDielectric = (CustomDielectricMaterial) material;
            customDielectric.addCustomDielectricChangeListener( customDielectricChangeListener );
        }
    }
    
    //----------------------------------------------------------------------------------
    //
    // Notification mechanisms
    //
    //----------------------------------------------------------------------------------
    
    public interface BatteryCapacitorCircuitChangeListener extends EventListener {
        public void voltageChanged();
        public void capacitanceChanged();
        public void chargeChanged();
        public void efieldChanged();
        public void energyChanged();
        public void batteryConnectedChanged();
        public void currentChanged();
    }
    
    public static class BatteryCapacitorCircuitChangeAdapter implements BatteryCapacitorCircuitChangeListener {
        public void voltageChanged() {}
        public void capacitanceChanged() {}
        public void chargeChanged() {}
        public void efieldChanged() {}
        public void energyChanged() {}
        public void batteryConnectedChanged() {}
        public void currentChanged() {}
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
    
    private void fireCurrentChanged() {
        for ( BatteryCapacitorCircuitChangeListener listener : listeners.getListeners( BatteryCapacitorCircuitChangeListener.class ) ) {
            listener.currentChanged();
        }
    }
}
