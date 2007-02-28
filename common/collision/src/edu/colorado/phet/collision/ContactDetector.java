/**
 * Class: ContactDetector
 * Package: edu.colorado.phet.lasers.model.collision
 * Author: Another Guy
 * Date: Mar 26, 2003
 */
package edu.colorado.phet.collision;

public interface ContactDetector {
    boolean areInContact( Collidable bodyA, Collidable bodyB );
    boolean applies( Collidable bodyA, Collidable bodyB );
}
