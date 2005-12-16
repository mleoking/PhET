/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.model.clock;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Dec 15, 2005
 * Time: 12:02:55 PM
 * Copyright (c) Dec 15, 2005 by Sam Reid
 */

public abstract class Clock implements AbstractClock {
    private ArrayList listeners = new ArrayList();
    private TimeConverter timeConverter;
    private double lastSimulationTime = 0.0;
    private double simulationTime = 0.0;
    private long lastWallTime = 0;
    private long wallTime = 0;
    private double tickOnceTimeChange;

    public Clock( TimeConverter timeConverter, double tickOnceTimeChange ) {
        this.timeConverter = timeConverter;
        this.tickOnceTimeChange = tickOnceTimeChange;
    }

    public void addClockListener( ClockListener clockListener ) {
        listeners.add( clockListener );
    }

    public void removeClockListener( ClockListener clockListener ) {
        listeners.remove( clockListener );
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

        notifyClockTicked();
        testNotifySimulationTimeChange();
    }

    public void tickOnce() {
        lastWallTime = wallTime;
        wallTime = System.currentTimeMillis();

        setSimulationTimeNoUpdate( simulationTime + tickOnceTimeChange );

        notifyClockTicked();
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

    public TimeConverter getTimeConverter() {
        return timeConverter;
    }

    public void setTimeConverter( TimeConverter timeConverter ) {
        this.timeConverter = timeConverter;
    }

    protected void notifyClockTicked() {
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

    public double getTickOnceTimeChange() {
        return tickOnceTimeChange;
    }

    public void setTickOnceTimeChange( double tickOnceTimeChange ) {
        this.tickOnceTimeChange = tickOnceTimeChange;
    }

    /**
     * @deprecated
     */
    public void setPaused( boolean b ) {
        if( b ) {
            pause();
        }
        else {
            start();
        }
    }

    /**
     * @deprecated
     */
    public void addClockTickListener( final ClockTickListener clockListener ) {
        addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                clockListener.clockTicked( new ClockTickEvent( Clock.this ) );
            }
        } );
    }
}