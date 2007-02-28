/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.tests;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * User: Sam Reid
 * Date: Feb 27, 2005
 * Time: 3:41:35 PM
 * Copyright (c) Feb 27, 2005 by Sam Reid
 */

public class TestViewport {
//    ModelViewTransform2D transform2D;
    ApparatusPanel2 apparatusPanel2;
    private BaseModel baseModel = new BaseModel();
    private long startTime;
    private Viewport viewport;

    public TestViewport( IClock clock ) {
//        transform2D = new ModelViewTransform2D( new Rectangle2D.Double( 0, 0, 1000, 1000 ), new Rectangle( 0, 0, 600, 600 ) );
        apparatusPanel2 = new ApparatusPanel2( baseModel, clock );
        Rectangle rectangle = new Rectangle( 0, 0, 100, 100 );
        final PhetShapeGraphic phetShapeGraphic = new PhetShapeGraphic( apparatusPanel2, rectangle, Color.blue );
        phetShapeGraphic.setCursorHand();

        viewport = new Viewport( apparatusPanel2 );
//        apparatusPanel2.getTransformManager().setScale( 0.5 );
        apparatusPanel2.setGraphic( viewport );
//        apparatusPanel2.addGraphic( phetShapeGraphic );
        clock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent event ) {
                baseModel.clockTicked( event );
            }
        } );
        phetShapeGraphic.addTranslationListener( new TranslationListener() {
            public void translationOccurred( TranslationEvent translationEvent ) {
                phetShapeGraphic.setLocation( translationEvent.getX(), translationEvent.getY() );
            }
        } );

        Landscape landscape = new Landscape( apparatusPanel2 );
//        apparatusPanel2.addGraphic( landscape );
        viewport.addGraphic( landscape );
        apparatusPanel2.addGraphicsSetup( new BasicGraphicsSetup() );
//        clock.addClockListener( new ClockAdapter() {
//            public void clockTicked( ClockEvent event ) {
//                apparatusPanel2.setScale( 0.2);
//            }
//        } );
    }

    public static void main( String[] args ) {
        IClock clock = new SwingClock( 1, 20 );
        clock.start();
        new TestViewport( clock ).start();

    }

    private void start() {
        JFrame f = new JFrame( "Frame" );
        f.setContentPane( apparatusPanel2 );
        f.setSize( 600, 600 );
        f.setVisible( true );
        f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        try {
            Thread.sleep( 500 );
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }
//        apparatusPanel2.setScale( 0.1 );
//        apparatusPanel2.paintImmediately( 0, 0, apparatusPanel2.getWidth(), apparatusPanel2.getHeight() );

        Runnable r = new Runnable() {
            public void run() {
                try {
                    startTime = System.currentTimeMillis();
                    while( true ) {
                        Thread.sleep( 30 );
                        long time = System.currentTimeMillis() - startTime;
                        double t = time / 1000.0;
                        if( t == 0 ) {
                            t = 0.1;
                        }
//                        double scale = apparatusPanel2.getScale();
//                        apparatusPanel2.setScale( scale * 0.99 );
                        AffineTransform transform = new AffineTransform();
                        transform.translate( -250, 0 );

//                        double sx = 5.0 / t;
//                        if (sx>100){
//                            sx=100;
//                        }
//                        transform.scale( sx, sx);
//                        apparatusPanel2.getGraphic().scale( scale);
//                        apparatusPanel2.getGraphic().setTransform( transform );
                        viewport.setTransform( transform );
                        apparatusPanel2.repaint( 0, 0, apparatusPanel2.getWidth(), apparatusPanel2.getHeight() );
//                        apparatusPanel2.paintImmediately( 0, 0, apparatusPanel2.getWidth(), apparatusPanel2.getHeight() );
                    }
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        };
        new Thread( r ).start();
    }
}
