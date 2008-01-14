/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

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
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param xScale x scale for mapping from model to view
     * @param yScale y scale for mapping from model to view
     * @param xOffset x translation for mapping from model to view
     * @param yOffset y translation for mapping from model to view
     */
    public ModelViewTransform( double xScale, double yScale, double xOffset, double yOffset ) {
        
        _modelToViewTransform = new AffineTransform();
        _modelToViewTransform.scale( xScale, yScale );
        _modelToViewTransform.translate( xOffset, yOffset );
        
        _viewToModelTransform = new AffineTransform();
        _viewToModelTransform.translate( -xOffset, -yOffset );
        _viewToModelTransform.scale( 1d / xScale, 1d / yScale );
    }
    
    //----------------------------------------------------------------------------
    // Model-to-view transforms
    //----------------------------------------------------------------------------
    
    /**
     * Maps a point from model to view coordinates.
     * If pView is not null, the result is returned in pView.
     * 
     * @param pModel point in model coordinates
     * @param pView point in view coordinates, possibly null
     * @return point in view coordinates
     */
    public Point2D modelToView( Point2D pModel, Point2D pView ) {
        return _modelToViewTransform.transform( pModel, pView );
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
     * Maps a rectangle from model to view coordinates.
     * 
     * @param rModel
     * @return Rectangle2D in view coordinates
     */
    public Rectangle2D modelToView( Rectangle2D rModel ) {
        return _modelToViewTransform.createTransformedShape( rModel ).getBounds2D();
    }
    
    //----------------------------------------------------------------------------
    // View-to-model transforms
    //----------------------------------------------------------------------------
    
    /**
     * Maps a point from view to model coordinates.
     * If pModel is not null, the result is returned in pModel.
     * 
     * @param pView point in view coordinates
     * @param pModel point in model coordinates, possibly null
     * @return point in model coordinates
     */
    public Point2D viewToModel( Point2D pView, Point2D pModel ) {
        return _viewToModelTransform.transform( pView, pModel );
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
        return _viewToModelTransform.createTransformedShape( rView ).getBounds2D();
    }
}
