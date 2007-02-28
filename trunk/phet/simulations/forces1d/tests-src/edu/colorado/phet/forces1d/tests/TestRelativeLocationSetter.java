/* Copyright 2004, Sam Reid */
package edu.colorado.phet.forces1d.tests;

import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.RepaintDebugGraphic;
import edu.colorado.phet.forces1d.common.HelpItem2;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Mar 6, 2005
 * Time: 4:43:03 PM
 * Copyright (c) Mar 6, 2005 by Sam Reid
 */

public class TestRelativeLocationSetter {
    ApparatusPanel apparatusPanel = new ApparatusPanel();
    private JFrame frame;

    public TestRelativeLocationSetter() {
        frame = new JFrame( "Test" );
        frame.setContentPane( apparatusPanel );
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        BasicGraphicsSetup bsg = new BasicGraphicsSetup() {
            public void setup( Graphics2D graphics ) {
                graphics.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
                super.setup( graphics );
            }
        };
        apparatusPanel.addGraphicsSetup( bsg );

        Rectangle2D.Double square = new Rectangle2D.Double( 0, 0, 100, 100 );
        final PhetShapeGraphic phetShapeGraphic2 = new PhetShapeGraphic( apparatusPanel, square, Color.red, new BasicStroke( 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ), Color.black );

        apparatusPanel.addGraphic( phetShapeGraphic2 );

        HelpItem2 helpItem2 = new HelpItem2( apparatusPanel, "<html>Drag the red square<br>to move it.</html>" );
        helpItem2.pointUpAt( phetShapeGraphic2, 20 );
        apparatusPanel.addGraphic( helpItem2 );

        phetShapeGraphic2.addTranslationListener( new TranslationListener() {
            public void translationOccurred( TranslationEvent translationEvent ) {
                phetShapeGraphic2.setLocation( translationEvent.getX(), translationEvent.getY() );
            }
        } );

        HelpItem2 help2 = new HelpItem2( apparatusPanel, "<html>That's a PhetHelp<sup><small>TM</small></sup> item!</html>" );
        help2.pointLeftAt( helpItem2, 50 );

        apparatusPanel.addGraphic( help2 );
        phetShapeGraphic2.setCursorHand();

        SwingTimerClock clock = new SwingTimerClock( 1, 30 );
        RepaintDebugGraphic repaintDebugGraphic = new RepaintDebugGraphic( apparatusPanel, clock );
        clock.start();

        apparatusPanel.addGraphic( repaintDebugGraphic );
        repaintDebugGraphic.setVisible( false );

        phetShapeGraphic2.setLocation( 100, 100 );
    }

    public void start() {
        frame.setVisible( true );
    }

    public static void main( String[] args ) {
        new TestRelativeLocationSetter().start();
    }
}
