/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.waveinterference.util.WIStrings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Apr 16, 2006
 * Time: 9:16:12 PM
 * Copyright (c) Apr 16, 2006 by Sam Reid
 */

public class LaserWaveChartControl extends PNode {
    private LaserWaveChartGraphic laserWaveChartGraphic;
    private JCheckBox curve;
    private JCheckBox vectors;

    public LaserWaveChartControl( PSwingCanvas pSwingCanvas, final LaserWaveChartGraphic laserWaveChartGraphic ) {
        this.laserWaveChartGraphic = laserWaveChartGraphic;
        curve = new JCheckBox( WIStrings.getString( "curve" ), laserWaveChartGraphic.isCurveVisible() );
        curve.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                laserWaveChartGraphic.setCurveVisible( curve.isSelected() );
            }
        } );
        vectors = new JCheckBox( WIStrings.getString( "vectors" ), laserWaveChartGraphic.isVectorsVisible() );
        vectors.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                laserWaveChartGraphic.setVectorsVisible( vectors.isSelected() );
            }
        } );
//        final JCheckBox colorized = new JCheckBox( WIStrings.getString( "colorize" ), laserWaveChartGraphic.getColorized() );
//        colorized.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                laserWaveChartGraphic.setColorized( colorized.isSelected() );
//            }
//        } );

        VerticalLayoutPanel verticalLayoutPanel = new VerticalLayoutPanel();
        verticalLayoutPanel.add( curve );
        verticalLayoutPanel.add( vectors );
//        verticalLayoutPanel.add( colorized );

        PSwing pSwing = new PSwing( pSwingCanvas, verticalLayoutPanel );
        addChild( pSwing );
        reset();
    }

    public void reset() {
        curve.setSelected( true );
        vectors.setSelected( false );

        laserWaveChartGraphic.setCurveVisible( curve.isSelected() );
        laserWaveChartGraphic.setVectorsVisible( vectors.isSelected() );
    }
}
