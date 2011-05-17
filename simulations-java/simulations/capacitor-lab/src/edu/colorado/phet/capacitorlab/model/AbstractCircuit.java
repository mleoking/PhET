// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.capacitorlab.CLConstants;

/**
 * Base class for all circuits.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractCircuit implements ICircuit {

    private final String displayName;
    private final Battery battery;
    private final EventListenerList listeners;

    protected AbstractCircuit( String displayName, CLModelViewTransform3D mvt ) {
        this.displayName = displayName;
        this.battery = new Battery( CLConstants.BATTERY_LOCATION, CLConstants.BATTERY_VOLTAGE_RANGE.getDefault(), mvt );
        this.listeners = new EventListenerList();
    }

    public String getDisplayName() {
        return displayName;
    }

    public Battery getBattery() {
        return battery;
    }

    public void reset() {
        battery.reset();
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
