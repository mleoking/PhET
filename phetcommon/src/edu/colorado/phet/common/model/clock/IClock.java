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

    long getWallTime();

    long getWallTimeChangeMillis();

    double getSimulationTimeChange();

    double getSimulationTime();

    void setSimulationTime( double simulationTime );

    void tickOnce();

    void setDelay( int delay );
}
