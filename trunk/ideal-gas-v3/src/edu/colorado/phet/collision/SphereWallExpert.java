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

    public static final int NO_CONTACT = 0, LEFT_SIDE = 1, RIGHT_SIDE = 2, TOP = 3, BOTTOM = 4;

    private IdealGasModel model;

    /**
     * @param model
     */
    public SphereWallExpert( IdealGasModel model ) {
        this.model = model;
    }

    public boolean detectAndDoCollision( CollidableBody bodyA, CollidableBody bodyB ) {
        boolean haveCollided = false;
        ContactDescriptor contactType = getContactType( bodyA, bodyB );
        if( contactType != null ) {
            new SphereWallCollision( contactType.sphere, contactType.wall, contactType.type, model ).collide();
            haveCollided = true;
        }
        return haveCollided;
    }

    public boolean applies( CollidableBody bodyA, CollidableBody bodyB ) {
        return ( bodyA instanceof SphericalBody && bodyB instanceof Wall )
               || ( bodyA instanceof Wall && bodyB instanceof SphericalBody );
    }

    public boolean areInContact( CollidableBody bodyA, CollidableBody bodyB ) {
        return getContactType( bodyA, bodyB ).type != NO_CONTACT;
    }

    private ContactDescriptor getContactType( CollidableBody bodyA, CollidableBody bodyB ) {
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
                return null;
            }
        }
        else if( bodyB instanceof Wall ) {
            wall = (Wall)bodyB;
            if( bodyA instanceof SphericalBody ) {
                sphere = (SphericalBody)bodyA;
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }

        Rectangle2D bounds = wall.getBounds();
        Rectangle2D prevBounds = wall.getPrevBounds();
        double sphereX = sphere.getPosition().getX();
        double sphereXPrev = sphere.getPositionPrev().getX();
        double sphereY = sphere.getPosition().getY();
        double sphereYPrev = sphere.getPositionPrev().getY();
        double sphereRadius = sphere.getRadius();

        // Hitting left side of wall?
//        if( sphereX + sphereRadius >= bounds.getMinX() && sphereXPrev + sphereRadius < prevBounds.getMinX()
//            && sphereY >= bounds.getMinY() - sphereRadius && sphereY <= bounds.getMaxY() + sphereRadius ) {
//            result = LEFT_SIDE;
//        }
        if( sphereX + sphereRadius >= bounds.getMinX() && sphereX + sphereRadius < bounds.getMaxX()
            && sphere.getVelocity().getX() > 0
            && sphereY >= bounds.getMinY() - sphereRadius && sphereY <= bounds.getMaxY() + sphereRadius
            && result != LEFT_SIDE ) {
            result = LEFT_SIDE;
        }

        // Hitting right side?
//        if( sphereX - sphereRadius <= bounds.getMaxX() && sphereXPrev - sphereRadius > prevBounds.getMaxX()
//            && sphereY >= bounds.getMinY() - sphereRadius && sphereY <= bounds.getMaxY() + sphereRadius ) {
//            result = RIGHT_SIDE;
//        }
        if( sphereX - sphereRadius <= bounds.getMaxX() && sphereX - sphereRadius > bounds.getMinX()
            && sphere.getVelocity().getX() < 0
            && sphereY >= bounds.getMinY() - sphereRadius && sphereY <= bounds.getMaxY() + sphereRadius
            && result != RIGHT_SIDE ) {
            result = RIGHT_SIDE;
        }

        // Hitting top?
//        if( sphereY + sphereRadius >= bounds.getMinY() && sphereYPrev + sphereRadius < prevBounds.getMinY()
//            && sphereX >= bounds.getMinX() - sphereRadius && sphereX <= bounds.getMaxX() + sphereRadius ) {
//            result = TOP;
//        }
        if( sphereY + sphereRadius >= bounds.getMinY() && sphereY + sphereRadius < bounds.getMaxY()
        && sphere.getVelocity().getY() > 0
        && sphereX >= bounds.getMinX() && sphereX <= bounds.getMaxX() ){
            result = TOP;
        }


        // Hitting bottom?
//        if( sphereY - sphereRadius <= bounds.getMaxY() && sphereYPrev - sphereRadius > prevBounds.getMaxY()
//            && sphereX >= bounds.getMinX() - sphereRadius && sphereX <= bounds.getMaxX() + sphereRadius ) {
//            result = BOTTOM;
//        }
        if( sphereY - sphereRadius <= bounds.getMaxY() && sphereY - sphereRadius > bounds.getMinY()
        && sphere.getVelocity().getY() < 0
        && sphereX >= bounds.getMinX() && sphereX <= bounds.getMaxX() ){
            result = BOTTOM;
        }

        ContactDescriptor contactDescriptor = null;
        if( result != NO_CONTACT ) {
            contactDescriptor = new ContactDescriptor( wall, sphere, result );
        }
        return contactDescriptor;
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    /**
     * Data type that describes the input parameters for a collision
     */
    private class ContactDescriptor {
        Wall wall;
        SphericalBody sphere;
        int type;

        public ContactDescriptor( Wall wall, SphericalBody sphere, int type ) {
            this.wall = wall;
            this.sphere = sphere;
            this.type = type;
        }
    }
}
