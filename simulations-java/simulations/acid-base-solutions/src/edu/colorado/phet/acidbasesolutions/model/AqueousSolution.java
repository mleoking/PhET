package edu.colorado.phet.acidbasesolutions.model;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Base class for all aqueous solutions.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AqueousSolution {

    private final Water water;
    private double initialConcentration;
    private final ArrayList<SolutionListener> listeners;
    
    protected AqueousSolution( double initialConcentration ) {
        this.water = new Water();
        this.initialConcentration = initialConcentration;
        this.listeners = new ArrayList<SolutionListener>();
    }
    
    public Water getWater() {
        return water;
    }
    
    // c
    public void setInitialConcentration( double initialConcentration ) {
        if ( initialConcentration != this.initialConcentration ) {
            this.initialConcentration = initialConcentration;
            notifyConcentrationChanged();
        }
    }
    
    // c
    public double getInitialConcentration() {
        return initialConcentration;
    }
    
    public interface SolutionListener {
        public void concentrationChanged();
    }
    
    public void addSolutionListener( SolutionListener listener ) {
        listeners.add( listener );
    }
    
    public void removeSolutionListener( SolutionListener listener ) {
        listeners.remove( listener );
    }
    
    private void notifyConcentrationChanged() {
        Iterator<SolutionListener> i = listeners.iterator();
        while ( i.hasNext() ) {
            i.next().concentrationChanged();
        }
    }
}
