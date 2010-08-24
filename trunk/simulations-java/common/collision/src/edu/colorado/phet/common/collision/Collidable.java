/**
 * Class: Collidable
 * Package: edu.colorado.phet.collision
 * Author: Another Guy
 * Date: Oct 7, 2004
 */
package edu.colorado.phet.common.collision;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2DInterface;

public interface Collidable {
    Vector2DInterface getVelocityPrev();

    Point2D getPositionPrev();
}
