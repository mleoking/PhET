/**
 * Class: SphereBoxExpert
 * Package: edu.colorado.phet.collision
 * Author: Another Guy
 * Date: Sep 21, 2004
 */
package edu.colorado.phet.idealgas.collision;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.idealgas.model.IdealGasModel;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
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

    /**
     * Determines if the sphere is hitting the wall and returns an object that describes the
     * nature of the contact.
     *
     * @param bodyA
     * @param bodyB
     * @return
     */
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

    /**
     * Determines if the sphere is hitting the wall and if it is, which side of the wall it's hitting
     *
     * @param sphere
     * @param wall
     * @return
     */
    private int check3( SphericalBody sphere, Wall wall ) {
        int result = NO_CONTACT;

        WallDescriptor wallDesc = (WallDescriptor)walls.get( wall );
        if( wallDesc == null ) {
            wallDesc = new WallDescriptor( wall, sphere.getRadius() );
            walls.put( wall, wallDesc );
        }

        // P is the previous position of the sphere
        // Q is the current position of the sphere
        Point2D Q = sphere.getPosition();
        Point2D P = sphere.getPositionPrev();
        Line2D PQ = new Line2D.Double( Q, P );
        if( PQ.intersectsLine( wallDesc.AB ) && sphere.getVelocity().getY() > 0 ) {
            result = TOP;
        }
        if( PQ.intersectsLine( wallDesc.BC ) && sphere.getVelocity().getX() < 0 ) {
            result = RIGHT_SIDE;
        }
        if( PQ.intersectsLine( wallDesc.CD ) && sphere.getVelocity().getY() < 0 ) {
            result = BOTTOM;
        }
        if( PQ.intersectsLine( wallDesc.AD ) && sphere.getVelocity().getX() > 0 ) {
            result = LEFT_SIDE;
        }
        return result;
    }

    /**
     * Last-ditch method to get a sphere out of the wall. A total hack.
     *
     * @param wall
     * @param sphere
     */
    public void fixup( Wall wall, SphericalBody sphere ) {
        WallDescriptor wallDesc = new WallDescriptor( wall, sphere.getRadius() );

        // Figure out which boundary of the wall the sphere crossed to get inside
        Vector2D v = new Vector2D.Double( -sphere.getVelocity().getX(), -sphere.getVelocity().getY() ).normalize().scale( 1000 );
        Point2D Q = sphere.getPosition();
        Point2D P = new Point2D.Double( Q.getX() + v.getX(), Q.getY() + v.getY() );
        Line2D PQ = new Line2D.Double( Q, P );
        if( PQ.intersectsLine( wallDesc.AB ) ) {
            sphere.setVelocity( sphere.getVelocity().getX(), -sphere.getVelocity().getY() );
            sphere.setPosition( sphere.getPosition().getX(), wallDesc.AB.getY1() );
            System.out.println( "SphereWallExpert.fixup: AB" );
        }
        if( PQ.intersectsLine( wallDesc.BC ) ) {
            sphere.setVelocity( -sphere.getVelocity().getX(), sphere.getVelocity().getY() );
            sphere.setPosition( wallDesc.BC.getX1(), sphere.getPosition().getY() );
            System.out.println( "SphereWallExpert.fixup: BC" );
        }
        if( PQ.intersectsLine( wallDesc.CD ) ) {
            sphere.setVelocity( sphere.getVelocity().getX(), -sphere.getVelocity().getY() );
            sphere.setPosition( sphere.getPosition().getX(), wallDesc.CD.getY1() );
            System.out.println( "SphereWallExpert.fixup: CD" );
        }
        if( PQ.intersectsLine( wallDesc.AD ) ) {
            sphere.setVelocity( -sphere.getVelocity().getX(), sphere.getVelocity().getY() );
            sphere.setPosition( wallDesc.AD.getX1(), sphere.getPosition().getY() );
            System.out.println( "SphereWallExpert.fixup: AD" );
        }
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
