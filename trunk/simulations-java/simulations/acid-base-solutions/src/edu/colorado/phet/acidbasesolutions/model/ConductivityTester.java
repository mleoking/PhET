/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import java.awt.geom.Point2D;
import java.util.EventListener;

import javax.swing.event.EventListenerList;

import edu.umd.cs.piccolo.util.PDimension;

/**
 * Model for conductivity testing device.
 * The conductivity tester has 2 probes that can be moved independently.
 * Origin is at top center.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConductivityTester extends SolutionRepresentation {
    
    private final Beaker beaker;
    private final PDimension probeSize;
    private Point2D positiveProbeLocation, negativeProbeLocation;
    private final EventListenerList listeners;

    public ConductivityTester( AqueousSolution solution, Point2D location, boolean visible, PDimension probeSize, Point2D positiveProbeLocation, Point2D negativeProbeLocation, Beaker beaker ) {
        super( solution, location, visible );
        this.probeSize = new PDimension( probeSize );
        this.positiveProbeLocation = new Point2D.Double( positiveProbeLocation.getX(), positiveProbeLocation.getY() );
        this.negativeProbeLocation = new Point2D.Double( negativeProbeLocation.getX(), negativeProbeLocation.getY() );
        this.beaker = beaker;
        this.listeners = new EventListenerList();
    }
    
    public PDimension getProbeSizeReference() {
        return probeSize;
    }
    
    public void setPositiveProbeLocation( Point2D p ) {
        if ( !p.equals( positiveProbeLocation ) ) {
            positiveProbeLocation.setLocation( p.getX(), constrainY( p.getY() ) );
            firePositiveProbeLocationChanged();
        }
    }
    
    public Point2D getPositiveProbeLocationReference() {
        return positiveProbeLocation;
    }
    
    public void setNegativeProbeLocation( Point2D p ) {
        if ( !p.equals( negativeProbeLocation ) ) {
            negativeProbeLocation.setLocation( p.getX(), constrainY( p.getY() ) );
            fireNegativeProbeLocationChanged();
        }
    }
    
    public Point2D getNegativeProbeLocationReference() {
        return negativeProbeLocation;
    }
    
    /*
     * Constraints a y coordinate to be in or slightly above the solution.
     */
    private double constrainY( double requestedY ) {
        double min = beaker.getLocationReference().getY() - beaker.getHeight() - probeSize.getHeight() - 20;
        double max = beaker.getLocationReference().getY() - probeSize.getHeight() - 20;
        double y = requestedY;
        if ( y < min ) {
            y = min;
        }
        else if ( y > max ) {
            y = max;
        }
        return y;
    }
    
    /**
     * Gets the value, a number between 0 and 1 inclusive.
     * 0 indicates no conductivity (open circuit).
     * 1 indicates maximum conductivity that the device can display. 
     * @return
     */
    public double getValue() {
        double value = 0;
        if ( beaker.inSolution( positiveProbeLocation ) && beaker.inSolution( negativeProbeLocation ) ) {
            value = 1; //TODO value should be a function of pH
        }
        return value;
    }
    
    public interface ConductivityTesterChangeListener extends EventListener {
        public void positiveProbeLocationChanged();
        public void negativeProbeLocationChanged();
    }
    
    public static class ConductivityTesterChangeAdapter implements ConductivityTesterChangeListener {
        public void positiveProbeLocationChanged() {}
        public void negativeProbeLocationChanged() {}
    }
    
    public void addConductivityTesterChangeListener( ConductivityTesterChangeListener listener ) {
        listeners.add( ConductivityTesterChangeListener.class, listener );
    }
    
    public void removeConductivityTesterChangeListener( ConductivityTesterChangeListener listener ) {
        listeners.remove( ConductivityTesterChangeListener.class, listener );
    }
    
    private void firePositiveProbeLocationChanged() {
        for ( ConductivityTesterChangeListener listener : listeners.getListeners( ConductivityTesterChangeListener.class ) ) {
            listener.positiveProbeLocationChanged();
        }
    }
    
    private void fireNegativeProbeLocationChanged() {
        for ( ConductivityTesterChangeListener listener : listeners.getListeners( ConductivityTesterChangeListener.class ) ) {
            listener.negativeProbeLocationChanged();
        }
    }
}
