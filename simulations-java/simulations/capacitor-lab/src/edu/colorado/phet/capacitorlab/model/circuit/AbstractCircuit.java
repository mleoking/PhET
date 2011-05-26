// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model.circuit;

import java.awt.*;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.Battery;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Base class for all circuits.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractCircuit implements ICircuit {

    private final String displayName;
    private final IClock clock;
    private final ClockAdapter clockListener;
    private final Battery battery;
    private final EventListenerList listeners;
    private Property<Double> currentAmplitudeProperty; // dV/dt, rate of voltage change
    private double previousTotalCharge; // total charge the previous time the clock ticked, used to compute current amplitude

    protected AbstractCircuit( String displayName, IClock clock, CLModelViewTransform3D mvt, Point3D batteryLocation ) {

        this.displayName = displayName;
        this.clock = clock;
        this.battery = new Battery( batteryLocation, CLConstants.BATTERY_VOLTAGE_RANGE.getDefault(), mvt );
        this.listeners = new EventListenerList();
        this.currentAmplitudeProperty = new Property<Double>( 0d );
        this.previousTotalCharge = -1; // no value

        // update current amplitude on each clock tick
        clockListener = new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                updateCurrentAmplitude();
            }
        };
        clock.addClockListener( clockListener );
    }

    public void cleanup() {
        clock.removeClockListener( clockListener );
    }

    public void reset() {
        battery.reset();
    }

    public String getDisplayName() {
        return displayName;
    }

    public Battery getBattery() {
        return battery;
    }

    public boolean isBatteryConnected() {
        return true;
    }

    // Q_total = V_total * C_total
    public double getTotalCharge() {
        return getTotalVoltage() * getTotalCapacitance();
    }

    public double getTotalVoltage() {
        return battery.getVoltage();
    }

    // @see ICircuit.getVoltageBetween
    public double getVoltageBetween( Shape positiveShape, Shape negativeShape ) {
        return getVoltageAt( positiveShape ) - getVoltageAt( negativeShape );
    }

    // @see ICircuit.getStoredEnergy
    public double getStoredEnergy() {
        double C_total = getTotalCapacitance(); // F
        double V_total = getTotalVoltage(); // V
        return 0.5 * C_total * V_total * V_total; // Joules (J)
    }

    // @see ICircuit.getEffectiveEFieldAt
    public double getEffectiveEFieldAt( Point3D location ) {
        double eField = 0;
        for ( Capacitor capacitor : getCapacitors() ) {
            if ( capacitor.isBetweenPlatesShape( location ) ) {
                eField = capacitor.getEffectiveEField();
                break;
            }
        }
        return eField;
    }

    // @see ICircuit.getPlatesDielectricEFieldAt
    public double getPlatesDielectricEFieldAt( Point3D location ) {
        double eField = 0;
        for ( Capacitor capacitor : getCapacitors() ) {
            if ( capacitor.isInsideDielectricBetweenPlatesShape( location ) ) {
                eField = capacitor.getPlatesDielectricEField();
                break;
            }
            else if ( capacitor.isInsideAirBetweenPlatesShape( location ) ) {
                eField = capacitor.getPlatesAirEField();
                break;
            }
        }
        return eField;
    }

    // @see ICircuit.getDielectricEFieldAt
    public double getDielectricEFieldAt( Point3D location ) {
        double eField = 0;
        for ( Capacitor capacitor : getCapacitors() ) {
            if ( capacitor.isInsideDielectricBetweenPlatesShape( location ) ) {
                eField = capacitor.getDielectricEField();
                break;
            }
            else if ( capacitor.isInsideAirBetweenPlatesShape( location ) ) {
                eField = capacitor.getAirEField();
                break;
            }
        }
        return eField;
    }

    public double getCurrentAmplitude() {
        return currentAmplitudeProperty.get();
    }

    // Current amplitude is proportional to dQ/dt, the change in charge (Q_total) over time.
    private void updateCurrentAmplitude() {
        double Q = getTotalCharge();
        if ( previousTotalCharge != -1 ) {
            double dQ = Q - previousTotalCharge;
            double dt = clock.getSimulationTimeChange();
            double amplitude = dQ / dt;
            currentAmplitudeProperty.set( amplitude );
        }
        previousTotalCharge = Q;
    }

    public void addCurrentAmplitudeObserver( SimpleObserver o ) {
        currentAmplitudeProperty.addObserver( o );
    }

    // @see ICircuit.addCircuitChangeListener
    public void addCircuitChangeListener( CircuitChangeListener listener ) {
        listeners.add( CircuitChangeListener.class, listener );
    }

    // @see ICircuit.removeCircuitChangeListener
    public void removeCircuitChangeListener( CircuitChangeListener listener ) {
        listeners.remove( CircuitChangeListener.class, listener );
    }

    protected void fireCircuitChanged() {
        for ( CircuitChangeListener listener : listeners.getListeners( CircuitChangeListener.class ) ) {
            listener.circuitChanged();
        }
    }
}
