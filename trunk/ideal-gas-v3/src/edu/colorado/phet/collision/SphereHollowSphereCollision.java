/**
 * Class: SphereHollowSphereCollision
 * Class: edu.colorado.phet.collision
 * User: Ron LeMaster
 * Date: Sep 19, 2004
 * Time: 9:06:50 PM
 */
package edu.colorado.phet.collision;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.idealgas.model.HollowSphere;
import edu.colorado.phet.idealgas.model.SphericalBody;
import edu.colorado.phet.mechanics.Body;

import java.awt.geom.Point2D;

public class SphereHollowSphereCollision implements Collision {
//public class SphereHollowSphereCollision extends HardsphereCollision {
    private HollowSphere hollowSphere;
    private SphericalBody sphere;
    private Vector2D loa = new Vector2D.Double();
    private SphereHollowSphereContactDetector contactDetector = new SphereHollowSphereContactDetector();

    public SphereHollowSphereCollision( HollowSphere hollowSphere, SphericalBody sphere ) {
        this.hollowSphere = hollowSphere;
        this.sphere = sphere;
    }

    protected Vector2D getLoa( CollidableBody particleA, CollidableBody particleB ) {
        Point2D posA = particleA.getPosition();
        Point2D posB = particleB.getPosition();
        loa.setX( posA.getX() - posB.getX() );
        loa.setY( posA.getY() - posB.getY() );
        return loa;
    }

    public void collide() {
//        super.collide( hollowSphere, sphere, getLoa( hollowSphere, sphere), dt, model );
        double dist = Math.sqrt( hollowSphere.getPosition().distanceSq( sphere.getPosition() ) );
        double ratio = hollowSphere.getRadius() / dist;
        Point2D.Double contactPt = new Point2D.Double( hollowSphere.getPosition().getX() + ( sphere.getPosition().getX() - hollowSphere.getPosition().getX() ) * ratio,
                                                       hollowSphere.getPosition().getY() + ( sphere.getPosition().getY() - hollowSphere.getPosition().getY() ) * ratio );
        SphereHollowSphereCollision.doCollision( hollowSphere, sphere, getLoa( hollowSphere, sphere ),
                                                 contactPt );
    }

    static Vector2D vRel = new Vector2D.Double();
    static Vector2D n = new Vector2D.Double();

    public static void doCollision( Body bodyA, Body bodyB, Vector2D loa, Point2D collisionPt ) {

        // Get the total energy of the two objects, so we can conserve it
        double totalEnergy0 = bodyA.getKineticEnergy() + bodyB.getKineticEnergy();

        // Get the vectors from the bodies' CMs to the point of contact
        Vector2D r1 = new Vector2D.Float( (float)( collisionPt.getX() - bodyA.getPosition().getX() ),
                                          (float)( collisionPt.getY() - bodyA.getPosition().getY() ) );
        Vector2D r2 = new Vector2D.Float( (float)( collisionPt.getX() - bodyB.getPosition().getX() ),
                                          (float)( collisionPt.getY() - bodyB.getPosition().getY() ) );

        // Get the unit vector along the line of action
        n.setComponents( loa.getX(), loa.getY() );
        n.normalize();

        // If the relative velocity show the points moving apart, then there is no collision.
        // This is a key check to solve otherwise sticky collision problems
        vRel.setComponents( bodyA.getVelocity().getX(), bodyA.getVelocity().getY() );
        vRel.subtract( bodyB.getVelocity() );
        if( vRel.dot( n ) <= 0 ) {

            // Compute the relative velocities of the contact points
//            vAng1.setComponents( (float)( -bodyA.getOmega() * r1.getY() ), (float)( bodyA.getOmega() * r1.getX() ) );
//            vAng2.setComponents( (float)( -bodyB.getOmega() * r2.getY() ), (float)( bodyB.getOmega() * r2.getX() ) );
//            angRel.setComponents( vAng1.getX(), vAng1.getY() );
//            angRel.subtract( vAng2 );
//            float vr = vRel.dot( n ) + angRel.dot( n );
            double vr = vRel.dot( n );

            // Assume the coefficient of restitution is 1
            float e = 1;

            // Compute the impulse, j
            double numerator = -vr * ( 1 + e );

//            Vector3D n3D = new Vector3D( n );
//            Vector3D r13D = new Vector3D( r1 );
//            Vector3D t1 = r13D.crossProduct( n3D ).multiply( (float)( 1 / bodyA.getMomentOfInertia() ) );
//            Vector3D t1A = t1.crossProduct( r13D );
//            double t1B = n3D.dot( t1A );
//            Vector3D r23D = new Vector3D( r2 );
//            Vector3D t2 = r23D.crossProduct( n3D ).multiply( (float)( 1 / bodyB.getMomentOfInertia() ) );
//            Vector3D t2A = t2.crossProduct( r23D );
//            double t2B = n3D.dot( t2A );
//            double denominator = ( 1 / bodyA.getMass() + 1 / bodyB.getMass() ) + t1B + t2B;
            double denominator = ( 1 / bodyA.getMass() + 1 / bodyB.getMass() );
            double j = numerator / denominator;

            // Compute the new linear and angular velocities, based on the impulse
            Vector2D vA = new Vector2D.Double( n.getX(), n.getY() ).scale( j / bodyA.getMass() );
            bodyA.getVelocity().add( vA );

            Vector2D vB = new Vector2D.Double( n.getX(), n.getY() ).scale( -j / bodyB.getMass() );
            bodyB.getVelocity().add( vB );
        }
    }
}
