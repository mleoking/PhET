/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.hydrogenatom.HAConstants;

/**
 * ModelViewTransform is the transform between model coordinates 
 * and the view coordinates of the animation region.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ModelViewTransform {

    private static AffineTransform transform;
    
    /* Not intended for instantiation */
    private ModelViewTransform() {}
    
    /**
     * Maps a point from model to view coordinates.
     * @param p point in model coordinates
     * @return point in view coordinates
     */
    public static final Point2D transform( Point2D p ) {
        if ( transform == null ) {
            initTransform();
        }
        return transform.transform( p, null );
    }
    
    /**
     * Maps a distance from model to view coordinates.
     * @param distance distance in model coordinates
     * @return distance in view coordinates
     */
    public static final double transform( double distance ) {
        if ( transform == null ) {
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
        transform = new AffineTransform();
        transform.scale( 1, 1 );
        transform.translate( HAConstants.ANIMATION_BOX_SIZE.width / 2, HAConstants.ANIMATION_BOX_SIZE.height );
    }
}
