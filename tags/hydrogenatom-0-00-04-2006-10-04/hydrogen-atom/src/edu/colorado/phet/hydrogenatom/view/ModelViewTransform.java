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
    
    public static final Point2D translate( Point2D p ) {
        if ( transform == null ) {
            int boxWidth = SimStrings.getInt( "animationRegion.width", HAConstants.DEFAULT_ANIMATION_REGION_SIZE.width );
            int boxHeight = SimStrings.getInt( "animationRegion.height", HAConstants.DEFAULT_ANIMATION_REGION_SIZE.height );
            transform = new AffineTransform();
            transform.translate( boxWidth / 2, boxHeight );
        }
        return transform.transform( p, null );
    }
}
