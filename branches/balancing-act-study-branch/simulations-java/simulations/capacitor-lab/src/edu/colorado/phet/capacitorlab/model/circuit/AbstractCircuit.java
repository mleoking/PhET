// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model.circuit;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.Battery;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeListener;
import edu.colorado.phet.capacitorlab.model.CircuitConfig;
import edu.colorado.phet.capacitorlab.model.wire.Wire;
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

    // Function for creating capacitors.
    public interface CreateCapacitors {
        public ArrayList<Capacitor> apply( CircuitConfig config, Integer numberOfCapacitors );
    }

    // Function for creating wires.
    public interface CreateWires {
        ArrayList<Wire> apply( CircuitConfig config, Battery battery, ArrayList<Capacitor> capacitors );
    }

    private final String displayName; // localized name that is visible to the user
    private final IClock clock;
    private final ClockAdapter clockListener;
    private final Battery battery;
    private final ArrayList<Capacitor> capacitors;
    private final ArrayList<Wire> wires;
    private final ArrayList<CircuitChangeListener> listeners;
    private Property<Double> currentAmplitudeProperty; // simulates current flow. 0=no flow, non-zero=flow
    private double previousTotalCharge; // total charge the previous time the clock ticked, used to compute current amplitude

    /**
     * Constructor
     *
     * @param config             circuit configuration values
     * @param displayName        localized name this is visible to the user
     * @param numberOfCapacitors number of capacitors in the circuit
     * @param createCapacitors   function for creating capacitors
     * @param createWires        function for creating wires
     */
    protected AbstractCircuit( CircuitConfig config, String displayName, int numberOfCapacitors, CreateCapacitors createCapacitors, CreateWires createWires ) {

        this.displayName = displayName;
        this.clock = config.clock;
        this.listeners = new ArrayList<CircuitChangeListener>();
        this.currentAmplitudeProperty = new Property<Double>( 0d );
        this.previousTotalCharge = -1; // no value

        // create circuit components
        battery = new Battery( config.batteryLocation, CLConstants.BATTERY_VOLTAGE_RANGE.getDefault(), config.mvt );
        capacitors = createCapacitors.apply( config, numberOfCapacitors );
        assert ( capacitors.size() >= 1 );
        wires = createWires.apply( config, battery, capacitors );
        assert ( wires.size() >= 2 );

        // update current amplitude on each clock tick
        clockListener = new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                updateCurrentAmplitude();
            }
        };
        clock.addClockListener( clockListener );

        // observe capacitors
        CapacitorChangeListener capacitorChangeListener = new CapacitorChangeListener() {
            public void capacitorChanged() {
                updatePlateVoltages();
                fireCircuitChanged();
            }
        };
        for ( Capacitor capacitor : capacitors ) {
            capacitor.addCapacitorChangeListener( capacitorChangeListener );
        }

        /*
         * When the battery voltage changes, update the plate voltages.
         * Do NOT automatically do this when adding the observer because
         * updatePlateVoltages is implemented by the subclass, and all
         * necessary fields in the subclass may not be initialized.
         */
        battery.addVoltageObserver( new SimpleObserver() {
            public void update() {
                updatePlateVoltages();
            }
        }, false /* notifyOnAdd */ );
    }

    /*
     * Updates the plate voltages.
     * Subclasses must call this at the end of their constructor, see note in constructor.
     */
    protected abstract void updatePlateVoltages();

    public void cleanup() {
        clock.removeClockListener( clockListener );
    }

    public void reset() {
        battery.reset();
        for ( Capacitor capacitor : capacitors ) {
            capacitor.reset();
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public Battery getBattery() {
        return battery;
    }

    /*
     * Default implementation has a connected battery.
     * In the "single capacitor" circuit, we'll override this and add a setter,
     * so that the battery can be dynamically connected and disconnected in the "Dielectric" module.
     */
    public boolean isBatteryConnected() {
        return true;
    }

    public ArrayList<Capacitor> getCapacitors() {
        return new ArrayList<Capacitor>( capacitors );
    }

    public ArrayList<Wire> getWires() {
        return new ArrayList<Wire>( wires );
    }

    // Gets the wire connected to the battery's top terminal.
    public Wire getTopWire() {
        return wires.get( 0 );
    }

    // Gets the wire connected to the battery's bottom terminal.
    public Wire getBottomWire() {
        return wires.get( wires.size() - 1 );
    }

    // Q_total = V_total * C_total
    public double getTotalCharge() {
        return getTotalVoltage() * getTotalCapacitance();
    }

    // Since the default is a connected battery, the total voltage is the battery voltage.
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
            if ( capacitor.isBetweenPlates( location ) ) {
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
            if ( capacitor.isInsideDielectricBetweenPlates( location ) ) {
                eField = capacitor.getPlatesDielectricEField();
                break;
            }
            else if ( capacitor.isInsideAirBetweenPlates( location ) ) {
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
            if ( capacitor.isInsideDielectricBetweenPlates( location ) ) {
                eField = capacitor.getDielectricEField();
                break;
            }
            else if ( capacitor.isInsideAirBetweenPlates( location ) ) {
                eField = capacitor.getAirEField();
                break;
            }
        }
        return eField;
    }

    // 0 == current is not flowing, !0 == current is flowing
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
        listeners.add( listener );
    }

    // @see ICircuit.removeCircuitChangeListener
    public void removeCircuitChangeListener( CircuitChangeListener listener ) {
        listeners.remove( listener );
    }

    // Notifies all listeners that the circuit has changed.
    protected void fireCircuitChanged() {
        for ( CircuitChangeListener listener : new ArrayList<CircuitChangeListener>( listeners ) ) {
            listener.circuitChanged();
        }
    }
}
