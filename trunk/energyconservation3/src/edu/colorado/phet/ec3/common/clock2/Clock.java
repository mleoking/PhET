/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.common.clock2;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Dec 15, 2005
 * Time: 12:02:55 PM
 * Copyright (c) Dec 15, 2005 by Sam Reid
 */

public abstract class Clock {
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

    public void removeListener( ClockListener clockListener ) {
        listeners.add( clockListener );
    }

    protected void notifyTicked() {
        for( int i = 0; i < listeners.size(); i++ ) {
            ClockListener clockListener = (ClockListener)listeners.get( i );
            clockListener.clockTicked( this );
        }
    }

    protected void notifyClockPaused() {
        for( int i = 0; i < listeners.size(); i++ ) {
            ClockListener clockListener = (ClockListener)listeners.get( i );
            clockListener.clockPaused( this );
        }
    }

    protected void notifyClockStarted() {
        for( int i = 0; i < listeners.size(); i++ ) {
            ClockListener clockListener = (ClockListener)listeners.get( i );
            clockListener.clockStarted( this );
        }
    }

    protected void notifyClockReset() {
        for( int i = 0; i < listeners.size(); i++ ) {
            ClockListener clockListener = (ClockListener)listeners.get( i );
            clockListener.clockReset( this );
        }
    }

    protected void notifySimulationTimeChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            ClockListener clockListener = (ClockListener)listeners.get( i );
            clockListener.simulationTimeChanged( this );
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
        if( getSimulationTimeChange() != 0.0 ) {
            notifySimulationTimeChanged();
        }
    }

    protected void doTick() {
        lastWallTime = wallTime;
        wallTime = System.currentTimeMillis();

        setSimulationTime( simulationTime + timeConverter.getSimulationTimeChange( this ) );

        notifyTicked();
        if( getSimulationTimeChange() != 0.0 ) {
            notifySimulationTimeChanged();
        }
    }

    private void setSimulationTime( double simulationTime ) {
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
}
