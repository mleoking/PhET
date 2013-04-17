// Copyright 2002-2012, University of Colorado

/**
 * Class: Collidable
 * Package: edu.colorado.phet.collision
 * Author: Another Guy
 * Date: Oct 7, 2004
 */
package edu.colorado.phet.common.collision;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;

public interface Collidable {
    MutableVector2D getVelocityPrev();

    Point2D getPositionPrev();
}
