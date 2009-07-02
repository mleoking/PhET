/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.model;

import java.util.LinkedList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;

/**
 * GlaciersClock is the clock for this simulation.
 * The simulation time change (dt) on each clock tick is constant,
 * regardless of when (in wall time) the ticks actually happen.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NaturalSelectionClock extends ConstantDtClock {
    private List<Listener> physicalToAdd = new LinkedList<Listener>();
    private List<Listener> timeToAdd = new LinkedList<Listener>();
    private List<Listener> physicalToRemove = new LinkedList<Listener>();
    private List<Listener> timeToRemove = new LinkedList<Listener>();
    private List<Listener> physicalListeners = new LinkedList<Listener>();
    private List<Listener> timeListeners = new LinkedList<Listener>();
    private boolean notifying = false;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public NaturalSelectionClock( int framesPerSecond, double dt ) {
        super( 1000 / framesPerSecond, dt );
    }

    //----------------------------------------------------------------------------
    // Superclass overrides
    //----------------------------------------------------------------------------

    /**
     * Reset the clock when dt is changed.
     *
     * @param dt
     */
    public void setDt( double dt ) {
        super.setDt( dt );
        resetSimulationTime();
    }

    @Override
    public void stepClockWhilePaused() {
        for ( int i = 0; i < NaturalSelectionConstants.getSettings().getTicksPerYear() / 4; i++ ) {
            super.stepClockWhilePaused();
        }
    }

    public void addPhysicalListener( Listener listener ) {
        if ( notifying ) {
            physicalToAdd.add( listener );
        }
        else {
            physicalListeners.add( listener );
        }
    }

    public void removePhysicalListener( Listener listener ) {
        if ( notifying ) {
            physicalToRemove.add( listener );
        }
        else {
            physicalListeners.remove( listener );
        }
    }

    public void addTimeListener( Listener listener ) {
        if ( notifying ) {
            timeToAdd.add( listener );
        }
        else {
            timeListeners.add( listener );
        }
    }

    public void removeTimeListener( Listener listener ) {
        if ( notifying ) {
            timeToRemove.add( listener );
        }
        else {
            timeListeners.remove( listener );
        }
    }

    public void notifyPhysicalListeners( ClockEvent event ) {
        notifying = true;
        for ( Listener listener : physicalListeners ) {
            listener.onTick( event );
        }
        notifying = false;
        checkAfterNotify();
    }

    public void notifyTimeListeners( ClockEvent event ) {
        notifying = true;
        for ( Listener listener : timeListeners ) {
            listener.onTick( event );
        }
        notifying = false;
        checkAfterNotify();
    }

    public void checkAfterNotify() {
        if ( !physicalToAdd.isEmpty() ) {
            for ( Listener listener : physicalToAdd ) {
                physicalListeners.add( listener );
            }
            physicalToAdd.clear();
        }
        if ( !physicalToRemove.isEmpty() ) {
            for ( Listener listener : physicalToRemove ) {
                physicalListeners.remove( listener );
            }
            physicalToRemove.clear();
        }

        if ( !timeToAdd.isEmpty() ) {
            for ( Listener listener : timeToAdd ) {
                timeListeners.add( listener );
            }
            timeToAdd.clear();
        }
        if ( !timeToRemove.isEmpty() ) {
            for ( Listener listener : timeToRemove ) {
                timeListeners.remove( listener );
            }
            timeToRemove.clear();
        }
    }

    public static interface Listener {
        public void onTick( ClockEvent event );
    }
}
