// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

/**
 * @author John Blanco
 */
public class AttachmentBar extends ShapeModelElement {
    public static final double WIDTH = 0.05; // In meters.
    private final Plank plank;

    /**
     * Constructor.
     *
     * @param plank The plank to which this bar is attached.  The fulcrum is not
     *              necessary, since the plank keeps track of both the pivot
     *              point and the attachment point.
     */
    public AttachmentBar( final Plank plank ) {
        super( generateShape( plank.getPivotPoint(), plank.bottomCenterPoint.get() ) );
        this.plank = plank;
        plank.bottomCenterPoint.addObserver( new VoidFunction1<Point2D>() {
            public void apply( Point2D point2D ) {
                setShape( generateShape( plank.getPivotPoint(), plank.bottomCenterPoint.get() ) );
            }
        } );
    }

    public Point2D getPivotPoint() {
        return plank.getPivotPoint();
    }

    /**
     * Get the angle of deflection.
     *
     * @return Zero if the bar is pointing straight down, positive if the bottom
     *         is to the right of the pivot point, negative if the bottom is to
     *         the left of the pivot point.
     */
    public double getDeflectionAngle() {
        return plank.getTiltAngle();
    }

    private static Shape generateShape( Point2D pivotPoint, Point2D attachmentPoint ) {
        double distance = pivotPoint.distance( attachmentPoint );
        DoubleGeneralPath path = new DoubleGeneralPath( 0, 0 );
        path.lineTo( WIDTH / 2, 0 );
        path.lineTo( WIDTH / 2, distance );
        path.lineTo( -WIDTH / 2, distance );
        path.lineTo( -WIDTH / 2, 0 );
        path.closePath();
        Shape shape = AffineTransform.getRotateInstance( Math.atan2( attachmentPoint.getY() - pivotPoint.getY(), attachmentPoint.getX() - pivotPoint.getX() ) - Math.PI / 2 ).createTransformedShape( path.getGeneralPath() );
        shape = AffineTransform.getTranslateInstance( pivotPoint.getX(), pivotPoint.getY() ).createTransformedShape( shape );
        return shape;
    }
}
