/**
 * Class: SphereSphereContactDetector
 * Package: edu.colorado.phet.lasers.physics.collision
 * Author: Another Guy
 * Date: Mar 26, 2003
 */
package edu.colorado.phet.microwaves.model;

import edu.colorado.phet.common_microwaves.math.Vector2D;
import edu.colorado.phet.microwaves.coreadditions.Body;
import edu.colorado.phet.microwaves.coreadditions.Vector3D;
import edu.colorado.phet.microwaves.coreadditions.collision.Box2D;

import java.awt.geom.Point2D;


public class WaterMoleculeWallCollisionExpert {

    private static Vector2D loa = new Vector2D();
    private static Point2D.Double collisionPt;
    private static Vector2D n = new Vector2D();

    /**
     * Note: This method is not thread-safe, because we use an instance attribute
     * to avoid allocating a new Vector2D on every invocation.
     *
     * @param bodyA
     * @param bodyB
     * @return
     */
    public static boolean areInContact( Body bodyA, Body bodyB ) {

        Box2D box = null;
        WaterMolecule molecule;
        if( bodyA instanceof Box2D ) {
            box = (Box2D)bodyA;
            if( bodyB instanceof WaterMolecule ) {
                molecule = (WaterMolecule)bodyB;
            }
            else {
                throw new RuntimeException( "bad args" );
            }
        }
        else if( bodyB instanceof Box2D ) {
            box = (Box2D)bodyB;
            if( bodyA instanceof WaterMolecule ) {
                molecule = (WaterMolecule)bodyA;
            }
            else {
                throw new RuntimeException( "bad args" );
            }
        }
        else {
            throw new RuntimeException( "bad args" );
        }

        // Get the total energy of the two objects, so we can conserve it
        double totalEnergy0 = molecule.getKineticEnergy() + bodyB.getKineticEnergy();

        Lobe[] lobes = molecule.getLobes();
        for( int i = 0; i < lobes.length; i++ ) {
            Lobe lobe = lobes[i];

            // Hitting left wall?
            double dx = lobe.getCenterX() - lobe.getRadius() - box.getMinX();
            if( dx <= 0 && molecule.getVelocity().getX() < 0 ) {
                loa.setComponents( 1, 0 );
                collisionPt = new Point2D.Double( lobe.getCenterX() - lobe.getRadius(),
                                                  lobe.getCenterY() );
                doCollision( molecule, box.getWalls()[0], loa, collisionPt );
                Point2D.Double p = molecule.getLocation();
                p.setLocation( p.getX() - dx, p.getY() );
                molecule.setLocation( p.getX(), p.getY() );
                break;
            }

            // Hitting right wall?
            dx = lobe.getCenterX() + lobe.getRadius() - box.getMaxX();
            if( dx >= 0 && molecule.getVelocity().getX() > 0 ) {
                loa.setComponents( 1, 0 );
                collisionPt = new Point2D.Double( lobe.getCenterX() + lobe.getRadius(),
                                                  lobe.getCenterY() );
                doCollision( molecule, box.getWalls()[1], loa, collisionPt );
                Point2D.Double p = molecule.getLocation();
                p.setLocation( p.getX() - dx, p.getY() );
                molecule.setLocation( p.getX(), p.getY() );
                break;
            }

            // Hitting bottom wall?
            double dy = lobe.getCenterY() - lobe.getRadius() - box.getMinY();
            if( dy <= 0 && molecule.getVelocity().getY() < 0 ) {
                loa.setComponents( 0, 1 );
                collisionPt = new Point2D.Double( lobe.getCenterX(),
                                                  lobe.getCenterY() - lobe.getRadius() );
                doCollision( molecule, box.getWalls()[2], loa, collisionPt );
                Point2D.Double p = molecule.getLocation();
                p.setLocation( p.getX(), p.getY() - dy );
                molecule.setLocation( p.getX(), p.getY() );
                break;
            }

            // Hitting top wall?
            dy = lobe.getCenterY() + lobe.getRadius() - box.getMaxY();
            if( dy >= 0 && molecule.getVelocity().getY() > 0 ) {
                loa.setComponents( 0, 1 );
                collisionPt = new Point2D.Double( lobe.getCenterX(),
                                                  lobe.getCenterY() + lobe.getRadius() );
                doCollision( molecule, box.getWalls()[3], loa, collisionPt );
                Point2D.Double p = molecule.getLocation();
                p.setLocation( p.getX(), p.getY() - dy );
                molecule.setLocation( p.getX(), p.getY() );
                break;
            }
        }

        // tweak the energy to be constant
//        double totalEnergy1 = molecule.getKineticEnergy();
//        double de = totalEnergy0 - totalEnergy1;
//        double dv = Math.sqrt( 2 * Math.abs( de ) ) * MathUtil.getSign( de );
//        double ratA = 1;
//        double dvA = dv * ratA;
//        double vMagA = molecule.getVelocity().getMagnitude();
//        double fvA = ( vMagA - dvA ) / vMagA;
//        bodyA.getVelocity().multiply( (float)fvA );

        return false;
    }


