// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.model.weights;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * @author John Blanco
 */
public class Brick extends Weight {
    private static final double WIDTH = 0.2;
    private static final double HEIGHT = WIDTH / 3;
    private static final double MASS = 20; // In kg.  Yeah, it's one heavy brick.

    public Brick( Point2D initialCenterBottom ) {
        super( generateShape( initialCenterBottom, 0 ), MASS );
    }

    // Generate the shape for this object.  This is static so that it can be
    // used in the constructor.
    private static Shape generateShape( Point2D centerBottom, double rotationAngle ) {
        Rectangle2D rect = new Rectangle2D.Double( -WIDTH / 2, -HEIGHT / 2, WIDTH, HEIGHT );
        Shape rotatedShape = AffineTransform.getRotateInstance( rotationAngle ).createTransformedShape( rect );
        Shape translatedShape = AffineTransform.getTranslateInstance( centerBottom.getX(), centerBottom.getY() + HEIGHT / 2 ).createTransformedShape( rotatedShape );
        return translatedShape;
    }

    @Override public void translate( ImmutableVector2D modelDelta ) {
        positionHandle.setLocation( modelDelta.getAddedInstance( positionHandle.getX(), positionHandle.getY() ).toPoint2D() );
        updateShape();
    }

    @Override public void setPosition( double x, double y ) {
        positionHandle.setLocation( x, y );
        updateShape();
    }

    private void updateShape() {
        setShapeProperty( generateShape( positionHandle, rotationAngle ) );
    }

    @Override public void setRotationAngle( double angle ) {
        super.setRotationAngle( angle );
        updateShape();
    }
}
