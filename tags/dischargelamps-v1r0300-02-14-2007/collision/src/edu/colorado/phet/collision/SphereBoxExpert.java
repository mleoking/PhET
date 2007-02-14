/**
 * Class: SphereBoxExpert
 * Package: edu.colorado.phet.collision
 * Author: Another Guy
 * Date: Sep 21, 2004
 */
package edu.colorado.phet.collision;

public class SphereBoxExpert implements CollisionExpert, ContactDetector {

    private Collision collision;

    public boolean detectAndDoCollision( Collidable bodyA, Collidable bodyB ) {
        boolean haveCollided = false;
        if( applies( bodyA, bodyB ) && areInContact( bodyA, bodyB ) ) {
            SphericalBody sphere = bodyA instanceof SphericalBody ?
                                   (SphericalBody)bodyA : (SphericalBody)bodyB;
            Box2D box = bodyA instanceof Box2D ?
                        (Box2D)bodyA : (Box2D)bodyB;
            collision = new SphereBoxCollision( sphere, box );
            collision.collide();
            haveCollided = true;
        }
        return haveCollided;
    }

    public boolean applies( Collidable bodyA, Collidable bodyB ) {
        return ( bodyA instanceof SphericalBody && bodyB instanceof Box2D )
               || ( bodyA instanceof Box2D && bodyB instanceof SphericalBody );
    }

    public boolean areInContact( Collidable bodyA, Collidable bodyB ) {
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
        if( dx <= 0 ) {
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
