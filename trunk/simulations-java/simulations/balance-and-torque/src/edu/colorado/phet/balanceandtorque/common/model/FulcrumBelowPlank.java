// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.common.model;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

/**
 * This is the fulcrum upon which the teeter-totter is balanced.  This is the
 * fulcrum that is used when the balance point is on the underside of the
 * plank.
 *
 * @author Sam Reid
 */
public class FulcrumBelowPlank extends ShapeModelElement {
    //Distance from the base of the fulcrum to its top
    private static final double HEIGHT = 0.6;

    //Length of the base of the triangle
    private static final double WIDTH = 0.5;

    public FulcrumBelowPlank() {
        super( new DoubleGeneralPath( 0, 0 ) {{
            lineTo( -WIDTH / 2, 0 );
            lineTo( 0, HEIGHT );
            lineTo( WIDTH / 2, 0 );
            lineTo( 0, 0 );
        }}.getGeneralPath() );
    }

    public static double getHeight() {
        return HEIGHT;
    }
}
