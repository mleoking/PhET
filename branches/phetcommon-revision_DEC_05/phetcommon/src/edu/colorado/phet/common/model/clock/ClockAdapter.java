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
 * Implements the ClockListener interface with empty implementations for convenience.
 *
 * @author Sam Reid
 */

public class ClockAdapter implements ClockListener {

    /**
     * Invoked when the clock ticks.
     *
     * @param clockEvent
     */
    public void clockTicked( ClockEvent clockEvent ) {
    }

    /**
     * Invoked when the clock starts.
     *
     * @param clockEvent
     */
    public void clockStarted( ClockEvent clockEvent ) {
    }

    /**
     * Invoked when the clock is paused.
     *
     * @param clockEvent
     */
    public void clockPaused( ClockEvent clockEvent ) {
    }

    /**
     * Invoked when the running time of the simulation has changed.
     *
     * @param clockEvent
     */
    public void simulationTimeChanged( ClockEvent clockEvent ) {
    }

    /**
     * Invoked when the clock's simulation time is reset.
     *
     * @param clockEvent
     */
    public void simulationTimeReset( ClockEvent clockEvent ) {
    }
}