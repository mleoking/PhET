// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.solublesalts.model.ion;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.MutableVector2D;

/**
 * Sodium
 *
 * @author Ron LeMaster
 */
public class Sodium extends Ion {
    public static final double RADIUS = 4;
    private static IonProperties ionProperties = new IonProperties( 11, 1, RADIUS );

    public Sodium() {
        super( ionProperties );
    }

    public Sodium( Point2D position, MutableVector2D velocity, MutableVector2D acceleration ) {
        super( position,
               velocity,
               acceleration,
               ionProperties );
    }
}