    public static void doCollision( Body bodyA, Body bodyB, Vector2D loa, Point2D.Double collisionPt ) {

        // Get the total energy of the two objects, so we can conserve it
        double totalEnergy0 = bodyA.getKineticEnergy() /*+ bodyB.getKineticEnergy()*/;

        // Get the vectors from the bodies' CMs to the point of contact
        Vector2D r1 = new Vector2D( (float)( collisionPt.getX() - bodyA.getLocation().getX() ),
                                    (float)( collisionPt.getY() - bodyA.getLocation().getY() ) );

        // Get the unit vector along the line of action
        n.setComponents( loa ).normalize();

        // Get the magnitude along the line of action of the bodies' relative velocities at the
        // point of contact
        Vector3D omega = new Vector3D( 0, 0, (float)bodyA.getOmega() );
        Vector3D ot = omega.crossProduct( new Vector3D( r1 ) ).add( new Vector3D( bodyA.getVelocity() ) );
        float vr = ot.dot( new Vector3D( n ) );

        // Assume the coefficient of restitution is 1
        float e = 1;

        // Compute the impulse, j
        float numerator = -vr * ( 1 + e );
        Vector3D n3D = new Vector3D( n );
        Vector3D r13D = new Vector3D( r1 );
        Vector3D t1 = r13D.crossProduct( n3D ).multiply( (float)( 1 / bodyA.getMomentOfInertia() ) );
        Vector3D t1A = t1.crossProduct( t1 );
        float t1B = n3D.dot( t1A );
        double denominator = ( 1 / bodyA.getMass() ) + t1B;
        denominator = ( 1 / bodyA.getMass() ) +
                      ( n3D.dot( r13D.crossProduct( n3D ).multiply( 1 / (float)bodyA.getMomentOfInertia() ).crossProduct( r13D ) ) );
        double j = numerator / denominator;

        // Compute the new linear and angular velocities, based on the impulse
        bodyA.getVelocity().add( new Vector2D( n ).multiply( (float)( j / bodyA.getMass() ) ) );

        double I = bodyA.getMomentOfInertia();
        Vector3D nj = new Vector3D( n ).multiply( (float)j );
        double omegaB = bodyA.getOmega() + ( r13D.crossProduct( nj ).getZ() / bodyA.getMomentOfInertia() );
        bodyA.setOmega( bodyA.getOmega() + ( r1.getX() * n.getY() - r1.getY() * n.getX() ) * j / ( bodyA.getMomentOfInertia() ) );

        // tweak the energy to be constant
//        double totalEnergy1 = bodyA.getKineticEnergy() /*+ bodyB.getKineticEnergy()*/;
//        double de = totalEnergy0 - totalEnergy1;
//        double dv = Math.sqrt( 2 * Math.abs( de ) ) * MathUtil.getSign( de );
//        double ratA = 1;
//        double dvA = dv * ratA;
//        double vMagA = bodyA.getVelocity().getMagnitude();
//        double fvA = ( vMagA - dvA ) / vMagA;
//        bodyA.getVelocity().multiply( (float)fvA );
    }

    public static boolean areOverlapping( WaterMolecule molecule, Box2D box ) {

        for( int i = 0; i < molecule.getLobes().length; i++ ) {
            Lobe lobe = molecule.getLobes()[i];
            // Hitting left wall?
            if( lobe.getCenterX() - lobe.getRadius() <= box.getMinX() ) {
                return true;
            }

            // Hitting right wall?
            if( lobe.getCenterX() + lobe.getRadius() >= box.getMaxX() ) {
                return true;
            }

            // Hitting bottom wall?
            if( lobe.getCenterY() - lobe.getRadius() <= box.getMinY() ) {
                return true;
            }

            // Hitting top wall?
            if( lobe.getCenterY() + lobe.getRadius() >= box.getMaxY() ) {
                return true;
            }
        }
        return false;
    }
}
