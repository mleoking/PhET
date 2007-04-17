/**
 * Class: Collidable
 * Package: edu.colorado.phet.collision
 * Author: Another Guy
 * Date: Oct 7, 2004
 */
package edu.colorado.phet.common.collision;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

import java.awt.geom.Point2D;

public interface Collidable {
    Vector2D getVelocityPrev();

    Point2D getPositionPrev();
}
