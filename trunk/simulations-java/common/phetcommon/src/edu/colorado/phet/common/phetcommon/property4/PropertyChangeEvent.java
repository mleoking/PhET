// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.property4;

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

    public final T old; // the old (previous) value
    public final T value; // the current value

    public PropertyChangeEvent( T old, T value ) {
        this.old = old;
        this.value = value;
    }

    @Override public String toString() {
        return "old=" + old.toString() + ",value=" + value.toString();
    }
}
