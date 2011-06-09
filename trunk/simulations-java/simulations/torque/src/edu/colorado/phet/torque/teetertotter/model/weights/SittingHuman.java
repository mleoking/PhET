// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.model.weights;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * Class that represents a sitting person in the model.  The person will
 * adjust their appearance based on the angle of the surface upon which they
 * are seated.  The person is situated such that no parts go below the
 * surface where they are sitting.
 *
 * @author John Blanco
 */
public class SittingHuman extends Weight {
    private static final double MASS = 20;
    private static final double HEAD_WIDTH = 0.15;
    private static final double HEAD_HEIGHT = 0.3;

    int numBricks = 1;

    public SittingHuman( Point2D initialCenterBottom ) {
        super( generateShape( initialCenterBottom, 0 ), MASS );
        this.numBricks = numBricks;
    }

    // Generate the shape for this object.  This is static so that it can be
    // used in the constructor.
    private static Shape generateShape( Point2D centerBottom, double rotationAngle ) {
        Shape shape = new Ellipse2D.Double( -HEAD_WIDTH / 2, -HEAD_HEIGHT / 2, HEAD_WIDTH, HEAD_HEIGHT );
        Shape translatedShape = AffineTransform.getTranslateInstance( centerBottom.getX(), centerBottom.getY() ).createTransformedShape( shape );
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
