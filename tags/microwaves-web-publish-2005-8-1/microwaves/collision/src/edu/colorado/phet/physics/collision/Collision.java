/**
 * Class: Collision
 * Package: edu.colorado.phet.physics.collision
 * Author: Another Guy
 * Date: Mar 24, 2003
 */
package edu.colorado.phet.physics.collision;


import edu.colorado.phet.coreadditions.Body;

public interface Collision {

    void collide();

    Collision createIfApplicable( Body bodyA, Body bodyB);
}
