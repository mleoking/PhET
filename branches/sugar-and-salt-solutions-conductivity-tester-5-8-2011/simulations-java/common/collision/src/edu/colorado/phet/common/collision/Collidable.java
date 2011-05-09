// Copyright 2002-2011, University of Colorado

/**
 * Class: Collidable
 * Package: edu.colorado.phet.collision
 * Author: Another Guy
 * Date: Oct 7, 2004
 */
package edu.colorado.phet.common.collision;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

public interface Collidable {
    Vector2D getVelocityPrev();

    Point2D getPositionPrev();
}
