// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model.circuit;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial;
import edu.colorado.phet.capacitorlab.model.ICapacitor.CapacitorChangeListener;
import edu.colorado.phet.capacitorlab.model.Wire;
import edu.colorado.phet.capacitorlab.model.Wire.BottomWire;
import edu.colorado.phet.capacitorlab.model.Wire.TopWire;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Model of a circuit with a battery connected to a single capacitor.
 * This circuit is used in all 3 modules.
 * <p/>
 * Variable names used in this implementation where chosen to match the specification
 * in the design document, and therefore violate Java naming conventions.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SingleCircuit extends AbstractCircuit {

    // immutable instance data
    private final Capacitor capacitor;
    private final Wire topWire, bottomWire;
    private final IClock clock;

    // observable properties
    private Property<Double> currentAmplitudeProperty; // dV/dt, rate of voltage change

    // mutable instance data
    private Property<Boolean> batteryConnectedProperty; // is the battery connected to the circuit?
    private double disconnectedPlateCharge; // charge set manually by the user, used when battery is disconnected
    private double previousTotalCharge; // total charge the previous time the clock ticked, used to compute current amplitude

    public SingleCircuit( IClock clock, CLModelViewTransform3D mvt, Point3D batteryLocation, Point3D capacitorLocation,
                          double plateWidth, double plateSeparation, DielectricMaterial dielectricMaterial, double dielectricOffset ) {
        this( clock, mvt, batteryLocation, capacitorLocation, plateWidth, plateSeparation, dielectricMaterial, dielectricOffset, true /* batteryConnected */ );
    }

    public SingleCircuit( IClock clock, CLModelViewTransform3D mvt, Point3D batteryLocation, Point3D capacitorLocation,
                          double plateWidth, double plateSeparation, DielectricMaterial dielectricMaterial, double dielectricOffset,
                          boolean batteryConnected ) {
        super( CLStrings.SINGLE, mvt, batteryLocation );

        this.clock = clock;
        this.capacitor = new Capacitor( capacitorLocation, plateWidth, plateSeparation, dielectricMaterial, dielectricOffset, mvt );
        this.batteryConnectedProperty = new Property<Boolean>( batteryConnected );
        this.disconnectedPlateCharge = getTotalCharge();
        this.previousTotalCharge = getTotalCharge();
        this.currentAmplitudeProperty = new Property<Double>( 0d );

        // update current amplitude on each clock tick
        clock.addClockListener( new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                updateCurrentAmplitude();
            }
        } );

        // Create the wires
        topWire = new TopWire( getBattery(), capacitor, CLConstants.WIRE_THICKNESS, mvt );
        bottomWire = new BottomWire( getBattery(), capacitor, CLConstants.WIRE_THICKNESS, mvt );

        // observe battery
        getBattery().addVoltageObserver( new SimpleObserver() {
            public void update() {
                if ( isBatteryConnected() ) {
                    updateVoltages();
                }
            }
        } );

        // observe capacitor
        capacitor.addCapacitorChangeListener( new CapacitorChangeListener() {
            public void capacitorChanged() {
                if ( !isBatteryConnected() ) {
                    updateVoltages();
                }
                fireCircuitChanged();
            }
        } );
    }

    @Override public void reset() {
        super.reset();
        capacitor.reset();
        batteryConnectedProperty.reset();
    }

    //----------------------------------------------------------------------------------
    // Circuit components
    //----------------------------------------------------------------------------------

    public Capacitor getCapacitor() {
        return capacitor;
    }

    public ArrayList<Capacitor> getCapacitors() {
        return new ArrayList<Capacitor>() {{
            add( capacitor );
        }};
    }

    public Wire getTopWire() {
        return topWire;
    }

    public Wire getBottomWire() {
        return bottomWire;
    }

    //----------------------------------------------------------------------------------
    // Battery connectivity
    //----------------------------------------------------------------------------------

    /**
     * Is the battery connected to the capacitor?
     *
     * @return
     */
    public boolean isBatteryConnected() {
        return batteryConnectedProperty.get();
    }

    /**
     * Determines whether the battery is connected to the capacitor.
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
            updateVoltages();
            fireCircuitChanged();
        }
    }

    /*
     * Updates the capacitor and wire voltages, depending on whether the battery is connected.
     */
    private void updateVoltages() {
        double V = getBattery().getVoltage();
        if ( !isBatteryConnected() ) {
            V = disconnectedPlateCharge / capacitor.getTotalCapacitance(); // V = Q/C
        }
        //TODO:
        // There's an order dependency here. Voltmeter is listening for a circuitChanged notification,
        // so if we set the plate voltage first and a probe is on a wire, then the meter will be
        // reading a stale wire voltage.
        bottomWire.setVoltage( 0 );
        topWire.setVoltage( V );
        capacitor.setPlatesVoltage( V );
    }

    //----------------------------------------------------------------------------------
    // Plate voltage (V)
    //----------------------------------------------------------------------------------

    public double getTotalVoltage() {
        if ( isBatteryConnected() ) {
            return super.getTotalVoltage();
        }
        else {
            return capacitor.getPlatesVoltage();
        }
    }

    // @see ICircuit.getVoltageAt
    public double getVoltageAt( Shape s ) {
        double voltage = Double.NaN;
        if ( ( isBatteryConnected() && ( topWire.intersects( s ) || getBattery().intersectsTopTerminal( s ) ) ) || capacitor.intersectsTopPlateShape( s ) ) {
            voltage = getTotalVoltage();
        }
        else if ( ( isBatteryConnected() && ( bottomWire.intersects( s ) || getBattery().intersectsBottomTerminal( s ) ) ) || capacitor.intersectsBottomPlateShape( s ) ) {
            voltage = 0;
        }
        return voltage;
    }

    //----------------------------------------------------------------------------------
    // Capacitance (C)
    //----------------------------------------------------------------------------------

    // @see ICircuit.getTotalCapacitance
    public double getTotalCapacitance() {
        return capacitor.getTotalCapacitance();
    }

    //----------------------------------------------------------------------------------
    // Plate charge (Q)
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
                updateVoltages();
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

    //----------------------------------------------------------------------------------
    // Current
    //----------------------------------------------------------------------------------

    public double getCurrentAmplitude() {
        return currentAmplitudeProperty.get();
    }

    /*
     * Current amplitude is proportional to dQ/dt, the change in charge (Q_total) over time.
     */
    private void updateCurrentAmplitude() {
        double Q = getTotalCharge();
        double dQ = Q - previousTotalCharge;
        double dt = clock.getSimulationTimeChange();
        previousTotalCharge = Q;
        currentAmplitudeProperty.set( dQ / dt );
    }

    public void addCurrentAmplitudeObserver( SimpleObserver o ) {
        currentAmplitudeProperty.addObserver( o );
    }
}
