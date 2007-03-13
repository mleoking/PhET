/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.colorado.phet.piccolo.PhetPCanvas;

/**
 * ModelWorldTransform provides the transform between model and world coordinate systems. 
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ModelWorldTransform {

    private AffineTransform _modelToWorldTransform;
    private AffineTransform _worldToModelTransform;
    private Point2D _pModelDistance, _pWorldDistance; // reusable points for transforming distances
    
    /**
     * Constructor.
     * 
     * @param scale
     * @param xOffset
     * @param yOffset
     */
    public ModelWorldTransform( double scale, double xOffset, double yOffset ) {
        
        _modelToWorldTransform = new AffineTransform();
        _modelToWorldTransform.scale( scale, scale );
        _modelToWorldTransform.translate( xOffset, yOffset );
        
        _worldToModelTransform = new AffineTransform();
        _worldToModelTransform.translate( -xOffset, -yOffset );
        _worldToModelTransform.scale( 1d/scale, 1d/scale );
        
        _pModelDistance = new Point2D.Double();
        _pWorldDistance = new Point2D.Double();
    }
    
    /**
     * Maps a point from model to world coordinates.
     * 
     * @param pModel point in model coordinates
     * @return point in world coordinates
     */
    public Point2D modelToWorld( Point2D pModel ) {
        return modelToWorld( pModel, null );
    }
    
    /**
     * Maps a point from model to world coordinates.
     * 
     * @param pModel point in model coordinates
     * @param pWorld point in view coordinates, possibly null
     * @return point in world coordinates
     */
    public Point2D modelToWorld( Point2D pModel, Point2D pWorld ) {
        return _modelToWorldTransform.transform( pModel, pWorld );
    }
    
    /**
     * Maps a distance from model to world coordinates.
     * 
     * @param distance distance in model coordinates
     * @return distance in world coordinates
     */
    public double modelToWorld( double distance ) {
        _pModelDistance.setLocation( distance, 0 );
        modelToWorld( _pModelDistance, _pWorldDistance );
        return _pWorldDistance.getX();
    }
    
    /**
     * Maps a point from world to model coordinates.
     * 
     * @param pModel point in model coordinates
     * @return point in world coordinates
     */
    public Point2D worldToModel( Point2D pView ) {
        return worldToModel( pView, null );
    }
    
    /**
     * Maps a point from world to model coordinates.
     * 
     * @param pWorld point in view coordinates
     * @param pModel point in model coordinates, possibly null
     * @return point in view coordinates
     */
    public Point2D worldToModel( Point2D pWorld, Point2D pModel ) {
        return _worldToModelTransform.transform( pWorld, pModel );
    }
    
    /**
     * Maps a distance from world to model coordinates.
     * 
     * @param distance distance in world coordinates
     * @return distance in model coordinates
     */
    public double worldToModel( double distance ) {
        _pWorldDistance.setLocation( distance, 0 );
        worldToModel( _pWorldDistance, _pModelDistance );
        return _pModelDistance.getX();
    }
}
