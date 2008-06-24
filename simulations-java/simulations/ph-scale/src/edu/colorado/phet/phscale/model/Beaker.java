/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.model;

import java.util.ArrayList;
import java.util.Iterator;


public class Beaker {
    
    public static final double MAX_VOLUME = 1.25; // liters

    public LiquidDescriptor _liquid;
    public final ArrayList _listeners;
    
    public Beaker() {
        _listeners = new ArrayList();
    }
    
    public void setLiquid( LiquidDescriptor liquid ) {
        if ( liquid != _liquid ) {
            _liquid = liquid;
            notifyLiquidChanged();
        }
    }
    
    public LiquidDescriptor getLiquid() {
        return _liquid;
    }
    
    public double getMaxVolume() {
        return MAX_VOLUME;
    }
    
    public void empty() {
        if ( _liquid != null ) {
            _liquid = null;
            notifyEmptied();
        }
    }
    
    public boolean isEmpty() {
        return ( _liquid == null );
    }
    
    public interface BeakerListener {
        public void liquidChanged();
        public void emptied();
    }
    
    public void addBeakerListener( BeakerListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeBeakerListener( BeakerListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyLiquidChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (BeakerListener) i.next() ).liquidChanged();
        }
    }
    
    private void notifyEmptied() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (BeakerListener) i.next() ).emptied();
        }
    }
    
    
}
