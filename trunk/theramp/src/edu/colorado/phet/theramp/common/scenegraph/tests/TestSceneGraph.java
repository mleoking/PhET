/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph.tests;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.theramp.common.scenegraph.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Jun 2, 2005
 * Time: 3:33:20 AM
 * Copyright (c) Jun 2, 2005 by Sam Reid
 */

public class TestSceneGraph {
    static AbstractClock clock = new SwingTimerClock( 1, 30 );

    public static void main( String[] args ) {
        final SceneGraphPanel sceneGraphPanel = new SceneGraphPanel();
        sceneGraphPanel.getGraphic().setName( "root" );

        AbstractGraphic userRoot = new TextGraphic( "MYTEXT!" );
        userRoot.setName( "MyText!" );
        userRoot.setFontLucidaSansBold( 32 );
        userRoot.setCursorHand();
        userRoot.addMouseListener( new Translator() );
        userRoot.addMouseListener( new Rotator() );
//        userRoot.addMouseListener( new Repaint() );

        GraphicListNode m2 = createMainTree();
        m2.scale( 2, 2 );
        sceneGraphPanel.addGraphic( m2 );

        GraphicListNode m3 = createMainTree();
        m3.scale( .5, .5 );
        m3.setComposite( true );
        m3.setDrawBorderDebug( true );
        m3.setCursorHand();
        m3.addMouseListener( new Translator() );
        m3.addMouseListener( new Rotator() );
//        m3.addMouseListener( new Repaint() );
        sceneGraphPanel.addGraphic( m3 );

        m3.setLocation( new Point2D.Double( 100, 100 ) );
        m3.setRegistrationPoint( new Point2D.Double( -75, -75 ) );

        sceneGraphPanel.addMouseMotionListener( new MouseMotionAdapter() {
            // implements java.awt.event.MouseMotionListener
            public void mouseDragged( MouseEvent e ) {
                if( e.isShiftDown() ) {
                    sceneGraphPanel.getGraphic().rotate( Math.PI / 32, sceneGraphPanel.getWidth() / 2, sceneGraphPanel.getHeight() / 2 );
                    sceneGraphPanel.repaint();
                }
            }
        } );

        JFrame frame = new JFrame( "Test" );

        SceneGraphJComponent.init( frame );
        JButton jButton = new JButton( "text" );
        AbstractGraphic sceneGraphJComponent = SceneGraphJComponent.newInstance( jButton );
        sceneGraphPanel.addGraphic( sceneGraphJComponent );
//        sceneGraphJComponent.addMouseListener( new Repaint() );
        sceneGraphJComponent.translate( 400, 400 );
        sceneGraphJComponent.setAntialias( true );
        sceneGraphJComponent.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
        sceneGraphJComponent.setCursorHand();
        sceneGraphJComponent.addMouseListener( new Translator() );
        sceneGraphJComponent.addMouseListener( new Rotator() );

        TextGraphic textGraphic = new TextGraphic( "Top-Level text" );
        sceneGraphPanel.addGraphic( textGraphic );
        textGraphic.addMouseListener( new Rotator() );
        textGraphic.addMouseListener( new Translator() );
//        textGraphic.addMouseListener( new Repaint() );

        frame.setContentPane( sceneGraphPanel );
        frame.setSize( 800, 800 );
        frame.setVisible( true );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        sceneGraphPanel.setRequestFocusOnMousePress( true );
        clock.start();
    }

    private static GraphicListNode createMainTree() {
        GraphicListNode mainTree = new GraphicListNode();
        mainTree.setName( "Main Tree" );

        TextGraphic textGraphic = new TextGraphic( "Test" );
        textGraphic.setFont( new Font( "Lucida Sans", Font.BOLD, 28 ) );
        textGraphic.setAntialias( true );
        mainTree.addGraphic( textGraphic );
        textGraphic.translate( 100, 100 );

        GraphicListNode list = new GraphicListNode();
        list.setName( "Blocks" );
        for( int i = 0; i < 10; i++ ) {
//            FillGraphic blockGraphic = new FillGraphic( new Ellipse2D.Double( 0, 0, 20, 20 ) );
            final FillGraphic blockGraphic = new FillGraphic( new Rectangle( 20, 20 ) );
            blockGraphic.setCursorHand();
            blockGraphic.addMouseListener( new Translator() );
            blockGraphic.addMouseListener( new Rotator() );

//            blockGraphic.addMouseListener( new Repaint() );

            blockGraphic.setColor( randomColor() );
            blockGraphic.translate( 10 + i * 30, 50 );
            RotatorGraphic rotatorGraphic = new RotatorGraphic( blockGraphic, clock, Math.PI / 64 );
            list.addGraphic( rotatorGraphic );
            final int i1 = i;
            blockGraphic.addKeyListener( new KeyListener() {
                public void keyPressed( KeyEvent e ) {
                    System.out.println( "TestSceneGraph.keyPressed @ i=" + i1 );
                }

                public void keyReleased( KeyEvent e ) {
                    System.out.println( "TestSceneGraph.keyReleased@ i=" + i1 );
                }

                public void keyTyped( KeyEvent e ) {
                    //To change body of implemented methods use File | Settings | File Templates.
                }
            } );
//            list.addGraphic(blockGraphic);
//            clock.addClockTickListener(new ClockTickListener() {
//                public void clockTicked(ClockTickEvent event) {
//                    Point2D center=RectangleUtils.getCenter2D(blockGraphic.getLocalBounds());
//                    blockGraphic.rotate(Math.PI/64,center.getX(),center.getY());
//                }
//            });
        }
        list.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        mainTree.addGraphic( list );

        textGraphic.addMouseListener( new Translator() );
        textGraphic.addMouseListener( new Rotator() );
        textGraphic.setCursorHand();
//        textGraphic.addMouseListener( new Repaint() );

//        mainTree.scale( 2, 2 );

        try {
            ImageGraphic imageGraphic = new ImageGraphic( ImageLoader.loadBufferedImage( "images/Phet-Flatirons-logo-3-small.gif" ) );
//            imageGraphic.addMouseListener( new CursorHand() );
            mainTree.setCursorHand();
            mainTree.addGraphic( imageGraphic );
            imageGraphic.translate( 10, 10 );
            imageGraphic.scale( 2, 2 );

            imageGraphic.rotate( Math.PI / 16 );
            imageGraphic.addMouseListener( new Translator() );
            imageGraphic.addMouseListener( new Rotator() );
//            imageGraphic.addMouseListener( new Repaint() );
            imageGraphic.setCursorHand();
            imageGraphic.addMouseListener( new SceneGraphMouseAdapter() {
                public void mousePressed( SceneGraphMouseEvent event ) {
                    System.out.println( "Pressed: event = " + event );
                }
            } );
            imageGraphic.addMouseListener( new SceneGraphMouseAdapter() {
                public void mouseReleased( SceneGraphMouseEvent event ) {
                    System.out.println( "Released: event = " + event );
                }
            } );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        return mainTree;
    }

    private static Color randomColor() {
        return new Color( (int)( Math.random() * 255 ), (int)( Math.random() * 255 ), (int)( Math.random() * 255 ) );
    }
}
