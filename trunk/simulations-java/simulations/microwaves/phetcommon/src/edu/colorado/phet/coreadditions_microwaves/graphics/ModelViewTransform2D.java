/**
 * Class: ModelViewTransform2D
 * Package: edu.colorado.phet.common.view.graphics
 * Author: Another Guy
 * Date: Aug 29, 2003
 */
package edu.colorado.phet.coreadditions_microwaves.graphics;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class ModelViewTransform2D {
    private Rectangle2D.Double modelBounds;
    private Rectangle viewBounds;
    private CompositeTransformListener listeners = new CompositeTransformListener();

    /**
     * Constructs a ModelViewTransform2d2 with model rectangle {0,0,1,1}.
     * @param viewBounds
     */
//    public ModelViewTransform2D(Rectangle viewBounds) {
//        this(new Rectangle2D.Double(0, 0, 1, 1), viewBounds);
//    }

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

    public Point modelToView( Point2D.Double pt ) {
        return modelToView( pt.x, pt.y );
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

    public AffineTransform toAffineTransform() {
        return toAffineTransform( new Point2D.Double() );
    }

    /**
     * Creates a new AffineTransform that corresponds to this transformation.
     *
     * @return a new AffineTransform that corresponds to this transformation.
     */
    public AffineTransform toAffineTransform( Point2D.Double src ) {
//        Point2D.Double p = new Point2D.Double( 0, 0 );

//        System.out.println( "src = " + src +"____________");
//        System.out.println( "modelBounds = " + modelBounds );
//        System.out.println( "viewBounds = " + viewBounds );
//
//        AffineTransform aTx = new AffineTransform();
//        double sx = viewBounds.width / modelBounds.width;
//        double sy = viewBounds.height / modelBounds.height;
//
//        Point2D p2 = aTx.transform( src, null );
//
//        System.out.println("after identity, p2="+p2);
//
//        // Move to origin
//        aTx.translate( -viewBounds.x, -viewBounds.y );
//        p2 = aTx.transform( src, null );
//        System.out.println("after translate, p2="+p2);
//
//        // Scale to model coords
//        aTx.scale( sx, sy );
//        p2 = aTx.transform( src, null );
//        System.out.println("after scale, p2="+p2);
//
//        // Flip it
//        aTx.scale( 1, -1 );
//        p2 = aTx.transform( src, null );
//        System.out.println("after flip, p2="+p2);
//
//        // Translate back to final resting place
//        aTx.translate( modelBounds.x, -(modelBounds.y + modelBounds.height) );
//        p2 = aTx.transform( src, null );
//        System.out.println("after translate, p2="+p2);
//
//        return aTx;

//        aTx.scale( sx, sy );
//        AffineTransform scaling=AffineTransform.getScaleInstance(sx,sy);
//
//        double tx=(viewBounds.x-modelBounds.x*sx);
//        double ty=(viewBounds.y-modelBounds.y*sy);
//        AffineTransform translate=AffineTransform.getTranslateInstance(tx,ty);

        //////// old code below
//        AffineTransform flip=AffineTransform.getScaleInstance(1,-1);

//        AffineTransform total=new AffineTransform(scaling);
//        total.preConcatenate(translate);
////        total.concatenate(flip);
//        return total;

//        AffineTransform translation=

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

}
