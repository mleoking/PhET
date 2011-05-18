// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model.circuit;

import java.awt.*;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.Battery;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.common.phetcommon.math.Point3D;

/**
 * Base class for all circuits.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractCircuit implements ICircuit {

    private final String displayName;
    private final Battery battery;
    private final EventListenerList listeners;

    protected AbstractCircuit( String displayName, CLModelViewTransform3D mvt, Point3D batteryLocation ) {
        this.displayName = displayName;
        this.battery = new Battery( batteryLocation, CLConstants.BATTERY_VOLTAGE_RANGE.getDefault(), mvt );
        this.listeners = new EventListenerList();
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
