/**
 * Class: ModelViewTransform2D
 * Package: edu.colorado.phet.common.view.graphics
 * Author: Another Guy
 * Date: Aug 29, 2003
 */
package edu.colorado.phet.semiconductor.phetcommon.view.graphics.transforms;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.semiconductor.util.math.PhetVector;

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
        if ( viewBounds.getWidth() <= 0 ) {
            throw new RuntimeException( "View Bounds width must be positive." );
        }
        if ( viewBounds.getHeight() <= 0 ) {
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

    public Point modelToView( Point2D.Double pt ) {
        return modelToView( pt.x, pt.y );
    }

    public Point modelToView( PhetVector pt ) {
        return modelToView( pt.toPoint2D() );
    }

    public int modelToViewX( double x ) {
        double m = viewBounds.width / modelBounds.width;
        int out = (int) ( m * ( x - modelBounds.x ) + viewBounds.x );
        return out;
    }

    public int modelToViewY( double y ) {
        double m = -viewBounds.height / modelBounds.height;
        int out = (int) ( m * ( y - modelBounds.y - modelBounds.height ) + viewBounds.y );
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

    public Rectangle2D.Double getModelBounds() {
        return modelBounds;
    }

    public void setViewBounds( Rectangle viewBounds ) {
        if ( viewBounds.getWidth() <= 0 ) {
            throw new RuntimeException( "View Bounds width must be positive." );
        }
        if ( viewBounds.getHeight() <= 0 ) {
            throw new RuntimeException( "View Bounds height must be positive." );
        }
        this.viewBounds = viewBounds;
        listeners.transformChanged( this );
    }

    public int modelToViewDifferentialY( double dy ) {
        double m = -viewBounds.height / modelBounds.height;
        return (int) ( m * dy );
    }

    public double viewToModelDifferentialY( double dy ) {
        double m = -modelBounds.height / viewBounds.height;
        return m * dy;
    }

    public double viewToModelDifferentialX( double dx ) {
        double m = modelBounds.width / viewBounds.width;
        return m * dx;
    }

    public Point2D.Double viewToModelDifferential( int dx, int dy ) {
        return new Point2D.Double( viewToModelDifferentialX( dx ), viewToModelDifferentialY( dy ) );
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

    public Shape createTransformedShape( Shape shape ) {
        return toAffineTransform().createTransformedShape( shape );
    }

}
