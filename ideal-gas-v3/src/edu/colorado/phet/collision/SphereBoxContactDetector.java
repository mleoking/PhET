/**
 * Class: SphereBoxContactDetector
 * Class: edu.colorado.phet.idealgas.physics.collision
 * User: Ron LeMaster
 * Date: Apr 4, 2003
 * Time: 12:01:33 PM
 */
package edu.colorado.phet.collision;

import edu.colorado.phet.idealgas.model.SphericalBody;
import edu.colorado.phet.idealgas.model.Box2D;

public class SphereBoxContactDetector extends ContactDetector {

    protected boolean applies( CollidableBody bodyA, CollidableBody bodyB ) {
        return( bodyA instanceof SphericalBody && bodyB instanceof Box2D
            || bodyB instanceof SphericalBody && bodyA instanceof Box2D );
    }

    public boolean areInContact( CollidableBody bodyA, CollidableBody bodyB ) {
        Box2D box = null;
        SphericalBody sphere = null;
        box = bodyA instanceof Box2D ? (Box2D)bodyA : (Box2D)bodyB;
        sphere = bodyA instanceof SphericalBody ? (SphericalBody)bodyA : (SphericalBody)bodyB;
        if(box.isInContactWithParticle( sphere )) {
            System.out.println( "!!" );
        }
        return box.isInContactWithParticle( sphere );
    }
}
