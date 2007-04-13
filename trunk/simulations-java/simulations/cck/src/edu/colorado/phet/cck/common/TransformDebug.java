/** Sam Reid*/
package edu.colorado.phet.cck.common;

import edu.colorado.phet.common_cck.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common_cck.view.graphics.transforms.TransformListener;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Sep 27, 2004
 * Time: 1:15:03 PM
 * Copyright (c) Sep 27, 2004 by Sam Reid
 */
public class TransformDebug extends ModelViewTransform2D {

    /**
     * Constructs a forwardTransform from the specified model bounds to view bounds.
     *
     * @param modelBounds
     * @param viewBounds
     */
    public TransformDebug( Rectangle2D.Double modelBounds, Rectangle viewBounds ) {
        super( modelBounds, viewBounds );
    }

    public TransformDebug( Rectangle2D.Double modelBounds, Rectangle viewBounds, boolean invertY ) {
        super( modelBounds, viewBounds, invertY );
    }

    /**
     * Constructs a transform from two points in the model reference frame and two points
     * int the view reference frame.
     *
     * @param mp1 The point in the model frame that corresponds to vp1 in the view reference frame
     * @param mp2 The point in the model frame that corresponds to vp2 in the view reference frame
     * @param vp1 The point in the view frame that corresponds to mp1 in the model reference frame
     * @param vp2 The point in the view frame that corresponds to mp2 in the model reference frame
     */
    public TransformDebug( Point2D mp1, Point2D mp2, Point vp1, Point vp2 ) {
        super( mp1, mp2, vp1, vp2 );
    }

    public void addTransformListener( TransformListener tl ) {
        super.addTransformListener( tl );
        debug();
    }

    private void debug() {
        if( super.numTransformListeners() > 30 ) {
            System.out.println( "Too many transform listeners: " + numTransformListeners() );
            System.out.println( Arrays.asList( getTransformListeners() ) );
            throw new RuntimeException( "Too many TL's." );
        }
    }

    public void removeTransformListener( TransformListener transformListener ) {
        super.removeTransformListener( transformListener );
        debug();
    }
}
