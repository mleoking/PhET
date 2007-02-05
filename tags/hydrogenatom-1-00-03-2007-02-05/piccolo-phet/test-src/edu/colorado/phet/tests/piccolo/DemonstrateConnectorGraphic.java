package edu.colorado.phet.tests.piccolo;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PPath;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Sep 9, 2005
 * Time: 10:00:05 PM
 * Copyright (c) Sep 9, 2005 by Sam Reid
 */

public class DemonstrateConnectorGraphic {
    private JFrame frame;
    private PCanvas canvas;

    public DemonstrateConnectorGraphic() {
        frame = new JFrame( "Coordinate Frames" );
        canvas = new PCanvas();
        frame.setContentPane( canvas );
        PPath a = new PPath( new Rectangle( 10, 10, 10, 10 ) );
        PPath b = new PPath( new Rectangle( 50, 50, 10, 10 ) );
        frame.setSize( 200, 200 );

        canvas.getLayer().addChild( a );
        canvas.getLayer().addChild( b );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setContentPane( canvas );

        PPath connector = new PPath();
        connectRectsWithLine( a, b, connector );
//        canvas.getLayer().addChild( connector );//this would work
        canvas.getCamera().addChild( connector );//this fails if view transform!=1.0
        canvas.getCamera().setViewScale( 0.8 );
    }

    public void connectRectsWithLine( PPath rect1, PPath rect2, PPath line ) {

        // First get the center of each rectangle in the
        // local coordinate system of each rectangle.
        Point2D r1c = rect1.getBounds().getCenter2D();
        Point2D r2c = rect2.getBounds().getCenter2D();

        // Next convert that center point for each rectangle
        // into global coordinate system.
        rect1.localToGlobal( r1c );
        rect2.localToGlobal( r2c );

        // Now that the centers are in global coordinates they
        // can be converted into the local coordinate system
        // of the line node.
        line.globalToLocal( r1c );
        line.globalToLocal( r2c );

        // Finish by setting the endpoints of the line to
        // the center points of the rectangles, now that those
        // center points are in the local coordinate system of the line.
        line.setPathTo( new Line2D.Double( r1c, r2c ) );
    }

    private void start() {
        frame.setVisible( true );
    }

    public static void main( String[] args ) {
        new DemonstrateConnectorGraphic().start();
    }

}
