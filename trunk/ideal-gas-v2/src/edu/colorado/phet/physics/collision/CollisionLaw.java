/*
 * Class: CollisionLaw
 * Package: edu.colorado.phet.physicaldomain
 *
 * Created by: Ron LeMaster
 * Date: Oct 22, 2002
 */
package edu.colorado.phet.physics.collision;

import edu.colorado.phet.physics.Law;
import edu.colorado.phet.physics.PhysicalSystem;
import edu.colorado.phet.physics.Vector2D;
import edu.colorado.phet.physics.body.Body;
import edu.colorado.phet.physics.body.PhysicalEntity;
import edu.colorado.phet.util.MathUtil;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.mechanics.Body;
//import edu.colorado.phet.idealgas.physics.GasMolecule;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * This class implements hard contact, perfectly elastic collisions.
 */
public class CollisionLaw implements Law {

    Body[] collidingBodies = new Body[2];

    private CollisionLaw() {
        // To ensure a singleton
    }

    public void apply( float time, PhysicalSystem system ) {
        List bodies = system.getBodies();
        for( int i = 0; i < bodies.size() - 1; i++ ) {
            for( int j = i + 1; j < bodies.size(); j++ ) {
                apply( (PhysicalEntity)bodies.get( i ), (PhysicalEntity)bodies.get( j ) );
            }
        }
    }

    private void hardSphereCollision( PhysicalSystem system ) {
        // Go through all the bodies and determine their collisions within this
        // time step. Record each collision in a tree map, sorted by when they
        // occured
        TreeMap collisionMap = new TreeMap();
        List bodies = system.getBodies();
        for( int i = 0; i < bodies.size() - 1; i++ ) {
            Body bodyA = (Body)bodies.get( i );
            double earliestContact = Double.MAX_VALUE;
            Body contactBody = null;
            for( int j = i + 1; j < bodies.size(); j++ ) {
                Body bodyB = (Body)bodies.get( j );
                if( bodyA.isInContactWithBody( bodyB ) ) {
                    double contactTime = CollisionLaw.computeT1( bodyA, bodyB );
                    if( contactTime < earliestContact ) {
                        earliestContact = contactTime;
                        contactBody = bodyB;
                    }
                }
            }
            if( contactBody != null ) {
                collidingBodies[0] = bodyA;
                collidingBodies[1] = contactBody;
                List collisionList = (List)collisionMap.get( new Double( earliestContact ) );
                if( collisionList == null ) {
                    collisionList = new ArrayList();
                    collisionMap.put( new Double( earliestContact ), collisionList );
                }
                collisionList.add( collidingBodies );
            }
        }

        // Execute the collisions in the time step in the order they occurred
        while( !collisionMap.isEmpty() ) {
            Double collisionTime = (Double)collisionMap.firstKey();
            List collisionList = (List)collisionMap.get( collisionTime );
            for( int i = 0; i < collisionList.size(); i++ ) {
                CollidableBody[] collidingBodies = (CollidableBody[])collisionList.get( i );
//                collidingBodies[0].collideWithBody( collidingBodies[1] );
                Collision collision = CollisionFactory.create( collidingBodies[0], collidingBodies[1] );
                collision.collide();
            }

            // Remove the record of the collision we just performed, and remove all
            // other collision that involved the two bodies
            collisionMap.remove( collisionTime );
        }
    }

    /**
     *
     */
    public void apply( PhysicalEntity bodyA, PhysicalEntity bodyB ) {
        if( bodyA instanceof CollidableBody
                && bodyB instanceof CollidableBody ) {
            apply( (CollidableBody)bodyA, (CollidableBody)bodyB );
        }
    }

    /**
     * Applies the law to a pair of bodies
     *
     */
    public void apply( CollidableBody bodyA, CollidableBody bodyB ) {
        if( bodyA.isCollidable() && bodyB.isCollidable()
        && ( ContactDetector.areContacting( bodyA, bodyB ) )) {

            Collision collision = CollisionFactory.create( bodyA, bodyB );
            if( collision != null ) {
                collision.collide();
            }
        }
    }



    //
    // Static fields and methods
    //

    private static Vector2D s_xUnit = new Vector2D.Double( 1, 0 );
    private static Vector2D s_yUnit = new Vector2D.Double( 0, 1 );

