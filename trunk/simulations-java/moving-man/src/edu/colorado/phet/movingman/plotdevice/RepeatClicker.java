/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.plotdevice;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * User: Sam Reid
 * Date: Apr 12, 2005
 * Time: 8:03:28 AM
 * Copyright (c) Apr 12, 2005 by Sam Reid
 */
public class RepeatClicker extends MouseAdapter {
    private ActionListener target;
    private ActionListener discrete;
    private int initDelay = 300;
    private int delay = 30;
    private Timer timer;
    private long pressTime;

    public RepeatClicker( ActionListener smooth, ActionListener discrete ) {
        this.target = smooth;
        this.discrete = discrete;
    }

    public void mouseClicked( MouseEvent e ) {
    }

    public void mousePressed( MouseEvent e ) {
        pressTime = System.currentTimeMillis();
        timer = new Timer( delay, target );
        timer.setInitialDelay( initDelay );
        timer.start();
    }

    public void mouseReleased( MouseEvent e ) {
        if( timer != null ) {
            timer.stop();
            long time = System.currentTimeMillis();
            if( time - pressTime < initDelay ) {
                discrete.actionPerformed( null );
            }
        }
    }
}
