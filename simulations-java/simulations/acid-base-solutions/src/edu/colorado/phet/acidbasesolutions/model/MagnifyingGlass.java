/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import java.awt.geom.Point2D;
import java.util.EventListener;

import javax.swing.event.EventListenerList;

/**
 * Model of a magnifying glass.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MagnifyingGlass extends SolutionRepresentation {
    
    private final double diameter;
    private boolean waterVisibile;
    private final EventListenerList listeners;
    
    public MagnifyingGlass( AqueousSolution solution, Point2D location, boolean visible, double diameter, boolean waterVisible ) {
        super( solution, location, visible );
        this.diameter = diameter;
        this.waterVisibile = waterVisible;
        listeners = new EventListenerList();
    }
    
    public double getDiameter() {
        return diameter;
    }
    
    public void setWaterVisible( boolean waterVisible ) {
        if ( waterVisible != this.waterVisibile ) {
            this.waterVisibile = waterVisible;
            fireWaterVisibleChanged();
        }
    }
    
    public boolean isWaterVisible() {
        return waterVisibile;
    }
    
    public interface MagnifyingGlassChangeListener extends EventListener {
        public void waterVisibleChanged();
    }
    
    public void addMagnifyingGlassListener( MagnifyingGlassChangeListener listener ) {
        listeners.add( MagnifyingGlassChangeListener.class, listener );
    }
    
    public void removeMagnifyingGlassListener( MagnifyingGlassChangeListener listener ) {
        listeners.remove( MagnifyingGlassChangeListener.class, listener );
    }
    
    private void fireWaterVisibleChanged() {
        for ( MagnifyingGlassChangeListener listener : listeners.getListeners( MagnifyingGlassChangeListener.class ) ) {
            listener.waterVisibleChanged();
        }
    }
}
