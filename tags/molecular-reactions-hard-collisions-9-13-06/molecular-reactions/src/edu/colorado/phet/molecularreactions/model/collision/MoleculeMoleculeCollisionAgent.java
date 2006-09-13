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
import edu.colorado.phet.molecularreactions.model.reactions.Reaction;
import edu.colorado.phet.molecularreactions.model.reactions.A_AB_BC_C_Reaction;

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

    private Reaction.ReactionCriteria reactionCriteria;
    private SpringCollision springCollision;

    /**
     * @param model
     */
    public MoleculeMoleculeCollisionAgent( final MRModel model, SpringCollision springCollision ) {
        reactionCriteria = model.getReaction().getReactionCriteria();
        this.springCollision = springCollision;
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
        MoleculeMoleculeCollisionSpec collisionSpec = null;

        // Do bounding box test to avoid more computation for most pairs of molecules
        // Todo: possible performance bottleneck here.
        boolean boundingBoxesOverlap = false;
        boundingBoxesOverlap = moleculeA.getBoundingBox().intersects( moleculeB.getBoundingBox() );
//        double dx = Math.abs( moleculeA.getPosition().getX() - moleculeB.getPosition().getX() );
//        double dy = Math.abs( moleculeA.getPosition().getY() - moleculeB.getPosition().getY() );
//        boundingBoxesOverlap = dx <= moleculeA.getBoundingBox().getWidth() + moleculeB.getBoundingBox().getWidth()
//                               && dy < moleculeA.getBoundingBox().getHeight() + moleculeB.getBoundingBox().getHeight();

        // Don't go farther if the bounding boxes overlap, or either of the molecules is part of a
        // composite
        double d = bodyA.getPosition().distance( bodyB.getPosition() );
        if(
//        if( boundingBoxesOverlap
            /*&&*/!moleculeA.isPartOfComposite()
            && !moleculeB.isPartOfComposite() ) {
            collisionSpec = getCollisionSpec( moleculeA, moleculeB );
            if( collisionSpec != null ) {
                springCollision.collide( moleculeA, moleculeB, collisionSpec );
            }

        }
        return ( collisionSpec != null );
    }


    /**
     * Determines the parameters of the collision between two molecules.
     *
     * @param moleculeA
     * @param moleculeB
     * @return A CollisionSpec, if a collision occurs, otherwise return null
     */
    private MoleculeMoleculeCollisionSpec getCollisionSpec( Molecule moleculeA, Molecule moleculeB ) {
        MoleculeMoleculeCollisionSpec collisionSpec = null;

        // If both the molecules are simple molecules, we can determine if they are colliding
        if( moleculeA instanceof SimpleMolecule && moleculeB instanceof SimpleMolecule ) {
            SimpleMolecule rmA = (SimpleMolecule)moleculeA;
            SimpleMolecule rmB = (SimpleMolecule)moleculeB;
            if( rmA.getPosition().distanceSq( rmB.getPosition() )
                <= springCollision.getSpring().getRestingLength() * springCollision.getSpring().getRestingLength() ) {

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
                Molecule moleculeC = cmA.getComponentMolecules()[j];
                cs = getCollisionSpec( moleculeC, moleculeB );
            }
            return cs;
        }
        else if( moleculeB instanceof CompositeMolecule ) {
            CompositeMolecule cmB = (CompositeMolecule)moleculeB;
            MoleculeMoleculeCollisionSpec cs = null;
            for( int j = 0; j < cmB.getComponentMolecules().length && cs == null; j++ ) {
                Molecule moleculeC = cmB.getComponentMolecules()[j];
                cs = getCollisionSpec( moleculeA, moleculeC );
            }
            return cs;
        }
        else {
            throw new RuntimeException( "error in function" );
        }

        return collisionSpec;
    }

    private Bond createBond( SimpleMolecule simpleMolecule, CompositeMolecule compositeMolecule ) {
        Bond bond = new Bond( simpleMolecule, compositeMolecule.getComponentMolecules()[0] );
        return bond;
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
}
