package edu.colorado.phet.common.phetcommon.view.graphics.transforms;

import java.awt.Shape;
import java.awt.geom.*;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * Provides a mapping between model and view coordinates.  Convenience constructors and methods around AffineTransform.
 *
 * @author Jon Olson
 * @author Sam Reid
 */
public class ModelViewTransform {
    private AffineTransform transform;

    private ModelViewTransform( AffineTransform transform ) {
        this.transform = transform;
    }

    /*---------------------------------------------------------------------------*
    * Factory methods
    *----------------------------------------------------------------------------*/

    public static ModelViewTransform getIdentity() {
        return new ModelViewTransform( new AffineTransform() );
    }

    public static ModelViewTransform getOffsetScaleMapping( Point2D.Double offset, double scale ) {
        return new ModelViewTransform( new AffineTransform( scale, 0, 0, scale, offset.getX(), offset.getY() ) );
    }

    public static ModelViewTransform getOffsetScaleMapping( Point2D.Double offset, double xScale, double yScale ) {
        return new ModelViewTransform( new AffineTransform( xScale, 0, 0, yScale, offset.getX(), offset.getY() ) );
    }

    public static ModelViewTransform getSinglePointScaleMapping( Point2D.Double modelPoint, Point2D.Double viewPoint, double xScale, double yScale ) {
        // mx * scale + ox = vx
        // my * scale + oy = vy
        double offsetX = viewPoint.getX() - modelPoint.getX() * xScale;
        double offsetY = viewPoint.getY() - modelPoint.getY() * yScale;
        return getOffsetScaleMapping( new Point2D.Double( offsetX, offsetY ), xScale, yScale );
    }

    public static ModelViewTransform getSinglePointScaleMapping( Point2D.Double modelPoint, Point2D.Double viewPoint, double scale ) {
        return getSinglePointScaleMapping( modelPoint, viewPoint, scale, scale );
    }

    public static ModelViewTransform getSinglePointScaleInvertedYMapping( Point2D.Double modelPoint, Point2D.Double viewPoint, double scale ) {
        return getSinglePointScaleMapping( modelPoint, viewPoint, scale, -scale );
    }

    public static ModelViewTransform getRectangleMapping( Rectangle2D.Double modelBounds, Rectangle2D.Double viewBounds ) {
        double m00 = viewBounds.getWidth() / modelBounds.getWidth();
        double m02 = viewBounds.getX() - m00 * modelBounds.getX();
        double m11 = viewBounds.getHeight() / modelBounds.getHeight();
        double m12 = viewBounds.getY() - m11 * modelBounds.getY();
        return new ModelViewTransform( new AffineTransform( m00, 0, 0, m11, m02, m12 ) );
    }

    public static ModelViewTransform getRectangleInvertedYMapping( Rectangle2D.Double modelBounds, Rectangle2D.Double viewBounds ) {
        double m00 = viewBounds.getWidth() / modelBounds.getWidth();
        double m02 = viewBounds.getX() - m00 * modelBounds.getX();
        double m11 = -viewBounds.getHeight() / modelBounds.getHeight();
        // vY == (mY + mHeight) * m11 + m12
        double m12 = viewBounds.getY() - m11 * modelBounds.getMaxY();
        return new ModelViewTransform( new AffineTransform( m00, 0, 0, m11, m02, m12 ) );
    }


    /**
     * Returns a defensive copy of the AffineTransform in the ModelViewTransform.
     *
     * @return
     */
    public AffineTransform getTransform() {
        return new AffineTransform( transform );
    }

    /*---------------------------------------------------------------------------*
    * Model To View transforms
    *----------------------------------------------------------------------------*/

    public Point2D modelToView( Point2D pt ) {
        return transform.transform( pt, null );
    }

    public ImmutableVector2D modelToView( ImmutableVector2D vector2D ) {
        return new ImmutableVector2D( transform.transform( vector2D.toPoint2D(), null ) );
    }

    public Point2D modelToViewDelta( Point2D delta ) {
        return transform.deltaTransform( delta, null );
    }

    public ImmutableVector2D modelToViewDelta( ImmutableVector2D delta ) {
        return new ImmutableVector2D( modelToViewDelta( delta.toPoint2D() ) );
    }

    public Shape modelToView( Shape shape ) {
        return transform.createTransformedShape( shape );
    }


    public double modelToViewX( double x ) {
        return modelToView( new Point2D.Double( x, 0 ) ).getX();
    }

    public double modelToViewY( double y ) {
        return modelToView( 0, y ).getY();
    }

    public Point2D modelToView( double x, double y ) {
        return modelToView( new Point2D.Double( x, y ) );
    }

    public double modelToViewDeltaX( double x ) {
        return modelToViewDelta( new Point2D.Double( x, 0 ) ).getX();
    }

    public double modelToViewDeltaY( double y ) {
        return modelToViewDelta( new Point2D.Double( 0, y ) ).getY();
    }

    public Dimension2D modelToViewDelta( Dimension2D delta ) {
        final Point2D pt = modelToViewDelta( new Point2D.Double( delta.getWidth(), delta.getHeight() ) );
        return new Dimension2DDouble( pt.getX(), pt.getY() );
    }

    /*---------------------------------------------------------------------------*
    * View to Model transforms
    *----------------------------------------------------------------------------*/

    public Point2D viewToModel( Point2D pt ) {
        try {
            return transform.createInverse().transform( pt, null );
        }
        catch ( NoninvertibleTransformException e ) {
            throw new RuntimeException( e );
        }
    }

    public ImmutableVector2D viewToModel( ImmutableVector2D vector2D ) {
        return new ImmutableVector2D( viewToModel( vector2D.toPoint2D() ) );
    }

    public Point2D viewToModelDelta( Point2D delta ) {
        try {
            return transform.createInverse().deltaTransform( delta, null );
        }
        catch ( NoninvertibleTransformException e ) {
            throw new RuntimeException( e );
        }
    }

    public ImmutableVector2D viewToModelDelta( ImmutableVector2D delta ) {
        return new ImmutableVector2D( viewToModelDelta( delta.toPoint2D() ) );
    }


    public double viewToModelX( double x ) {
        return viewToModel( x, 0 ).getX();
    }

    public double viewToModelY( double y ) {
        return viewToModel( 0, y ).getY();
    }

    public Point2D viewToModel( double x, double y ) {
        return viewToModel( new Point2D.Double( x, y ) );
    }

    public Dimension2D viewToModelDelta( Dimension2D delta ) {
        final Point2D pt = viewToModelDelta( new Point2D.Double( delta.getWidth(), delta.getHeight() ) );
        return new Dimension2DDouble( pt.getX(), pt.getY() );
    }

    /**
     * Inner implementation of Dimension2D since one is not provided by awt.
     */
    public static class Dimension2DDouble extends Dimension2D {
        public double width;
        public double height;

        public Dimension2DDouble( double width, double height ) {
            this.width = width;
            this.height = height;
        }
        
        public Dimension2DDouble( Dimension2D size ) {
            this( size.getWidth(), size.getHeight() );
        }

        @Override
        public double getWidth() {
            return width;
        }

        @Override
        public double getHeight() {
            return height;
        }

        @Override
        public void setSize( double width, double height ) {
            this.width = width;
            this.height = height;
        }
        
        @Override
        public String toString() {
            final StringBuffer result = new StringBuffer();
            result.append( super.toString().replaceAll( ".*\\.", "" ) ); // abbreviate the class name
            result.append( '[' );
            result.append( "width=" );
            result.append( width );
            result.append( ",height=" );
            result.append( height );
            result.append( ']' );
            return result.toString();
        }
    }
}
