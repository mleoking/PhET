/**
 * Class: SphereBoxExpert
 * Package: edu.colorado.phet.collision
 * Author: Another Guy
 * Date: Sep 21, 2004
 */
package edu.colorado.phet.collision;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.idealgas.model.IdealGasModel;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

public class SphereWallExpert implements CollisionExpert, ContactDetector {

    public static final int NO_CONTACT = 0, LEFT_SIDE = 1, RIGHT_SIDE = 2, TOP = 3, BOTTOM = 4;

    private IdealGasModel model;
    private Map walls = new HashMap();

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

        result = check3( sphere, wall );
        ContactDescriptor contactDescriptor = null;
        if( result != NO_CONTACT ) {
            contactDescriptor = new ContactDescriptor( wall, sphere, result );
        }
        return contactDescriptor;
    }


    private int check3( SphericalBody sphere, Wall wall ) {
        int result = NO_CONTACT;

        WallDescriptor wallDesc = (WallDescriptor)walls.get( wall );
        if( wallDesc == null ) {
            wallDesc = new WallDescriptor( wall, sphere.getRadius() );
            walls.put( wall, wallDesc );
        }

        if( wallDesc.contactBounds.contains( sphere.getPosition() ) ) {
            // P is the previous position of the sphere
            // Q is the current position of the sphere
            Point2D Q = sphere.getPosition();
            double Qx = Q.getX();
            double Qy = Q.getY();

            Vector2D v = new Vector2D.Double( -sphere.getVelocity().getX(), -sphere.getVelocity().getY() ).normalize().scale( 1000 );
            Point2D P = new Point2D.Double( Qx + v.getX(), Qy + v.getY() );
            Line2D PQ = new Line2D.Double( Q, P );
            if( PQ.intersectsLine( wallDesc.AB ) ) {
                result = TOP;
            }
            if( PQ.intersectsLine( wallDesc.BC ) ) {
                result = RIGHT_SIDE;
            }
            if( PQ.intersectsLine( wallDesc.CD ) ) {
                result = BOTTOM;
            }
            if( PQ.intersectsLine( wallDesc.AD ) ) {
                result = LEFT_SIDE;
            }
        }
        return result;
    }

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
        if( SphereWallCollision.cnt >= 56 ) {
            System.out.println( "SphereWallExpert.check2" );
        }

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

    /**
     * Describes the contact bounds of a wall, and line segments that constitute those bounds
     */
    private class WallDescriptor implements Wall.ChangeListener {
        Rectangle2D contactBounds;
        // A is the upper left corner of the wall
        // B is the upper right corner of the wall
        // C is the lower right corner of the wall
        // D is the lower left corner of the wall
        Line2D AB;
        Line2D BC;
        Line2D CD;
        Line2D AD;
        private double contactRadius;

        public WallDescriptor( Wall wall, double contactRadius ) {
            this.contactRadius = contactRadius;
            computeDescriptor( wall );
            wall.addChangeListener( this );
        }

        private void computeDescriptor( Wall wall ) {
            contactBounds = new Rectangle2D.Double( wall.getBounds().getMinX() - contactRadius,
                                                                wall.getBounds().getMinY() - contactRadius,
                                                                wall.getBounds().getWidth() + 2 * contactRadius,
                                                                wall.getBounds().getHeight() + 2 * contactRadius );
            Point2D A = new Point2D.Double( contactBounds.getMinX(),
                                            contactBounds.getMinY() );
            Point2D B = new Point2D.Double( contactBounds.getMaxX(),
                                            contactBounds.getMinY() );
            Point2D C = new Point2D.Double( contactBounds.getMaxX(),
                                            contactBounds.getMaxY() );
            Point2D D = new Point2D.Double( contactBounds.getMinX(),
                                            contactBounds.getMaxY() );

            AB = new Line2D.Double( A, B );
            BC = new Line2D.Double( B, C );
            CD = new Line2D.Double( C, D );
            AD = new Line2D.Double( A, D );
        }

        public void wallChanged( Wall.ChangeEvent event ) {
            computeDescriptor( event.getWall() );
        }
    }
}
