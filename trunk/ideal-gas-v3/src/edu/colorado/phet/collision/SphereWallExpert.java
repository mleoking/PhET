/**
 * Class: SphereBoxExpert
 * Package: edu.colorado.phet.collision
 * Author: Another Guy
 * Date: Sep 21, 2004
 */
package edu.colorado.phet.collision;

import edu.colorado.phet.idealgas.model.IdealGasModel;

import java.awt.geom.Rectangle2D;

public class SphereWallExpert implements CollisionExpert, ContactDetector {

    public static final int LEFT_SIDE = 1, RIGHT_SIDE = 2, TOP = 3, BOTTOM = 4, NO_CONTACT = 0;
    private Collision collision;
    private IdealGasModel model;

    public SphereWallExpert( IdealGasModel model ) {
        this.model = model;
    }

    public boolean detectAndDoCollision( CollidableBody bodyA, CollidableBody bodyB ) {
        boolean haveCollided = false;
        if( applies( bodyA, bodyB ) && areInContact( bodyA, bodyB ) ) {
            SphericalBody sphere = bodyA instanceof SphericalBody ?
                                   (SphericalBody)bodyA : (SphericalBody)bodyB;
            Wall wall = bodyA instanceof Wall ?
                        (Wall)bodyA : (Wall)bodyB;
            int contactType = getContactType( bodyA, bodyB );
            collision = new SphereWallCollision( sphere, wall, contactType, model );
            collision.collide();
            haveCollided = true;
        }
        return haveCollided;
    }

    public boolean applies( CollidableBody bodyA, CollidableBody bodyB ) {
        return ( bodyA instanceof SphericalBody && bodyB instanceof Wall )
               || ( bodyA instanceof Wall && bodyB instanceof SphericalBody );
    }

    public boolean areInContact( CollidableBody bodyA, CollidableBody bodyB ) {
        return getContactType( bodyA, bodyB ) != NO_CONTACT;
    }

    private int getContactType( CollidableBody bodyA, CollidableBody bodyB ) {
        int result = NO_CONTACT;
        Wall wall = null;
        SphericalBody sphere;

        // Check that the arguments are valid
        if( bodyA instanceof Wall ) {
            wall = (Wall)bodyA;
            if( bodyB instanceof SphericalBody ) {
                sphere = (SphericalBody)bodyB;
            }
            else {
                throw new RuntimeException( "bad args" );
            }
        }
        else if( bodyB instanceof Wall ) {
            wall = (Wall)bodyB;
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

        Rectangle2D bounds = wall.getBounds();
        Rectangle2D prevBounds = wall.getPrevBounds();
        double sphereX = sphere.getPosition().getX();
        double sphereXPrev = sphere.getPositionPrev().getX();
        double sphereY = sphere.getPosition().getY();
        double sphereYPrev = sphere.getPositionPrev().getY();
        // Hitting left side of wall?
        if( sphereX + sphere.getRadius() >= bounds.getMinX() && sphereXPrev + sphere.getRadius() < prevBounds.getMinX()
            && sphereY >= bounds.getMinY() && sphereY <= bounds.getMaxY() ) {
            result = LEFT_SIDE;
        }

        // Hitting right side?
        if( sphereX - sphere.getRadius() <= bounds.getMaxX() && sphereXPrev - sphere.getRadius() > prevBounds.getMaxX()
            && sphereY >= bounds.getMinY() && sphereY <= bounds.getMaxY() ) {
            result = RIGHT_SIDE;
        }

        // Hitting top?
        if( sphereY + sphere.getRadius() >= bounds.getMinY() && sphereYPrev + sphere.getRadius() < prevBounds.getMinY()
            && sphereX >= bounds.getMinX() && sphereX <= bounds.getMaxX() ) {
            result = TOP;
        }

        // Hitting bottom?
        if( sphereY - sphere.getRadius() <= bounds.getMaxY() && sphereYPrev - sphere.getRadius() > prevBounds.getMaxY()
            && sphereX >= bounds.getMinX() && sphereX <= bounds.getMaxX() ) {
            result = BOTTOM;
        }
        return result;
    }


}
