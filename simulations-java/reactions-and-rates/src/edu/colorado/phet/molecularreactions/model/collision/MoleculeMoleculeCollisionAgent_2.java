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
import edu.colorado.phet.mechanics.Body;
import edu.colorado.phet.mechanics.Vector3D;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.model.reactions.A_BC_AB_C_Reaction;
import edu.colorado.phet.molecularreactions.model.reactions.Reaction;

import java.awt.geom.Point2D;

/**
 * MoleculeMoleculeCollisionAgent
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculeMoleculeCollisionAgent_2 implements MRCollisionAgent {

    private Vector2D n = new Vector2D.Double();
    private Vector2D vRel = new Vector2D.Double();
    private Vector2D vAng1 = new Vector2D.Double();
    private Vector2D vAng2 = new Vector2D.Double();
    private Vector2D angRel = new Vector2D.Double();
    private Vector2D loa = new Vector2D.Double();

    private Reaction.ReactionCriteria reactionCriteria;

    /**
     * @param model
     */
    public MoleculeMoleculeCollisionAgent_2( final MRModel model ) {
        reactionCriteria = model.getReaction().getReactionCriteria();
    }

    /**
     * Note: This method is not thread-safe, because we use an instance attribute
     * to avoid allocating A new Vector2D on every invocation.
     *
     * @param bodyA
     * @param bodyB
     * @return true if A collision occured
     */
    public boolean detectAndDoCollision( MRModel model, Body bodyA, Body bodyB ) {

        AbstractMolecule moleculeA = (AbstractMolecule)bodyA;
        AbstractMolecule moleculeB = (AbstractMolecule)bodyB;
        MoleculeMoleculeCollisionSpec collisionSpec = null;

        // Do bounding box test to avoid more computation for most pairs of molecules
        // Todo: possible performance bottleneck here.
        boolean boundingBoxesOverlap = false;
        boundingBoxesOverlap = moleculeA.getBoundingBox().intersects( moleculeB.getBoundingBox() );

        // Don't go farther if the bounding boxes overlap, or either of the molecules is part of A
        // composite
        if( boundingBoxesOverlap
            && !moleculeA.isPartOfComposite()
            && !moleculeB.isPartOfComposite() ) {
            collisionSpec = getCollisionSpec( moleculeA, moleculeB );
            if( collisionSpec != null ) {
                doCollision( model, moleculeA, moleculeB, collisionSpec );
            }
        }
        return ( collisionSpec != null );
    }


    /**
     * Determines the parameters of the collision between two molecules.
     *
     * @param moleculeA
     * @param moleculeB
     * @return A CollisionSpec, if A collision occurs, otherwise return null
     */
    private MoleculeMoleculeCollisionSpec getCollisionSpec( AbstractMolecule moleculeA, AbstractMolecule moleculeB ) {
        MoleculeMoleculeCollisionSpec collisionSpec = null;

        // If both the molecules are simple molecules, we can determine if they are colliding
        if( moleculeA instanceof SimpleMolecule && moleculeB instanceof SimpleMolecule ) {
            SimpleMolecule rmA = (SimpleMolecule)moleculeA;
            SimpleMolecule rmB = (SimpleMolecule)moleculeB;
            if( rmA.getPosition().distanceSq( rmB.getPosition() )
                <= ( rmA.getRadius() + rmB.getRadius() ) * ( rmA.getRadius() + rmB.getRadius() ) ) {

                double xDiff = rmA.getCM().getX() - rmB.getCM().getX();
                double yDiff = rmA.getCM().getY() - rmB.getCM().getY();
                double aFrac = rmA.getRadius() / ( rmA.getRadius() + rmB.getRadius() );
                Point2D.Double collisionPt = new Point2D.Double( rmA.getCM().getX() - xDiff * aFrac,
                                                                 rmA.getCM().getY() - yDiff * aFrac );
                loa.setComponents( xDiff, yDiff );
                collisionSpec = new MoleculeMoleculeCollisionSpec( loa, collisionPt,
                                                                   (SimpleMolecule)moleculeA,
                                                                   (SimpleMolecule)moleculeB );
            }
        }

        // If one of the molecules is a componsite molecule, recursively descend through it
        // to get down to the simple molecules before we can determine if there is a collision
        else if( moleculeA instanceof CompositeMolecule ) {
            CompositeMolecule cmA = (CompositeMolecule)moleculeA;
            MoleculeMoleculeCollisionSpec cs = null;
            for( int j = 0; j < cmA.getComponentMolecules().length && cs == null; j++ ) {
                AbstractMolecule moleculeC = cmA.getComponentMolecules()[j];
                cs = getCollisionSpec( moleculeC, moleculeB );
            }
            return cs;
        }
        else if( moleculeB instanceof CompositeMolecule ) {
            CompositeMolecule cmB = (CompositeMolecule)moleculeB;
            MoleculeMoleculeCollisionSpec cs = null;
            for( int j = 0; j < cmB.getComponentMolecules().length && cs == null; j++ ) {
                AbstractMolecule moleculeC = cmB.getComponentMolecules()[j];
                cs = getCollisionSpec( moleculeA, moleculeC );
            }
            return cs;
        }
        else {
            throw new RuntimeException( "error in function" );
        }
        return collisionSpec;
    }

    /**
     * Produces the results of a collision between molecules
     * 
     * @param model
     * @param bodyA
     * @param bodyB
     * @param collisionSpec
     */
    public void doCollision( MRModel model, Body bodyA, Body bodyB, MoleculeMoleculeCollisionSpec collisionSpec ) {
        Vector2D loa = collisionSpec.getLoa();
        Point2D.Double collisionPt = collisionSpec.getCollisionPt();

        // If the loa vector has zero length, then the two bodies are right on top of each other,
        // and the collision can't be computed
        if( loa.getMagnitude() == 0 ) {
            return;
        }

        // If the molecules aren't of a type that could react, simply do a hard sphere collision
        if( !model.getReaction().moleculesAreProperTypes( (AbstractMolecule)bodyA,
                                                          (AbstractMolecule)bodyB )) {
            doHardSphereCollision( collisionPt, bodyA, bodyB, loa );
        }
        // Otherwise, create a composite molecule if ReactionCriteria are met. This is a pretty
        // complicated deal here. We first determine if the colliding mollecules have made it up
        // to the top of the energy curve, by seeing if they overlap sufficiently.
        // If that has happened, then a reaction occurs. If it hasn't, we determine if there is
        // some collision energy left, in which case we don't do anything, and let the molecules
        // move closer together in the next time step. BUT, in a final test, we see if the collision
        // is occuring between a free A or C molecule and the A or C component of a composite
        // molecule. If that's the case, then the molecules should just undergo a hard sphere
        // collision. I've tried rearranging this code so it's not so obtuse, but haven't been
        // successful.
        else {
            SimpleMolecule mA = collisionSpec.getFreeMolecule();
            SimpleMolecule mB = collisionSpec.getSimpleMoleculeB();
            EnergyProfile profile = model.getEnergyProfile();
            double thresholdWidth = Math.max( mA.getRadius(), mB.getRadius() );
            double floorLevel = collisionSpec.getCompositeMolecule() instanceof MoleculeAB ?
                                profile.getRightLevel() : profile.getLeftLevel();
            double hillHeight = profile.getPeakLevel() - floorLevel;
            double slope = Math.atan2( hillHeight, thresholdWidth );

            boolean outOfEnergy = false;
            boolean reactionReached = false;
            double collisionDistance = model.getReaction().getDistanceToCollision( collisionSpec.getFreeMolecule(),
                                                                                   collisionSpec.getCompositeMolecule() );

            // If the molecules are overlapping enough, then there is a reaction
            double dCE = MRModelUtil.getCollisionEnergy( collisionSpec.getFreeMolecule(),
                                                         collisionSpec.getCompositeMolecule() );
            reactionReached = -collisionDistance >= thresholdWidth && dCE > hillHeight;
            if( reactionReached ) {
                A_BC_AB_C_Reaction reaction = (A_BC_AB_C_Reaction)model.getReaction();
                reaction.doReaction( collisionSpec.getCompositeMolecule(), collisionSpec.getFreeMolecule() );
                // todo: this is a bad place to exit the method!
                return;
            }

            // Otherwise we need to know whether they have gotten as far up the slope of the
            // energy profile as they can get, and need to start moving appart
            else {
                // Otherwise, see if they have enough energy to keep getting closer
                double dE = Math.tan( slope ) * Math.abs( collisionDistance );
                outOfEnergy = dE > dCE;

                // If are out of energy, do a hard sphere collision
                if( outOfEnergy ) {
                    doHardSphereCollision( collisionPt, bodyA, bodyB, loa );
                }

                // If we have a free molecule hitting the non-B component of a composite
                // molecule, do a hard sphere collision
                if( ( collisionSpec.getSimpleMoleculeA() instanceof MoleculeA
                      && collisionSpec.getSimpleMoleculeB() instanceof MoleculeC
                      || collisionSpec.getSimpleMoleculeA() instanceof MoleculeC
                         && collisionSpec.getSimpleMoleculeB() instanceof MoleculeA ) ) {
                    doHardSphereCollision( collisionPt, bodyA, bodyB, loa );

                }
            }
        }
    }

    private void doHardSphereCollision( Point2D.Double collisionPt, Body bodyA, Body bodyB, Vector2D loa ) {
        // Get the vectors from the bodies' CMs to the point of contact
        Vector2D r1 = new Vector2D.Double( collisionPt.getX() - bodyA.getPosition().getX(),
                                           collisionPt.getY() - bodyA.getPosition().getY() );
        Vector2D r2 = new Vector2D.Double( collisionPt.getX() - bodyB.getPosition().getX(),
                                           collisionPt.getY() - bodyB.getPosition().getY() );

        // Get the unit vector along the line of action
        n.setComponents( loa.getX(), loa.getY() );
        if( n.getMagnitude() == 0 ) {
            System.out.println( "MoleculeMoleculeCollisionAgent.doCollision" );
        }
        n.normalize();

        // If the relative velocity show the points moving apart, then there is no collision.
        // This is A key check to solve otherwise sticky collision problems
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
        }
    }
}
