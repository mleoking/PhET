/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * ModelViewTransform provides the transforms between model and view coordinate systems.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ModelViewTransform {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final AffineTransform _modelToViewTransform;
    private final AffineTransform _viewToModelTransform;
    
    private final Point2D _pModelDistance, _pViewDistance; // reusable points for transforming distances
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor with unity scale and no offset.
     */
    public ModelViewTransform() {
        this( 1, 1, 0, 0 );
    }
    
    /**
     * Constructor.
     * 
     * @param xScale x scale for mapping from model to view
     * @param yScale y scale for mapping from model to view
     */
    public ModelViewTransform( double xScale, double yScale ) {
        this( xScale, yScale, 0, 0 );
    }
    
    /**
     * Constructor.
     * 
     * @param xScale x scale for mapping from model to view
     * @param yScale y scale for mapping from model to view
     * @param xOffset x offset for mapping from model to view
     * @param yOffset y offset for mapping from model to view
     */
    public ModelViewTransform( double xScale, double yScale, double xOffset, double yOffset ) {
        
        _modelToViewTransform = new AffineTransform();
        _modelToViewTransform.scale( xScale, yScale );
        _modelToViewTransform.translate( xOffset, yOffset );
        
        _viewToModelTransform = new AffineTransform();
        _viewToModelTransform.translate( -xOffset, -yOffset );
        _viewToModelTransform.scale( 1d/xScale, 1d/yScale );
        
        _pModelDistance = new Point2D.Double();
        _pViewDistance = new Point2D.Double();
    }
    
    //----------------------------------------------------------------------------
    // Model-to-view transforms
    //----------------------------------------------------------------------------
    
    /**
     * Maps a point from model to view coordinates.
     * 
     * @param pModel point in model coordinates
     * @return point in view coordinates
     */
    public Point2D modelToView( Point2D pModel ) {
        return modelToView( pModel, null );
    }
    
    public Point2D modelToView( double x, double y ) {
        return modelToView( new Point2D.Double( x, y ) );
    }
    
    /**
     * Maps a point from model to view coordinates.
     * 
     * @param pModel point in model coordinates
     * @param pView point in view coordinates, possibly null
     * @return point in view coordinates
     */
    public Point2D modelToView( Point2D pModel, Point2D pView ) {
        return _modelToViewTransform.transform( pModel, pView );
    }
    
    /**
     * Maps a distance from model to view coordinates.
     * 
     * @param distance distance in model coordinates
     * @return distance in view coordinates
     */
    public double modelToView( double distance ) {
        _pModelDistance.setLocation( distance, 0 );
        modelToView( _pModelDistance, _pViewDistance );
        return _pViewDistance.getX();
    }
    
    //----------------------------------------------------------------------------
    // View-to-model transforms
    //----------------------------------------------------------------------------
    
    /**
     * Maps a point from view to model coordinates.
     * 
     * @param pView point in view coordinates
     * @return point in model coordinates
     */
    public Point2D viewToModel( Point2D pView ) {
        return viewToModel( pView, null );
    }
    
    public Point2D viewToModel( double x, double y ) {
        return viewToModel( new Point2D.Double( x, y ) );
    }
    
    /**
     * Maps a point from view to model coordinates.
     * 
     * @param pView point in view coordinates
     * @param pModel point in model coordinates, possibly null
     * @return point in model coordinates
     */
    public Point2D viewToModel( Point2D pView, Point2D pModel ) {
        return _viewToModelTransform.transform( pView, pModel );
    }
    
    /**
     * Maps a distance from view to model coordinates.
     * 
     * @param distance distance in view coordinates
     * @return distance in model coordinates
     */
    public double viewToModel( double distance ) {
        _pViewDistance.setLocation( distance, 0 );
        viewToModel( _pViewDistance, _pModelDistance );
        return _pModelDistance.getX();
    }
}
