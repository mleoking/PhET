/* Copyright 2010, University of Colorado */

package edu.colorado.phet.membranechannels.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;


/**
 * Strategy for controlling how open a membrane channel is.
 * 
 * @author John Blanco
 */
public abstract class MembraneChannelOpennessStrategy {

    protected MembraneChannelOpennessStrategy(ConstantDtClock clock) {
        // Listen to clock and step this strategy with each clock tick.
        clock.addClockListener( new ClockAdapter(){
            public void clockTicked( ClockEvent clockEvent ) {
                stepInTime( clockEvent.getSimulationTimeChange() );
            }
        });
    }

    protected MembraneChannelOpennessStrategy() {
        // Default constructor has no clock.  Thus, any subclass that uses
        // this constructor will not exhibit any time-dependent behavior.
    }

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
    
    protected void stepInTime( double dt ){
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
