/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.waveinterference.util.WIStrings;
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

    private boolean expanded = false;
    private PhetPNode expandNode;
    private ScreenChartGraphic screenChart;
    private PhetPNode closeNode;

    public ExpandableScreenChartGraphic( PSwingCanvas pSwingCanvas, ScreenChartGraphic screenChart ) {
        this.screenChart = screenChart;
        JButton showChart = new JButton( WIStrings.getString( "intensity.graph" ) );
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
        closeNode = new PhetPNode( new PSwing( pSwingCanvas, closeButton ) );
        expandNode = new PhetPNode( new PSwing( pSwingCanvas, showChart ) );
        addChild( screenChart );
        addChild( expandNode );
        addChild( closeNode );
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
            expandNode.setVisible( false );
            screenChart.setVisible( true );
            closeNode.setVisible( true );
        }
        else {
            expandNode.setVisible( true );
            screenChart.setVisible( false );
            closeNode.setVisible( false );
        }
//        expandNode.setVisible( screenChart.get);
        updateLocation();
        updateChart();
    }

    private void updateLocation() {
//        expandNode.setOffset( screenChart.getChartBounds().getMinX() + 2, screenChart.getChartBounds().getY() + 50 );
        expandNode.setOffset( screenChart.getChartBounds().getMinX() + 2, screenChart.getChartBounds().getCenterY() );
        closeNode.setOffset( screenChart.getChartBounds().getMaxX(), Math.max( screenChart.getChartBounds().getY(), 0 ) );
    }

    public void updateChart() {
        if( screenChart.getVisible() ) {
            screenChart.updateChart();
        }
    }

    public void reset() {
        setExpanded( false );
        updateChart();
        updateLocation();
    }
}
