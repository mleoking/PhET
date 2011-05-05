// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.property4;

import java.util.ArrayList;

/**
 * Notifies registered listeners that a property has changed.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class PropertyChangeNotifier<T> {

    private ArrayList<PropertyChangeListener> listeners;

    public PropertyChangeNotifier() {
        this.listeners = new ArrayList<PropertyChangeListener>();
    }

    /**
     * Adds a listener. No binding (immediate notification) occurs, because adding a listener
     * does not constitute a property value change. Providing automatic notification gets us
     * into the nasty situation of defining the property's old value when the property's
     * initial value has never changed. Treating the property old value correctly as "undefined"
     * (via null, Option.NONE, or some other mechanism) results in additional checks in
     * listeners, and defeats the convenience of binding.  Binding would also require knowledge
     * of the old and new value, which this notification base class does not (and should not) have.
     *
     * @param listener
     */
    public void addListener( PropertyChangeListener listener ) {
        listeners.add( listener );
    }

    public void removeListener( PropertyChangeListener listener ) {
        listeners.add( listener );
    }

    protected void firePropertyChanged( T oldValue, T newValue ) {
        PropertyChangeEvent<T> event = new PropertyChangeEvent<T>( oldValue, newValue );
        for ( PropertyChangeListener listener : new ArrayList<PropertyChangeListener>( listeners ) ) {
            listener.propertyChanged( event );
        }
    }
}
