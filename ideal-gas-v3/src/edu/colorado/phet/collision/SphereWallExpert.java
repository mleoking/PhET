/**
 * Class: SphereBoxExpert
 * Package: edu.colorado.phet.collision
 * Author: Another Guy
 * Date: Sep 21, 2004
 */
package edu.colorado.phet.collision;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.idealgas.model.IdealGasModel;

import java.awt.geom.Point2D;
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

    int leftCnt = 0;

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

        if( true ) {
            result = check2( sphere, wall );
        }
        else {


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

            Point2D sp = sphere.getPosition();
            if( sphereXPrev + sphereRadius < bounds.getMinX()
                && sphereX + sphereRadius >= bounds.getMinX() && sphereX + sphereRadius < bounds.getMaxX()
                && sphere.getVelocity().getX() > 0
                && ( sphereY >= bounds.getMinY() && sphereY <= bounds.getMaxY()
                     || sp.distanceSq( wall.getBounds().getMinX(), wall.getBounds().getMinY() ) < sphereRadius * sphereRadius
                     || sp.distanceSq( wall.getBounds().getMaxX(), wall.getBounds().getMinY() ) < sphereRadius * sphereRadius ) ) {
//            && sphereY >= bounds.getMinY() /*- sphereRadius */ && sphereY <= bounds.getMaxY() /* + sphereRadius*/
                result = LEFT_SIDE;

                leftCnt++;
                if( leftCnt >= 2 ) {
                    System.out.println( "!!!" );
                }
            }

            // Hitting right side?
//        if( sphereX - sphereRadius <= bounds.getMaxX() && sphereXPrev - sphereRadius > prevBounds.getMaxX()
//            && sphereY >= bounds.getMinY() - sphereRadius && sphereY <= bounds.getMaxY() + sphereRadius ) {
//            result = RIGHT_SIDE;
//        }
            if( sphereXPrev - sphereRadius > prevBounds.getMaxX()
                && sphereX - sphereRadius <= bounds.getMaxX() && sphereX - sphereRadius > bounds.getMinX()
                && sphere.getVelocity().getX() < 0
                && sphereY >= bounds.getMinY()/* - sphereRadius */ && sphereY <= bounds.getMaxY() /* + sphereRadius */
                && result != RIGHT_SIDE ) {
                result = RIGHT_SIDE;
            }

            // Hitting top?
//        if( sphereY + sphereRadius >= bounds.getMinY() && sphereYPrev + sphereRadius < prevBounds.getMinY()
//            && sphereX >= bounds.getMinX() - sphereRadius && sphereX <= bounds.getMaxX() + sphereRadius ) {
//            result = TOP;
//        }
            if( sphereYPrev + sphereRadius < bounds.getMinY()
                && sphereY + sphereRadius >= bounds.getMinY() && sphereY + sphereRadius < bounds.getMaxY()
                && sphere.getVelocity().getY() > 0
                && ( sphereX >= bounds.getMinX() && sphereX <= bounds.getMaxX()
                     || sphere.getPosition().distanceSq( wall.getBounds().getMinX(), wall.getBounds().getMinY() ) < sphereRadius * sphereRadius
                     || sphere.getPosition().distanceSq( wall.getBounds().getMaxX(), wall.getBounds().getMinY() ) < sphereRadius * sphereRadius ) ) {
//            && sphereX >= bounds.getMinX() && sphereX <= bounds.getMaxX() ) {
                result = TOP;
            }


            // Hitting bottom?
//        if( sphereY - sphereRadius <= bounds.getMaxY() && sphereYPrev - sphereRadius > prevBounds.getMaxY()
//            && sphereX >= bounds.getMinX() - sphereRadius && sphereX <= bounds.getMaxX() + sphereRadius ) {
//            result = BOTTOM;
//        }
            if( sphereYPrev - sphereRadius > bounds.getMaxY()
                && sphereY - sphereRadius <= bounds.getMaxY() && sphereY - sphereRadius > bounds.getMinY()
                && sphere.getVelocity().getY() < 0
                && sphereX >= bounds.getMinX() && sphereX <= bounds.getMaxX() ) {
                if( SphereWallCollision.cnt == 30 ) {
                    System.out.println( "SphereWallExpert.getContactType" );
                }
                result = BOTTOM;
            }

        }


        ContactDescriptor contactDescriptor = null;
        if( result != NO_CONTACT ) {
            contactDescriptor = new ContactDescriptor( wall, sphere, result );
        }
        return contactDescriptor;
    }


    int cnt = 0;

    private int check2( SphericalBody sphere, Wall wall ) {
        int result = NO_CONTACT;

        // P is the previous position of the sphere
        // Q is the current position of the sphere
        // A is the upper left corner of the wall
        // B is the upper right corner of the wall
        // C is the lower right corner of the wall
        // D is the lower left corner of the wall
        Point2D P = sphere.getPositionPrev();
        double Px = P.getX();
        double Py = P.getY();
        Point2D Q = sphere.getPosition();
        double Qx = Q.getX();
        double Qy = Q.getY();
//        Vector2D v = new Vector2D.Double();
        Vector2D v = new Vector2D.Double( sphere.getVelocity() ).normalize();
        double r = sphere.getRadius();

        Point2D A = new Point2D.Double( wall.getBounds().getMinX(), wall.getBounds().getMinY() );
        double Ax = A.getX();
        double Ay = A.getY();
        Point2D B = new Point2D.Double( wall.getBounds().getMaxX(), wall.getBounds().getMinY() );
        double Bx = B.getX();
        double By = B.getY();
        Point2D C = new Point2D.Double( wall.getBounds().getMaxX(), wall.getBounds().getMaxY() );
        double Cx = C.getX();
        double Cy = C.getY();
        Point2D D = new Point2D.Double( wall.getBounds().getMinX(), wall.getBounds().getMaxY() );
        double Dx = D.getX();
        double Dy = D.getY();

//        System.out.println( "=====" );
//        System.out.println( "cnt++ = " + cnt++ );
//        System.out.println( "Py = " + Py );
//        System.out.println( "Qy = " + Qy );
//        System.out.println( "Ay = " + Ay );
//        System.out.println( "Dy = " + Dy );
//        System.out.println( "Px = " + Px );
//        System.out.println( "Qx = " + Qx );
//        System.out.println( "Ax = " + Ax );
//
//        if( cnt >= 592 && cnt % 3 == 1 ) {
//            System.out.println( "stop!" );
//        }

        if( Py + r * v.getY() <= Dy && Qy + r * v.getY() >= Ay
            || Py - r * v.getY() >= Ay && Qy - r * v.getY() <= Dy ) {
            if( Px + r < Ax
                && Qx + r >= Ax ) {
//            if( Px + r * v.getX() < Ax
//                && Qx + r * v.getX() >= Ax ) {
                result = LEFT_SIDE;
            }

            if( Px - r > Bx
                && Qx - r <= Bx ) {
//            if( Px + r * v.getX() > Bx
//                && Qx + r * v.getX() <= Bx ) {
                result = RIGHT_SIDE;
            }
        }

        if( Px + r * v.getX() <= Bx && Qx + r * v.getX() >= Ax
            || Px - r * v.getX() >= Ax && Qx - r * v.getX() <= Bx ) {
            if( Py + r < Ay
                && Qy + r >= Ay ) {
//            if( Py + r * v.getY() < Ay
//                && Qy + r * v.getY() >= Ay ) {
                result = TOP;
            }

            if( Py - r > Dy
                && Qy - r <= Dy ) {
//            if( Py + r * v.getY() > Dy
//                && Qy + r * v.getY() <= Dy ) {
                result = BOTTOM;
            }
        }
        return result;
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
