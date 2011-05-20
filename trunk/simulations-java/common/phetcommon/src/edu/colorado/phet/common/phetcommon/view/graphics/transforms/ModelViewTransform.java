// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.view.graphics.transforms;

import java.awt.*;
import java.awt.geom.*;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
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

    public static ModelViewTransform createIdentity() {
        return new ModelViewTransform( new AffineTransform() );
    }

    public static ModelViewTransform createOffsetScaleMapping( Point2D offset, double scale ) {
        return new ModelViewTransform( new AffineTransform( scale, 0, 0, scale, offset.getX(), offset.getY() ) );
    }

    public static ModelViewTransform createOffsetScaleMapping( Point2D offset, double xScale, double yScale ) {
        return new ModelViewTransform( new AffineTransform( xScale, 0, 0, yScale, offset.getX(), offset.getY() ) );
    }

    public static ModelViewTransform createSinglePointScaleMapping( Point2D modelPoint, Point2D viewPoint, double xScale, double yScale ) {
        // mx * scale + ox = vx
        // my * scale + oy = vy
        double offsetX = viewPoint.getX() - modelPoint.getX() * xScale;
        double offsetY = viewPoint.getY() - modelPoint.getY() * yScale;
        return createOffsetScaleMapping( new Point2D.Double( offsetX, offsetY ), xScale, yScale );
    }

    public static ModelViewTransform createSinglePointScaleMapping( Point2D modelPoint, Point2D viewPoint, double scale ) {
        return createSinglePointScaleMapping( modelPoint, viewPoint, scale, scale );
    }

    public static ModelViewTransform createSinglePointScaleInvertedYMapping( Point2D modelPoint, Point2D viewPoint, double scale ) {
        return createSinglePointScaleMapping( modelPoint, viewPoint, scale, -scale );
    }

    public static ModelViewTransform createRectangleMapping( Rectangle2D modelBounds, Rectangle2D viewBounds ) {
        double m00 = viewBounds.getWidth() / modelBounds.getWidth();
        double m02 = viewBounds.getX() - m00 * modelBounds.getX();
        double m11 = viewBounds.getHeight() / modelBounds.getHeight();
        double m12 = viewBounds.getY() - m11 * modelBounds.getY();
        return new ModelViewTransform( new AffineTransform( m00, 0, 0, m11, m02, m12 ) );
    }

    public static ModelViewTransform createRectangleInvertedYMapping( Rectangle2D modelBounds, Rectangle2D viewBounds ) {
        double m00 = viewBounds.getWidth() / modelBounds.getWidth();
        double m02 = viewBounds.getX() - m00 * modelBounds.getX();
        double m11 = -viewBounds.getHeight() / modelBounds.getHeight();
        // vY == (mY + mHeight) * m11 + m12
        double m12 = viewBounds.getY() - m11 * modelBounds.getMaxY();
        return new ModelViewTransform( new AffineTransform( m00, 0, 0, m11, m02, m12 ) );
    }

    /*---------------------------------------------------------------------------*
     * Accessors
     *---------------------------------------------------------------------------*/

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

    //Transforms a size in the model to a size in the view (without potentially inverting axes)
    public Dimension2D modelToViewSize( Dimension2D modelSize ) {
        Rectangle2D viewShape = modelToView( new Rectangle2D.Double( 0, 0, modelSize.getWidth(), modelSize.getHeight() ) ).getBounds2D();
        return new Dimension2DDouble( viewShape.getWidth(), viewShape.getHeight() );
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

    /**
     * Transform a rectangle from model to view coordinates, with ranges [x, x + width] and [y, y + height] with width>=0, height>=0
     *
     * @param r Input rectangle
     * @return Transformed rectangle
     */
    public Rectangle2D modelToViewRectangle( Rectangle2D r ) {
        return modelToView( r ).getBounds2D();
    }

    /*---------------------------------------------------------------------------*
    * View to Model transforms
    *----------------------------------------------------------------------------*/

    public Point2D viewToModel( Point2D pt ) {
        return getInverseTransform().transform( pt, null );
    }

    public ImmutableVector2D viewToModel( ImmutableVector2D vector2D ) {
        return new ImmutableVector2D( viewToModel( vector2D.toPoint2D() ) );
    }

    public Point2D viewToModelDelta( Point2D delta ) {
        return getInverseTransform().deltaTransform( delta, null );
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

    protected AffineTransform getInverseTransform() {
        try {
            return transform.createInverse();
        }
        catch ( NoninvertibleTransformException e ) {
            throw new RuntimeException( e );
        }
    }

    public Shape viewToModel( Shape shape ) {
        return getInverseTransform().createTransformedShape( shape );
    }

    public double viewToModelDeltaX( double x ) {
        return viewToModelDelta( new Point2D.Double( x, 0 ) ).getX();
    }

    public double viewToModelDeltaY( double y ) {
        return viewToModelDelta( new Point2D.Double( 0, y ) ).getY();
    }

    public Dimension2DDouble viewToModel( Dimension2D delta ) {
        final Point2D point2D = viewToModel( delta.getWidth(), delta.getHeight() );
        return new Dimension2DDouble( point2D.getX(), point2D.getY() );
    }

    /**
     * Transform a rectangle from view to model coordinates, with ranges [x, x + width] and [y, y + height] with width>=0, height>=0
     *
     * @param r Input rectangle
     * @return Transformed rectangle
     */
    public Rectangle2D viewToModelRectangle( Rectangle2D r ) {
        return viewToModel( r ).getBounds2D();
    }

    //Machine generated (and fine-tuned) equality test
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null ) {
            return false;
        }

        if ( !transform.equals( ( (ModelViewTransform) o ).transform ) ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return transform.hashCode();
    }

    public ImmutableRectangle2D modelToView( ImmutableRectangle2D modelRect ) {
        return new ImmutableRectangle2D( modelToView( modelRect.toRectangle2D() ) );
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
