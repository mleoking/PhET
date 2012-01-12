// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.hydrogenatom.view;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.colorado.phet.hydrogenatom.HAConstants;

/**
 * ModelViewTransform is the transform between model coordinates 
 * and the view coordinates of the AnimationBoxNode.
 * The origin of the model coordinate system is at the bottom-center
 * of the AnimationBoxNode.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HAModelViewTransform {

    private static AffineTransform _transform;
    
    /* Not intended for instantiation */
    private HAModelViewTransform() {}
    
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
        return p2.getY() - HAConstants.ANIMATION_BOX_SIZE.height;
    }
    
    /*
     * Initializes the transform.
     */
    private static void initTransform() {
        _transform = new AffineTransform();
        _transform.translate( HAConstants.ANIMATION_BOX_SIZE.width / 2, HAConstants.ANIMATION_BOX_SIZE.height );
    }
}
