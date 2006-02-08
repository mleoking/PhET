/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.piccolo;

import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Draws a boundary around the specified PNode.
 */

public class BoundGraphic extends PPath {
    private PNode src;
    private double insetX;
    private double insetY;

    public BoundGraphic( PNode src ) {
        this( src, 0, 0 );
    }

    public BoundGraphic( PNode src, double insetX, double insetY ) {
        this.src = src;
        this.insetX = insetX;
        this.insetY = insetY;
        PropertyChangeListener listener = new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                update();
            }
        };
        src.addPropertyChangeListener( listener );
    }

    public double getInsetX() {
        return insetX;
    }

    public void setInsetX( double insetX ) {
        this.insetX = insetX;
        update();
    }

    public double getInsetY() {
        return insetY;
    }

    public void setInsetY( double insetY ) {
        this.insetY = insetY;
        update();
    }

    private void update() {

        // First get the center of each rectangle in the
        // local coordinate system of each rectangle.
        Point2D r1c = src.getGlobalFullBounds().getCenter2D();
        Point2D r2c = new Point2D.Double( src.getGlobalFullBounds().getMaxX(), src.getGlobalFullBounds().getMaxY() );

        this.globalToLocal( r1c );
        this.globalToLocal( r2c );

        // Finish by setting the endpoints of the line to
        // the center points of the rectangles, now that those
        // center points are in the local coordinate system of the line.
        Rectangle2D rect = new Rectangle2D.Double();
        rect.setFrameFromCenter( r1c, r2c );
        rect = RectangleUtils.expand( rect, insetX, insetY );
        super.setPathTo( rect );
        repaint();
    }
}