/**
 * Class: SphereHollowSphereCollision
 * Class: edu.colorado.phet.collision
 * User: Ron LeMaster
 * Date: Sep 19, 2004
 * Time: 9:06:50 PM
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.collision;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.Particle;

import java.awt.geom.Point2D;

public class SphereHollowSphereCollision implements Collision {
    private HollowSphere hollowSphere;
    private SphericalBody sphere;
    private Vector2D loa = new Vector2D.Double();

    public SphereHollowSphereCollision( HollowSphere hollowSphere, SphericalBody sphere ) {
        this.hollowSphere = hollowSphere;
        this.sphere = sphere;
    }

    protected Vector2D getLoa( Particle particleA, Particle particleB ) {
        Point2D posA = particleA.getPosition();
        Point2D posB = particleB.getPosition();
        loa.setX( posA.getX() - posB.getX() );
        loa.setY( posA.getY() - posB.getY() );
        return loa;
    }

    public void collide() {
        double dist = Math.sqrt( hollowSphere.getPosition().distanceSq( sphere.getPosition() ) );
        double ratio = hollowSphere.getRadius() / dist;
        Point2D.Double contactPt = new Point2D.Double( hollowSphere.getPosition().getX() + ( sphere.getPosition().getX() - hollowSphere.getPosition().getX() ) * ratio,
                                                       hollowSphere.getPosition().getY() + ( sphere.getPosition().getY() - hollowSphere.getPosition().getY() ) * ratio );

        hollowSphere.setContactPt( contactPt );
        doCollision( getLoa( hollowSphere, sphere ), contactPt );
    }

    static Vector2D vRel = new Vector2D.Double();
    static Vector2D n = new Vector2D.Double();

    public void doCollision( Vector2D loa, Point2D contactPt ) {

        // Get the total energy of the two objects, so we can conserve it
        double totalEnergy0 = hollowSphere.getKineticEnergy() + sphere.getKineticEnergy();

        // Get the vectors from the bodies' CMs to the point of contact
        Vector2D r1 = new Vector2D.Double( contactPt.getX() - hollowSphere.getPosition().getX(),
                                          contactPt.getY() - hollowSphere.getPosition().getY() );
        Vector2D r2 = new Vector2D.Double( contactPt.getX() - sphere.getPosition().getX(),
                                          contactPt.getY() - sphere.getPosition().getY() );

        // Get the unit vector along the line of action
        n.setComponents( loa.getX(), loa.getY() );
        n.normalize();

        // If the relative velocity show the points moving apart, then there is no collision.
        // This is a key check to solve otherwise sticky collision problems
        vRel.setComponents( hollowSphere.getVelocity().getX(), hollowSphere.getVelocity().getY() );
        vRel.subtract( sphere.getVelocity() );

        //todo:!!!!!
        //        if( vRel.dot( n ) <= 0 ) {

        // Compute correct position of the bodies following the collision
        Vector2D.Double tangentVector = new Vector2D.Double( loa.getY(), -loa.getX() );
        double offset = this.sphere.getPositionPrev().distance( this.hollowSphere.getPositionPrev() ) < this.hollowSphere.getRadius() ?
                        -this.sphere.getRadius() : this.sphere.getRadius();
        double offsetX = n.getX() * offset;
        double offsetY = n.getY() * offset;
        Point2D.Double linePt = new Point2D.Double( contactPt.getX() - offsetX, contactPt.getY() - offsetY );
        Point2D p = MathUtil.reflectPointAcrossLine( sphere.getPosition(), linePt,
                                                     Math.atan2( tangentVector.getY(), tangentVector.getX() ) );
//        System.out.println( "\nbodyB old position: " + sphere.getPosition() );
        sphere.setPosition( p );
//        System.out.println( "sphere new position: " + sphere.getPosition() );
//        System.out.println( "contact pt: " + contactPt );
//        System.out.println( "tangentVector: " + tangentVector );

        // Compute the relative velocities of the contact points
        //            vAng1.setComponents( (float)( -hollowSphere.getOmega() * r1.getY() ), (float)( hollowSphere.getOmega() * r1.getX() ) );
        //            vAng2.setComponents( (float)( -sphere.getOmega() * r2.getY() ), (float)( sphere.getOmega() * r2.getX() ) );
        //            angRel.setComponents( vAng1.getX(), vAng1.getY() );
        //            angRel.subtract( vAng2 );
        //            float vr = vRel.dot( n ) + angRel.dot( n );
        double vr = vRel.dot( n );

        // Assume the coefficient of restitution is 1
        double e = 1;

        // Compute the impulse, j
        double numerator = -vr * ( 1 + e );

        //            Vector3D n3D = new Vector3D( n );
        //            Vector3D r13D = new Vector3D( r1 );
        //            Vector3D t1 = r13D.crossProduct( n3D ).multiply( (float)( 1 / hollowSphere.getMomentOfInertia() ) );
        //            Vector3D t1A = t1.crossProduct( r13D );
        //            double t1B = n3D.dot( t1A );
        //            Vector3D r23D = new Vector3D( r2 );
        //            Vector3D t2 = r23D.crossProduct( n3D ).multiply( (float)( 1 / sphere.getMomentOfInertia() ) );
        //            Vector3D t2A = t2.crossProduct( r23D );
        //            double t2B = n3D.dot( t2A );
        //            double denominator = ( 1 / hollowSphere.getMass() + 1 / sphere.getMass() ) + t1B + t2B;
        double denominator = ( 1 / hollowSphere.getMass() + 1 / sphere.getMass() );
        double j = numerator / denominator;

        // Compute the new linear and angular velocities, based on the impulse
        Vector2D vA = new Vector2D.Double( n.getX(), n.getY() ).scale( j / hollowSphere.getMass() );
        hollowSphere.getVelocity().add( vA );

        Vector2D vB = new Vector2D.Double( n.getX(), n.getY() ).scale( -j / sphere.getMass() );
        sphere.getVelocity().add( vB );

        //        }
    }
}
