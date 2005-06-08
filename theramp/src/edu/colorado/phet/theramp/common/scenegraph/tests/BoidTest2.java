/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph.tests;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.theramp.common.scenegraph.AbstractGraphic;
import edu.colorado.phet.theramp.common.scenegraph.GraphicLayerNode;
import edu.colorado.phet.theramp.common.scenegraph.SceneGraphPanel;

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

public class BoidTest2 extends SceneGraphPanel {
    private BoidTest boidTest;
    public GraphicLayerNode centerNode;
    public GraphicLayerNode clipNode;

    public BoidTest2() {
        boidTest = new BoidTest();
        clipNode = new GraphicLayerNode();
        clipNode.setClip( new Rectangle( 100, 100, 400, 400 ) );
        centerNode = new GraphicLayerNode();
        clipNode.addGraphic( centerNode );
        centerNode.addGraphic( boidTest.getGraphic() );
        addGraphic( clipNode );
    }

    private void viewBoid( AbstractGraphic graphic, BoidTest.Boid b0 ) {
        Rectangle viewport = new Rectangle();
        viewport.setFrameFromCenter( b0.getLocation(), new Point2D.Double( b0.getLocation().getX() + getWidth(), b0.getLocation().getY() + getHeight() ) );
        graphic.setTransformViewport( new Rectangle( getWidth(), getHeight() ), viewport );
    }

    public static void main( String[] args ) {
        AbstractClock clock = new SwingTimerClock( 1, 30 );

        final BoidTest2 boidTest = new BoidTest2();
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
        viewBoid( centerNode, boidTest.boidAt( 0 ) );
        repaint();
    }
}
