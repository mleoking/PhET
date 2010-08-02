/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import java.awt.geom.Point2D;
import java.util.EventListener;

import javax.swing.event.EventListenerList;

/**
 * Base class for things that provide some representation of a solution.
 * These model elements have an associated solution, a physical location and visibility.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class SolutionRepresentation {

    private final EventListenerList listeners;
    
    // mutable properties
    private AqueousSolution solution;
    private Point2D location;
    private boolean visible;
    
    public SolutionRepresentation( AqueousSolution solution, Point2D location, boolean visible ) {
        this.solution = solution;
        this.location = new Point2D.Double( location.getX(), location.getY() );
        this.visible = visible;
        listeners = new EventListenerList();
    }
    
    public void setSolution( AqueousSolution solution ) {
        if ( solution != this.solution ) {
            this.solution = solution;
            fireSolutionChanged();
        }
    }
    
    public AqueousSolution getSolution() {
        return solution;
    }
    
    public void setLocation( Point2D location ) {
        setLocation( location.getX(), location.getY() );
    }
    
    public void setLocation( double x, double y ) {
        if ( x != location.getX() || y != location.getY() ) {
            this.location.setLocation( x, y );
            fireLocationChanged();
        }
    }
    
    public Point2D getLocationReference() {
        return location;
    }
    
    public double getX() {
        return location.getX();
    }
    
    public double getY() {
        return location.getY();
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
    
    public interface SolutionRepresentationChangeListener extends EventListener {
        public void solutionChanged();
        public void locationChanged();
        public void visibilityChanged();
    }
    
    public static class SolutionRepresentationChangeAdapter implements SolutionRepresentationChangeListener {
        public void solutionChanged() {}
        public void locationChanged() {}
        public void visibilityChanged() {}
    }
    
    public void addModelElementChangeListener( SolutionRepresentationChangeListener listener ) {
        listeners.add( SolutionRepresentationChangeListener.class, listener );
    }
    
    public void removeModelElementChangeListener( SolutionRepresentationChangeListener listener ) {
        listeners.remove( SolutionRepresentationChangeListener.class, listener );
    }
    
    private void fireSolutionChanged() {
        for ( SolutionRepresentationChangeListener listener : listeners.getListeners( SolutionRepresentationChangeListener.class ) ) {
            listener.solutionChanged();
        }
    }
    
    private void fireLocationChanged() {
        for ( SolutionRepresentationChangeListener listener : listeners.getListeners( SolutionRepresentationChangeListener.class ) ) {
            listener.locationChanged();
        }
    }
    
    private void fireVisibilityChanged() {
        for ( SolutionRepresentationChangeListener listener : listeners.getListeners( SolutionRepresentationChangeListener.class ) ) {
            listener.visibilityChanged();
        }
    }
}
