/**
 * Class: Collision
 * Package: edu.colorado.phet.physics.collision
 * Author: Another Guy
 * Date: Mar 24, 2003
 */
package edu.colorado.phet.physics.collision;

//import edu.colorado.phet.physics.body.Particle;


public interface Collision {

    void collide();

    Collision createIfApplicable( CollidableBody particleA, CollidableBody particleB );
//    Collision createIfApplicable( Particle particleA, Particle particleB );
}
