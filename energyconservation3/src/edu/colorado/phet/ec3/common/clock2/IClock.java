package edu.colorado.phet.ec3.common.clock2;

/**
 * User: Sam Reid
 * Date: Dec 15, 2005
 * Time: 1:28:36 PM
 * Copyright (c) Dec 15, 2005 by Sam Reid
 */
public interface IClock {
    void start();

    void pause();

    boolean isPaused();

    boolean isRunning();

    void addClockListener( ClockListener clockListener );

    void removeClockListener( ClockListener clockListener );

    void resetSimulationTime();

    long getWallTimeChangeMillis();

    double getSimulationTimeChange();

    double getSimulationTime();

    long getWallTime();

    void setSimulationTime( double simulationTime );
}
