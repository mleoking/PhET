/**
 * Class: SphereSphereExpert
 * Package: edu.colorado.phet.collision
 * Author: Another Guy
 * Date: Sep 21, 2004
 */
package edu.colorado.phet.collision;

import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.model.SphericalBody;

public class SphereSphereExpert implements CollisionExpert {
    private SphereSphereContactDetector detector = new SphereSphereContactDetector();
    private IdealGasModel model;
    private double dt;

    public SphereSphereExpert( IdealGasModel model, double dt ) {
        this.model = model;
        this.dt = dt;
    }

    public void detectAndDoCollision( CollidableBody bodyA, CollidableBody bodyB ) {
        if( detector.applies( bodyA, bodyB ) && detector.areInContact( bodyA, bodyB )) {
            Collision collision = new SphereSphereCollision( (SphericalBody)bodyA,
                                                             (SphericalBody)bodyB,
                                                             model, dt );
            collision.collide();
        }
    }
}
