/**
 * Class: SphereSphereCollision
 * Class: edu.colorado.phet.physics.collision
 * User: Ron LeMaster
 * Date: Apr 4, 2003
 * Time: 10:28:08 AM
 */
package edu.colorado.phet.idealgas.collision;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.Vector2D;

import java.awt.geom.Point2D;

/**
 * Note: This class is not thread-safe!!!!!
 */
public class SphereSphereCollision implements Collision {
//public class SphereSphereCollision extends HardsphereCollision {

    private SphericalBody sphereA;
    private SphericalBody sphereB;
    private Vector2D loa = new Vector2D.Double();

    public SphereSphereCollision( SphericalBody sphereA, SphericalBody sphereB ) {
        this.sphereA = sphereA;
        this.sphereB = sphereB;
    }

    protected Vector2D getLoa( CollidableBody particleA, CollidableBody particleB ) {
        Point2D posA = particleA.getPosition();
        Point2D posB = particleB.getPosition();
        loa.setX( posA.getX() - posB.getX() );
        loa.setY( posA.getY() - posB.getY() );
        return loa;
    }

    public void collide() {
        double dist = Math.sqrt( sphereA.getPosition().distanceSq( sphereB.getPosition() ) );
        double ratio = sphereA.getRadius() / dist;
        Point2D.Double contactPt = new Point2D.Double( sphereA.getPosition().getX() + ( sphereB.getPosition().getX() - sphereA.getPosition().getX() ) * ratio,
                                                       sphereA.getPosition().getY() + ( sphereB.getPosition().getY() - sphereA.getPosition().getY() ) * ratio );

        doCollision( getLoa( sphereA, sphereB ), contactPt );
    }

    static Vector2D vRel = new Vector2D.Double();
    static Vector2D n = new Vector2D.Double();

