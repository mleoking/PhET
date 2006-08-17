/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.util.collision;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.mechanics.Body;
import edu.colorado.phet.mechanics.Vector3D;
import edu.colorado.phet.molecularreactions.model.Molecule;
import edu.colorado.phet.molecularreactions.model.RoundMolecule;
import edu.colorado.phet.molecularreactions.model.CompoundMolecule;

import java.awt.geom.Point2D;

/**
 * MoleculeMoleculeCollisionAgent
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculeMoleculeCollisionAgent {

    private Vector2D n = new Vector2D.Double();
    private Vector2D vRel = new Vector2D.Double();
    private Vector2D vAng1 = new Vector2D.Double();
    private Vector2D vAng2 = new Vector2D.Double();
    private Vector2D angRel = new Vector2D.Double();
    private Vector2D loa = new Vector2D.Double();


    /**
     * Note: This method is not thread-safe, because we use an instance attribute
     * to avoid allocating a new Vector2D on every invocation.
     *
     * @param bodyA
     * @param bodyB
     * @return
     */
    public boolean detectAndDoCollision( Body bodyA, Body bodyB ) {

        Molecule moleculeA = (Molecule)bodyA;
        Molecule moleculeB = (Molecule)bodyB;

        // Do bounding box test
        boolean boundingBoxesOverlap = false;
        double dx = Math.abs( moleculeA.getPosition().getX() - moleculeB.getPosition().getX() );
        double dy = Math.abs( moleculeA.getPosition().getY() - moleculeB.getPosition().getY() );
        boundingBoxesOverlap = dx <= moleculeA.getBoundingBox().getWidth() + moleculeB.getBoundingBox().getWidth()
                               && dy < moleculeA.getBoundingBox().getHeight() + moleculeB.getBoundingBox().getHeight();

        CollisionSpec collisionSpec = null;
        if( boundingBoxesOverlap ) {
            collisionSpec = getCollisionSpec( moleculeA, moleculeB );
            if( collisionSpec != null ) {
                doCillision( moleculeA, moleculeB, collisionSpec.getLoa(), collisionSpec.getCollisionPt() );
            }

        }
        return ( collisionSpec != null );
    }


    private CollisionSpec getCollisionSpec( Molecule moleculeA, Molecule moleculeB ) {
        CollisionSpec collisionSpec = null;
        if( moleculeA instanceof RoundMolecule && moleculeB instanceof RoundMolecule ) {
            RoundMolecule rmA = (RoundMolecule)moleculeA;
            RoundMolecule rmB = (RoundMolecule)moleculeB;
            if( rmA.getPosition().distanceSq( rmB.getPosition() )
                <= ( rmA.getRadius() + rmB.getRadius() )
                   * ( rmA.getRadius() + rmB.getRadius() ) ) {

                double xDiff = rmA.getCM().getX() - rmB.getCM().getX();
                double yDiff = rmA.getCM().getY() - rmB.getCM().getY();
                double aFrac = rmA.getRadius() / ( rmA.getRadius() + rmB.getRadius() );
                Point2D.Double collisionPt = new Point2D.Double( rmA.getCM().getX() - xDiff * aFrac,
                                                                 rmA.getCM().getY() - yDiff * aFrac );
                loa.setComponents( xDiff, yDiff );
                collisionSpec = new CollisionSpec( loa, collisionPt );
            }
        }
        else if( moleculeA instanceof CompoundMolecule ) {
            CompoundMolecule cmA = (CompoundMolecule)moleculeA;
            for( int j = 0; j < cmA.getComponentMolecules().length; j++ ) {
                Molecule moleculeC = cmA.getComponentMolecules()[j];
                return getCollisionSpec( moleculeC, moleculeB );
            }
        }
        else if( moleculeB instanceof CompoundMolecule ) {
            CompoundMolecule cmB = (CompoundMolecule)moleculeB;
            for( int j = 0; j < cmB.getComponentMolecules().length; j++ ) {
                Molecule moleculeC = cmB.getComponentMolecules()[j];
                return getCollisionSpec( moleculeA, moleculeC );
            }
        }
        else {
            throw new RuntimeException( "error in function" );
        }

        return collisionSpec;
    }

    /**
     * @param bodyA
     * @param bodyB
     * @param loa
     */
    public void doCillision( Body bodyA, Body bodyB, Vector2D loa, Point2D.Double collisionPt ) {

        // Get the total energy of the two objects, so we can conserve it
        double totalEnergy0 = bodyA.getKineticEnergy() + bodyB.getKineticEnergy();

        // Get the vectors from the bodies' CMs to the point of contact
        Vector2D r1 = new Vector2D.Double( collisionPt.getX() - bodyA.getPosition().getX(),
                                           collisionPt.getY() - bodyA.getPosition().getY());
        Vector2D r2 = new Vector2D.Double(  collisionPt.getX() - bodyB.getPosition().getX(),
                                           collisionPt.getY() - bodyB.getPosition().getY() );

        // Get the unit vector along the line of action
        n.setComponents( loa.getX(), loa.getY() );
        n.normalize();

        // If the relative velocity show the points moving apart, then there is no collision.
        // This is a key check to solve otherwise sticky collision problems
        vRel.setComponents( bodyA.getVelocity().getX(), bodyA.getVelocity().getY() );
        vRel.subtract( bodyB.getVelocity() );
        if( vRel.dot( n ) <= 0 ) {

            // Compute the relative velocities of the contact points
            vAng1.setComponents( -bodyA.getOmega() * r1.getY(), bodyA.getOmega() * r1.getX() );
            vAng2.setComponents( -bodyB.getOmega() * r2.getY(), bodyB.getOmega() * r2.getX() );
            angRel.setComponents( vAng1.getX(), vAng1.getY() );
            angRel.subtract( vAng2 );
            double vr = vRel.dot( n ) + angRel.dot( n );

            // Assume the coefficient of restitution is 1
            double e = 1;

            // Compute the impulse, j
            double numerator = -vr * ( 1 + e );

            Vector3D n3D = new Vector3D( n );
            Vector3D r13D = new Vector3D( r1 );
            Vector3D t1 = r13D.crossProduct( n3D ).multiply( 1 / bodyA.getMomentOfInertia() );
            Vector3D t1A = t1.crossProduct( r13D );
            double t1B = n3D.dot( t1A );
            Vector3D r23D = new Vector3D( r2 );
            Vector3D t2 = r23D.crossProduct( n3D ).multiply( 1 / bodyB.getMomentOfInertia() );
            Vector3D t2A = t2.crossProduct( r23D );
            double t2B = n3D.dot( t2A );
            double denominator = ( 1 / bodyA.getMass() + 1 / bodyB.getMass() ) + t1B + t2B;
            double j = numerator / denominator;

            // Compute the new linear and angular velocities, based on the impulse
            bodyA.getVelocity().add( new Vector2D.Double( n ).scale( ( j / bodyA.getMass() ) ) );
            bodyB.getVelocity().add( new Vector2D.Double( n ).scale( ( -j / bodyB.getMass() ) ) );

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
//        bodyA.getVelocity().multiply( fvA );
//        double vMagB = bodyB.getVelocity().getMagnitude();
//        double fvB = ( vMagB - dvB ) / vMagB;
//        bodyB.getVelocity().multiply( fvB );
        }
    }

    //--------------------------------------------------------------------------------------------------
    //  Inner classes
    //--------------------------------------------------------------------------------------------------

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
