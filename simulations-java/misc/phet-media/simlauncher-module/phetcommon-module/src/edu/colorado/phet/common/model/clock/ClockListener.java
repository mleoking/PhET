/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/phetcommon/src/edu/colorado/phet/common/model/clock/ClockListener.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.4 $
 * Date modified : $Date: 2006/01/04 22:00:24 $
 */
package edu.colorado.phet.common.model.clock;

import java.util.EventListener;

/**
 * Listens to changes in both Clock state (running, paused)
 * and clock ticks  (in both wall time and simulation time).
 */

public interface ClockListener extends EventListener {
    /**
     * Invoked when the clock ticks.
     *
     * @param clockEvent
     */
    void clockTicked( ClockEvent clockEvent );

    /**
     * Invoked when the clock starts.
     *
     * @param clockEvent
     */
    void clockStarted( ClockEvent clockEvent );

    /**
     * Invoked when the clock is paused.
     *
     * @param clockEvent
     */
    void clockPaused( ClockEvent clockEvent );

    /**
     * Invoked when the running time of the simulation has changed.
     *
     * @param clockEvent
     */
    void simulationTimeChanged( ClockEvent clockEvent );

    /**
     * Invoked when the clock's simulation time is reset.
     *
     * @param clockEvent
     */
    void simulationTimeReset( ClockEvent clockEvent );
}