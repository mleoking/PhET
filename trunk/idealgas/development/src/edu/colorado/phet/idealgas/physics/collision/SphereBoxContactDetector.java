/**
 * Class: SphereBoxContactDetector
 * Class: edu.colorado.phet.idealgas.physics.collision
 * User: Ron LeMaster
 * Date: Apr 4, 2003
 * Time: 12:01:33 PM
 */
package edu.colorado.phet.idealgas.physics.collision;

import edu.colorado.phet.physics.collision.ContactDetector;
import edu.colorado.phet.physics.collision.SphericalBody;
import edu.colorado.phet.physics.collision.Box2D;
import edu.colorado.phet.physics.body.Body;

public class SphereBoxContactDetector extends ContactDetector {

    protected boolean applies( Body bodyA, Body bodyB ) {
        return( bodyA instanceof SphericalBody && bodyB instanceof Box2D
            || bodyB instanceof SphericalBody && bodyA instanceof Box2D );
    }

    public boolean areInContact( Body bodyA, Body bodyB ) {
        Box2D box = null;
        SphericalBody sphere = null;
        box = bodyA instanceof Box2D ? (Box2D)bodyA : (Box2D)bodyB;
        sphere = bodyA instanceof SphericalBody ? (SphericalBody)bodyA : (SphericalBody)bodyB;

        return box.isInContactWithParticle( sphere );
    }
}
