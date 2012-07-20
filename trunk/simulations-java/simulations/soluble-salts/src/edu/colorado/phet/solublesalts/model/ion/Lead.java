// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.solublesalts.model.ion;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.MutableVector2D;

/**
 * Sodium
 *
 * @author Ron LeMaster
 */
public class Lead extends Ion {
    public static final double RADIUS = 6;
    private static IonProperties ionProperties = new IonProperties( 82, 1, RADIUS );

    public Lead() {
        super( ionProperties );
    }

    public Lead( Point2D position, MutableVector2D velocity, MutableVector2D acceleration ) {
        super( position,
               velocity,
               acceleration,
               ionProperties );
    }
}
