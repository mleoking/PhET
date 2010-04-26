/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

import javax.swing.event.EventListenerList;

/**
 * Model of a capacitor dieletric.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Dielectric extends Box {
    
    private double offset;
    private final EventListenerList listeners;

    public Dielectric( double width, double height, double depth, double offset ) {
        super( width, height, depth );
        this.offset = offset;
        listeners = new EventListenerList();
    }
    
    public void setOffset( double offset ) {
        if ( offset != this.offset ) {
            double oldOffset = offset;
            this.offset = offset;
            fireOffsetChanged( oldOffset, offset );
        }
    }
    
    public double getOffset() {
        return offset;
    }
    
    public interface DielectricChangeListener extends BoxChangeListener {
        public void offsetChanged( double oldOffset, double newOffset );
    }
    
    public class DielectricChangeAdapter extends BoxChangeAdapter implements DielectricChangeListener {
        public void offsetChanged( double oldOffset, double newOffset ) {}
    }
    
    public void addDielectricChangeListener( DielectricChangeListener listener ) {
        listeners.add( DielectricChangeListener.class, listener );
    }
    
    public void removeDielectricChangeListener( DielectricChangeListener listener ) {
        listeners.remove( DielectricChangeListener.class, listener );
    }
    
    private void fireOffsetChanged( double oldOffset, double newOffset ) {
        for ( DielectricChangeListener listener : listeners.getListeners( DielectricChangeListener.class ) ) {
            listener.offsetChanged( oldOffset, newOffset );
        }
    }
}
