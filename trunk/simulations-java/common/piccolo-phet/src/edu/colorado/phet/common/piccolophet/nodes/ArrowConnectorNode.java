// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.umd.cs.piccolo.PNode;

/**
 * Shows the connection between two nodes as an arrow.
 */
public class ArrowConnectorNode extends ConnectorNode {
    private double headHeight;
    private double headWidth;
    private double tailWidth;
    private double fractionalHeadHeight;
    private boolean scaleTailToo;
    private int distFromTarget = 50;
    private int minLength = 100;

    public ArrowConnectorNode( PNode src, PNode dst ) {
        this( src, dst, 30, 30, 5, 1.0, true );
    }

    public ArrowConnectorNode( PNode src, PNode dst, double headHeight, double headWidth, double tailWidth, double fractionalHeadHeight, boolean scaleTailToo ) {
        super( src, dst );
        this.headHeight = headHeight;
        this.headWidth = headWidth;
        this.tailWidth = tailWidth;
        this.fractionalHeadHeight = fractionalHeadHeight;
        this.scaleTailToo = scaleTailToo;
    }

    protected void updateShape( Point2D r1c, Point2D r2c ) {
        ImmutableVector2D vector = new Vector2D( r1c, r2c );
        vector = vector.getInstanceOfMagnitude( Math.max( vector.getMagnitude() - distFromTarget, minLength ) );
        Arrow arrow = new Arrow( r1c, vector.getDestination( r1c ), headHeight, headWidth, tailWidth, fractionalHeadHeight, scaleTailToo );

        GradientPaint gradientPaint = new GradientPaint( r1c, Color.blue, vector.getDestination( r1c ), Color.red, false );
        setPaint( gradientPaint );
        setPathTo( arrow.getShape() );
    }
}