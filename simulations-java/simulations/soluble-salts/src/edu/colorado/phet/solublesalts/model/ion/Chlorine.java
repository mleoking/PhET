// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.solublesalts.model.ion;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;

/**
 * Chloride
 *
 * @author Ron LeMaster
 */
public class Chlorine extends Ion {
    public static final double RADIUS = 8;
    private static IonProperties ionProperties = new IonProperties( 17, -1, Chlorine.RADIUS );

    public Chlorine() {
        super( Chlorine.ionProperties );
    }

    public Chlorine( Point2D position, MutableVector2D velocity, MutableVector2D acceleration ) {
        super( position, velocity, acceleration, Chlorine.ionProperties );
    }
}
