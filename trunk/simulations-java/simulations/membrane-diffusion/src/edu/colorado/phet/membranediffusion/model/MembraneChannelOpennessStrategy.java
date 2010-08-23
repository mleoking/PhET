/* Copyright 2010, University of Colorado */

package edu.colorado.phet.membranediffusion.model;

import java.util.ArrayList;


/**
 * Strategy for controlling how open a membrane channel is.
 * 
 * @author John Blanco
 */
public abstract class MembraneChannelOpennessStrategy {

    // Openness value.
    double openness = 0;
    
    // Array of listeners.
    private ArrayList<Listener> listeners = new ArrayList<Listener>();
    
    /**
     * Get the openness.
     * 
     * @return - a value from 0 (completely closed) to 1 (completely open).
     */
    public double getOpenness(){
        return openness;
    }
    
    protected void setOpenness(double newOpenness){
        assert openness >= 0 && openness <= 1;
        if (openness != newOpenness){
            openness = newOpenness;
            notifyOpennessChanged();
        }
    }
    
    public void stepInTime( double dt ){
        // Does nothing by default.  Descendant classes should implement any
        // time-dependent behavior.
    }
    
    public void addListener(Listener listener){
        listeners.add(listener);
    }
    
    public void removeListener(Listener listener){
        listeners.remove(listener);
    }
    
    protected void notifyOpennessChanged(){
        for (Listener listener : listeners){
            listener.opennessChanged();
        }
    }

    public interface Listener {
        void opennessChanged();
    }
}
