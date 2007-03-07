/**
 * Class: Collision
 * Package: edu.colorado.phet.physics.collision
 * Author: Another Guy
 * Date: Mar 24, 2003
 */
package edu.colorado.phet.physics.collision_microwaves;


import edu.colorado.phet.coreadditions_microwaves.Body;

public interface Collision {

    void collide();

    Collision createIfApplicable( Body bodyA, Body bodyB);
}
