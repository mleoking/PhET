/**
 * Class: ModelViewTransform2D
 * Package: edu.colorado.phet.common.view.graphics
 * Author: Another Guy
 * Date: Aug 29, 2003
 */
package edu.colorado.phet.common.view.graphics.transforms;

//import edu.colorado.phet.common.math.PhetVector;

import edu.colorado.phet.common.math.ImmutableVector2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class ModelViewTransform2D {
    private Rectangle2D.Double modelBounds;
    private Rectangle viewBounds;
    private CompositeTransformListener listeners = new CompositeTransformListener();

    /**
     * Constructs a transform from the specified model bounds to view bounds.
     *
     * @param modelBounds
     * @param viewBounds
     */
    public ModelViewTransform2D( Rectangle2D.Double modelBounds, Rectangle viewBounds ) {
        this.modelBounds = modelBounds;
        this.viewBounds = viewBounds;
        if( viewBounds.getWidth() <= 0 ) {
            throw new RuntimeException( "View Bounds width must be positive." );
        }
        if( viewBounds.getHeight() <= 0 ) {
            throw new RuntimeException( "View Bounds height must be positive." );
        }
    }

    public void addTransformListener( TransformListener tl ) {
        listeners.addTransformListener( tl );
    }

    /**
     * Transforms the model coordinate to the corresponding view coordinate.
     */
    public Point modelToView( double x, double y ) {
        return new Point( modelToViewX( x ), modelToViewY( y ) );
    }

    public Point modelToView( Point2D pt ) {
        //    public Point modelToView( Point2D.Double pt ) {
        return modelToView( pt.getY(), pt.getY() );
        //        return modelToView( pt.x, pt.y );
    }

    public Point modelToView( ImmutableVector2D pt ) {
        return modelToView( pt.toPoint2D() );
    }

    public int modelToViewX( double x ) {
        double m = viewBounds.width / modelBounds.width;
        int out = (int)( m * ( x - modelBounds.x ) + viewBounds.x );
        return out;
    }

    public int modelToViewY( double y ) {
        double m = -viewBounds.height / modelBounds.height;
        int out = (int)( m * ( y - modelBounds.y - modelBounds.height ) + viewBounds.y );
        return out;
    }

    /**
     * Creates a new AffineTransform that corresponds to this transformation.
     *
     * @return a new AffineTransform that corresponds to this transformation.
     */
    public AffineTransform toAffineTransform() {
        double m00 = viewBounds.width / modelBounds.width;
        double m01 = 0;
        double m02 = viewBounds.x - m00 * modelBounds.x;
        double m10 = 0;
        double m11 = -viewBounds.height / modelBounds.height;
        double m12 = viewBounds.y + viewBounds.height / modelBounds.height * ( modelBounds.y + modelBounds.height );
        //        double m12 =viewBounds.y-m11*modelBounds.y;
        return new AffineTransform( m00, m10, m01, m11, m02, m12 );
    }

    public Point2D.Double viewToModel( int x, int y ) {
        return new Point2D.Double( viewToModelX( x ), viewToModelY( y ) );
    }

    public Point2D.Double viewToModel( Point pt ) {
        return viewToModel( pt.x, pt.y );
    }

    public double viewToModelY( int y ) {
        double m = -viewBounds.height / modelBounds.height;
        double out = ( y - viewBounds.y ) / m + modelBounds.height + modelBounds.y;
        return out;
    }

    public double viewToModelX( double x ) {
        double m = modelBounds.width / viewBounds.width;
        return m * ( x - viewBounds.x ) + modelBounds.x;
    }

    public Rectangle2D.Double getModelBounds() {
        return modelBounds;
    }

    public void setModelBounds( Rectangle2D.Double modelBounds ) {
        this.modelBounds = modelBounds;
        listeners.transformChanged( this );
    }

    public void setViewBounds( Rectangle viewBounds ) {
        if( viewBounds.getWidth() <= 0 ) {
            throw new RuntimeException( "View Bounds width must be positive." );
        }
        if( viewBounds.getHeight() <= 0 ) {
            throw new RuntimeException( "View Bounds height must be positive." );
        }
        this.viewBounds = viewBounds;
        listeners.transformChanged( this );
    }

    public Rectangle getViewBounds() {
        return viewBounds;
    }

    public int modelToViewDifferentialY( double dy ) {
        double m = -viewBounds.height / modelBounds.height;
        return (int)( m * dy );
    }

    public int modelToViewDifferentialX( double dx ) {
        double m = viewBounds.width / modelBounds.width;
        return (int)( m * dx );
    }

    public double viewToModelDifferentialY( double dy ) {
        double m = -modelBounds.height / viewBounds.height;
        return m * dy;
    }

    public double viewToModelDifferentialX( double dx ) {
        double m = modelBounds.width / viewBounds.width;
        return m * dx;
    }

    public Point2D.Double viewToModelDifferential( Point rel ) {
        return viewToModelDifferential( rel.x, rel.y );
    }

    public Point2D.Double viewToModelDifferential( int dx, int dy ) {
        return new Point2D.Double( viewToModelDifferentialX( dx ), viewToModelDifferentialY( dy ) );
    }

    public Point modelToViewDifferential( double dx, double dy ) {
        return new Point( modelToViewDifferentialX( dx ), modelToViewDifferentialY( dy ) );
    }

    /**
     * Converts a model rectangle to the corresponding view rectangle.
     */
    public Rectangle modelToView( Rectangle2D.Double modelRect ) {
        Point cornerA = modelToView( modelRect.x, modelRect.y );
        Point cornerB = modelToView( modelRect.x + modelRect.width, modelRect.y + modelRect.height );
        Rectangle out = new Rectangle( cornerA.x, cornerA.y, 0, 0 );
        out.add( cornerB );
        return out;
    }

    public static AffineTransform getFlippedTx( Rectangle2D.Double modelBounds, Rectangle viewBounds ) {
        AffineTransform aTx = new AffineTransform();
        aTx.translate( viewBounds.getMinX(), viewBounds.getMinY() );
        aTx.scale( viewBounds.getWidth() / modelBounds.getWidth(), viewBounds.getHeight() / -modelBounds.getHeight() );
        aTx.translate( -modelBounds.getMinX(), -modelBounds.getMaxY() );
        return aTx;
    }

    public static AffineTransform createTransform( Rectangle2D.Double inputBounds, Rectangle2D.Double outputBounds ) {
        double m00 = outputBounds.width / inputBounds.width;
        double m01 = 0;
        double m02 = outputBounds.x - m00 * inputBounds.x;
        double m10 = 0;
        double m11 = -outputBounds.height / inputBounds.height;
        double m12 = outputBounds.y + outputBounds.height / inputBounds.height * ( inputBounds.y + inputBounds.height );
        //        double m12 =outputBounds.y-m11*inputBounds.y;
        return new AffineTransform( m00, m10, m01, m11, m02, m12 );
    }

    public Shape createTransformedShape( Shape shape ) {
        return toAffineTransform().createTransformedShape( shape );
    }

    public void removeTransformListener( TransformListener transformListener ) {
        listeners.removeTransformListener( transformListener );
    }
}
