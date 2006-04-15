/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.waveinterference.view.ScreenChartGraphic;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Apr 15, 2006
 * Time: 1:42:49 PM
 * Copyright (c) Apr 15, 2006 by Sam Reid
 */

public class ExpandableScreenChartGraphic extends PNode {

    boolean expanded = false;
    private PSwing showChartPSwing;
    private ScreenChartGraphic screenChart;
    private PSwing closePSwing;

    public ExpandableScreenChartGraphic( PSwingCanvas pSwingCanvas, ScreenChartGraphic screenChart ) {
        this.screenChart = screenChart;
        JButton showChart = new JButton( "Intensity Graph" );
        showChart.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setExpanded( true );
            }
        } );
        JButton closeButton = null;
        try {
            closeButton = new JButton( new ImageIcon( ImageLoader.loadBufferedImage( "images/x-20.png" ) ) );
            closeButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    setExpanded( false );
                }
            } );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        closePSwing = new PSwing( pSwingCanvas, closeButton );
        showChartPSwing = new PSwing( pSwingCanvas, showChart );
        addChild( screenChart );
        addChild( showChartPSwing );
        addChild( closePSwing );
        screenChart.addPropertyChangeListener( "fullBounds", new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateLocation();
            }
        } );
        update();
        updateLocation();
    }

    private void setExpanded( boolean b ) {
        this.expanded = b;
        update();
    }

    private void update() {
        if( expanded ) {
            showChartPSwing.setVisible( false );
            screenChart.setVisible( true );
            closePSwing.setVisible( true );
        }
        else {
            showChartPSwing.setVisible( true );
            screenChart.setVisible( false );
            closePSwing.setVisible( false );
        }
        updateLocation();
    }

    private void updateLocation() {
        showChartPSwing.setOffset( screenChart.getFullBounds().getMaxX() + 2, screenChart.getFullBounds().getY() );
        closePSwing.setOffset( screenChart.getFullBounds().getMaxX(), screenChart.getFullBounds().getY() );
    }
}
