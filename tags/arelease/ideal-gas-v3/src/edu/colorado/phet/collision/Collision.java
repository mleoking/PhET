/**
 * Class: Collision
 * Package: edu.colorado.phet.physics.collision
 * Author: Another Guy
 * Date: Mar 24, 2003
 */
package edu.colorado.phet.collision;

import edu.colorado.phet.common.model.Particle;

public interface Collision {
    void collide();

    Collision createIfApplicable( Particle particleA, Particle particleB );
}
