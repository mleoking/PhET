/**
 * Class: SphereSphereContactDetector
 * Package: edu.colorado.phet.lasers.physics.collision
 * Author: Another Guy
 * Date: Mar 26, 2003
 */
package edu.colorado.phet.collision;

import edu.colorado.phet.idealgas.model.Box2D;

/**
 * TODO: Remove. Class is vestigial
 */
public class SphereBoxCollisionExpert implements ContactDetector {

    public boolean applies( CollidableBody bodyA, CollidableBody bodyB ) {
        return ( bodyA instanceof SphericalBody && bodyB instanceof Box2D )
               || ( bodyA instanceof Box2D && bodyB instanceof SphericalBody );
    }

    public boolean areInContact( CollidableBody bodyA, CollidableBody bodyB ) {
        boolean result = false;
        Box2D box = null;
        SphericalBody sphere;

        // Check that the arguments are valid
        if( bodyA instanceof Box2D ) {
            box = (Box2D)bodyA;
            if( bodyB instanceof SphericalBody ) {
                sphere = (SphericalBody)bodyB;
            }
            else {
                throw new RuntimeException( "bad args" );
            }
        }
        else if( bodyB instanceof Box2D ) {
            box = (Box2D)bodyB;
            if( bodyA instanceof SphericalBody ) {
                sphere = (SphericalBody)bodyA;
            }
            else {
                throw new RuntimeException( "bad args" );
            }
        }
        else {
            throw new RuntimeException( "bad args" );
        }

        // Hitting left wall?
        double dx = sphere.getCenter().getX() - sphere.getRadius() - box.getMinX();
        if( dx <= 0 && sphere.getVelocity().getX() - box.getLeftWallVx() < 0 ) {
            result = true;
        }

        // Hitting right wall?
        dx = sphere.getCenter().getX() + sphere.getRadius() - box.getMaxX();
        if( dx >= 0 && sphere.getVelocity().getX() > 0 ) {
            result = true;
        }

        // Hitting bottom wall?
        double dy = sphere.getCenter().getY() - sphere.getRadius() - box.getMinY();
        if( dy <= 0 && sphere.getVelocity().getY() < 0 ) {
            result = true;
        }

        // Hitting top wall?
        dy = sphere.getCenter().getY() + sphere.getRadius() - box.getMaxY();
        if( dy >= 0 && sphere.getVelocity().getY() > 0 ) {
            result = true;
        }
        return result;
    }
}
