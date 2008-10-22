/**
 * Class: SphereSphereContactDetector
 * Package: edu.colorado.phet.lasers.physics.collision
 * Author: Another Guy
 * Date: Mar 26, 2003
 */
package edu.colorado.phet.microwaves.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.microwaves.coreadditions.Body;
import edu.colorado.phet.microwaves.coreadditions.Vector2D;
import edu.colorado.phet.microwaves.coreadditions.Vector3D;


public class WaterMoleculeWaterMoleculeCollisionExpert {

    private static Vector2D n = new Vector2D();
    private static Vector2D vRel = new Vector2D();
    private static Vector2D vAng1 = new Vector2D();
    private static Vector2D vAng2 = new Vector2D();
    private static Vector2D angRel = new Vector2D();
    private static Vector2D loa = new Vector2D();


    /**
     * Note: This method is not thread-safe, because we use an instance attribute
     * to avoid allocating a new Vector2D on every invocation.
     *
     * @param bodyA
     * @param bodyB
     * @return
     */
    public static boolean areInContact( Body bodyA, Body bodyB ) {

        WaterMolecule moleculeA = (WaterMolecule) bodyA;
        WaterMolecule moleculeB = (WaterMolecule) bodyB;

        if ( moleculeA.isVisible() && moleculeB.isVisible()
             || !moleculeA.isVisible() && !moleculeB.isVisible() ) {

            CollisionSpec collisionSpec = getCollisionSpec( moleculeA, moleculeB );
            if ( collisionSpec != null ) {
                doCillision( moleculeA, moleculeB, collisionSpec.getLoa(), collisionSpec.getCollisionPt() );
            }
            return ( collisionSpec != null );
        }
        else {
            return false;
        }
    }


    /**
     * This is so similar to the areInContact() method that we should try to figure out how to
     * combine them
     *
     * @param bodyA
     * @param bodyB
     * @return
     */
    public static boolean areOverlapping( Body bodyA, Body bodyB ) {
        WaterMolecule moleculeA = (WaterMolecule) bodyA;
        WaterMolecule moleculeB = (WaterMolecule) bodyB;
        return getCollisionSpec( moleculeA, moleculeB ) != null;
    }


    static int cnt = 0;

    private static CollisionSpec getCollisionSpec( WaterMolecule moleculeA, WaterMolecule moleculeB ) {

        CollisionSpec collisionSpec = null;

        // Do bounding box test
        boolean boundingBoxesOverlap = false;
        double dx = Math.abs( moleculeA.getLocation().getX() - moleculeB.getLocation().getX() );
        double dy = Math.abs( moleculeA.getLocation().getY() - moleculeB.getLocation().getY() );
        boundingBoxesOverlap = dx <= moleculeA.getWidth() + moleculeB.getWidth()
                               && dy < moleculeA.getHeight() + moleculeB.getHeight();

        // Do finer grained test. Each molecule is composed of three circles
        Lobe[] lobesA = moleculeA.getLobes();
        Lobe[] lobesB = moleculeB.getLobes();
        boolean inContact = false;
        for ( int i = 0;
              boundingBoxesOverlap && !inContact && i < lobesA.length;
              i++ ) {
            for ( int j = 0; !inContact && j < lobesB.length; j++ ) {

                // Are lobes touching?
                if ( lobesA[i].getDistanceSq( lobesB[j] )
                     <= ( lobesA[i].getRadius() + lobesB[j].getRadius() )
                        * ( lobesA[i].getRadius() + lobesB[j].getRadius() ) ) {
                    inContact = true;
                    loa.setComponents( (float) ( lobesA[i].getCenterX() - lobesB[j].getCenterX() ),
                                       (float) ( lobesA[i].getCenterY() - lobesB[j].getCenterY() ) );

                    double xDiff = lobesB[j].getCenterX() - lobesA[i].getCenterX();
                    double yDiff = lobesB[j].getCenterY() - lobesA[i].getCenterY();
                    double aFrac = lobesA[i].getRadius() / ( lobesA[i].getRadius() + lobesB[j].getRadius() );
                    Point2D.Double collisionPt = new Point2D.Double( lobesA[i].getCenterX() + xDiff * aFrac,
                                                                     lobesA[i].getCenterY() + yDiff * aFrac );
                    collisionSpec = new CollisionSpec( loa, collisionPt );
                    break;
                }
            }
        }
        return collisionSpec;
    }

