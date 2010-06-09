/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import java.util.EventListener;

import javax.swing.event.EventListenerList;

/**
 * Model for the "Acid-Base Solutions" simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ABSModel {

    private final Beaker beaker;
    private final MagnifyingGlass magnifyingGlass;
    private AqueousSolution solution;
    private EventListenerList listeners;
    
    public ABSModel() {
        beaker = new Beaker();
        magnifyingGlass = new MagnifyingGlass();
        solution = new PureWaterSolution();
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
    
    public interface ModelListener extends EventListener {
        public void solutionChanged();
    }
    
    public void addModelListener( ModelListener listener ) {
        listeners.add(  ModelListener.class, listener );
    }
    
    public void removeModelListener( ModelListener listener ) {
        listeners.remove(  ModelListener.class, listener );
    }
    
    private void fireSolutionChanged() {
        for ( ModelListener listener : listeners.getListeners( ModelListener.class ) ) {
            listener.solutionChanged();
        }
    }
}
