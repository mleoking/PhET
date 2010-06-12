/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import java.awt.geom.Point2D;
import java.util.EventListener;

import javax.swing.event.EventListenerList;

/**
 * Base class for things that have a physical location and visibility.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class ABSModelElement {

    private final EventListenerList listeners;
    private Point2D location;
    private boolean visible;
    
    public ABSModelElement( Point2D location, boolean visible ) {
        this.location = new Point2D.Double( location.getX(), location.getY() );
        this.visible = visible;
        listeners = new EventListenerList();
    }
    
    public void setLocation( Point2D location ) {
        if ( !location.equals( this.location ) ) {
            this.location.setLocation( location );
            fireLocationChanged();
        }
    }
    
    public Point2D getLocationReference() {
        return location;
    }
    
    public void setVisible( boolean visible ) {
        if ( visible != this.visible ) {
            this.visible = visible;
            fireVisibilityChanged();
        }
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public interface ModelElementChangeListener extends EventListener {
        public void locationChanged();
        public void visibilityChanged();
    }
    
    public static class ModelElementChangeAdapter implements ModelElementChangeListener {
        public void locationChanged() {}
        public void visibilityChanged() {}
    }
    
    public void addModelElementChangeListener( ModelElementChangeListener listener ) {
        listeners.add( ModelElementChangeListener.class, listener );
    }
    
    public void removeModelElementChangeListener( ModelElementChangeListener listener ) {
        listeners.remove( ModelElementChangeListener.class, listener );
    }
    
    private void fireLocationChanged() {
        for ( ModelElementChangeListener listener : listeners.getListeners( ModelElementChangeListener.class ) ) {
            listener.locationChanged();
        }
    }
    
    private void fireVisibilityChanged() {
        for ( ModelElementChangeListener listener : listeners.getListeners( ModelElementChangeListener.class ) ) {
            listener.visibilityChanged();
        }
    }
}
