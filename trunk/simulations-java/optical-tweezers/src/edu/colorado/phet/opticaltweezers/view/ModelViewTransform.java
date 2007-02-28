/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

/**
 * ModelViewTransform provides the transform between model and view coordinate systems. 
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ModelViewTransform {

    private AffineTransform _transform;
    private Point2D _pModelDistance, _pViewDistance; // reusable points for transforming distances
    
    /**
     * Constructor.
     * 
     * @param scale
     * @param xOffset
     * @param yOffset
     */
    public ModelViewTransform( double scale, double xOffset, double yOffset ) {
        
        _transform = new AffineTransform();
        _transform.scale( scale, scale );
        _transform.translate( xOffset, yOffset );
        
        _pModelDistance = new Point2D.Double();
        _pViewDistance = new Point2D.Double();
    }
    
    /**
     * Maps a point from model to view coordinates.
     * 
     * @param pModel point in model coordinates
     * @return point in view coordinates
     */
    public Point2D transform( Point2D pModel ) {
        return transform( pModel, null );
    }
    
    /**
     * Maps a point from model to view coordinates.
     * 
     * @param pModel point in model coordinates
     * @param pView point in view coordinates, possibly null
     * @return point in view coordinates
     */
    public Point2D transform( Point2D pModel, Point2D pView ) {
        return _transform.transform( pModel, pView );
    }
    
    /**
     * Maps a distance from model to view coordinates.
     * 
     * @param distance distance in model coordinates
     * @return distance in view coordinates
     */
    public double transform( double distance ) {
        _pModelDistance.setLocation( distance, 0 );
        transform( _pModelDistance, _pViewDistance );
        return _pViewDistance.getX();
    }
    
    /**
     * Maps a point from view to model coordinates.
     * 
     * @param pModel point in model coordinates
     * @return point in view coordinates
     * @throws NoninvertibleTransformException if the matrix cannot be inverted
     */
    public Point2D inverseTransform( Point2D pView ) throws NoninvertibleTransformException {
        return inverseTransform( pView, null );
    }
    
    /**
     * Maps a point from view to model coordinates.
     * 
     * @param pView point in view coordinates
     * @param pModel point in model coordinates, possibly null
     * @return point in view coordinates
     * @throws NoninvertibleTransformException if the matrix cannot be inverted
     */
    public Point2D inverseTransform( Point2D pView, Point2D pModel ) throws NoninvertibleTransformException {
        return _transform.inverseTransform( pView, pModel );
    }
    
    /**
     * Maps a distance from view to model coordinates.
     * 
     * @param distance distance in viuew coordinates
     * @return distance in model coordinates
     * @throws NoninvertibleTransformException if the matrix cannot be inverted
     */
    public double inverseTransform( double distance ) throws NoninvertibleTransformException {
        _pViewDistance.setLocation( distance, 0 );
        inverseTransform( _pViewDistance, _pModelDistance );
        return _pViewDistance.getX();
    }
}