    /**
     * @param bodyA
     * @param bodyB
     * @param loa
     */
    public static void doCillision( Body bodyA, Body bodyB, Vector2D loa, Point2D.Double collisionPt ) {

        // Get the total energy of the two objects, so we can conserve it
        double totalEnergy0 = bodyA.getKineticEnergy() + bodyB.getKineticEnergy();

        // Get the vectors from the bodies' CMs to the point of contact
        Vector2D r1 = new Vector2D( (float) ( collisionPt.getX() - bodyA.getLocation().getX() ),
                                    (float) ( collisionPt.getY() - bodyA.getLocation().getY() ) );
        Vector2D r2 = new Vector2D( (float) ( collisionPt.getX() - bodyB.getLocation().getX() ),
                                    (float) ( collisionPt.getY() - bodyB.getLocation().getY() ) );

        // Get the unit vector along the line of action
        n.setComponents( loa ).normalize();

        // If the relative velocity show the points moving apart, then there is no collision.
        // This is a key check to solve otherwise sticky collision problems
        vRel.setComponents( bodyA.getVelocity().getX(), bodyA.getVelocity().getY() );
        vRel.subtract( bodyB.getVelocity() );
        if ( vRel.dot( n ) <= 0 ) {

            // Compute the relative velocities of the contact points
            vAng1.setComponents( (float) ( -bodyA.getOmega() * r1.getY() ), (float) ( bodyA.getOmega() * r1.getX() ) );
            vAng2.setComponents( (float) ( -bodyB.getOmega() * r2.getY() ), (float) ( bodyB.getOmega() * r2.getX() ) );
            angRel.setComponents( vAng1.getX(), vAng1.getY() );
            angRel.subtract( vAng2 );
            float vr = vRel.dot( n ) + angRel.dot( n );

            // Assume the coefficient of restitution is 1
            float e = 1;

            // Compute the impulse, j
            float numerator = -vr * ( 1 + e );

            Vector3D n3D = new Vector3D( n );
            Vector3D r13D = new Vector3D( r1 );
            Vector3D t1 = r13D.crossProduct( n3D ).multiply( (float) ( 1 / bodyA.getMomentOfInertia() ) );
            Vector3D t1A = t1.crossProduct( r13D );
            float t1B = n3D.dot( t1A );
            Vector3D r23D = new Vector3D( r2 );
            Vector3D t2 = r23D.crossProduct( n3D ).multiply( (float) ( 1 / bodyB.getMomentOfInertia() ) );
            Vector3D t2A = t2.crossProduct( r23D );
            float t2B = n3D.dot( t2A );
            double denominator = ( 1 / bodyA.getMass() + 1 / bodyB.getMass() ) + t1B + t2B;
            double j = numerator / denominator;

            // Compute the new linear and angular velocities, based on the impulse
            bodyA.getVelocity().add( new Vector2D( n ).multiply( (float) ( j / bodyA.getMass() ) ) );
            bodyB.getVelocity().add( new Vector2D( n ).multiply( (float) ( -j / bodyB.getMass() ) ) );

            double dOmegaA = ( r1.getX() * n.getY() - r1.getY() * n.getX() ) * j / ( bodyA.getMomentOfInertia() );
            double dOmegaB = ( r2.getX() * n.getY() - r2.getY() * n.getX() ) * -j / ( bodyB.getMomentOfInertia() );
            bodyA.setOmega( bodyA.getOmega() + dOmegaA );
            bodyB.setOmega( bodyB.getOmega() + dOmegaB );

            // tweak the energy to be constant
            double d1 = bodyA.getKineticEnergy();
            double d2 = bodyB.getKineticEnergy();
            double totalEnergy1 = bodyA.getKineticEnergy() + bodyB.getKineticEnergy();
            double de = totalEnergy0 - totalEnergy1;
//        double dv = Math.sqrt( 2 * Math.abs( de ) ) * MathUtil.getSign( de );
//        double ratA = bodyA.getMass() / ( bodyA.getMass() + bodyB.getMass() );
//        double dvA = dv * Math.sqrt( ratA );
//        double dvB = dv * ( 1 - ratA );
//        double vMagA = bodyA.getVelocity().getMagnitude();
//        double fvA = ( vMagA - dvA ) / vMagA;
//        bodyA.getVelocity().multiply( (float)fvA );
//        double vMagB = bodyB.getVelocity().getMagnitude();
//        double fvB = ( vMagB - dvB ) / vMagB;
//        bodyB.getVelocity().multiply( (float)fvB );
        }
    }

    private static class CollisionSpec {

        private Vector2D loa;
        private Point2D.Double collisionPt;

        public CollisionSpec( Vector2D loa, Point2D.Double collisionPt ) {
            this.loa = loa;
            this.collisionPt = collisionPt;
        }

        public Vector2D getLoa() {
            return loa;
        }

        public Point2D.Double getCollisionPt() {
            return collisionPt;
        }
    }
}
