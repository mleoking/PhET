/**
 * Class: BalloonSphereCollision
 * Package: edu.colorado.phet.idealgas.model.collision
 * Author: Another Guy
 * Date: Mar 12, 2004
 */
package edu.colorado.phet.idealgas.model.collision;

import edu.colorado.phet.physics.collision.*;
import edu.colorado.phet.physics.body.Particle;
import edu.colorado.phet.physics.Vector2D;
import edu.colorado.phet.idealgas.model.body.Balloon;

public class BalloonSphereCollision implements Collision {
//public class BalloonSphereCollision implements Collision {
    private Balloon balloon;
    private Particle particle;
    private Vector2D momentumPre = new Vector2D( );
    private Vector2D momentumPost = new Vector2D( );

    public BalloonSphereCollision() {
    }

    public BalloonSphereCollision( Balloon balloon, Particle particle ) {
        this.balloon = balloon;
        this.particle = particle;
    }

    public void collide() {

        // Get the momentum of the balloon before the collision
        momentumPre.setX( balloon.getVelocity().getX() );
        momentumPre.setY( balloon.getVelocity().getY() );
        momentumPre = momentumPre.multiply( balloon.getMass() );

        // Do the collisions itself
        SphereSphereCollision collision = new SphereSphereCollision( (SphericalBody)balloon,
                                                                     (SphericalBody)particle );
        collision.collide( );

        // Get the new momentum of the balloon
        momentumPost.setX( balloon.getVelocity().getX() );
        momentumPost.setY( balloon.getVelocity().getY() );
        momentumPost = momentumPost.multiply( balloon.getMass() );

        // Compute the change in momentum and record it as pressure
        Vector2D momentumChange = momentumPost.subtract( momentumPre );
        float impact = momentumChange.getLength();
        balloon.recordImpact( impact, particle );

    }

    public Collision createIfApplicable( Particle particleA, Particle particleB ) {
        Collision collision = null;
        if( particleA instanceof Balloon && particleB instanceof SphericalBody ) {
            collision = new BalloonSphereCollision( (Balloon)particleA, particleB );
        }
        else if( particleB instanceof Balloon && particleA instanceof SphericalBody) {
            collision = new BalloonSphereCollision( (Balloon)particleB, particleA );
        }
        return collision;
    }

    //
    // Static fields and methods
    //
    static public void register() {
        CollisionFactory.addPrototype( new BalloonSphereCollision() );
    }
}
