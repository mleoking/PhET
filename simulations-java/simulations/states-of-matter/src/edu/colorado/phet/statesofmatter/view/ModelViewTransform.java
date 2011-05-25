// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.statesofmatter.view;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * ModelViewTransform provides the transforms between model and view coordinate systems.
 *
 * @author John Blanco
 * @deprecated please use edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform
 */
public class ModelViewTransform {

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private final AffineTransform _modelToViewTransform;
    private final AffineTransform _viewToModelTransform;
    private final boolean _flipSignX, _flipSignY;
    
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
     * @param flipSignX if true, flip the sign on the x component
     * @param flipSignY if true, flip the sign on the y component
     */
    public ModelViewTransform( double xScale, double yScale, double xOffset, double yOffset, boolean flipSignX, boolean flipSignY ) {
        
        _flipSignX = flipSignX;
        _flipSignY = flipSignY;
        
        _modelToViewTransform = new AffineTransform();
        _modelToViewTransform.scale( xScale, yScale );
        _modelToViewTransform.translate( xOffset, yOffset );
        
        _viewToModelTransform = new AffineTransform();
        _viewToModelTransform.translate( -xOffset, -yOffset );
        _viewToModelTransform.scale( 1d / xScale, 1d / yScale );
    }
    
    public ModelViewTransform( double xScale, double yScale, double xOffset, double yOffset ) {
        this( xScale, yScale, xOffset, yOffset, false, false );
    }
    
    /**
     * Constructs an identity transform.
     */
    public ModelViewTransform() {
        this( 1, 1, 0, 0, false, false );
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
        Point2D p = _modelToViewTransform.transform( pModel, pView );
        if ( _flipSignX ) {
            p.setLocation( -p.getX(), p.getY() );
        }
        if ( _flipSignY ) {
            p.setLocation( p.getX(), -p.getY() );
        }
        return p;
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
        Rectangle2D r = _modelToViewTransform.createTransformedShape( rModel ).getBounds2D();
        if ( _flipSignX ) {
            r.setRect( -r.getWidth() - r.getX(), r.getY(), r.getWidth(), r.getHeight() );
        }
        if ( _flipSignY ) {
            r.setRect( r.getX(), -r.getHeight() - r.getY(), r.getWidth(), r.getHeight() );
        }
        return r;
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
        Point2D p = _viewToModelTransform.transform( pView, pModel );
        if ( _flipSignX ) {
            p.setLocation( -p.getX(), p.getY() );
        }
        if ( _flipSignY ) {
            p.setLocation( p.getX(), -p.getY() );
        }
        return p;
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
        Rectangle2D r = _viewToModelTransform.createTransformedShape( rView ).getBounds2D();
        if ( _flipSignX ) {
            r.setRect( -r.getX(), r.getY(), r.getWidth(), r.getHeight() );
        }
        if ( _flipSignY ) {
            r.setRect( r.getX(), -r.getY(), r.getWidth(), r.getHeight() );
        }
        return r;
    }
}
