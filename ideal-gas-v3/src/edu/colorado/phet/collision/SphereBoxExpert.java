/**
 * Class: SphereBoxExpert
 * Package: edu.colorado.phet.collision
 * Author: Another Guy
 * Date: Sep 21, 2004
 */
package edu.colorado.phet.collision;

import edu.colorado.phet.idealgas.model.Box2D;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.model.SphericalBody;

public class SphereBoxExpert implements CollisionExpert {

    private SphereBoxCollisionExpert detector = new SphereBoxCollisionExpert();
    private Collision collision;
    private IdealGasModel model;

    public SphereBoxExpert( IdealGasModel model ) {
        this.model = model;
    }

    public boolean detectAndDoCollision( CollidableBody bodyA, CollidableBody bodyB ) {
        boolean haveCollided = false;
        if( detector.applies( bodyA, bodyB ) && detector.areInContact( bodyA, bodyB ) ) {
            SphericalBody sphere = bodyA instanceof SphericalBody ?
                                   (SphericalBody)bodyA : (SphericalBody)bodyB;
            Box2D box = bodyA instanceof Box2D ?
                        (Box2D)bodyA : (Box2D)bodyB;
            collision = new SphereBoxCollision( sphere, box, model );
            collision.collide();
            haveCollided = true;
        }
        return haveCollided;
    }
}
