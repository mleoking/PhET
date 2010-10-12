/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

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
public class ModelViewTransform {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final AffineTransform modelToViewTransform2D;
    private final AffineTransform viewToModelTransform2D;
    private final double pitch, yaw;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param scale scale for mapping from model to view (x and y scale are identical)
     * @param offset translation for mapping from model to view, in model coordinates
     * @param pitch rotation about the horizontal (x) axis, sign determined using the right-hand rule (radians)
     * @param yaw rotation about the vertical (y) axis, sign determined using the right-hand rule (radians)
     */
    public ModelViewTransform( double scale, double pitch, double yaw ) {
        
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
     * Maps a distance from model to view coordinates.
     * Causes no allocation.
     * 
     * @param distance
     */
    public double modelToView( double distance ) {
        return distance * modelToViewTransform2D.getScaleX();
    }
    
    /**
     * Maps a 3D model point to a 2D view point.
     * Allocates 2 Point2D objects.
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
     * Maps a 3D model point to a 2D view point.
     * Allocates 3 Point2D objects.
     * 
     * @param x
     * @param y
     * @param z
     * @return
     */
    public Point2D modelToView( double x, double y, double z ) {
        return modelToView( new Point3D.Double( x, y, z ) );
    }
    
    //----------------------------------------------------------------------------
    // View-to-model transforms
    //----------------------------------------------------------------------------
    
    /**
     * Maps a distance from view to model coordinates.
     * Causes no allocation.
     * 
     * @param distance
     */
    public double viewToModel( double distance ) {
        return distance * viewToModelTransform2D.getScaleX();
    }
    
    /**
     * Maps a point from view to model coordinates.
     * If pModel is not null, the result is returned in pModel.
     * Otherwise a Point2D is allocated and returned.
     * 
     * @param pView point in view coordinates
     * @param pModel point in model coordinates, possibly null
     * @return point in model coordinates
     */
    public Point2D viewToModel( Point2D pView, Point2D pModel ) {
        return viewToModelTransform2D.transform( pView, pModel );
    }
    
    /**
     * Maps a point from view to model coordinates.
     * Allocates 1 Point2D.
     * 
     * @param pView
     * @return
     */
    public Point2D viewToModel( Point2D pView ) {
        return viewToModel( pView, null );
    }
}
