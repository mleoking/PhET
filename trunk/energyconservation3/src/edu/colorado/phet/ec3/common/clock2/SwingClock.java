/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.common.clock2;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Dec 15, 2005
 * Time: 12:06:10 PM
 * Copyright (c) Dec 15, 2005 by Sam Reid
 */

public class SwingClock extends Clock {
    private Timer timer;

    public SwingClock( int delay, TimeConverter timeConverter ) {
        super( timeConverter );
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

}
