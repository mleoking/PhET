/* Copyright 2004, Sam Reid */
package edu.colorado.phet.piccolo;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jul 15, 2005
 * Time: 1:45:27 PM
 * Copyright (c) Jul 15, 2005 by Sam Reid
 */

public class Connector extends PPath {

    private PNode src;
    private PNode dst;
    private StayConnected connectActivity;

    public Connector( PNode src, PNode dst ) {
        this.src = src;
        this.dst = dst;
        connectActivity = new StayConnected();//todo make this automatically start & stop.
    }

    public StayConnected getConnectActivity() {
        return connectActivity;
    }

    class StayConnected extends PActivity {

        public StayConnected() {
            super( -1 );
        }

        protected void activityStep( long elapsedTime ) {
            super.activityStep( elapsedTime );
            update();
        }
    }

    private void update() {
        connectRectsWithLine();
    }
    // This method connects the centers of two
// rectangle nodes with a line node. If you know that two nodes
// exist in the same coordinate system then you don't need to make
// all these conversions. This example assumes the most general case where
// they all exist in different coordinate systems.

    public void connectRectsWithLine() {

        // First get the center of each rectangle in the
        // local coordinate system of each rectangle.
        Point2D r1c = src.getBounds().getCenter2D();
        Point2D r2c = dst.getBounds().getCenter2D();

        // Next convert that center point for each rectangle
        // into global coordinate system.
        src.localToGlobal( r1c );
        dst.localToGlobal( r2c );

        // Now that the centers are in global coordinates they
        // can be converted into the local coordinate system
        // of the line node.
        globalToLocal( r1c );
        globalToLocal( r2c );

        // Finish by setting the endpoints of the line to
        // the center points of the rectangles, now that those
        // center points are in the local coordinate system of the line.
        Line2D.Double aShape = new Line2D.Double( r1c, r2c );
//        System.out.println( "aShape = " + aShape.getP1()+",-> "+aShape.getP2() );
        setPathTo( aShape );

        repaint();
    }
}
