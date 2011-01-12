/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.modules.interactiveisotope.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Piccolo node that represents a scale on which an atom can be weighed.  This
 * node is intended to have a faux 3D look to it, but is not truly 3D in any
 * way.
 *
 * @author John Blanco
 */
public class AtomScaleNode extends PNode {

    private static final Dimension2D SIZE = new PDimension( 250, 100 );
    private static final double WIEIGH_PLATE_WIDTH = SIZE.getWidth() * 0.70;
    private static final Stroke STROKE = new BasicStroke( 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
    private static final Paint STROKE_PAINT = Color.BLACK;

    public AtomScaleNode() {
        addChild(new PhetPPath( new Rectangle2D.Double(0, 0, SIZE.getWidth(), SIZE.getHeight()), Color.PINK ));

        // Set up some helper variables.
        double centerX = SIZE.getWidth() / 2;

        // The scale shapes are generated from the bottom up, since adding
        // them in this order creates the correct layering effect.

        // Add the front of the scale base.
        // TODO: Need to add readouts here.
        Rectangle2D frontOfBaseShape = new Rectangle2D.Double( 0, SIZE.getHeight() * 0.55, SIZE.getWidth(), SIZE.getHeight() * 0.5 );
        addChild( new PhetPPath( frontOfBaseShape, Color.LIGHT_GRAY, STROKE, STROKE_PAINT ) );

        // Add the top portion of the scale body.  This is meant to look like
        // a tilted rectangle.  Because, hey, it's all a matter of
        // perspective.
        DoubleGeneralPath scaleBaseTopShape = new DoubleGeneralPath();
        scaleBaseTopShape.moveTo( SIZE.getWidth() * 0.15, SIZE.getHeight() * 0.375 );
        scaleBaseTopShape.lineTo( SIZE.getWidth() * 0.85, SIZE.getHeight() * 0.375 );
        scaleBaseTopShape.lineTo( SIZE.getWidth(), SIZE.getHeight() * 0.55 );
        scaleBaseTopShape.lineTo( 0, SIZE.getHeight() * 0.55 );
        scaleBaseTopShape.closePath();
        Rectangle2D scaleBaseTopShapeBounds = scaleBaseTopShape.getGeneralPath().getBounds2D();
        GradientPaint scaleBaseTopPaint = new GradientPaint(
                (float) scaleBaseTopShapeBounds.getCenterX(),
                (float) scaleBaseTopShapeBounds.getMaxY(),
                Color.LIGHT_GRAY,
                (float) scaleBaseTopShapeBounds.getCenterX(),
                (float) scaleBaseTopShapeBounds.getMinY(),
                Color.DARK_GRAY );
        PNode scaleBaseTop = new PhetPPath( scaleBaseTopShape.getGeneralPath(), scaleBaseTopPaint, STROKE, STROKE_PAINT );
        addChild( scaleBaseTop );

        // Add the shaft that connects the base to the weigh plate.
        DoubleGeneralPath connectingShaftShape = new DoubleGeneralPath();
        double connectingShaftDistanceFromTop = SIZE.getHeight() * 0.15;
        double connectingShaftWidth = 30;
        double connectingShaftHeight = 30;
        connectingShaftShape.moveTo( centerX - connectingShaftWidth / 2, connectingShaftDistanceFromTop );
        connectingShaftShape.lineTo( centerX - connectingShaftWidth / 2, connectingShaftDistanceFromTop + connectingShaftHeight );
        connectingShaftShape.quadTo( centerX, connectingShaftDistanceFromTop + connectingShaftHeight * 1.2, SIZE.getWidth() / 2 + connectingShaftWidth / 2, connectingShaftDistanceFromTop + connectingShaftHeight );
        connectingShaftShape.lineTo( centerX + connectingShaftWidth / 2, connectingShaftDistanceFromTop );
        Rectangle2D connectingShaftShapeBounds = connectingShaftShape.getGeneralPath().getBounds2D();
        GradientPaint connectingShaftPaint = new GradientPaint(
                (float) connectingShaftShapeBounds.getMinX(),
                (float) connectingShaftShapeBounds.getCenterY(),
                Color.LIGHT_GRAY,
                (float) connectingShaftShapeBounds.getMaxX(),
                (float) connectingShaftShapeBounds.getCenterY(),
                Color.DARK_GRAY );
        PNode connectingShaft = new PhetPPath( connectingShaftShape.getGeneralPath(), connectingShaftPaint, STROKE, STROKE_PAINT );
        addChild( connectingShaft );

        // Draw the top of the weigh plate.  This is meant to look like a
        // tilted rectangle.
        DoubleGeneralPath weighPlateTopShape = new DoubleGeneralPath();
        weighPlateTopShape.moveTo( centerX - WIEIGH_PLATE_WIDTH * 0.35, 0 );
        weighPlateTopShape.lineTo( centerX + WIEIGH_PLATE_WIDTH * 0.35, 0 );
        weighPlateTopShape.lineTo( centerX + WIEIGH_PLATE_WIDTH / 2, SIZE.getHeight() * 0.125 );
        weighPlateTopShape.lineTo( centerX - WIEIGH_PLATE_WIDTH / 2, SIZE.getHeight() * 0.125 );
        weighPlateTopShape.closePath();
        Rectangle2D weighPlateTopShapeBounds = weighPlateTopShape.getGeneralPath().getBounds2D();
        GradientPaint weighPlateTopPaint = new GradientPaint(
                (float) weighPlateTopShapeBounds.getCenterX(),
                (float) weighPlateTopShapeBounds.getMaxY(),
                Color.LIGHT_GRAY,
                (float) weighPlateTopShapeBounds.getCenterX(),
                (float) weighPlateTopShapeBounds.getMinY(),
                Color.DARK_GRAY );
        PNode scaleTop = new PhetPPath( weighPlateTopShape.getGeneralPath(), weighPlateTopPaint, STROKE, STROKE_PAINT );
        addChild( scaleTop );

        // Add the front of the weigh plate.
        Rectangle2D frontOfWeighPlateShape = new Rectangle2D.Double( centerX - WIEIGH_PLATE_WIDTH / 2,
                SIZE.getHeight() * 0.125, WIEIGH_PLATE_WIDTH, SIZE.getHeight() * 0.15 );
        addChild( new PhetPPath( frontOfWeighPlateShape, Color.LIGHT_GRAY, STROKE, STROKE_PAINT ) );
    }
}
