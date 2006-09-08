/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view.plot;

import edu.colorado.phet.common.math.MathUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Aug 8, 2005
 * Time: 11:23:40 PM
 * Copyright (c) Aug 8, 2005 by Sam Reid
 */

public class ZoomButton extends JButton {
    private double value;
    private double min;
    private double max;
    private ArrayList listeners = new ArrayList();

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public double getValue() {
        return value;
    }

    public void setValue( double value ) {
        this.value = value;
    }

    public static interface Listener {
        public void zoomChanged();
    }

    public ZoomButton( Icon icon, double dzPress, double dzHold, double min, double max, double value, String tooltip ) {
        super( icon );
        setMargin( new Insets( 1,1,1,1) );
        this.min = min;
        this.max = max;
        this.value = value;
        ActionListener smooth = new Changer( dzHold );
        ActionListener discrete = new Changer( dzPress );
        addMouseListener( new RepeatClicker( smooth, discrete ) );
        setToolTipText( tooltip );
    }

    class Changer implements ActionListener {
        double dz;

        public Changer( double dz ) {
            this.dz = dz;
        }

        public void actionPerformed( ActionEvent e ) {
            double origValue = value;
            value += dz;
            System.out.println( "value = " + value );
            value = MathUtil.clamp( min, value, max );
            if( origValue != value ) {
                notifyListeners();
            }
        }
    }

    private void notifyListeners() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.zoomChanged();
        }
    }

    static class RepeatClicker extends MouseAdapter {
        ActionListener smooth;
        private ActionListener discrete;
        int initDelay = 300;
        int delay = 30;
        Timer timer;
        private long pressTime;

        public RepeatClicker( ActionListener smooth, ActionListener discrete ) {
            this.smooth = smooth;
            this.discrete = discrete;
        }

        public void mouseClicked( MouseEvent e ) {
        }

        public void mousePressed( MouseEvent e ) {
            pressTime = System.currentTimeMillis();
            timer = new Timer( delay, smooth );
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

}
