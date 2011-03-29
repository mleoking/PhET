// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.common.phetcommon.math.Point3D;

/**
 * ModelViewTransform provides the transforms between model and view coordinate systems.
 * In both coordinate systems, +x is to the right, +y is down, +z is away from the viewer.
 * Sign of rotation angles is specified using the right-hand rule.
 * <p>
 * <code>
 *   +y
 *    ^    +z
 *    |   /
 *    |  /
 *    | /
 *    +-------> +x
 * </code>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CLModelViewTransform3D {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private final AffineTransform modelToViewTransform2D;
    private final AffineTransform viewToModelTransform2D;
    private final double pitch, yaw;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public CLModelViewTransform3D() {
        this( CLConstants.MVT_SCALE, CLConstants.MVT_PITCH, CLConstants.MVT_YAW );
    }

    /**
     * Constructor.
     *
     * @param scale scale for mapping from model to view (x and y scale are identical)
     * @param offset translation for mapping from model to view, in model coordinates
     * @param pitch rotation about the horizontal (x) axis, sign determined using the right-hand rule (radians)
     * @param yaw rotation about the vertical (y) axis, sign determined using the right-hand rule (radians)
     */
    public CLModelViewTransform3D( double scale, double pitch, double yaw ) {

        modelToViewTransform2D = new AffineTransform();
        modelToViewTransform2D.scale( scale, scale );

        viewToModelTransform2D = new AffineTransform();
        viewToModelTransform2D.scale( 1d / scale, 1d / scale );

        this.pitch = pitch;
        this.yaw = yaw;
    }

    public double getYaw() {
        return yaw;
    }

    //----------------------------------------------------------------------------
    // Model-to-view transforms
    //----------------------------------------------------------------------------

    /**
     * Maps a point from 3D model coordinates to 2D view coordinates.
     *
     * @param pModel
     * @param pView
     * @return
     */
    public Point2D modelToView( Point3D pModel ) {
        double xModel = pModel.getX() + ( pModel.getZ() * Math.sin( pitch ) * Math.cos( yaw ) );
        double yModel = pModel.getY() + ( pModel.getZ() * Math.sin( pitch ) * Math.sin( yaw ) );
        return modelToViewTransform2D.transform( new Point2D.Double( xModel, yModel ), null );
    }

    /**
     * Maps a point from 3D model coordinates to 2D view coordinates.
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public Point2D modelToView( double x, double y, double z ) {
        return modelToView( new Point3D.Double( x, y, z ) );
    }

    /**
     * Maps a delta from 3D model coordinates to 2D view coordinates.
     *
     * @param delta
     * @return
     */
    public Point2D modelToViewDelta( Point3D delta ) {
        Point2D origin = modelToView( new Point3D.Double( 0, 0, 0 ) );
        Point2D p = modelToView( delta );
        return new Point2D.Double( p.getX() - origin.getX(), p.getY() - origin.getY() );
    }

    /**
     * Maps a delta from 3D model coordinates to 2D view coordinates.
     *
     * @param xDelta
     * @param yDelta
     * @param zDelta
     * @return
     */
    public Point2D modelToViewDelta( double xDelta, double yDelta, double zDelta ) {
        return modelToViewDelta( new Point3D.Double( xDelta, yDelta, zDelta ) );
    }

    /**
     * Model shapes are all in the 2D xy plane, and have no depth.
     * @param modelShape
     * @return
     */
    public Shape modelToView( Shape modelShape ) {
        return modelToViewTransform2D.createTransformedShape( modelShape );
    }

    //----------------------------------------------------------------------------
    // View-to-model transforms
    //----------------------------------------------------------------------------

    /**
     * Maps a point from 2D view coordinates to 3D model coordinates.
     * The z coordinate will be zero.
     *
     * @param pView
     * @return
     */
    public Point3D viewToModel( Point2D pView ) {
        Point2D p = viewToModelTransform2D.transform( pView, null );
        return new Point3D.Double( p.getX(), p.getY(), 0 );
    }

    /**
     * Maps a point from 2D view coordinates to 3D model coordinates.
     * The z coordinate will be zero.
     *
     * @param x
     * @param y
     * @return
     */
    public Point3D viewToModel( double x, double y ) {
        return viewToModel( new Point2D.Double( x, y ) );
    }

    /**
     * Maps a delta from 2D view coordinates to 3D model coordinates.
     * The z coordinate will be zero.
     *
     * @param delta
     * @return
     */
    public Point3D viewToModelDelta( Point2D delta ) {
        Point3D origin = viewToModel( new Point2D.Double( 0, 0 ) );
        Point3D p = viewToModel( delta );
        return new Point3D.Double( p.getX() - origin.getX(), p.getY() - origin.getY(), p.getZ() - origin.getZ() );
    }

    /**
     * Maps a delta from 2D view coordinates to 3D model coordinates.
     * The z coordinate will be zero.
     *
     * @param xDelta
     * @param yDelta
     * @param zDelta
     * @return
     */
    public Point3D viewToModelDelta( double xDelta, double yDelta ) {
        return viewToModelDelta( new Point2D.Double( xDelta, yDelta ) );
    }
}
