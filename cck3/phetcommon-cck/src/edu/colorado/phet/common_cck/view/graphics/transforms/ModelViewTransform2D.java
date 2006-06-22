/**
 * Class: ModelViewTransform2D
 * Package: edu.colorado.phet.common.view.graphics
 * Author: Another Guy
 * Date: Aug 29, 2003
 */
package edu.colorado.phet.common_cck.view.graphics.transforms;

import edu.colorado.phet.common_cck.math.AbstractVector2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class ModelViewTransform2D {
    private Rectangle2D.Double modelBounds;
    private Rectangle viewBounds;
    private CompositeTransformListener listeners = new CompositeTransformListener();
    private boolean forwardTransformDirty = true;
    private AffineTransform forwardTransform;
    private boolean backTransformDirty = true;
    private AffineTransform backTransform;
    private boolean invertY;

    /**
     * Constructs a forwardTransform from the specified model bounds to view bounds.
     *
     * @param modelBounds
     * @param viewBounds
     */
    public ModelViewTransform2D( Rectangle2D.Double modelBounds, Rectangle viewBounds ) {
        this( modelBounds, viewBounds, true );
    }

    public ModelViewTransform2D( Rectangle2D.Double modelBounds, Rectangle viewBounds, boolean invertY ) {
        setModelBounds( modelBounds );
        setViewBounds( viewBounds );
        this.invertY = invertY;
    }

    /**
     * Constructs a transform from two points in the model reference frame and two points
     * int the view reference frame.
     *
     * @param mp1 The point in the model frame that corresponds to vp1 in the view reference frame
     * @param mp2 The point in the model frame that corresponds to vp2 in the view reference frame
     * @param vp1 The point in the view frame that corresponds to mp1 in the model reference frame
     * @param vp2 The point in the view frame that corresponds to mp2 in the model reference frame
     */
    public ModelViewTransform2D( Point2D mp1, Point2D mp2,
                                 Point vp1, Point vp2 ) {
        Rectangle2D.Double mr = new Rectangle2D.Double( mp1.getX(), mp1.getY(), 0, 0 );
        mr.add( mp2 );
        Rectangle vr = new Rectangle( vp1.x, vp1.y, 0, 0 );
        vr.add( vp2 );
        setModelBounds( mr );
        setViewBounds( vr );
    }

    public void addTransformListener( TransformListener tl ) {
        listeners.addTransformListener( tl );
    }

    private static Point toPoint( Point2D pt ) {
        if( pt instanceof Point ) {
            return (Point)pt;
        }
        else {
            return new Point( (int)pt.getX(), (int)pt.getY() );
        }
    }

    /**
     * Transforms the model coordinate to the corresponding view coordinate.
     */
    public Point modelToView( double x, double y ) {
        return modelToView( new Point2D.Double( x, y ) );
    }

    public Point modelToView( Point2D pt ) {
        fixForwardTransform();
        Point2D out = forwardTransform.transform( pt, null );
        return toPoint( out );
    }

    private void fixForwardTransform() {
        if( forwardTransformDirty ) {
            forwardTransform = createForwardTransform();
            forwardTransformDirty = false;
        }
    }

    protected AffineTransform createForwardTransform() {
        if( invertY ) {
            return createTXInvertY( viewBounds, modelBounds );
        }
        else {
            return createTX( viewBounds, modelBounds );
        }
    }

    public Point modelToView( AbstractVector2D vec ) {
        return modelToView( vec.toPoint2D() );
    }

    public int modelToViewX( double x ) {
        return modelToView( new Point2D.Double( x, 0 ) ).x;
    }

    public int modelToViewY( double y ) {
        return modelToView( new Point2D.Double( 0, y ) ).y;
    }

    public static AffineTransform createTX( Rectangle viewBounds, Rectangle2D.Double modelBounds ) {
        double m00 = viewBounds.width / modelBounds.width;
        double m02 = viewBounds.x - m00 * modelBounds.x;
        double m11 = viewBounds.height / modelBounds.height;
        double m12 = viewBounds.y - m11 * modelBounds.y;
        return new AffineTransform( m00, 0, 0, m11, m02, m12 );
    }

    public static AffineTransform createTXInvertY( Rectangle viewBounds, Rectangle2D.Double modelBounds ) {
        double m00 = viewBounds.width / modelBounds.width;
        double m11 = -viewBounds.height / modelBounds.height;
        double m02 = viewBounds.x - m00 * modelBounds.x;
        double m12 = viewBounds.y + viewBounds.height / modelBounds.height * ( modelBounds.y + modelBounds.height );
        return new AffineTransform( m00, 0, 0, m11, m02, m12 );
    }

    public Point2D viewToModel( int x, int y ) {
        return viewToModel( new Point( x, y ) );
    }

    public Point2D viewToModel( Point pt ) {
        fixBackTransform();
        return backTransform.transform( pt, null );
    }

    private void fixBackTransform() {
        if( backTransformDirty ) {
            backTransform = createBackTransform();
            backTransformDirty = false;
        }
    }

    private AffineTransform createBackTransform() {
        fixForwardTransform();
        try {
            return forwardTransform.createInverse();
        }
        catch( NoninvertibleTransformException e ) {
            throw new RuntimeException( e );
        }
    }

    public double viewToModelY( int y ) {
        return viewToModel( 0, y ).getY();
    }

    public double viewToModelX( int x ) {
        return viewToModel( x, 0 ).getX();
    }

    public Rectangle2D.Double getModelBounds() {
        return modelBounds;
    }

    public void setModelBounds( Rectangle2D.Double modelBounds ) {
        if( modelBounds.width <= 0 ) {
            throw new RuntimeException( "Model Width <= 0" );
        }
        else if( modelBounds.height <= 0 ) {
            throw new RuntimeException( "Model height<= 0" );
        }
        this.modelBounds = modelBounds;
        forwardTransformDirty = true;
        backTransformDirty = true;
        listeners.transformChanged( this );
    }

    public void setViewBounds( Rectangle viewBounds ) {
        if( viewBounds.getWidth() <= 0 ) {
            throw new RuntimeException( "View Bounds width must be positive." );
        }
        if( viewBounds.getHeight() <= 0 ) {
            throw new RuntimeException( "View Bounds height must be positive." );
        }
        forwardTransformDirty = true;
        backTransformDirty = true;
        this.viewBounds = viewBounds;
        listeners.transformChanged( this );
    }

    public Rectangle getViewBounds() {
        return viewBounds;
    }

    public int modelToViewDifferentialY( double dy ) {
        return modelToViewDifferential( 0, dy ).y;
    }

    public int modelToViewDifferentialX( double dx ) {
        return modelToViewDifferential( dx, 0 ).x;
    }

    public double viewToModelDifferentialY( int dy ) {
        return viewToModelDifferential( 0, dy ).getY();
    }

    public double viewToModelDifferentialX( int dx ) {
        return viewToModelDifferential( dx, 0 ).getX();
    }

    public Point2D viewToModelDifferential( Point rel ) {
        fixBackTransform();
        return backTransform.deltaTransform( rel, null );
    }

    public Point2D viewToModelDifferential( int dx, int dy ) {
        return viewToModelDifferential( new Point( dx, dy ) );
    }

    public Point modelToViewDifferential( double dx, double dy ) {
        return modelToViewDifferential( new Point2D.Double( dx, dy ) );
    }

    private Point modelToViewDifferential( Point2D.Double pt ) {
        fixForwardTransform();
        return toPoint( forwardTransform.deltaTransform( pt, null ) );
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
        fixForwardTransform();
        return forwardTransform.createTransformedShape( shape );
    }

    public void removeTransformListener( TransformListener transformListener ) {
        listeners.removeTransformListener( transformListener );
    }

    public AffineTransform getAffineTransform() {
        fixForwardTransform();
        return forwardTransform;
    }

    public AffineTransform getInverseTransform() {
        fixBackTransform();
        return backTransform;
    }

    public int numTransformListeners() {
        return this.listeners.numTransformListeners();
    }

    public TransformListener[] getTransformListeners() {
        return this.listeners.getTransformListeners();
    }
}
