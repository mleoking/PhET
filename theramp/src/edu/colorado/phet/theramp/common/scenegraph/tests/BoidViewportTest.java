/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph.tests;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.theramp.common.scenegraph.GraphicListNode;
import edu.colorado.phet.theramp.common.scenegraph.Rotator;
import edu.colorado.phet.theramp.common.scenegraph.SceneGraphPanel;
import edu.colorado.phet.theramp.common.scenegraph.Translator;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Jun 7, 2005
 * Time: 9:14:13 AM
 * Copyright (c) Jun 7, 2005 by Sam Reid
 */

public class BoidViewportTest extends SceneGraphPanel {
    private BoidTest boidTest;
    public ViewportGraphic viewportGraphic;
    public Dimension clip;

    public BoidViewportTest() {
        boidTest = new BoidTest();
        clip = new Dimension( 400, 400 );

        GraphicListNode root = new GraphicListNode();
//        root.setComposite( true );



        viewportGraphic = new ViewportGraphic( boidTest.getGraphic(), clip );
        viewportGraphic.setLocation( 100, 100 );
        viewportGraphic.addMouseListener( new Rotator() );
        viewportGraphic.addMouseListener( new Translator() );
//        viewportGraphic.addMouseListener( new Rotator() );
        root.addGraphic( viewportGraphic );
        addGraphic( root );
    }

    private void viewBoid( BoidTest.Boid boid ) {
        Rectangle viewport = new Rectangle();
        viewport.setFrameFromCenter( boid.getLocation(), new Point2D.Double( boid.getLocation().getX() + clip.getWidth(), boid.getLocation().getY() + clip.getHeight() ) );
        boidTest.getGraphic().setTransformViewport( new Rectangle( clip ), viewport );
    }

    public static void main( String[] args ) {


        ArrayList list = new ArrayList();
        Point2D[] points = new Point2D[10];
        for( int i = 0; i < points.length; i++ ) {
            points[i] = new Point2D.Double();
        }
        list.addAll( Arrays.asList( points ) );
        list.add( new Point( 4, 5 ) );
//        Point2D[] pArray = (Point2D[])list.toArray( new Point2D.Double[0] );
        Point2D[] pArray = (Point2D[])list.toArray( new Point2D[0] );
        System.out.println( "pArray = " + pArray );

        if( true ) {
            return;
        }
        AbstractClock clock = new SwingTimerClock( 1, 30 );

        final BoidViewportTest boidTest = new BoidViewportTest();
        JFrame jf = new JFrame();
        jf.setSize( 600, 600 );
        jf.setContentPane( boidTest );
        jf.setVisible( true );
        jf.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        boidTest.getGraphic().setTransformViewport( new Rectangle( 0, 0, boidTest.getWidth(), boidTest.getHeight() ),
                                                    new Rectangle2D.Double( -100, -100, 800, 800 ) );
        clock.addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                boidTest.tick( event );
                boidTest.repaint();
            }
        } );
        clock.start();
    }

    private void tick( ClockTickEvent event ) {
        boidTest.updateBoids( event );
        viewBoid( boidTest.boidAt( 0 ) );
        repaint();
    }


}
