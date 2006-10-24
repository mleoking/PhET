/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model.collision;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.mechanics.Vector3D;
import edu.colorado.phet.molecularreactions.model.AbstractMolecule;

import java.awt.geom.Point2D;

/**
 * HardSpereCollision
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class HardSphereCollision implements Collision {
    private Vector2D n = new Vector2D.Double();
    private Vector2D vRel = new Vector2D.Double();
    private Vector2D vAng1 = new Vector2D.Double();
    private Vector2D vAng2 = new Vector2D.Double();
    private Vector2D angRel = new Vector2D.Double();


    public void collide( AbstractMolecule moleculeA, AbstractMolecule moleculeB, MoleculeMoleculeCollisionSpec collisionSpec ) {
        Vector2D loa = collisionSpec.getLoa();
        Point2D.Double collisionPt = collisionSpec.getCollisionPt();

        // If the loa vector has zero length, then the two bodies are right on top of each other,
        // and the collision can't be computed
        if( loa.getMagnitude() == 0 ) {
            return;
        }

        // Get the total energy of the two objects, so we can conserve it
        double totalEnergy0 = moleculeA.getKineticEnergy() + moleculeB.getKineticEnergy();


        // Otherwise, do A perfectly elastic collision
            // Get the vectors from the bodies' CMs to the point of contact
            Vector2D r1 = new Vector2D.Double( collisionPt.getX() - moleculeA.getPosition().getX(),
                                               collisionPt.getY() - moleculeA.getPosition().getY() );
            Vector2D r2 = new Vector2D.Double( collisionPt.getX() - moleculeB.getPosition().getX(),
                                               collisionPt.getY() - moleculeB.getPosition().getY() );

            // Get the unit vector along the line of action
            n.setComponents( loa.getX(), loa.getY() );
            if( n.getMagnitude() == 0 ) {
                System.out.println( "MoleculeMoleculeCollisionAgent.doCollision" );
            }
            n.normalize();

            // If the relative velocity show the points moving apart, then there is no collision.
            // This is A key check to solve otherwise sticky collision problems
            vRel.setComponents( moleculeA.getVelocity().getX(), moleculeA.getVelocity().getY() );
            vRel.subtract( moleculeB.getVelocity() );
            if( vRel.dot( n ) <= 0 ) {

                // Compute the relative velocities of the contact points
                vAng1.setComponents( -moleculeA.getOmega() * r1.getY(), moleculeA.getOmega() * r1.getX() );
                vAng2.setComponents( -moleculeB.getOmega() * r2.getY(), moleculeB.getOmega() * r2.getX() );
                angRel.setComponents( vAng1.getX(), vAng1.getY() );
                angRel.subtract( vAng2 );
                double vr = vRel.dot( n ) + angRel.dot( n );

                // Assume the coefficient of restitution is 1
                double e = 1;

                // Compute the impulse, j
                double numerator = -vr * ( 1 + e );

                Vector3D n3D = new Vector3D( n );
                Vector3D r13D = new Vector3D( r1 );
                Vector3D t1 = r13D.crossProduct( n3D ).multiply( 1 / moleculeA.getMomentOfInertia() );
                Vector3D t1A = t1.crossProduct( r13D );
                double t1B = n3D.dot( t1A );
                Vector3D r23D = new Vector3D( r2 );
                Vector3D t2 = r23D.crossProduct( n3D ).multiply( 1 / moleculeB.getMomentOfInertia() );
                Vector3D t2A = t2.crossProduct( r23D );
                double t2B = n3D.dot( t2A );
                double denominator = ( 1 / moleculeA.getMass() + 1 / moleculeB.getMass() ) + t1B + t2B;
                double j = numerator / denominator;

                // Compute the new linear and angular velocities, based on the impulse
                moleculeA.getVelocity().add( new Vector2D.Double( n ).scale( ( j / moleculeA.getMass() ) ) );
                moleculeB.getVelocity().add( new Vector2D.Double( n ).scale( ( -j / moleculeB.getMass() ) ) );

                double dOmegaA = ( r1.getX() * n.getY() - r1.getY() * n.getX() ) * j / ( moleculeA.getMomentOfInertia() );
                double dOmegaB = ( r2.getX() * n.getY() - r2.getY() * n.getX() ) * -j / ( moleculeB.getMomentOfInertia() );
                moleculeA.setOmega( moleculeA.getOmega() + dOmegaA );
                moleculeB.setOmega( moleculeB.getOmega() + dOmegaB );

                // tweak the energy to be constant
                double d1 = moleculeA.getKineticEnergy();
                double d2 = moleculeB.getKineticEnergy();
                double totalEnergy1 = moleculeA.getKineticEnergy() + moleculeB.getKineticEnergy();
                double de = totalEnergy0 - totalEnergy1;
//        double dv = Math.sqrt( 2 * Math.abs( de ) ) * MathUtil.getSign( de );
//        double ratA = moleculeA.getMass() / ( moleculeA.getMass() + moleculeB.getMass() );
//        double dvA = dv * Math.sqrt( ratA );
//        double dvB = dv * ( 1 - ratA );
//        double vMagA = moleculeA.getVelocity().getMagnitude();
//        double fvA = ( vMagA - dvA ) / vMagA;
//        moleculeA.getVelocity().multiply( fvA );
//        double vMagB = moleculeB.getVelocity().getMagnitude();
//        double fvB = ( vMagB - dvB ) / vMagB;
//        moleculeB.getVelocity().multiply( fvB );
            }
    }

    public double getInteractionDistance() {
        return 0;
    }
}
