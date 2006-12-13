/* Copyright 2004, Sam Reid */
package edu.colorado.phet.tests.piccolo;

import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.piccolo.nodes.MeasuringTape;
import edu.umd.cs.piccolo.PCanvas;

import javax.swing.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Mar 23, 2006
 * Time: 11:39:03 PM
 * Copyright (c) Mar 23, 2006 by Sam Reid
 */

public class TestMeasuringTape {
    private JFrame frame;

    public TestMeasuringTape() {
        frame = new JFrame( "Test Measuring Tape" );
        PCanvas contentPane = new PCanvas();
        contentPane.setPanEventHandler( null );
        frame.setContentPane( contentPane );
        MeasuringTape child = new MeasuringTape( new ModelViewTransform2D( new Rectangle2D.Double( 0, 0, 100, 100 ), new Rectangle2D.Double( 0, 0, 100, 100 ) ), new Point2D.Double( 0, 0 ) );
        child.setOffset( 100, 100 );
        contentPane.getLayer().addChild( child );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 600, 600 );
    }

    public static void main( String[] args ) {
        new TestMeasuringTape().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
