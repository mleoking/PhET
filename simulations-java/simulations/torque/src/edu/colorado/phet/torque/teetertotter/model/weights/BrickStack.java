// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.model.weights;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

/**
 * Class that represents a stack of bricks in the model.  Note that a single
 * brick is represented as a stack of size 1.
 *
 * @author John Blanco
 */
public class BrickStack extends Weight {
    private static final double BRICK_WIDTH = 0.2; // In meters.
    private static final double BRICK_HEIGHT = BRICK_WIDTH / 3;
    public static final double BRICK_MASS = 20; // In kg.  Yeah, it's one heavy brick.

    int numBricks = 1;

    public BrickStack( int numBricks, Point2D initialCenterBottom ) {
        super( generateShape( numBricks, initialCenterBottom, 0 ), numBricks * BRICK_MASS );
        this.numBricks = numBricks;
    }

    // Generate the shape for this object.  This is static so that it can be
    // used in the constructor.
    private static Shape generateShape( int numBricks, Point2D centerBottom, double rotationAngle ) {
        Point2D brickOrigin = new Point2D.Double( 0, 0 );
        // Create a path that represents a stack of bricks.
        DoubleGeneralPath brickPath = new DoubleGeneralPath();
        for ( int i = 0; i < numBricks; i++ ) {
            // Draw the brick.
            brickPath.moveTo( brickOrigin.getX(), brickOrigin.getY() );
            brickPath.lineTo( brickOrigin.getX() + BRICK_WIDTH / 2, brickOrigin.getY() );
            brickPath.lineTo( brickOrigin.getX() + BRICK_WIDTH / 2, brickOrigin.getY() + BRICK_HEIGHT );
            brickPath.lineTo( brickOrigin.getX() - BRICK_WIDTH / 2, brickOrigin.getY() + BRICK_HEIGHT );
            brickPath.lineTo( brickOrigin.getX() - BRICK_WIDTH / 2, brickOrigin.getY() );
            brickPath.lineTo( brickOrigin.getX(), brickOrigin.getY() );
            // Move the origin to the next brick.
            brickOrigin.setLocation( brickOrigin.getX(), brickOrigin.getY() + BRICK_HEIGHT );
        }
        Shape rotatedShape = AffineTransform.getRotateInstance( rotationAngle ).createTransformedShape( brickPath.getGeneralPath() );
        Shape translatedShape = AffineTransform.getTranslateInstance( centerBottom.getX(), centerBottom.getY() ).createTransformedShape( rotatedShape );
        return translatedShape;
    }

//    private static Shape generateShape( int numBricks, Point2D centerBottom, double rotationAngle ) {
//        Point2D brickOrigin = new Point2D.Double( 0, 0 );
//        // Draw the brick.
//        GeneralPath brickPath = new GeneralPath();
//        brickPath.moveTo( brickOrigin.getX(), brickOrigin.getY() );
//        brickPath.lineTo( brickOrigin.getX() + BRICK_WIDTH / 2, brickOrigin.getY() );
//        brickPath.lineTo( brickOrigin.getX() + BRICK_WIDTH / 2, brickOrigin.getY() + BRICK_HEIGHT );
//        brickPath.lineTo( brickOrigin.getX() - BRICK_WIDTH / 2, brickOrigin.getY() + BRICK_HEIGHT );
//        brickPath.lineTo( brickOrigin.getX() - BRICK_WIDTH / 2, brickOrigin.getY() );
//        brickPath.lineTo( brickOrigin.getX(), brickOrigin.getY() );
//        brickPath.closePath();
//        // Move the origin to the next brick.
//        Shape rotatedShape = AffineTransform.getRotateInstance( rotationAngle ).createTransformedShape( brickPath );
//        Shape translatedShape = AffineTransform.getTranslateInstance( centerBottom.getX(), centerBottom.getY() ).createTransformedShape( rotatedShape );
//        return translatedShape;
//    }

    @Override public void translate( ImmutableVector2D modelDelta ) {
        positionHandle.setLocation( modelDelta.getAddedInstance( positionHandle.getX(), positionHandle.getY() ).toPoint2D() );
        updateShape();
    }

    @Override public void setPosition( double x, double y ) {
        positionHandle.setLocation( x, y );
        updateShape();
    }

    private void updateShape() {
        setShapeProperty( generateShape( numBricks, positionHandle, rotationAngle ) );
    }

    @Override public void setRotationAngle( double angle ) {
        super.setRotationAngle( angle );
        updateShape();
    }
}
