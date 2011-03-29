// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model;

import java.awt.Shape;
import java.util.EventListener;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.CustomDielectricMaterial;
import edu.colorado.phet.capacitorlab.model.Wire.BottomWire;
import edu.colorado.phet.capacitorlab.model.Wire.TopWire;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

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
    private final Wire topWire, bottomWire;
    private final EventListenerList listeners;
    private final Battery battery;
    private final Capacitor capacitor;
    private final SimpleObserver dielectricConstantObserver;

    // mutable instance data
    private boolean batteryConnected;
    private double disconnectedPlateCharge; // charge set manually by the user, used when battery is disconnected
    private DielectricMaterial dielectricMaterial;
    private double currentAmplitude; // dV/dt, rate of voltage change
    private double previousCharge;

    public BatteryCapacitorCircuit( CLClock clock, final Battery battery, final Capacitor capacitor, boolean batteryConnected, CLModelViewTransform3D mvt ) {

        this.clock = clock;
        this.listeners = new EventListenerList();
        this.battery = battery;
        this.capacitor = capacitor;
        this.dielectricConstantObserver = new SimpleObserver() {
            public void update() {
                fireCircuitChanged();
            }
        };

        this.batteryConnected = batteryConnected;
        this.disconnectedPlateCharge = getTotalPlateCharge();
        this.dielectricMaterial = capacitor.getDielectricMaterial();
        this.currentAmplitude = 0;
        this.previousCharge = getTotalPlateCharge();

        // update current amplitude on each clock tick
        clock.addClockListener( new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                updateCurrentAmplitude();
            }
        });

        // Create the wires
        topWire = new TopWire( battery, capacitor, CLConstants.WIRE_THICKNESS, mvt );
        bottomWire = new BottomWire( battery, capacitor, CLConstants.WIRE_THICKNESS, mvt );

        // observe properties
        {
            // observe dielectric
            dielectricMaterial.addDielectricConstantObserver( dielectricConstantObserver );

            // observe battery
            battery.addVoltageObserver( new SimpleObserver() {
                public void update() {
                    handleBatteryVoltageChanged();
                }
            } );

            // observe capacitor
            capacitor.addTotalCapacitanceObserver( new SimpleObserver() {
                public void update() {
                    handleCapacitanceChanged();
                }
            } );
            capacitor.addDielectricMaterialObserver( new SimpleObserver() {
                public void update() {
                    handleDielectricMaterialChanged();
                }
            } );
        }
    }

    //----------------------------------------------------------------------------------
    //
    // Circuit components
    //
    //----------------------------------------------------------------------------------

    public Battery getBattery() {
        return battery;
    }

    public Capacitor getCapacitor() {
        return capacitor;
    }

    public Wire getTopWire() {
        return topWire;
    }

    public Wire getBottomWire() {
        return bottomWire;
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
            /*
             * When disconnecting the battery, set the disconnected plate charge to
             * whatever the total plate charge was with the battery connected.
             */
            if ( !connected ) {
                disconnectedPlateCharge = getTotalPlateCharge();
            }
            this.batteryConnected = connected;
            updateWireVoltages();
            fireCircuitChanged();
        }
    }

    private void updateWireVoltages() {
        if ( batteryConnected ) {
            topWire.setVoltage( battery.getVoltage() );
            bottomWire.setVoltage( 0 );
        }
        else {
            topWire.setVoltage( Double.NaN );
            bottomWire.setVoltage( Double.NaN );
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
     * (design doc symbol: V_plates)
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

    /**
     * Gets the voltage between 2 Shapes.
     * @param positiveShape
     * @param negativeShape
     * @return voltage, Double.NaN if the 2 Shape are not both connected to the circuit
     */
    public double getVoltageBetween( Shape positiveShape, Shape negativeShape ) {
        return getVoltage( positiveShape ) - getVoltage( negativeShape );
    }

    /*
     * Gets the voltage at a Shape.
     * @param p
     * @return
     */
    private double getVoltage( Shape s ) {
        double voltage = Double.NaN;
        if ( isBatteryConnected() && topWire.intersects( s ) ) {
            voltage = topWire.getVoltage();
        }
        else if ( isBatteryConnected() && bottomWire.intersects( s ) ) {
            voltage = bottomWire.getVoltage();
        }
        if ( isBatteryConnected() && battery.intersectsTopTerminal( s ) ) {
            voltage = battery.getVoltage();
        }
        else if ( capacitor.intersectsTopPlateShape( s ) ) {
            voltage = getPlatesVoltage();
        }
        else if ( capacitor.intersectsBottomPlateShape( s ) ) {
            voltage = 0;
        }
        return voltage;
    }

    //----------------------------------------------------------------------------------
    //
    // Plate Charge (Q)
    //
    //----------------------------------------------------------------------------------

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

    /**
     * Gets the charge for the portion of the top plate contacting the air.
     * @return charge, in Coulombs
     * (design doc symbol: Q_air)
     */
    public double getAirPlateCharge() {
        return capacitor.getAirCapacitance() * getPlatesVoltage();
    }

    /**
     * Gets the charge for the portion of the top plate contacting the dielectric.
     * (design doc symbol: Q_dielectric)
     *
     * @return charge, in Coulombs
     */
    public double getDielectricPlateCharge() {
        return capacitor.getDieletricCapacitance() * getPlatesVoltage();
    }

    /**
     * Gets the total charge on the top plate.
     * (design doc symbol: Q_total)
     *
     * @return charge, in Coulombs
     */
    public double getTotalPlateCharge() {
        return getDielectricPlateCharge() + getAirPlateCharge();
    }

    /**
     * Gets the excess plate charge due to plates contacting air.
     * (design doc symbol: Q_exess_air)
     *
     * @return excess charge, in Coulombs
     */
    public double getExcessAirPlateCharge() {
        return getExcessPlateCharge( EPSILON_AIR, capacitor.getAirCapacitance(), getPlatesVoltage() );
    }

    /**
     * Gets the excess plate charge due to plates contacting the dielectric.
     * (design doc symbol: Q_excess_dielectric)
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
     * Gets the maximum charge on the top plate (Q_total).
     * We compute this with the battery connected because this is used to determine the range of the Plate Charge slider.
     *
     * @return charge, in Coulombs
     */
    public static double getMaxPlateCharge() {
        CLModelViewTransform3D mvt = new CLModelViewTransform3D();
        DielectricMaterial material = new CustomDielectricMaterial( CLConstants.DIELECTRIC_CONSTANT_RANGE.getMax() );
        Capacitor capacitor = new Capacitor( new Point3D.Double(), CLConstants.PLATE_WIDTH_RANGE.getMax(),
                CLConstants.PLATE_SEPARATION_RANGE.getMin(), material, CLConstants.DIELECTRIC_OFFSET_RANGE.getMin(), mvt );
        Battery battery = new Battery( new Point3D.Double(), CLConstants.BATTERY_VOLTAGE_RANGE.getMax(), mvt );
        BatteryCapacitorCircuit circuit = new BatteryCapacitorCircuit( new CLClock(), battery, capacitor, true /* batteryConnected */, mvt );
        return circuit.getTotalPlateCharge();
    }

    /**
     * Gets the maximum excess charge for the dielectric area (Q_exess_dielectric).
     * @return
     */
    public static double getMaxExcessDielectricPlateCharge() {
        CLModelViewTransform3D mvt = new CLModelViewTransform3D();
        DielectricMaterial material = new CustomDielectricMaterial( CLConstants.DIELECTRIC_CONSTANT_RANGE.getMax() );
        Capacitor capacitor = new Capacitor( new Point3D.Double(), CLConstants.PLATE_WIDTH_RANGE.getMax(),
                CLConstants.PLATE_SEPARATION_RANGE.getMin(), material, CLConstants.DIELECTRIC_OFFSET_RANGE.getMin(), mvt );
        Battery battery = new Battery( new Point3D.Double(), CLConstants.BATTERY_VOLTAGE_RANGE.getMax(), mvt );
        BatteryCapacitorCircuit circuit = new BatteryCapacitorCircuit( new CLClock(), battery, capacitor, true /* batteryConnected */, mvt );
        return circuit.getExcessDielectricPlateCharge();
    }

    //----------------------------------------------------------------------------------
    //
    // Surface Charge Density (sigma)
    //
    //----------------------------------------------------------------------------------

    /**
     * Gets the surface density charge between the plates and air.
     * (design doc symbol: sigma_air)
     *
     * @return Coulombs/meters^2
     */
    public double getAirSurfaceChargeDensity() {
        return getSurfaceChargeDensity( EPSILON_AIR, getPlatesVoltage(), capacitor.getPlateSeparation() );
    }

    /**
     * Gets the surface density charge between the plates and the dielectric.
     * (design doc symbol: sigma_dielectric)
     *
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
     * (design doc symbol: E_effective)
     *
     * @return volts/meter
     */
    public double getEffectiveEfield() {
        return getPlatesVoltage() / capacitor.getPlateSeparation();
    }

    /**
     * Gets the effective E-field at a specified location.
     * Inside the plates, this is E_effective.
     * Outside the plates, it is zero.
     *
     * @param location
     * @return
     */
    public double getEffectiveEFieldAt( Point3D location ) {
        double eField = 0;
        if ( capacitor.isBetweenPlatesShape( location ) ) {
           eField = getEffectiveEfield();
        }
        return eField;
    }

    /**
     * Gets the field due to the plates in the capacitor volume that contains air.
     * (design doc symbol: E_plates_air)
     *
     * @return E-field, in Volts/meter
     */
    public double getPlatesAirEField() {
        return getPlatesEField( EPSILON_AIR,  getPlatesVoltage(), capacitor.getPlateSeparation() );
    }

    /**
     * Gets the field due to the plates in the capacitor volume that contains the dielectric.
     * (design doc symbol: E_plates_dielectric)
     *
     * @return E-field, in Volts/meter
     */
    public double getPlatesDielectricEField() {
        return getPlatesEField( capacitor.getDielectricConstant(), getPlatesVoltage(), capacitor.getPlateSeparation() );
    }

    /**
     * Field due to the plate, at a specific location.
     * Between the plates, the field is either E_plate_dielectric or E_plate_air, depending on whether the probe intersects the dielectric.
     * Outside the plates, the field is zero.
     *
     * @param location
     * @return
     */
    public double getPlatesDielectricEFieldAt( Point3D location ) {
        double eField = 0;
        if ( capacitor.isInsideDielectricBetweenPlatesShape( location ) ) {
            eField = getPlatesDielectricEField();
        }
        else if ( capacitor.isInsideAirBetweenPlatesShape( location ) ) {
            eField = getPlatesAirEField();
        }
        return eField;
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
     * (design doc symbol: E_air)
     *
     * @return E-field, in Volts/meter
     */
    public double getAirEField() {
        return getPlatesAirEField() - getEffectiveEfield();
    }

    /**
     * Gets the field due to dielectric polarization.
     * (design doc symbol: E_dielectric)
     *
     * @return E-field, in Volts/meter
     */
    public double getDielectricEField() {
        return getPlatesDielectricEField() - getEffectiveEfield();
    }

    /**
     * Gets the field due to dielectric polarization, at a specific location.
     * Between the plates, the field is either E_dielectric or E_air, depending on whether the probe intersects the dielectric.
     * Outside the plates, the field is zero.
     *
     * @param location
     * @return
     */
    public Double getDielectricEFieldAt( Point3D location ) {
        double eField = 0;
        if ( capacitor.isInsideDielectricBetweenPlatesShape( location ) ) {
            eField = getDielectricEField();
        }
        else if ( capacitor.isInsideAirBetweenPlatesShape( location ) ) {
            eField = getAirEField();
        }
        return eField;
    }

    /**
     * Gets the maximum effective E-field between the plates (E_effective).
     * The maximum occurs when the battery is disconnected, the Plate Charge control is set to its maximum,
     * the plate area is set to its minimum, and the dielectric constant is min, and the dielectric is fully inserted.
     * And in this situation, plate separation is irrelevant.
     * @return
     */
    public static double getMaxEffectiveEfield() {
        CLModelViewTransform3D mvt = new CLModelViewTransform3D();
        DielectricMaterial material = new CustomDielectricMaterial( CLConstants.DIELECTRIC_CONSTANT_RANGE.getMin() );
        Capacitor capacitor = new Capacitor( new Point3D.Double(), CLConstants.PLATE_WIDTH_RANGE.getMin(),
                CLConstants.PLATE_SEPARATION_RANGE.getMin(), material, CLConstants.DIELECTRIC_OFFSET_RANGE.getMin(), mvt );
        Battery battery = new Battery( new Point3D.Double(), 0, mvt );
        BatteryCapacitorCircuit circuit = new BatteryCapacitorCircuit( new CLClock(), battery, capacitor, false /* batteryConnected */, mvt );
        circuit.setDisconnectedPlateCharge( getMaxPlateCharge() );
        return circuit.getEffectiveEfield();
    }

    /**
     * Gets the maximum field due to dielectric polarization (E_dielectric).
     * @return
     */
    public static double getMaxDielectricEField() {
        CLModelViewTransform3D mvt = new CLModelViewTransform3D();
        DielectricMaterial material = new CustomDielectricMaterial( CLConstants.DIELECTRIC_CONSTANT_RANGE.getMax() );
        Capacitor capacitor = new Capacitor( new Point3D.Double(), CLConstants.PLATE_WIDTH_RANGE.getMin(),
                CLConstants.PLATE_SEPARATION_RANGE.getMin(), material, CLConstants.DIELECTRIC_OFFSET_RANGE.getMax(), mvt );
        Battery battery = new Battery( new Point3D.Double(), 0, mvt );
        BatteryCapacitorCircuit circuit = new BatteryCapacitorCircuit( new CLClock(), battery, capacitor, false /* batteryConnected */, mvt );
        circuit.setDisconnectedPlateCharge( getMaxPlateCharge() );
        return circuit.getDielectricEField();
    }

    /**
     * Gets the maximum E-field due to the plates in the capacitor volume that
     * contains the dielectric (E_plates_dielectric), with the battery connected.
     * @return
     */
    public static double getMaxPlatesDielectricEFieldWithBattery() {
        CLModelViewTransform3D mvt = new CLModelViewTransform3D();
        DielectricMaterial material = new CustomDielectricMaterial( CLConstants.DIELECTRIC_CONSTANT_RANGE.getMax() );
        Capacitor capacitor = new Capacitor( new Point3D.Double(), CLConstants.PLATE_WIDTH_RANGE.getMin(),
                CLConstants.PLATE_SEPARATION_RANGE.getMin(), material, CLConstants.DIELECTRIC_OFFSET_RANGE.getMax(), mvt );
        Battery battery = new Battery( new Point3D.Double(), CLConstants.BATTERY_VOLTAGE_RANGE.getMax(), mvt );
        BatteryCapacitorCircuit circuit = new BatteryCapacitorCircuit( new CLClock(), battery, capacitor, true /* batteryConnected */, mvt );
        return circuit.getPlatesDielectricEField();
    }

    //----------------------------------------------------------------------------------
    //
    // Stored Energy (U)
    //
    //----------------------------------------------------------------------------------

    /**
     * Gets the energy stored in the capacitor.
     * (design doc symbol: U)
     *
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
            fireCircuitChanged();
        }
    }

    /*
     * Current amplitude is proportional to dQ/dt, the change in charge (Q_total) over time.
     */
    private void updateCurrentAmplitude() {
        double Q = getTotalPlateCharge();
        double dQ = Q - previousCharge;
        double dt = clock.getDt();
        previousCharge = Q;
        setCurrentAmplitude( dQ / dt );
    }

    //----------------------------------------------------------------------------------
    //
    // Model update handlers
    //
    //----------------------------------------------------------------------------------

    private void handleBatteryVoltageChanged() {
        if ( isBatteryConnected() ) {
            updateWireVoltages();
            fireCircuitChanged();
        }
    }

    private void handleCapacitanceChanged() {
        updateWireVoltages();
        fireCircuitChanged();
    }

    private void handleDielectricMaterialChanged() {
        // rewire dielectric observer
        this.dielectricMaterial.removeDielectricConstantObserver( dielectricConstantObserver );
        this.dielectricMaterial = capacitor.getDielectricMaterial();
        this.dielectricMaterial.addDielectricConstantObserver( dielectricConstantObserver ); // this triggers a notification
    }

    //----------------------------------------------------------------------------------
    //
    // Notification mechanism.
    // Any change to the circuit results in a circuitChanged notification, with
    // no information about what has changed.  This may seem wasteful, but in
    // practice most things need to be changed when anything changes.  So this
    // simplifies the programming at the expense of some unneeded computation.
    // No performance problems have been noted, but this would be the first place
    // to start optimizing if performance becomes an issue.
    //
    //----------------------------------------------------------------------------------

    public interface BatteryCapacitorCircuitChangeListener extends EventListener {
        public void circuitChanged();
    }

    public void addBatteryCapacitorCircuitChangeListener( BatteryCapacitorCircuitChangeListener listener ) {
        listeners.add(  BatteryCapacitorCircuitChangeListener.class, listener );
    }

    public void removeBatteryCapacitorCircuitChangeListener( BatteryCapacitorCircuitChangeListener listener ) {
        listeners.remove(  BatteryCapacitorCircuitChangeListener.class, listener );
    }

    public void fireCircuitChanged() {
        for ( BatteryCapacitorCircuitChangeListener listener : listeners.getListeners( BatteryCapacitorCircuitChangeListener.class ) ) {
            listener.circuitChanged();
        }
    }
}
