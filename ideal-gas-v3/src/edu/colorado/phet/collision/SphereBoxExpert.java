/**
 * Class: SphereBoxExpert
 * Package: edu.colorado.phet.collision
 * Author: Another Guy
 * Date: Sep 21, 2004
 */
package edu.colorado.phet.collision;

import edu.colorado.phet.idealgas.model.Box2D;
import edu.colorado.phet.idealgas.model.IdealGasModel;

public class SphereBoxExpert implements CollisionExpert, ContactDetector {

//    private SphereBoxCollisionExpert detector = new SphereBoxCollisionExpert();
    private Collision collision;
    private IdealGasModel model;

    public SphereBoxExpert( IdealGasModel model ) {
        this.model = model;
    }

    static int cnt = 0;

    public boolean detectAndDoCollision( CollidableBody bodyA, CollidableBody bodyB ) {
        boolean haveCollided = false;
        if( applies( bodyA, bodyB ) && areInContact( bodyA, bodyB ) ) {
            SphericalBody sphere = bodyA instanceof SphericalBody ?
                                   (SphericalBody)bodyA : (SphericalBody)bodyB;
            Box2D box = bodyA instanceof Box2D ?
                        (Box2D)bodyA : (Box2D)bodyB;
            if( !box.isInOpening( sphere ) ) {

                boolean result = false;
                double sx = sphere.getPosition().getX();
                double sy = sphere.getPosition().getY();
                double r = sphere.getRadius();

                if( box.isInOpening( sphere ) ) {
                    return result;
                }

                // Check for contact with each of the walls
                boolean leftWall = ( sx - r ) <= box.getMinX();
                boolean rightWall = ( sx + r ) >= box.getMaxX();
                boolean topWall = ( sy - r ) <= box.getMinY();
                boolean bottomWall = ( sy + r ) >= box.getMaxY();

                //If the sphere is in contact with two opposing walls, don't do anything
                if( !( leftWall && rightWall || topWall && bottomWall ) ) {

                    collision = new SphereBoxCollision( sphere, box, model );
                    collision.collide();
                    // If the following line is enabled, the helium balloon will freeze the simulation
                    // if it expands to fill the box
                    haveCollided = true;
                }
            }
        }
        return haveCollided;
    }

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
