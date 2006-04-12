/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * User: Sam Reid
 * Date: Mar 14, 2006
 * Time: 6:17:12 PM
 * Copyright (c) Mar 14, 2006 by Sam Reid
 */

public class ComponentCenterer {
    private JComponent component;
    private Container parent;
//    private Timer timer;

    public ComponentCenterer( final JComponent component, final JComponent parent ) {
        this.component = component;
        this.parent = parent;
        parent.addComponentListener( new ComponentListener() {
            public void componentHidden( ComponentEvent e ) {
            }

            public void componentMoved( ComponentEvent e ) {
            }

            public void componentResized( ComponentEvent e ) {
                updateLocation();
            }

            public void componentShown( ComponentEvent e ) {
            }
        } );
//        timer = new Timer( 100,new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                updateLocation();
//            }
//        } );
    }

    private void updateLocation() {
        component.setLocation( parent.getWidth() / 2 - component.getWidth() / 2, component.getY() );
    }

//    public void start() {
//        timer.start();
//    }

    public void start() {
    }
}

