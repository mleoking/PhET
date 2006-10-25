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


public class ModelViewTransform {

    private static AffineTransform transform;
    
    private ModelViewTransform() {}
    
    public static final Point2D transform( Point2D p ) {
        if ( transform == null ) {
            initTransform();
        }
        return transform.transform( p, null );
    }
    
    public static final double transform( double distance ) {
        if ( transform == null ) {
            initTransform();
        }
        Point2D p = new Point2D.Double( 0, distance );
        Point2D p2 = transform( p );
        return p2.getY() - SimStrings.getInt( "animationRegion.height", HAConstants.DEFAULT_ANIMATION_REGION_SIZE.height );
    }
    
    private static void initTransform() {
        int boxWidth = SimStrings.getInt( "animationRegion.width", HAConstants.DEFAULT_ANIMATION_REGION_SIZE.width );
        int boxHeight = SimStrings.getInt( "animationRegion.height", HAConstants.DEFAULT_ANIMATION_REGION_SIZE.height );
        transform = new AffineTransform();
        transform.scale( 1, 1 );
        transform.translate( boxWidth / 2, boxHeight );
    }
}
