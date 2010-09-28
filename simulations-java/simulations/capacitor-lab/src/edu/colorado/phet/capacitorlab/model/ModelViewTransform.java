/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Point3D;

/**
 * ModelViewTransform provides the transforms between model and view coordinate systems.
 * In both coordinate systems, +x is to the right, +y is down, +z is away from the viewer.
 * Sign of rotation angles is specified using the right-hand rule.
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
     * @param pitch rotation about the horizontal (x) axis, sign determined using the right-hand rule
     * @param yaw rotation about the vertical (y) axis, sign determined using the right-hand rule
     */
    public ModelViewTransform( double scale, double pitch, double yaw ) {
        
        modelToViewTransform2D = new AffineTransform();
        modelToViewTransform2D.scale( scale, scale );
        
        viewToModelTransform2D = new AffineTransform();
        viewToModelTransform2D.scale( 1d / scale, 1d / scale );
        
        this.pitch = pitch;
        this.yaw = yaw;
    }
    
    //----------------------------------------------------------------------------
    // Model-to-view transforms
    //----------------------------------------------------------------------------
    
    /**
     * Maps a distance from model to view coordinates.
     * @param distance
     */
    public double modelToView( double distance ) {
        return distance * modelToViewTransform2D.getScaleX();
    }
    
    /**
     * Maps a 2D point from model to view coordinates.
     * If pView is not null, the result is returned in pView.
     * 
     * @param pModel point in model coordinates
     * @param pView point in view coordinates, possibly null
     * @return point in view coordinates
     */
    public Point2D modelToView( Point2D pModel, Point2D pView ) {
        return modelToViewTransform2D.transform( pModel, pView );
    }
    
    public Point2D modelToView( Point2D pModel ) {
        return modelToView( pModel, null );
    }
    
    public Point2D modelToView( double xModel, double yModel, Point2D pView ) {
        return modelToView( new Point2D.Double( xModel, yModel ), pView );
    }
    
    public Point2D modelToView( double xModel, double yModel ) {
        return modelToView( xModel, yModel, null );
    }
    
    /**
     * Maps a 3D model point to a 2D view point.
     * @param pModel
     * @param pView
     * @return
     */
    public Point2D modelToView( Point3D pModel ) {
        double xModel = pModel.getX() + ( pModel.getZ() * Math.sin( pitch ) * Math.cos( yaw ) );
        double yModel = pModel.getY() + ( pModel.getZ() * Math.sin( pitch ) * Math.sin( yaw ) );
        return modelToView( xModel, yModel );
    }
    
    public Point2D modelToView( double x, double y, double z ) {
        return modelToView( new Point3D.Double( x, y, z ) );
    }
    
    /**
     * Maps a rectangle from model to view coordinates.
     * 
     * @param rModel
     * @return Rectangle2D in view coordinates
     */
    public Rectangle2D modelToView( Rectangle2D rModel ) {
        return modelToViewTransform2D.createTransformedShape( rModel ).getBounds2D();
    }
    
    //----------------------------------------------------------------------------
    // View-to-model transforms
    //----------------------------------------------------------------------------
    
    /**
     * Maps a distance from view to model coordinates.
     * @param distance
     */
    public double viewToModel( double distance ) {
        return distance * viewToModelTransform2D.getScaleX();
    }
    
    /**
     * Maps a point from view to model coordinates.
     * If pModel is not null, the result is returned in pModel.
     * 
     * @param pView point in view coordinates
     * @param pModel point in model coordinates, possibly null
     * @return point in model coordinates
     */
    public Point2D viewToModel( Point2D pView, Point2D pModel ) {
        return viewToModelTransform2D.transform( pView, pModel );
    }
    
    public Point2D viewToModel( Point2D pView ) {
        return viewToModel( pView, null );
    }
    
    public Point2D viewToModel( double xView, double yView, Point2D pModel ) {
        return viewToModel( new Point2D.Double( xView, yView ), pModel );
    }
    
    public Point2D viewToModel( double xView, double yView ) {
        return viewToModel( new Point2D.Double( xView, yView ) );
    }
    
    /**
     * Maps a rectangle from view to model to coordinates.
     * 
     * @param rView
     * @param Rectangle2D in model coordinates
     */
    public Rectangle2D viewToModel( Rectangle2D rView ) {
        return viewToModelTransform2D.createTransformedShape( rView ).getBounds2D();
    }
}
