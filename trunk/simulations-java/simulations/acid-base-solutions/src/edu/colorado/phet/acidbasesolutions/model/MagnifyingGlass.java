/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import java.util.EventListener;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;

/**
 * Model of a magnifying glass.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MagnifyingGlass {
    
    private double diameter;
    private final EventListenerList listeners;
    
    public MagnifyingGlass() {
        this( ABSConstants.MAGNIFYING_GLASS_DIAMETER );
    }
    
    public MagnifyingGlass( double diameter ) {
        this.diameter = diameter;
        listeners = new EventListenerList();
    }
    
    public void setDiameter( int diameter ) {
        if ( diameter != this.diameter ) {
            this.diameter = diameter;
            fireDiameterChanged();
        }
    }
    
    public double getDiameter() {
        return diameter;
    }
    
    public interface MagnifyingGlassListener extends EventListener {
        public void diameterChanged();
    }
    
    public void addMagnifyingGlassListener( MagnifyingGlassListener listener ) {
        listeners.add( MagnifyingGlassListener.class, listener );
    }
    
    public void removeMagnifyingGlassListener( MagnifyingGlassListener listener ) {
        listeners.remove( MagnifyingGlassListener.class, listener );
    }
    
    private void fireDiameterChanged() {
        for ( MagnifyingGlassListener listener : listeners.getListeners( MagnifyingGlassListener.class ) ) {
            listener.diameterChanged();
        }
    }
}