    public void doCollision( Vector2D loa, Point2D contactPt ) {

        // Get the total energy of the two objects, so we can conserve it
        double totalEnergy0 = sphereA.getKineticEnergy() + sphereB.getKineticEnergy();

        // Get the vectors from the bodies' CMs to the point of contact
        Vector2D r1 = new Vector2D.Float( (float)( contactPt.getX() - sphereA.getPosition().getX() ),
                                          (float)( contactPt.getY() - sphereA.getPosition().getY() ) );
        Vector2D r2 = new Vector2D.Float( (float)( contactPt.getX() - sphereB.getPosition().getX() ),
                                          (float)( contactPt.getY() - sphereB.getPosition().getY() ) );

        // Get the unit vector along the line of action
        n.setComponents( loa.getX(), loa.getY() );
        n.normalize();

        // If the relative velocity show the points moving apart, then there is no collision.
        // This is a key check to solve otherwise sticky collision problems
        vRel.setComponents( sphereA.getVelocity().getX(), sphereA.getVelocity().getY() );
        vRel.subtract( sphereB.getVelocity() );

        //todo:!!!!!
        //        if( vRel.dot( n ) <= 0 ) {

        // Compute correct position of the bodies following the collision
        Vector2D.Double tangentVector = new Vector2D.Double( loa.getY(), -loa.getX() );

        // Determine the proper positions of the bodies following the collision
        Point2D prevPosB = sphereB.getPositionPrev();
        double prevDistB = prevPosB.distance( contactPt );
        double sB = sphereB.getRadius() / prevDistB;
//        Point2D.Double linePtB = new Point2D.Double( contactPt.getX() - (contactPt.getX() - prevPosB.getX()) * sB,
//                                                contactPt.getY() - (contactPt.getY() - prevPosB.getY()) * sB );
        double offsetB = this.sphereB.getPositionPrev().distance( this.sphereA.getPositionPrev() ) < this.sphereA.getRadius() ?
                         -this.sphereB.getRadius() : this.sphereB.getRadius();
        double offsetXB = n.getX() * offsetB;
        double offsetYB = n.getY() * offsetB;
        Point2D.Double linePtB = new Point2D.Double( contactPt.getX() - offsetXB, contactPt.getY() - offsetYB );
        Point2D pB = MathUtil.reflectPointAcrossLine( sphereB.getPosition(), linePtB,
                                                      Math.atan2( tangentVector.getY(), tangentVector.getX() ) );
//        System.out.println( "\nbodyB old position: " + sphereB.getPosition() );
        sphereB.setPosition( pB );
//        System.out.println( "sphereB new position: " + sphereB.getPosition() );
//        System.out.println( "contact pt: " + contactPt );
//        System.out.println( "tangentVector: " + tangentVector );

        // todo: The determination of the sign of the offset is wrong. It should be based on which side of the contact
        // tangent the CM was on in its previous position
        Point2D prevPosA = sphereA.getPositionPrev();
        double prevDistA = prevPosA.distance( contactPt );
        double sA = sphereA.getRadius() / prevDistA;
        Point2D.Double linePtA = new Point2D.Double( contactPt.getX() - ( contactPt.getX() - prevPosA.getX() ) * sA,
                                                     contactPt.getY() - ( contactPt.getY() - prevPosA.getY() ) * sA );
        double offsetA = -this.sphereA.getRadius();
//        double offsetA = this.sphereB.getPositionPrev().distance( this.sphereA.getPositionPrev() ) < this.sphereA.getRadius() ?
//                        -this.sphereA.getRadius() : this.sphereA.getRadius();
        double offsetXA = ( n.getX() * offsetA );
        double offsetYA = ( n.getY() * offsetA );
//        Point2D.Double linePtA = new Point2D.Double( contactPt.getX() - offsetXA, contactPt.getY() - offsetYA );
        Point2D pA = MathUtil.reflectPointAcrossLine( sphereA.getPosition(), linePtA,
                                                      Math.atan2( tangentVector.getY(), tangentVector.getX() ) );
        sphereA.setPosition( pA );

        // Compute the relative velocities of the contact points
        //            vAng1.setComponents( (float)( -sphereA.getOmega() * r1.getY() ), (float)( sphereA.getOmega() * r1.getX() ) );
        //            vAng2.setComponents( (float)( -sphereB.getOmega() * r2.getY() ), (float)( sphereB.getOmega() * r2.getX() ) );
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
        //            Vector3D t1 = r13D.crossProduct( n3D ).multiply( (float)( 1 / sphereA.getMomentOfInertia() ) );
        //            Vector3D t1A = t1.crossProduct( r13D );
        //            double t1B = n3D.dot( t1A );
        //            Vector3D r23D = new Vector3D( r2 );
        //            Vector3D t2 = r23D.crossProduct( n3D ).multiply( (float)( 1 / sphereB.getMomentOfInertia() ) );
        //            Vector3D t2A = t2.crossProduct( r23D );
        //            double t2B = n3D.dot( t2A );
        //            double denominator = ( 1 / sphereA.getMass() + 1 / sphereB.getMass() ) + t1B + t2B;
        double denominator = ( 1 / sphereA.getMass() + 1 / sphereB.getMass() );
        double j = numerator / denominator;

        // Compute the new linear and angular velocities, based on the impulse
        Vector2D vA = new Vector2D.Double( n.getX(), n.getY() ).scale( j / sphereA.getMass() );
        sphereA.getVelocity().add( vA );

        Vector2D vB = new Vector2D.Double( n.getX(), n.getY() ).scale( -j / sphereB.getMass() );
        sphereB.getVelocity().add( vB );

        //        }
    }
/*
    private static Vector2D loa = new Vector2D.Double();

    private SphericalBody sphereA;
    private SphericalBody sphereB;
    private double dt;
    private IdealGasModel model;

    public SphereSphereCollision( SphericalBody sphereA, SphericalBody sphereB,
                                  IdealGasModel model, double dt ) {
        this.sphereA = sphereA;
        this.sphereB = sphereB;
        this.model = model;
        this.dt = dt;
    }

    protected Vector2D getLoa( CollidableBody particleA, CollidableBody particleB ) {
        Point2D posA = particleA.getPosition();
        Point2D posB = particleB.getPosition();
        loa.setX( posA.getX() - posB.getX() );
        loa.setY( posA.getY() - posB.getY() );
        return loa;
    }

    public void collide() {
        super.collide( sphereA, sphereB, getLoa( sphereA, sphereB ), dt, model );
    }
*/
}
