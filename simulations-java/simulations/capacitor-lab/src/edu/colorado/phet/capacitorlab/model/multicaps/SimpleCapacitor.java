// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.model.multicaps;

import java.util.EventListener;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;

/**
 * Capacitor model used in the Multiple Capacitors tab, where the user
 * cannot directly manipulate the capacitor's geometry.
 * </p>
 * The user can change capacitance (via a slider) and voltage (via the battery).
 * The plate area is fixed, and separation varies with the inverse of capacitance.
 * This capacitor has air between plates.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimpleCapacitor {

    private final EventListenerList listeners;
    private final DoubleRange capacitanceRange, plateSeparationRange;
    private final LinearFunction capacitanceToPlateSeparation;

    private double capacitance;  // C, Farads
    private double plateVoltage; // V_plate, Volts

    public SimpleCapacitor( double capacitance ) {
        this( capacitance, new DoubleRange( 1E10 - 12, 3E10 - 12 ), new DoubleRange( 0.005, 0.01 ) );
    }

    private SimpleCapacitor( double capacitance, DoubleRange capacitanceRange, DoubleRange plateSeparationRange ) {
        this.capacitance = capacitance;
        this.capacitanceRange = capacitanceRange;
        this.plateSeparationRange = plateSeparationRange;
        this.capacitanceToPlateSeparation = new LinearFunction( capacitanceRange.getMin(), capacitanceRange.getMax(), plateSeparationRange.getMax(), plateSeparationRange.getMin() );
        this.listeners = new EventListenerList();
    }

    // C (Farads)
    public double getCapacitance() {
        return capacitance;
    }

    // C (Farads)
    public void setCapacitance( double capacitance ) {
        if ( !capacitanceRange.contains( capacitance ) ) {
            throw new IllegalArgumentException( "capacitance out of range: " + capacitance );
        }
        if ( capacitance != this.capacitance ) {
            this.capacitance = capacitance;
            fireCapacitorChanged();
        }
    }

    // L (meters)
    public double getPlateWidth() {
        return 0.01;
    }

    // d (meters)
    public double getPlateSeparation() {
        return capacitanceToPlateSeparation.evaluate( capacitance );
    }

    // V_plate (Volts)
    public double getPlateVoltage() {
        return plateVoltage;
    }

    // V_plate (Volts)
    public void setPlateVoltage( double plateVoltage ) {
        if ( plateVoltage != this.plateVoltage ) {
            this.plateVoltage = plateVoltage;
            fireCapacitorChanged();
        }
    }

    // E_plates_air = epsilon_air * V_plates / d  (Volts/meter)
    public double getEField() {
        return CLConstants.EPSILON_AIR * getPlateVoltage() / getPlateSeparation();
    }

    // Q = C * V (Coulombs)
    public double getCharge() {
        return getCapacitance() * getPlateVoltage();
    }

    public DoubleRange getPlateSeparationRange() {
        return plateSeparationRange;
    }

    public DoubleRange getCapacitanceRange() {
        return capacitanceRange;
    }

    public interface SimpleCapacitorChangeListener extends EventListener {
        void capacitorChanged();
    }

    public void addSimpleCapacitorChangeListener( SimpleCapacitorChangeListener listener ) {
        listeners.add( SimpleCapacitorChangeListener.class, listener );
    }

    public void removeSimpleCapacitorChangeListener( SimpleCapacitorChangeListener listener ) {
        listeners.remove( SimpleCapacitorChangeListener.class, listener );
    }

    private void fireCapacitorChanged() {
        for ( SimpleCapacitorChangeListener listener : listeners.getListeners( SimpleCapacitorChangeListener.class ) ) {
            listener.capacitorChanged();
        }
    }
}
