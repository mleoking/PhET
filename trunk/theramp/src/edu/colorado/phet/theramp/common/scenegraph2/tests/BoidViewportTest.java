/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph2.tests;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.theramp.common.scenegraph2.GraphicListNode;
import edu.colorado.phet.theramp.common.scenegraph2.Rotator;
import edu.colorado.phet.theramp.common.scenegraph2.SceneGraphPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

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
        root.setComposite( true );
        root.addMouseListener( new Rotator() );

        viewportGraphic = new ViewportGraphic( boidTest.getGraphic(), clip );
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
