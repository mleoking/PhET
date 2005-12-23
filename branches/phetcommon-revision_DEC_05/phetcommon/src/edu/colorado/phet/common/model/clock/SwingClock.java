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

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This extension of Clock uses a Swing Timer for tick notification.
 */

public class SwingClock extends Clock {
    private Timer timer;

    /**
     * Construct a SwingClock with specified wall time delay and constant simulation time change per tick.
     *
     * @param delay
     * @param constantTimeChange
     */
    public SwingClock( int delay, double constantTimeChange ) {
        this( delay, new TimeConverter.Constant( constantTimeChange ), constantTimeChange );
    }

    /**
     * Construct a SwingClock with a specified wall time delay, means of converting wall to simulation time,
     * and a value for the simulation time change in tickOnceTimeChange.
     *
     * @param delay
     * @param timeConverter
     * @param tickOnceTimeChange
     */
    public SwingClock( int delay, TimeConverter timeConverter, double tickOnceTimeChange ) {
        super( timeConverter, tickOnceTimeChange );
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( !isPaused() ) {
                    doTick();
                }
            }
        };
        timer = new Timer( delay, actionListener );
    }

    /**
     * Starts the Clock.
     */
    public void start() {
        if( isPaused() ) {
            timer.start();
            super.notifyClockStarted();
        }
    }

    /**
     * Stops the Clock.
     */
    public void pause() {
        if( isRunning() ) {
            timer.stop();
            super.notifyClockPaused();
        }
    }

    /**
     * Get whether the clock is paused.
     *
     * @return whether the clock is paused.
     */
    public boolean isPaused() {
        return !timer.isRunning();
    }

    /**
     * Get whether the clock is running.
     *
     * @return whether the clock is running.
     */
    public boolean isRunning() {
        return timer.isRunning();
    }

    /**
     * Sets the requested wall time delay between ticks.
     *
     * @param delay
     */
    public void setDelay( int delay ) {
        timer.setDelay( delay );
    }

    /**
     * Gets the requested wall time delay between ticks.
     *
     * @return the delay
     */
    public int getDelay() {
        return timer.getDelay();
    }

    /**
     * Gets the Swing Timer used to generate ticks.
     *
     * @return the Swing Timer used to generate ticks.
     */
    protected Timer getTimer() {
        return timer;
    }
}