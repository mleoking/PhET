/**
 * Class: SphereHollowSphereExpert
 * Package: edu.colorado.phet.collision
 * Author: Another Guy
 * Date: Sep 21, 2004
 */
package edu.colorado.phet.collision;

import edu.colorado.phet.idealgas.model.HollowSphere;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.model.SphericalBody;

public class SphereHollowSphereExpert implements CollisionExpert {

    private ContactDetector detector = new SphereHollowSphereContactDetector();
    private IdealGasModel model;
    private double dt;

    public SphereHollowSphereExpert( IdealGasModel model, double dt ) {
        this.model = model;
        this.dt = dt;
    }

    public boolean detectAndDoCollision( CollidableBody bodyA, CollidableBody bodyB ) {
        boolean haveCollided = false;
        if( detector.applies( bodyA, bodyB ) && detector.areInContact( bodyA, bodyB ) ) {
            Collision collision = new SphereHollowSphereCollision( (HollowSphere)bodyA,
                                                                   (SphericalBody)bodyB );
            collision.collide();
            haveCollided = true;
        }
        return haveCollided;
    }
}