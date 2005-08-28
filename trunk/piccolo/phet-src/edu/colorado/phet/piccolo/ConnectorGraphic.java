/* Copyright 2004, Sam Reid */
package edu.colorado.phet.piccolo;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Shows a Line connection between two PNodes.
 */

public class ConnectorGraphic extends PPath {

    private PNode src;
    private PNode dst;

    public ConnectorGraphic( PNode src, PNode dst ) {
        this.src = src;
        this.dst = dst;
        PropertyChangeListener changeHandler = new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                update();
            }
        };
        src.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, changeHandler );
        dst.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, changeHandler );
        update();
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
        Point2D r1c = src.getFullBounds().getCenter2D();
        Point2D r2c = dst.getFullBounds().getCenter2D();

        // Next convert that center point for each rectangle
        // into global coordinate system.
        src.getParent().localToGlobal( r1c );
        dst.getParent().localToGlobal( r2c );

        // Now that the centers are in global coordinates they
        // can be converted into the local coordinate system
        // of the line node.
        globalToLocal( r1c );
        globalToLocal( r2c );

        // Finish by setting the endpoints of the line to
        // the center points of the rectangles, now that those
        // center points are in the local coordinate system of the line.
        AbstractVector2D vector = new Vector2D.Double( r1c, r2c );
        vector = vector.getInstanceOfMagnitude( Math.max( vector.getMagnitude() - 50, 100 ) );
        Arrow arrow = new Arrow( r1c, vector.getDestination( r1c ), 30, 30, 5, 1.0, true );

        GradientPaint gradientPaint = new GradientPaint( r1c, Color.blue, vector.getDestination( r1c ), Color.red, false );
        setPaint( gradientPaint );
        setPathTo( arrow.getShape() );
//        Line2D.Double aShape = new Line2D.Double( r1c, r2c );
//        setPathTo( aShape );
        //        System.out.println( "aShape = " + aShape.getP1()+",-> "+aShape.getP2() );

        repaint();
    }
}
