package org.falstad;// QuantumCirc.java (C) 2001 by Paul Falstad, www.falstad.com

import java.applet.Applet;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class QuantumCirc extends Applet implements ComponentListener {
    QuantumCircFrame ogf;

    void destroyFrame() {
        if( ogf != null ) {
            ogf.dispose();
        }
        ogf = null;
        repaint();
    }

    boolean started = false;

    public void init() {
        addComponentListener( this );
    }

    void showFrame() {
        if( ogf == null ) {
            started = true;
            ogf = new QuantumCircFrame( this );
            ogf.init();
            repaint();
        }
    }

    public void paint( Graphics g ) {
        String s = "Applet is open in a separate window.";
        if( !started ) {
            s = "Applet is starting.";
        }
        else if( ogf == null ) {
            s = "Applet is finished.";
        }
        else {
            ogf.show();
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
        if( ogf != null ) {
            ogf.dispose();
        }
        ogf = null;
        repaint();
    }
};

;
