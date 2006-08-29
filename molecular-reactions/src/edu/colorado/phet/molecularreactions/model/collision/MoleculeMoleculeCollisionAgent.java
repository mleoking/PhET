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
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.mechanics.Body;
import edu.colorado.phet.mechanics.Vector3D;
import edu.colorado.phet.molecularreactions.model.*;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
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
public class MoleculeMoleculeCollisionAgent implements MRModel.ModelListener {

    private Vector2D n = new Vector2D.Double();
    private Vector2D vRel = new Vector2D.Double();
    private Vector2D vAng1 = new Vector2D.Double();
    private Vector2D vAng2 = new Vector2D.Double();
    private Vector2D angRel = new Vector2D.Double();
    private Vector2D loa = new Vector2D.Double();

    private double reactionThreshold;
    private ReactionCriteria reactionCriteria;

    /**
     * @param model
     */
    public MoleculeMoleculeCollisionAgent( final MRModel model ) {

        reactionCriteria = new SimpleMoleculeCompoundMoleculeCriteria();
//        reactionCriteria = new SimpleMoleculeReactionCriteria();

        model.getEnergyProfile().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                reactionThreshold = model.getEnergyProfile().getPeakLevel();
            }
        } );
        this.reactionThreshold = model.getEnergyProfile().getPeakLevel();
        model.addListener( this );
    }

    /**
     * Note: This method is not thread-safe, because we use an instance attribute
     * to avoid allocating a new Vector2D on every invocation.
     *
     * @param bodyA
     * @param bodyB
     * @return true if a collision occured
     */
    public boolean detectAndDoCollision( MRModel model, Body bodyA, Body bodyB ) {

        Molecule moleculeA = (Molecule)bodyA;
        Molecule moleculeB = (Molecule)bodyB;
        CollisionSpec collisionSpec = null;

        // Do bounding box test to avoid more computation for most pairs of molecules
        boolean boundingBoxesOverlap = false;
        double dx = Math.abs( moleculeA.getPosition().getX() - moleculeB.getPosition().getX() );
        double dy = Math.abs( moleculeA.getPosition().getY() - moleculeB.getPosition().getY() );
        boundingBoxesOverlap = dx <= moleculeA.getBoundingBox().getWidth() + moleculeB.getBoundingBox().getWidth()
                               && dy < moleculeA.getBoundingBox().getHeight() + moleculeB.getBoundingBox().getHeight();

        // Don't go farther if the bounding boxes overlap, or either of the molecules is part of a
        // composite
        if( boundingBoxesOverlap && !moleculeA.isPartOfComposite() && !moleculeB.isPartOfComposite() ) {
            collisionSpec = getCollisionSpec( moleculeA, moleculeB );
            if( collisionSpec != null ) {
                doCollision( model, moleculeA, moleculeB, collisionSpec );
            }

        }
        return ( collisionSpec != null );
    }


    private CollisionSpec getCollisionSpec( Molecule moleculeA, Molecule moleculeB ) {
        CollisionSpec collisionSpec = null;
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
                collisionSpec = new CollisionSpec( loa, collisionPt,
                                                   (SimpleMolecule)moleculeA,
                                                   (SimpleMolecule)moleculeB );
            }
        }
        else if( moleculeA instanceof CompositeMolecule ) {
            CompositeMolecule cmA = (CompositeMolecule)moleculeA;
            for( int j = 0; j < cmA.getComponentMolecules().length; j++ ) {
                Molecule moleculeC = cmA.getComponentMolecules()[j];
                return getCollisionSpec( moleculeC, moleculeB );
            }
        }
        else if( moleculeB instanceof CompositeMolecule ) {
            CompositeMolecule cmB = (CompositeMolecule)moleculeB;
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
     * @param model
     * @param bodyA
     * @param bodyB
     * @param collisionSpec
     */
    public void doCollision( MRModel model, Body bodyA, Body bodyB, CollisionSpec collisionSpec ) {
        Vector2D loa = collisionSpec.getLoa();
        Point2D.Double collisionPt = collisionSpec.getCollisionPt();

        // Get the total energy of the two objects, so we can conserve it
        double totalEnergy0 = bodyA.getKineticEnergy() + bodyB.getKineticEnergy();

        // Create a composite molecule if ReactionCriteria are met
        if( reactionCriteria.criteriaMet( (Molecule)bodyA, (Molecule)bodyB ) ) {

            if( bodyA instanceof SimpleMolecule && bodyB instanceof SimpleMolecule ) {
                CompositeMolecule compositeMolecule = new CompositeMolecule( (SimpleMolecule)bodyA, (SimpleMolecule)bodyB );
//                model.removeModelElement( bodyA );
//                model.removeModelElement( bodyB );
                model.addModelElement( compositeMolecule );
            }
            else if( bodyA instanceof SimpleMolecule && bodyB instanceof CompositeMolecule ) {
                Bond bond = new Bond( collisionSpec.getMoleculeA(), collisionSpec.getMoleculeB() );
                ( (CompositeMolecule)bodyB ).addSimpleMolecule( (SimpleMolecule)bodyA, bond );
                System.out.println( "MoleculeMoleculeCollisionAgent.doCollision" );
            }
            else if( bodyB instanceof SimpleMolecule && bodyA instanceof CompositeMolecule ) {
                Bond bond = createBond( (SimpleMolecule)bodyB, (CompositeMolecule)bodyA );
                ( (CompositeMolecule)bodyA ).addSimpleMolecule( (SimpleMolecule)bodyB, bond );
                System.out.println( "MoleculeMoleculeCollisionAgent.doCollision" );
            }
            else {
                throw new RuntimeException( "unexpected situation" );
            }
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
    }

    private Bond createBond( SimpleMolecule simpleMolecule, CompositeMolecule compositeMolecule ) {
        Bond bond = new Bond( simpleMolecule, compositeMolecule.getComponentMolecules()[0] );
        return bond;
    }

    private Bond[] createArrayOfBonds( Body bodyA, Body bodyB ) {
        if( bodyA instanceof SimpleMolecule && bodyB instanceof SimpleMolecule ) {
            return new Bond[]{new Bond( (SimpleMolecule)bodyA, (SimpleMolecule)bodyB )};
        }
        else if( bodyA instanceof SimpleMolecule ) {
            double shortestDistSq = Double.POSITIVE_INFINITY;
            SimpleMolecule[] am = ( (CompositeMolecule)bodyB ).getComponentMolecules();
            SimpleMolecule closestMolecule = null;
            for( int i = 0; i < am.length; i++ ) {
                SimpleMolecule m = am[i];
                double d = bodyA.getPosition().distanceSq( m.getPosition() );
                if( d < shortestDistSq ) {
                    closestMolecule = m;
                }
                shortestDistSq = d;
            }
            Bond newBond = new Bond( (SimpleMolecule)bodyA, closestMolecule );
        }
        return null;
    }

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

    private static class CollisionSpec {

        private Vector2D loa;
        private Point2D.Double collisionPt;
        private SimpleMolecule moleculeA;
        private SimpleMolecule moleculeB;

        public CollisionSpec( Vector2D loa,
                              Point2D.Double collisionPt,
                              SimpleMolecule moleculeA,
                              SimpleMolecule moleculeB ) {
            this.moleculeB = moleculeB;
            this.moleculeA = moleculeA;
            this.loa = loa;
            this.collisionPt = collisionPt;
        }

        public Vector2D getLoa() {
            return loa;
        }

        public Point2D.Double getCollisionPt() {
            return collisionPt;
        }

        public SimpleMolecule getMoleculeA() {
            return moleculeA;
        }

        public SimpleMolecule getMoleculeB() {
            return moleculeB;
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of MRModel.ModelListener
    //--------------------------------------------------------------------------------------------------

    public void reactionThresholdChanged( MRModel model ) {
        this.reactionThreshold = model.getEnergyProfile().getPeakLevel();
    }

    public void modelElementAdded( ModelElement element ) {
        // noop
    }

    public void modelElementRemoved( ModelElement element ) {
        // noop
    }

    //--------------------------------------------------------------------------------------------------
    // Reaction criteria
    //--------------------------------------------------------------------------------------------------

    interface ReactionCriteria {
        boolean criteriaMet( Molecule bodyA, Molecule bodyB );
    }

    /**
     * Combines two simple molecules of different types into one compound molecule
     */
    class SimpleMoleculeSimpleMoleculeReactionCriteria implements ReactionCriteria {
        public boolean criteriaMet( Molecule m1, Molecule m2 ) {
            return m1.getKineticEnergy() + m2.getKineticEnergy() > reactionThreshold
                   && m1 instanceof SimpleMolecule && m2 instanceof SimpleMolecule
                   && m1.getClass() != m2.getClass();
        }
    }

    /**
     * Combines any two molecules together
     */
    class SimpleMoleculeReactionCriteria implements ReactionCriteria {
        public boolean criteriaMet( Molecule m1, Molecule m2 ) {
            return m1.getKineticEnergy() + m2.getKineticEnergy() > reactionThreshold;
        }
    }

    /**
     * If a simple molecule hits a compound molecule, combines
     */
    class SimpleMoleculeCompoundMoleculeCriteria implements ReactionCriteria {
        public boolean criteriaMet( Molecule m1, Molecule m2 ) {
            double totalEnergy = m1.getKineticEnergy() + m2.getKineticEnergy();

            // One of the molecules must be a composite molecule whose components
            // are two simple molecules, and the other molecule must be a simple molecule
            if( m1 instanceof CompositeMolecule
                && ( (CompositeMolecule)m1 ).numSimpleMolecules() == 2
                && ( (CompositeMolecule)m1 ).numSimpleMolecules() == 2
                && m2 instanceof SimpleMolecule
                && totalEnergy >= reactionThreshold ) {
                return true;
            }
            if( m2 instanceof CompositeMolecule
                && ( (CompositeMolecule)m2 ).numSimpleMolecules() == 2
                && m1 instanceof SimpleMolecule
                && totalEnergy >= reactionThreshold ) {
                return true;
            }
            return false;
        }
    }
}
