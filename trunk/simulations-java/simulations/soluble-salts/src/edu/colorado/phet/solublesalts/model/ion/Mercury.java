// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.model.ion;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * Copper
 *
 * @author Ron LeMaster
 */
public class Mercury extends Ion {
    public static final double RADIUS = 6;
    private static IonProperties ionProperties = new IonProperties( 80, 1, RADIUS );

    public Mercury() {
        super( ionProperties );
    }

    public Mercury( Point2D position, Vector2D velocity, Vector2D acceleration ) {
        super( position,
               velocity,
               acceleration,
               ionProperties );
    }
}
