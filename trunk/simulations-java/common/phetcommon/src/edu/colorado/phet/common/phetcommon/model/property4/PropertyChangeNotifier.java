// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property4;

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
     * Adds a listener. No binding (immediate notification) occurs because
     * this base class has no knowledge of the property state. If you want
     * binding, it can be implemented by overriding this method.
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
