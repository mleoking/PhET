/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.tests.ui;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PPath;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jan 20, 2006
 * Time: 12:07:38 PM
 * Copyright (c) Jan 20, 2006 by Sam Reid
 */

public class TestGlassPane extends PCanvas {
    private Container contentPane;
    static ArrayList mouseListeners = new ArrayList();
    static ArrayList mouseMotionListeners = new ArrayList();
    static ArrayList mouseWheelListeners = new ArrayList();
    public static TestGlassPane instance;

    public synchronized void addMouseListener( MouseListener l ) {
        mouseListeners.add( l );
    }

    public synchronized void addMouseMotionListener( MouseMotionListener l ) {
        mouseMotionListeners.add( l );
    }

    public synchronized void addMouseWheelListener( MouseWheelListener l ) {
        mouseWheelListeners.add( l );
    }

    public TestGlassPane( final JFrame frame ) {
        instance = this;
        contentPane = frame.getContentPane();
        PPath path = new PPath( new Ellipse2D.Double( 50, 50, 200, 200 ) );
        path.setPaint( Color.blue );
        getLayer().addChild( path );
        setBackground( new Color( 0, 0, 0, 0 ) );
        setPanEventHandler( null );
        setZoomEventHandler( null );

        showAll( frame.getContentPane() );
//        frame.getContentPane().getComponentCount()
        Timer timer = new Timer( 1000, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getLayer().removeAllChildren();
                showAll( frame.getContentPane() );
            }
        } );
        timer.start();
    }

    private void showAll( Container container ) {

        for( int i = 0; i < container.getComponentCount(); i++ ) {
            Component c = container.getComponent( i );
            if( c.isVisible() ) {
                if( c instanceof JButton ) {
                    Point loc = SwingUtilities.convertPoint( c.getParent(), c.getLocation(), this );
                    PPath path = new PPath( new Ellipse2D.Double( -5, -5, 10, 10 ) );
                    path.setPaint( Color.red );
                    path.setOffset( loc );
                    getLayer().addChild( path );
                }
                else if( c instanceof JCheckBox ) {
                    Point loc = SwingUtilities.convertPoint( c.getParent(), c.getLocation(), this );
                    PPath path = new PPath( new Ellipse2D.Double( -5, -5, 10, 10 ) );
                    path.setPaint( Color.blue );
                    path.setOffset( loc );
                    getLayer().addChild( path );
                }
                else if( c instanceof JSlider ) {
                    Point loc = SwingUtilities.convertPoint( c.getParent(), c.getLocation(), this );
                    PPath path = new PPath( new Ellipse2D.Double( -5, -5, 10, 10 ) );
                    path.setPaint( Color.green );
                    path.setOffset( loc );
                    getLayer().addChild( path );
                }

                else if( c instanceof Container ) {
                    showAll( (Container)c );
                }
            }
        }
    }
}
