/**
 * Class: CollisionExpert
 * Package: edu.colorado.phet.collision
 * Author: Another Guy
 * Date: Sep 21, 2004
 */
package edu.colorado.phet.collision;

public interface CollisionExpert {
    abstract boolean detectAndDoCollision( Collidable bodyA, Collidable bodyB );
}
