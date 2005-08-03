/* Copyright 2004, Sam Reid */
package edu.colorado.phet.piccolo;

import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * User: Sam Reid
 * Date: Jul 15, 2005
 * Time: 1:45:27 PM
 * Copyright (c) Jul 15, 2005 by Sam Reid
 */

public class BoundGraphic extends PPath {
    private PNode src;
    private int dx;
    private int dy;

    public BoundGraphic( PNode src ) {
        this( src, 0, 0 );
    }

    public BoundGraphic( PNode src, int dx, int dy ) {
        this.src = src;
        this.dx = dx;
        this.dy = dy;
        PropertyChangeListener listener = new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                update();
            }
        };
        src.addPropertyChangeListener( listener );
    }

    private void update() {

        // First get the center of each rectangle in the
        // local coordinate system of each rectangle.
        Point2D r1c = src.getBounds().getCenter2D();
        Point2D r2c = new Point2D.Double( src.getBounds().getMaxX(), src.getBounds().getMaxY() );

        src.localToGlobal( r1c );
        src.localToGlobal( r2c );

        this.globalToLocal( r1c );
        this.globalToLocal( r2c );

        // Finish by setting the endpoints of the line to
        // the center points of the rectangles, now that those
        // center points are in the local coordinate system of the line.
        Rectangle2D rect = new Rectangle2D.Double();
        rect.setFrameFromCenter( r1c, r2c );
        rect = RectangleUtils.expand( rect, dx, dy );
        super.setPathTo( rect );
        repaint();
    }
}
