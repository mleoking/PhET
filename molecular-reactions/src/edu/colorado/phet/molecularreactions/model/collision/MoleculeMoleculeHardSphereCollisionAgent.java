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

import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.model.reactions.Reaction;
import edu.colorado.phet.molecularreactions.model.reactions.A_BC_AB_C_Reaction;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.mechanics.Body;
import edu.colorado.phet.mechanics.Vector3D;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * MoleculeMoleculeCollisionAgent
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculeMoleculeHardSphereCollisionAgent implements MRModel.ModelListener {

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
    public MoleculeMoleculeHardSphereCollisionAgent( final MRModel model ) {
        reactionCriteria = model.getReaction().getReactionCriteria();
        model.addListener( this );
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
//        double dx = Math.abs( moleculeA.getPosition().getX() - moleculeB.getPosition().getX() );
//        double dy = Math.abs( moleculeA.getPosition().getY() - moleculeB.getPosition().getY() );
//        boundingBoxesOverlap = dx <= moleculeA.getBoundingBox().getWidth() + moleculeB.getBoundingBox().getWidth()
//                               && dy < moleculeA.getBoundingBox().getHeight() + moleculeB.getBoundingBox().getHeight();

        // Don't go farther if the bounding boxes overlap, or either of the molecules is part of A
        // composite
        if( boundingBoxesOverlap
            && !moleculeA.isPartOfComposite()
            && !moleculeB.isPartOfComposite() ) {
            collisionSpec = getCollisionSpec( moleculeA, moleculeB );
            if( collisionSpec != null ) {
//                System.out.println( "MoleculeMoleculeHardSphereCollisionAgent.detectAndDoCollision" );
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
                <= ( rmA.getRadius() + rmB.getRadius() )
                   * ( rmA.getRadius() + rmB.getRadius() ) ) {

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

        // Get the total energy of the two objects, so we can conserve it
        double totalEnergy0 = bodyA.getKineticEnergy() + bodyB.getKineticEnergy();

        // Create a composite molecule if ReactionCriteria are met
//        if( model.getReaction().moleculesAreProperTypes( (AbstractMolecule)bodyA,
//                                                         (AbstractMolecule)bodyB )) {
        if( model.getReaction().areCriteriaMet( (AbstractMolecule)bodyA,
                                                (AbstractMolecule)bodyB,
                                                collisionSpec ) ) {

            // Get rid of the provisional bond between the molecules
//            ProvisionalBond.removeInstance( (AbstractMolecule)bodyA,
//                                                         (AbstractMolecule)bodyB );

            SimpleMolecule simpleMolecule = null;
            CompositeMolecule compositeMolecule = null;

            if( bodyA instanceof SimpleMolecule && bodyB instanceof CompositeMolecule ) {
                simpleMolecule = (SimpleMolecule)bodyA;
                compositeMolecule = (CompositeMolecule)bodyB;
            }
            else if( bodyB instanceof SimpleMolecule && bodyA instanceof CompositeMolecule ) {
                simpleMolecule = (SimpleMolecule)bodyB;
                compositeMolecule = (CompositeMolecule)bodyA;
            }
            else {
                throw new RuntimeException( "unexpected situation" );
            }

            A_BC_AB_C_Reaction reaction = (A_BC_AB_C_Reaction)model.getReaction();
            reaction.doReaction( compositeMolecule, simpleMolecule );
        }

        // Otherwise, do a perfectly elastic collision
        else {
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
    }

//    private Bond createBond( SimpleMolecule simpleMolecule, CompositeMolecule compositeMolecule ) {
//        Bond bond = new Bond( simpleMolecule, compositeMolecule.getComponentMolecules()[0] );
//        return bond;
//    }

    private SimpleMolecule[] createArrayOfAllSimpleMolecules( Body bodyA, Body bodyB ) {
        List molecules = new ArrayList();
        if( bodyA instanceof CompositeMolecule ) {
            CompositeMolecule compositeMolecule = (CompositeMolecule)bodyA;
            molecules.addAll( Arrays.asList( compositeMolecule.getComponentMolecules() ) );
        }
        else {
            molecules.add( bodyA );
        }
        if( bodyB instanceof CompositeMolecule ) {
            CompositeMolecule compositeMolecule = (CompositeMolecule)bodyB;
            molecules.addAll( Arrays.asList( compositeMolecule.getComponentMolecules() ) );
        }
        else {
            molecules.add( bodyB );
        }
        SimpleMolecule[] ma = (SimpleMolecule[])molecules.toArray( new SimpleMolecule[ molecules.size()] );
        return ma;
    }

    //--------------------------------------------------------------------------------------------------
    //  Inner classes
    //--------------------------------------------------------------------------------------------------

//    public static class CollisionSpec {
//
//        private Vector2D loa;
//        private Point2D.Double collisionPt;
//        private SimpleMolecule moleculeA;
//        private SimpleMolecule moleculeB;
//
//        public CollisionSpec( Vector2D loa,
//                              Point2D.Double collisionPt,
//                              SimpleMolecule moleculeA,
//                              SimpleMolecule moleculeB ) {
//            this.moleculeB = moleculeB;
//            this.moleculeA = moleculeA;
//            this.loa = loa;
//            this.collisionPt = collisionPt;
//        }
//
//        public Vector2D getLoa() {
//            return loa;
//        }
//
//        public Point2D.Double getCollisionPt() {
//            return collisionPt;
//        }
//
//        public SimpleMolecule getSimpleMoleculeA() {
//            return moleculeA;
//        }
//
//        public SimpleMolecule getSimpleMoleculeB() {
//            return moleculeB;
//        }
//    }
//
    //--------------------------------------------------------------------------------------------------
    // Implementation of MRModel.ModelListener
    //--------------------------------------------------------------------------------------------------

    public void reactionThresholdChanged( MRModel model ) {
//        this.reactionThreshold = model.getEnergyProfile().getPeakLevel();
    }

    public void modelElementAdded( ModelElement element ) {
        // noop
    }

    public void modelElementRemoved( ModelElement element ) {
        // noop
    }

//    //--------------------------------------------------------------------------------------------------
//    // Reaction criteria
//    //--------------------------------------------------------------------------------------------------
//
//    interface ReactionCriteria {
//        boolean criteriaMet( Molecule bodyA, Molecule bodyB );
//    }
//
//    /**
//     * Combines two simple molecules of different types into one compound molecule
//     */
//    class SimpleMoleculeSimpleMoleculeReactionCriteria implements ReactionCriteria {
//        public boolean criteriaMet( Molecule m1, Molecule m2 ) {
//            return m1.getKineticEnergy() + m2.getKineticEnergy() > reactionThreshold
//                   && m1 instanceof SimpleMolecule && m2 instanceof SimpleMolecule
//                   && m1.getClass() != m2.getClass();
//        }
//    }
//
//    /**
//     * Combines any two molecules together
//     */
//    class SimpleMoleculeReactionCriteria implements ReactionCriteria {
//        public boolean criteriaMet( Molecule m1, Molecule m2 ) {
//            return m1.getKineticEnergy() + m2.getKineticEnergy() > reactionThreshold;
//        }
//    }
//
//    /**
//     * If A simple molecule hits A compound molecule, combines
//     */
//    class SimpleMoleculeCompoundMoleculeCriteria implements ReactionCriteria {
//        public boolean criteriaMet( Molecule m1, Molecule m2 ) {
//
//            // Determine the kinetic energy in the collision. We consider this to be the
//            // kinetic energy of an object whose mass is equal to the total masses of
//            // the two molecules, moving at A speed equal to the magnitude of the
//            // relative velocity of the two molecules
//            Vector2D loa = new Vector2D.Double( m2.getPosition().getX() - m1.getPosition().getX(),
//                                                m2.getPosition().getY() - m1.getPosition().getY()).normalize();
//            double sRel = Math.max( m1.getVelocity().dot( loa ) - m2.getVelocity().dot( loa), 0 );
//            double ke = 0.5 * (m1.getMass() + m2.getMass() ) * sRel * sRel;
//
//            // Classify the two molecules. Note that there must be one and only one
//            // simple molecule, and one and only one composite molecule
//            CompositeMolecule cm = null;
//            SimpleMolecule sm = null;
//            if( m1 instanceof CompositeMolecule ) {
//                cm = (CompositeMolecule)m1;
//            }
//            else {
//                sm = (SimpleMolecule)m1;
//            }
//
//            if( m2 instanceof CompositeMolecule ) {
//                if( cm != null ) {
//                    return false;
//                }
//                else {
//                    cm = (CompositeMolecule)m2;
//                }
//            }
//            else {
//                if( sm != null ) {
//                    return false;
//                }
//                else {
//                    sm = (SimpleMolecule)m2;
//                }
//            }
//
//            // One of the molecules must be A composite molecule whose components
//            // are two simple molecules, and the other molecule must be A simple molecule
//            if( cm.numSimpleMolecules() == 2
//                && cm.getType() == CompositeMolecule.BB
//                && sm instanceof MoleculeA
//                && ke >= reactionThreshold ) {
//                return true;
//            }
//            if( cm.numSimpleMolecules() == 2
//                && cm.getType() == CompositeMolecule.AB
//                && sm instanceof MoleculeB
//                && ke >= reactionThreshold ) {
//                return true;
//            }
//            return false;
//        }
//    }
}
