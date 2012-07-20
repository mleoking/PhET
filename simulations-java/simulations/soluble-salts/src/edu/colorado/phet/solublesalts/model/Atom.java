// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.solublesalts.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.collision.SphericalBody;
import edu.colorado.phet.common.phetcommon.math.MutableVector2D;

/**
 * Atom
 *
 * @author Ron LeMaster
 */
public abstract class Atom extends SphericalBody {

    public Atom( double radius ) {
        super( radius );
    }

    protected Atom( Point2D center, MutableVector2D velocity, MutableVector2D acceleration, double mass, double radius ) {
        super( center, velocity, acceleration, mass, radius );
    }
}
