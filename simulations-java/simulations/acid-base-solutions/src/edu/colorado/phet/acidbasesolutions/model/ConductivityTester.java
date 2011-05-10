// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions.model;

import java.awt.geom.Point2D;
import java.util.EventListener;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.view.IConductivityTester;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Model for conductivity testing device.
 * The conductivity tester has 2 probes that can be moved independently.
 * Origin is at top center.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConductivityTester extends SolutionRepresentation implements IConductivityTester{
    
    private static final double NEUTRAL_PH = 7.0;
    private static final double NEUTRAL_BRIGHTNESS = 0.05; // brightness when pH == NEUTRAL_PH
    
    private final Beaker beaker;
    private final PDimension probeSize;
    private Point2D positiveProbeLocation, negativeProbeLocation;
    private final EventListenerList listeners;
    private double brightness;

    public ConductivityTester( AqueousSolution solution, Point2D location, boolean visible, PDimension probeSize, Point2D positiveProbeLocation, Point2D negativeProbeLocation, Beaker beaker ) {
        super( solution, location, visible );
        this.probeSize = new PDimension( probeSize );
        this.positiveProbeLocation = new Point2D.Double( positiveProbeLocation.getX(), positiveProbeLocation.getY() );
        this.negativeProbeLocation = new Point2D.Double( negativeProbeLocation.getX(), negativeProbeLocation.getY() );
        this.beaker = beaker;
        this.listeners = new EventListenerList();
        
        addSolutionRepresentationChangeListener( new SolutionRepresentationChangeAdapter() {
            
            @Override
            public void solutionChanged() {
                updateBrightness();
            }
            
            @Override
            public void concentrationChanged() {
                updateBrightness();
            }
            
            @Override
            public void strengthChanged() {
                updateBrightness();
            }
        });
        
        updateBrightness();
    }
    
    /**
     * Gets the value, a number between 0 and 1 inclusive.
     * 0 indicates no conductivity (open circuit).
     * 1 indicates maximum conductivity that the device can display. 
     * @return
     */
    public double getBrightness() {
        return brightness;
    }
    
    private void updateBrightness() {
        if ( isCircuitCompleted() ) {
            brightness = pHToBrightness( getSolution().getPH() );
        }
        else {
            brightness = 0; // open circuit
        }
        fireBrightnessChanged();
    }
    
    /*
     * This is the primary model portion of this class.
     * Converts pH to a brightness value for a "closed" circuit.
     */
    private double pHToBrightness( double pH ) {
        double brightness = 0;
        if ( pH < NEUTRAL_PH ) {
            brightness = NEUTRAL_BRIGHTNESS + ( ( 1 - NEUTRAL_BRIGHTNESS ) * ( ( NEUTRAL_PH - pH ) / ( NEUTRAL_PH - ABSConstants.MIN_PH ) ) );
        }
        else {
            brightness = NEUTRAL_BRIGHTNESS + ( ( 1 - NEUTRAL_BRIGHTNESS ) * ( ( pH - NEUTRAL_PH ) / ( ABSConstants.MAX_PH - NEUTRAL_PH ) ) );
        }
        return brightness;
    }
    
    public PDimension getProbeSizeReference() {
        return probeSize;
    }
    
    public void setPositiveProbeLocation( Point2D p ) {
        setPositiveProbeLocation( p.getX(), p.getY() );
    }
    
    public void setPositiveProbeLocation( double x, double y ) {
        if ( x != positiveProbeLocation.getX() || y != positiveProbeLocation.getY() ) {
            positiveProbeLocation.setLocation( x, constrainY( y ) );
            firePositiveProbeLocationChanged();
            updateBrightness();
        }
    }
    
    public Point2D getPositiveProbeLocationReference() {
        return positiveProbeLocation;
    }
    
    public void setNegativeProbeLocation( Point2D p ) {
        setNegativeProbeLocation( p.getX(), p.getY() );
    }
    
    public void setNegativeProbeLocation( double x, double y ) {
        if ( x != negativeProbeLocation.getX() || y != negativeProbeLocation.getY() ) {
            negativeProbeLocation.setLocation( x, constrainY( y ) );
            fireNegativeProbeLocationChanged();
            updateBrightness();
        }
    }
    
    public Point2D getNegativeProbeLocationReference() {
        return negativeProbeLocation;
    }
    
    private boolean isCircuitCompleted() {
        return ( beaker.inSolution( positiveProbeLocation ) && beaker.inSolution( negativeProbeLocation ) );
    }
    
    /*
     * Constraints a y coordinate to be in or slightly above the solution.
     */
    private double constrainY( double requestedY ) {
        double min = beaker.getLocationReference().getY() - beaker.getHeight() - 50;
        double max = beaker.getLocationReference().getY() - 20;
        double y = requestedY;
        if ( y < min ) {
            y = min;
        }
        else if ( y > max ) {
            y = max;
        }
        return y;
    }
    
    public interface ConductivityTesterChangeListener extends EventListener {
        public void brightnessChanged();
        public void positiveProbeLocationChanged();
        public void negativeProbeLocationChanged();
    }
    
    public static class ConductivityTesterChangeAdapter implements ConductivityTesterChangeListener {
        public void brightnessChanged() {}
        public void positiveProbeLocationChanged() {}
        public void negativeProbeLocationChanged() {}
    }

    public void addConductivityTesterChangeListener( ConductivityTesterChangeListener listener ) {
        listeners.add( ConductivityTesterChangeListener.class, listener );
    }
    
    public void removeConductivityTesterChangeListener( ConductivityTesterChangeListener listener ) {
        listeners.remove( ConductivityTesterChangeListener.class, listener );
    }
    
    private void fireBrightnessChanged() {
        for ( ConductivityTesterChangeListener listener : listeners.getListeners( ConductivityTesterChangeListener.class ) ) {
            listener.brightnessChanged();
        }
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
