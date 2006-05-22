/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 23, 2006
 * Time: 9:58:29 PM
 * Copyright (c) Mar 23, 2006 by Sam Reid
 */

/**
 * Decorates with buttons and controls.
 */
public class IntensityReaderDecorator extends PhetPNode {
    private ArrayList listeners = new ArrayList();
    private IntensityReader intensityReader;
    private PSwing buttonPSwing;
    private Point lastMovePoint = null;

    public IntensityReaderDecorator( String title, final PSwingCanvas pSwingCanvas, WaveModel waveModel, LatticeScreenCoordinates latticeScreenCoordinates, IClock clock ) {
        this.intensityReader = new IntensityReader( title, waveModel, latticeScreenCoordinates, clock );
        JButton close = null;
        try {
            close = new JButton( new ImageIcon( ImageLoader.loadBufferedImage( "images/x-20.png" ) ) );
            close.setOpaque( false );
            close.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    doDelete();
                }
            } );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        final JPopupMenu jPopupMenu = new JPopupMenu( "Popup Menu" );
        final JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem( "Display Readout", intensityReader.isReadoutVisible() );
        menuItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                intensityReader.setReadoutVisible( menuItem.isSelected() );
            }
        } );
        jPopupMenu.add( menuItem );
        jPopupMenu.addSeparator();
        JMenuItem deleteItem = new JMenuItem( "Delete" );
        deleteItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                doDelete();
            }

        } );
        jPopupMenu.add( deleteItem );
        pSwingCanvas.addMouseMotionListener( new MouseMotionListener() {
            public void mouseDragged( MouseEvent e ) {
                lastMovePoint = e.getPoint();
            }

            public void mouseMoved( MouseEvent e ) {
                lastMovePoint = e.getPoint();
            }
        } );

        buttonPSwing = new PSwing( pSwingCanvas, close );
        addChild( intensityReader );
        addChild( buttonPSwing );
        intensityReader.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateLocation();
            }
        } );
        intensityReader.getStripChartJFCNode().addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateLocation();
            }
        } );
    }

    private void doDelete() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.deleted();
        }
    }

    public void setConstrainedToMidline( boolean midline ) {
        intensityReader.setConstrainedToMidline( midline );
    }

    public static interface Listener {
        void deleted();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    private void updateLocation() {
//        buttonPSwing.setOffset( intensityReader.getFullBounds().getX(), intensityReader.getFullBounds().getY() - buttonPSwing.getFullBounds().getHeight() );
        Rectangle2D bounds = intensityReader.getStripChartJFCNode().getFullBounds();
//        intensityReader.getStripChartJFCNode().localToParent( bounds );
        intensityReader.localToParent( bounds );
//        buttonPSwing.setOffset( )
        buttonPSwing.setOffset( bounds.getX(), bounds.getY() - buttonPSwing.getFullBounds().getHeight() );
    }

    public void update() {
        intensityReader.update();
    }
}
