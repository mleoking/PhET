/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.util.Observable;

/**
 * GlaciersObservable is an extension of java.util.Observable that 
 * allows you to temporarily disable notification.  This is 
 * useful if you need to change a number of properties before
 * notifying Observers.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlaciersObservable extends Observable {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private boolean _notifyEnabled;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public GlaciersObservable() {
        super();
        _notifyEnabled = true;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setNotifyEnabled( boolean enabled ) {
        _notifyEnabled = enabled;
        notifyObservers();
    }
    
    public boolean isNotifyEnabled() {
        return _notifyEnabled;
    }
    
    //----------------------------------------------------------------------------
    // Observable overrides
    //----------------------------------------------------------------------------
    
    public void notifyObservers() {
        if ( _notifyEnabled ) {
            setChanged();
            super.notifyObservers();
            clearChanged();
        }
    }
    
    public void notifyObservers( Object arg ) {
        if ( _notifyEnabled ) {
            setChanged();
            super.notifyObservers( arg );
            clearChanged();
        }
    }
}
