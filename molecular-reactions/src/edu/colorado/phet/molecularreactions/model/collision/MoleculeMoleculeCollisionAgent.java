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
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.model.reactions.Reaction;

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

    private Reaction.ReactionCriteria reactionCriteria;
    private SpringCollision reactantCollision;
    private Collision nonReactantCollision;

    /**
     * @param model
     */
    public MoleculeMoleculeCollisionAgent( final MRModel model,
                                           SpringCollision reactantCollision,
                                           Collision nonReactantCollision ) {
        reactionCriteria = model.getReaction().getReactionCriteria();
        this.reactantCollision = reactantCollision;
        this.nonReactantCollision = nonReactantCollision;
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

        AbstractMolecule moleculeA = (AbstractMolecule)bodyA;
        AbstractMolecule moleculeB = (AbstractMolecule)bodyB;
        MoleculeMoleculeCollisionSpec collisionSpec = null;

        // Do bounding box test to avoid more computation for most pairs of molecules
        // Todo: possible performance bottleneck here.
//        double dx = Math.abs( moleculeA.getPosition().getX() - moleculeB.getPosition().getX() );
//        double dy = Math.abs( moleculeA.getPosition().getY() - moleculeB.getPosition().getY() );
//        boundingBoxesOverlap = dx <= moleculeA.getBoundingBox().getWidth() + moleculeB.getBoundingBox().getWidth()
//                               && dy < moleculeA.getBoundingBox().getHeight() + moleculeB.getBoundingBox().getHeight();

        // Don't go farther if the bounding boxes overlap, or either of the molecules is part of a
        // composite
        if( !moleculeA.isPartOfComposite()
            && !moleculeB.isPartOfComposite() ) {
//            if( model.getReaction().moleculesAreProperTypes( moleculeA, moleculeB ) ) {
                collisionSpec = getCollisionSpec( moleculeA, moleculeB, reactantCollision.getInteractionDistance() );
                if( collisionSpec != null ) {
                    reactantCollision.collide( moleculeA, moleculeB, collisionSpec );
                }
//            }
//            else {
//                collisionSpec = getCollisionSpec( moleculeA, moleculeB, nonReactantCollision.getInteractionDistance() );
//                if( collisionSpec != null ) {
//                    nonReactantCollision.collide( moleculeA, moleculeB, collisionSpec );
//                }
//            }
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
    private MoleculeMoleculeCollisionSpec getCollisionSpec
            ( AbstractMolecule
                    moleculeA, AbstractMolecule
                    moleculeB, double interactionDistance ) {
        MoleculeMoleculeCollisionSpec collisionSpec = null;

        // If both the molecules are simple molecules, we can determine if they are colliding
        if( moleculeA instanceof SimpleMolecule && moleculeB instanceof SimpleMolecule ) {
            SimpleMolecule rmA = (SimpleMolecule)moleculeA;
            SimpleMolecule rmB = (SimpleMolecule)moleculeB;
            if( rmA.getPosition().distance( rmB.getPosition() ) - rmA.getRadius() - rmB.getRadius()
                <= interactionDistance ) {

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
                cs = getCollisionSpec( moleculeC, moleculeB, interactionDistance );
            }
            return cs;
        }
        else if( moleculeB instanceof CompositeMolecule ) {
            CompositeMolecule cmB = (CompositeMolecule)moleculeB;
            MoleculeMoleculeCollisionSpec cs = null;
            for( int j = 0; j < cmB.getComponentMolecules().length && cs == null; j++ ) {
                AbstractMolecule moleculeC = cmB.getComponentMolecules()[j];
                cs = getCollisionSpec( moleculeA, moleculeC, interactionDistance );
            }
            return cs;
        }
        else {
            throw new RuntimeException( "error in function" );
        }

        return collisionSpec;
    }
}
