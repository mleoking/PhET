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
 * User: Sam Reid
 * Date: Dec 15, 2005
 * Time: 12:06:10 PM
 * Copyright (c) Dec 15, 2005 by Sam Reid
 */

public class SwingTimerClock extends Clock {
    private Timer timer;

    /**
     * @deprecated
     */
    public SwingTimerClock( double timeStep, int delay ) {
        this( timeStep, delay, true );
    }

    /**
     * @deprecated
     */
    public SwingTimerClock( double timeStep, int delay, boolean isFixed ) {
        this( delay, new TimeConverter.Constant( timeStep ), timeStep );
    }

    public SwingTimerClock( int delay, TimeConverter timeConverter, double tickOnceTimeChange ) {
        super( timeConverter, tickOnceTimeChange );
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                doTick();
            }
        };
        timer = new Timer( delay, actionListener );
    }


    public void start() {
        timer.start();
        super.notifyClockStarted();
    }

    public void pause() {
        timer.stop();
        super.notifyClockPaused();
    }

    public boolean isPaused() {
        return !timer.isRunning();
    }

    public boolean isRunning() {
        return timer.isRunning();
    }

    public void setDelay( int delay ) {
        timer.setDelay( delay );
    }

}