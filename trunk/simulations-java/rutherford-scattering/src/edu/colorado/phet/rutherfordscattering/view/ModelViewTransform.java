/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering.view;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.colorado.phet.rutherfordscattering.RSConstants;

/**
 * ModelViewTransform is the transform between model coordinates 
 * and the view coordinates of the animation region.
 * <p>
 * NOTE! This has never been tested with anything other than
 * a 1:1 transform, and I doubt that it works.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ModelViewTransform {

    private static AffineTransform _transform;
    
    /* Not intended for instantiation */
    private ModelViewTransform() {}
    
    /**
     * Maps a point from model to view coordinates.
     * @param p point in model coordinates
     * @return point in view coordinates
     */
    public static final Point2D transform( Point2D p ) {
        if ( _transform == null ) {
            initTransform();
        }
        return _transform.transform( p, null );
    }
    
    /**
     * Maps a distance from model to view coordinates.
     * @param distance distance in model coordinates
     * @return distance in view coordinates
     */
    public static final double transform( double distance ) {
        if ( _transform == null ) {
            initTransform();
        }
        Point2D p = new Point2D.Double( 0, distance );
        Point2D p2 = transform( p );
        return p2.getY() - RSConstants.ANIMATION_BOX_SIZE.height;
    }
    
    /*
     * Initializes the transform.
     */
    private static void initTransform() {
        _transform = new AffineTransform();
        _transform.scale( 1, 1 );
        _transform.translate( RSConstants.ANIMATION_BOX_SIZE.width / 2, RSConstants.ANIMATION_BOX_SIZE.height );
    }
}
