/**
 * Class: SphereHollowSphereExpert
 * Package: edu.colorado.phet.collision
 * Author: Another Guy
 * Date: Sep 21, 2004
 */
package edu.colorado.phet.collision;

import edu.colorado.phet.idealgas.model.IdealGasModel;

public class SphereBalloonExpert extends SphereHollowSphereExpert {

    private ContactDetector detector = new SphereHollowSphereContactDetector();
    private IdealGasModel model;
    private double dt;

    public SphereBalloonExpert( IdealGasModel model, double dt ) {
        super( model, dt );
        this.model = model;
        this.dt = dt;
    }

    public boolean detectAndDoCollision( CollidableBody bodyA, CollidableBody bodyB ) {
        boolean haveCollided = super.detectAndDoCollision( bodyA, bodyB );
//        if( haveCollided ) {
//            Balloon balloon = bodyA instanceof Balloon ? (Balloon)bodyA : (Balloon)bodyB;
//            balloon.collideWithParticle( bodyB );
//        }
        return haveCollided;
    }
}