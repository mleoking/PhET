/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import java.util.EventListener;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;

/**
 * Model for the "Acid-Base Solutions" simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ABSModel {

    private final Beaker beaker;
    private final MagnifyingGlass magnifyingGlass;
    private AqueousSolution solution;
    private boolean waterVisible; // water visibility, a global property, included in model for convenience
    
    private EventListenerList listeners;
    
    public ABSModel() {
        beaker = new Beaker();
        magnifyingGlass = new MagnifyingGlass();
        solution = new PureWaterSolution();
        waterVisible = ABSConstants.WATER_VISIBLE;
        listeners = new EventListenerList();
    }
    
    public Beaker getBeaker() {
        return beaker;
    }
    
    public MagnifyingGlass getMagnifyingGlass() {
        return magnifyingGlass;
    }
    
    public void setSolution( AqueousSolution solution ) {
        if ( solution != this.solution ) {  /* yes, referential equality */
            this.solution = solution;
            fireSolutionChanged();
        }
    }
    
    public AqueousSolution getSolution() {
        return solution;
    }
    
    public void setWaterVisible( boolean waterVisible ) {
        if ( waterVisible != this.waterVisible ) {
            this.waterVisible = waterVisible;
            fireWaterVisibleChanged();
        }
    }
    
    public boolean isWaterVisible() {
        return waterVisible;
    }
    
    public interface ModelChangeListener extends EventListener {
        public void solutionChanged();
        public void waterVisibleChanged();
    }
    
    public static class ModelChangeAdapter implements ModelChangeListener {
        public void solutionChanged() {}
        public void waterVisibleChanged() {}
    }
    
    public void addModelChangeListener( ModelChangeListener listener ) {
        listeners.add( ModelChangeListener.class, listener );
    }
    
    public void removeModelChangeListener( ModelChangeListener listener ) {
        listeners.remove( ModelChangeListener.class, listener );
    }
    
    private void fireSolutionChanged() {
        for ( ModelChangeListener listener : listeners.getListeners( ModelChangeListener.class ) ) {
            listener.solutionChanged();
        }
    }
    
    private void fireWaterVisibleChanged() {
        for ( ModelChangeListener listener : listeners.getListeners( ModelChangeListener.class ) ) {
            listener.waterVisibleChanged();
        }
    }
}
