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
     * Constructs a SwingClock with specified wall time between ticks 
     * and constant simulation time change per tick.
     *
     * @param delay      time between clock ticks, in wall time
     * @param dt         time per clock tick, in simulation time
     */
    public SwingClock( int delay, double dt ) {
        this( delay, dt, new TimeConverter.Constant( dt ) );
    }

    /**
     * Constructs a SwingClock with a specified wall time between clock ticks, 
     * a value for the simulation time change used by method <code>tickOnce</code>,
     * and a means for calculating the simulation time change that occurs when the clock ticks.
     * <p>
     * NOTE! The tickOnceTimeChange value is used only when tickOnce is called
     * (for example, when the simulation is paused and the user presses the Step
     * button).  When the clock is running normally, the timeConverter is responsible
     * for calculating the simulation time change (aka, dt).
     *
     * @param delay     time between clock ticks, in wall time
     * @param tickOnceTimeChange  simulation time change when tickOnce is called
     * @param timeConverter calculates simulation time change based on wall time change
     */
    public SwingClock( int delay, double tickOnceTimeChange, TimeConverter timeConverter ) {
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
     * Pauses the Clock.
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