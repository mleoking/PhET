/**
 * Class: Collision
 * Package: edu.colorado.phet.model.collision
 * Author: Another Guy
 * Date: Mar 24, 2003
 */
package edu.colorado.phet.physics.collision;

//import edu.colorado.phet.model.body.Particle;


public interface Collision {

    void collide();

    Collision createIfApplicable( CollidableBody particleA, CollidableBody particleB );
//    Collision createIfApplicable( Particle particleA, Particle particleB );
}
