/**
 * Class: SphereHollowSphereExpert
 * Package: edu.colorado.phet.collision
 * Author: Another Guy
 * Date: Sep 21, 2004
 */
package edu.colorado.phet.collision;

import edu.colorado.phet.idealgas.model.Balloon;
import edu.colorado.phet.idealgas.model.HollowSphere;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.model.SphericalBody;

public class SphereBalloonExpert implements CollisionExpert {

    private ContactDetector detector = new SphereHollowSphereContactDetector();
    private IdealGasModel model;
    private double dt;

    public SphereBalloonExpert( IdealGasModel model, double dt ) {
        this.model = model;
        this.dt = dt;
    }

    public boolean detectAndDoCollision( CollidableBody bodyA, CollidableBody bodyB ) {
        boolean haveCollided = false;
        if( detector.applies( bodyA, bodyB ) && detector.areInContact( bodyA, bodyB ) ) {
            Collision collision = new SphereSphereCollision( (HollowSphere)bodyA,
                                                                   (SphericalBody)bodyB );
//            Collision collision = new SphereHollowSphereCollision( (HollowSphere)bodyA,
//                                                                   (SphericalBody)bodyB );
            collision.collide();
            haveCollided = true;

                // Get the momentum of the balloon before the collision
            Balloon balloon = bodyA instanceof Balloon ? (Balloon)bodyA :
                              ( bodyB instanceof Balloon ? (Balloon)bodyB : null ) ;

//                balloon.getMomentumPre().setX( getVelocity().getX() );
//                momentumPre.setY( getVelocity().getY() );
//                momentumPre = momentumPre.scale( this.getMass() );
//
//                // This bizarre copying from one object to another is a total hack that
//                // was made neccessary by the creation of the CollisionGod class, and the
//                // fact that some of the system uses Particles from the common code, and
//                // some of it uses Particles from the ideal gas code. It is an embarassing
//                // mess that ought to be straightened out.
//                sphericalBody.setPosition( particle.getPosition() );
//                sphericalBody.setVelocity( particle.getVelocity() );
//                super.collideWithParticle( sphericalBody );
//
//                // Get the new momentum of the balloon
//                momentumPost.setX( this.getVelocity().getX() );
//                momentumPost.setY( this.getVelocity().getY() );
//                momentumPost = momentumPost.scale( this.getMass() );
//
//                // Compute the change in momentum and record it as pressure
//                Vector2D momentumChange = momentumPost.subtract( momentumPre );
//                double impact = momentumChange.getMagnitude();
//                ScalarDataRecorder recorder = this.containsBody( particle )
//                                              ? insidePressureRecorder
//                                              : outsidePressureRecorder;
//                recorder.addDataRecordEntry( impact );
            }

        return haveCollided;
    }
}