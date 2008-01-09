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
    
    private final Point2D _pModel, _pView; // reusable points
    
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
        
        _pModel = new Point2D.Double();
        _pView = new Point2D.Double();
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
    
    public Point2D modelToView( double xModel, double yModel ) {
        return modelToView( xModel, yModel, null );
    }
    
    public Point2D modelToView( double xModel, double yModel, Point2D pView ) {
        return modelToView( new Point2D.Double( xModel, yModel ), pView );
    }
    
    /**
     * Maps a distance from model to view coordinates.
     * 
     * @param distance distance in model coordinates
     * @return distance in view coordinates
     */
    public double modelToView( double distanceModel ) {
        _pModel.setLocation( distanceModel, 0 );
        modelToView( _pModel, _pView );
        return _pView.getX();
    }
    
    /**
     * Maps a rectangle from model to view coordinates.
     * If rView is not null, the result is returned in rView.
     * 
     * @param rModel
     * @param rView
     * @return
     */
    public Rectangle2D modelToView( Rectangle2D rModel, Rectangle2D rView ) {
        // position
        _pModel.setLocation( rModel.getX(), rModel.getY() );
        modelToView( _pModel, _pView );
        double x = _pView.getX();
        double y = _pView.getY();
        // dimensions
        _pModel.setLocation( rModel.getWidth(), rModel.getHeight() );
        modelToView( _pModel, _pView );
        double w = _pView.getX();
        double h = _pView.getY();
        // return value
        if ( rView == null ) {
            rView = new Rectangle2D.Double();
        }
        rView.setRect( x, y, w, h  );
        return rView;
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
    
    public Point2D viewToModel( double xView, double yView ) {
        return viewToModel( new Point2D.Double( xView, yView ) );
    }
    
    public Point2D viewToModel( double xView, double yView, Point2D pModel ) {
        return viewToModel( new Point2D.Double( xView, yView ), pModel );
    }
    
    /**
     * Maps a distance from view to model coordinates.
     * 
     * @param distance distance in view coordinates
     * @return distance in model coordinates
     */
    public double viewToModel( double distanceView ) {
        _pView.setLocation( distanceView, 0 );
        viewToModel( _pView, _pModel );
        return _pModel.getX();
    }
    
    /**
     * Maps a rectangle from view to model to coordinates.
     * If rModel is not null, the result is returned in rModel.
     * 
     * @param rModel
     * @param rView
     * @return
     */
    public Rectangle2D viewToModel( Rectangle2D rView, Rectangle2D rModel ) {
        // position
        _pView.setLocation( rView.getX(), rView.getY() );
        viewToModel( _pView, _pModel );
        double x = _pModel.getX();
        double y = _pModel.getY();
        // dimensions
        _pView.setLocation( rView.getWidth(), rView.getHeight() );
        viewToModel( _pView, _pModel );
        double w = _pModel.getX();
        double h = _pModel.getY();
        // return value
        if ( rModel == null ) {
            rModel = new Rectangle2D.Double();
        }
        rModel.setRect( x, y, w, h  );
        return rModel;
    }
}
