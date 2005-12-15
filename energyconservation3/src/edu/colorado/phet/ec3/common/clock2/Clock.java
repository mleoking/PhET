/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.common.clock2;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Dec 15, 2005
 * Time: 12:02:55 PM
 * Copyright (c) Dec 15, 2005 by Sam Reid
 */

public abstract class Clock implements IClock {
    private ArrayList listeners = new ArrayList();
    private TimeConverter timeConverter;
    private double lastSimulationTime = 0.0;
    private double simulationTime = 0.0;
    private long lastWallTime = 0;
    private long wallTime = 0;

    protected Clock() {
        this( new IdentityTimeConverter() );
    }

    public Clock( TimeConverter timeConverter ) {
        this.timeConverter = timeConverter;
    }

    public void addClockListener( ClockListener clockListener ) {
        listeners.add( clockListener );
    }

    public void removeClockListener( ClockListener clockListener ) {
        listeners.add( clockListener );
    }

    protected void notifyTicked() {
        ClockEvent clockEvent = new ClockEvent( this );
        for( int i = 0; i < listeners.size(); i++ ) {
            ClockListener clockListener = (ClockListener)listeners.get( i );
            clockListener.clockTicked( clockEvent );
        }
    }

    protected void notifyClockPaused() {
        ClockEvent clockEvent = new ClockEvent( this );
        for( int i = 0; i < listeners.size(); i++ ) {
            ClockListener clockListener = (ClockListener)listeners.get( i );
            clockListener.clockPaused( clockEvent );
        }
    }

    protected void notifyClockStarted() {
        ClockEvent clockEvent = new ClockEvent( this );
        for( int i = 0; i < listeners.size(); i++ ) {
            ClockListener clockListener = (ClockListener)listeners.get( i );
            clockListener.clockStarted( clockEvent );
        }
    }

    protected void notifyClockReset() {
        ClockEvent clockEvent = new ClockEvent( this );
        for( int i = 0; i < listeners.size(); i++ ) {
            ClockListener clockListener = (ClockListener)listeners.get( i );
            clockListener.clockReset( clockEvent );
        }
    }

    protected void notifySimulationTimeChanged() {
        ClockEvent clockEvent = new ClockEvent( this );
        for( int i = 0; i < listeners.size(); i++ ) {
            ClockListener clockListener = (ClockListener)listeners.get( i );
            clockListener.simulationTimeChanged( clockEvent );
        }
    }

    public TimeConverter getTimeConverter() {
        return timeConverter;
    }

    public void setTimeConverter( TimeConverter timeConverter ) {
        this.timeConverter = timeConverter;
    }

    public void resetSimulationTime() {
        setSimulationTime( 0.0 );
        notifyClockReset();
        testNotifySimulationTimeChange();
    }

    protected void doTick() {
        lastWallTime = wallTime;
        wallTime = System.currentTimeMillis();

        setSimulationTimeNoUpdate( simulationTime + timeConverter.getSimulationTimeChange( this ) );

        notifyTicked();
        testNotifySimulationTimeChange();
    }

    private void testNotifySimulationTimeChange() {
        if( getSimulationTimeChange() != 0.0 ) {
            notifySimulationTimeChanged();
        }
    }

    private void setSimulationTimeNoUpdate( double simulationTime ) {
        lastSimulationTime = this.simulationTime;
        this.simulationTime = simulationTime;
    }

    public long getWallTimeChangeMillis() {
        return wallTime - lastWallTime;
    }

    public double getSimulationTimeChange() {
        return simulationTime - lastSimulationTime;
    }

    public double getSimulationTime() {
        return simulationTime;
    }

    public long getWallTime() {
        return wallTime;
    }

    public void setSimulationTime( double simulationTime ) {
        setSimulationTimeNoUpdate( simulationTime );
        testNotifySimulationTimeChange();
    }

}
