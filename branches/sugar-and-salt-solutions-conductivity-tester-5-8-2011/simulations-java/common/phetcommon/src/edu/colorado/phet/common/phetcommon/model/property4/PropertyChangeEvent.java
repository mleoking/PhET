// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property4;

/**
 * Event used to notify that a property's has changed.
 * Includes both the old and new values.
 * </p>
 * All property change notification involves this event, and the client can
 * choose to use or ignore information contained in the event.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PropertyChangeEvent<T> {

    // As a compromise for improved code readability, these fields are made public.
    public final T oldValue; // the old (previous) value
    public final T value; // the current value

    public PropertyChangeEvent( T oldValue, T value ) {
        this.oldValue = oldValue;
        this.value = value;
    }

    @Override public String toString() {
        return "oldValue=" + oldValue + ",value=" + value;
    }
}
