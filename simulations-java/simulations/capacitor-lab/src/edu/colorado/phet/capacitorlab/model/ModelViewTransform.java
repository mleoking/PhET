/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

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
    
    private final AffineTransform modelToViewTransform;
    private final AffineTransform viewToModelTransform;
    private final boolean flipSignX, flipSignY;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param scale scale for mapping from model to view (x and y scale are identical)
     * @param offset translation for mapping from model to view
     * @param flipSignX if true, flip the sign on the x component
     * @param flipSignY if true, flip the sign on the y component
     */
    public ModelViewTransform( double scale, Point2D offset, boolean flipSignX, boolean flipSignY ) {
        
        this.flipSignX = flipSignX;
        this.flipSignY = flipSignY;
        
        modelToViewTransform = new AffineTransform();
        modelToViewTransform.scale( scale, scale );
        modelToViewTransform.translate( offset.getX(), offset.getY() );
        
        viewToModelTransform = new AffineTransform();
        viewToModelTransform.translate( -offset.getX(), -offset.getY() );
        viewToModelTransform.scale( 1d / scale, 1d / scale );
    }
    
    public ModelViewTransform( double scale, Point2D offset ) {
        this( scale, offset, false /* flipSignX */, false /* flipSignY */ );
    }
    
    //----------------------------------------------------------------------------
    // Model-to-view transforms
    //----------------------------------------------------------------------------
    
    /**
     * Maps a distance from model to view coordinates.
     * @param distance
     */
    public double modelToView( double distance ) {
        return distance * modelToViewTransform.getScaleX();
    }
    
    /**
     * Maps a point from model to view coordinates.
     * If pView is not null, the result is returned in pView.
     * 
     * @param pModel point in model coordinates
     * @param pView point in view coordinates, possibly null
     * @return point in view coordinates
     */
    public Point2D modelToView( Point2D pModel, Point2D pView ) {
        Point2D p = modelToViewTransform.transform( pModel, pView );
        if ( flipSignX ) {
            p.setLocation( -p.getX(), p.getY() );
        }
        if ( flipSignY ) {
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
        Rectangle2D r = modelToViewTransform.createTransformedShape( rModel ).getBounds2D();
        if ( flipSignX ) {
            r.setRect( -r.getX(), r.getY(), r.getWidth(), r.getHeight() );
        }
        if ( flipSignY ) {
            r.setRect( r.getX(), -r.getY(), r.getWidth(), r.getHeight() );
        }
        return r;
    }
    
    //----------------------------------------------------------------------------
    // View-to-model transforms
    //----------------------------------------------------------------------------
    
    /**
     * Maps a distance from view to model coordinates.
     * @param distance
     */
    public double viewToModel( double distance ) {
        return distance * viewToModelTransform.getScaleX();
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
        Point2D p = viewToModelTransform.transform( pView, pModel );
        if ( flipSignX ) {
            p.setLocation( -p.getX(), p.getY() );
        }
        if ( flipSignY ) {
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
        Rectangle2D r = viewToModelTransform.createTransformedShape( rView ).getBounds2D();
        if ( flipSignX ) {
            r.setRect( -r.getX(), r.getY(), r.getWidth(), r.getHeight() );
        }
        if ( flipSignY ) {
            r.setRect( r.getX(), -r.getY(), r.getWidth(), r.getHeight() );
        }
        return r;
    }
}
