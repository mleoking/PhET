/**
 * Class: ContactDetector
 * Package: edu.colorado.phet.lasers.physics.collision
 * Author: Another Guy
 * Date: Mar 26, 2003
 */
package edu.colorado.phet.collision;

public interface ContactDetector {
    boolean areInContact( CollidableBody bodyA, CollidableBody bodyB );
    boolean applies( CollidableBody bodyA, CollidableBody bodyB );
}