    /**
     * Computes the time since the beginning of the last time step when the particle
     * was at a specified y coordinate.
     */
    public static float computeT1( Body bodyA, Body bodyB ) {

        float t1 = 0;

        float ra = bodyA.getContactOffset( bodyB );
        float rb = bodyB.getContactOffset( bodyA );

        float vax0 = bodyA.getVelocityPrev().getX();
        float vay0 = bodyA.getVelocityPrev().getY();
        float vbx0 = bodyB.getVelocityPrev().getX();
        float vby0 = bodyB.getVelocityPrev().getY();
        float sax0 = bodyA.getPositionPrev().getX();
        float say0 = bodyA.getPositionPrev().getY();
        float sbx0 = bodyB.getPositionPrev().getX();
        float sby0 = bodyB.getPositionPrev().getY();

        // Compute the average velocity for the time step
        float vaxAve = ( vax0 + bodyA.getVelocity().getX() ) / 2;
        float vayAve = ( vay0 + bodyA.getVelocity().getY() ) / 2;
        float vbxAve = ( vbx0 + bodyB.getVelocity().getX() ) / 2;
        float vbyAve = ( vby0 + bodyB.getVelocity().getY() ) / 2;

        // Handle walls. They have undefined x and/or y positions
/*
        if( bodyA instanceof HorizontalWall ) {
            sax0 = sbx0;
            vaxAve = vbxAve;
        }
        if( bodyB instanceof HorizontalWall ) {
            sbx0 = sax0;
            vbxAve = vaxAve;
        }
        if( bodyA instanceof VerticalWall ) {
            say0 = sby0;
            vayAve = vbyAve;
        }
        if( bodyB instanceof VerticalWall ) {
            sby0 = say0;
            vbyAve = vayAve;
        }
*/

        float vxDiff = vaxAve - vbxAve;
        float vyDiff = vayAve - vbyAve;
        float sxDiff = sax0 - sbx0;
        float syDiff = say0 - sby0;

        float a = vxDiff * vxDiff + vyDiff * vyDiff;
        float b = 2 * ( vxDiff * sxDiff + vyDiff * syDiff );
        float c = sxDiff * sxDiff + syDiff * syDiff - ( ( ra + rb ) * ( ra + rb ) );

        if( a != 0 ) {

            float[] roots = MathUtil.quadraticRoots( a, b, c );

            // Find the correct root to return.
            float dt = PhysicalSystem.instance().getDt();
            if( roots[0] >= 0 && roots[0] <= dt
                    && roots[1] >= 0 && roots[1] <= dt ) {
                t1 = Math.min( roots[0], roots[1] );
            }
            else if( roots[0] >= 0 && roots[0] <= dt ) {
                t1 = roots[0];
            }
            else if( roots[1] >= 0 && roots[1] <= dt ) {
                t1 = roots[1];
            }

            // If both roots are > dt, then send NaN back, indicating we don't
            // know what to do
            else if( roots[0] > dt && roots[1] > dt ) {
//                t1 = 0;
                t1 = Float.NaN;
            }

            // If both roots are negative, then the bodies were in
            // contact at the beginning of the time step. Therefore, we
            // will back up to the start of the time step. [???]
            else if( roots[0] < 0 && roots[1] < 0 ) {
//                t1 = 0;
                t1 = Math.max( roots[0], roots[1] );

//                t1 = Double.NaN;
            }

            else if( roots[0] * roots[1] < 0 && Math.min( roots[0], roots[1] ) >= -2 * dt ) {
                t1 = Math.min( roots[0], roots[1] );

//                t1 = Double.NaN;
            }

            // The collision must have happened more than one time step ago
            // We're not sure what to do. Punt!
            else {
                t1 = 0;

                t1 = Float.NaN;
            }

//            if( Double.isNaN( t1 ) ) {
//                t1 = 0.0;
//            }
        }
        else if( b != 0 ) {
            t1 = -c / b;
        }
        else {
            t1 = 0;
        }
        return t1;
    }


    //
    // Static fields and methods
    //
    private static CollisionLaw instance = new CollisionLaw();

    public static CollisionLaw instance() {
        return instance;
    }
}
