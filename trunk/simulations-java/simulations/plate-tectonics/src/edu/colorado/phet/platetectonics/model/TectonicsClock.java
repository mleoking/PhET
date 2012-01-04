// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;

public class TectonicsClock implements IClock {
    private double lastSimulationTime = 0.0;
    private double simulationTime = 0.0;
    private long lastWallTime = 0;
    private long wallTime = 0;

    private double timeMultiplier;
    private final List<ClockListener> listeners = new ArrayList<ClockListener>();
    private final Property<Boolean> running = new Property<Boolean>( false );

    public TectonicsClock( double timeMultiplier ) {
        this.timeMultiplier = timeMultiplier;

        running.addObserver( new ChangeObserver<Boolean>() {
            @Override public void update( Boolean runningNow, Boolean wasRunning ) {
                ClockEvent event = new ClockEvent( TectonicsClock.this );
                if ( runningNow ) {
                    for ( ClockListener listener : listeners ) {
                        listener.clockStarted( event );
                    }
                }
                else {
                    for ( ClockListener listener : listeners ) {
                        listener.clockPaused( event );
                    }
                }
            }
        } );
    }

    public synchronized void stepByWallSeconds( float timeElapsed ) {
        if ( running.get() ) {
            tick( timeElapsed );
        }
    }

    protected synchronized void tick( float timeElapsed ) {
        lastWallTime = wallTime;
        wallTime = System.currentTimeMillis();

        setSimulationTime( simulationTime + timeElapsed * timeMultiplier );

        ClockEvent event = new ClockEvent( this );
        for ( ClockListener listener : listeners ) {
            listener.clockTicked( event );
        }
    }

    @Override public synchronized void start() {
        running.set( true );
    }

    @Override public synchronized void pause() {
        running.set( false );
    }

    @Override public synchronized boolean isPaused() {
        return !running.get();
    }

    @Override public synchronized boolean isRunning() {
        return running.get();
    }

    @Override public synchronized void addClockListener( ClockListener clockListener ) {
        listeners.add( clockListener );
    }

    @Override public synchronized void removeClockListener( ClockListener clockListener ) {
        listeners.remove( clockListener );
    }

    @Override public synchronized void resetSimulationTime() {
        setSimulationTime( 0 );

        ClockEvent event = new ClockEvent( this );
        for ( ClockListener listener : listeners ) {
            listener.simulationTimeReset( event );
        }
    }

    @Override public synchronized long getWallTime() {
        return wallTime;
    }

    @Override public synchronized long getWallTimeChange() {
        return wallTime - lastWallTime;
    }

    @Override public synchronized double getSimulationTimeChange() {
        return simulationTime - lastSimulationTime;
    }

    @Override public synchronized double getSimulationTime() {
        return simulationTime;
    }

    @Override public synchronized void setSimulationTime( double simulationTime ) {
        lastSimulationTime = this.simulationTime;
        this.simulationTime = simulationTime;

        ClockEvent event = new ClockEvent( this );
        for ( ClockListener listener : listeners ) {
            listener.simulationTimeChanged( event );
        }
    }

    @Override public synchronized void stepClockWhilePaused() {
        // TODO: updating here isn't fully working (nicely). investigate
        tick( 5 );
    }

    @Override public synchronized void stepClockBackWhilePaused() {
        tick( 5 );
    }

    @Override public synchronized boolean containsClockListener( ClockListener clockListener ) {
        return listeners.contains( clockListener );
    }

    public synchronized double getTimeMultiplier() {
        return timeMultiplier;
    }

    public synchronized void setTimeMultiplier( double timeMultiplier ) {
        this.timeMultiplier = timeMultiplier;
    }
}
