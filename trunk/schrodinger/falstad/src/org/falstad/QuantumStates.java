/* Copyright 2004, Sam Reid */
package org.falstad;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * User: Sam Reid
 * Date: Feb 9, 2006
 * Time: 9:14:49 PM
 * Copyright (c) Feb 9, 2006 by Sam Reid
 */
public class QuantumStates extends Applet implements ComponentListener {
    QuantumStatesFrame qf;

    void destroyFrame() {
        if( qf != null ) {
            qf.dispose();
        }
        qf = null;
        repaint();
    }

    boolean started = false;

    public void init() {
        addComponentListener( this );
    }

    void showFrame() {
        if( qf == null ) {
            started = true;
            qf = new QuantumStatesFrame( this );
            qf.init();
            repaint();
        }
    }

    public void paint( Graphics g ) {
        String s = "Applet is open in a separate window.";
        if( !started ) {
            s = "Applet is starting.";
        }
        else if( qf == null ) {
            s = "Applet is finished.";
        }
        else {
            qf.show();
        }
        g.drawString( s, 10, 30 );
    }

    public void componentHidden( ComponentEvent e ) {
    }

    public void componentMoved( ComponentEvent e ) {
    }

    public void componentShown( ComponentEvent e ) {
        showFrame();
    }

    public void componentResized( ComponentEvent e ) {
    }

    public void destroy() {
        if( qf != null ) {
            qf.dispose();
        }
        qf = null;
        repaint();
    }
}
