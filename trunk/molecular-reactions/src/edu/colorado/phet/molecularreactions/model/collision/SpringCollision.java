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
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.model.reactions.A_AB_BC_C_Reaction;
import edu.colorado.phet.molecularreactions.model.reactions.Reaction;

import java.awt.geom.Point2D;

/**
 * SpringCollision
 * <p/>
 * Models the collision of two molecules as compressing a spring, unless the molecules get close
 * enough together and are of the correct types for a reaction to occur.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SpringCollision implements Collision {
    private MRModel model;
    private Spring spring;

    public SpringCollision( MRModel model, Spring spring ) {
        this.model = model;
        this.spring = spring;
    }

    public void collide( Molecule mA, Molecule mB, MoleculeMoleculeCollisionSpec collisionSpec ) {

        // If the edges of the body are closer than the max length of
        // the spring, the magnitude of the force will > 0
        SimpleMolecule smA = collisionSpec.getSimpleMoleculeA();
        SimpleMolecule smB = collisionSpec.getSimpleMoleculeB();
        double separation = smA.getPosition().distance( smB.getPosition() )
                            - smA.getRadius() - smB.getRadius();

        // If the edges are touching or overlapping, then make a composite molecule
        Reaction.ReactionCriteria reactionCriteria = model.getReaction().getReactionCriteria();
        if( separation <= 0 && reactionCriteria.criteriaMet( mA, mB, collisionSpec ) ) {
            doReaction( mA, mB, collisionSpec );
        }

        // else, if the separation is less than the resting length, do the spring thing
        else if( separation <= spring.getRestingLength() ) {

            double fMag = Math.abs( spring.getRestingLength() - separation ) * spring.getK();

            // The direction of the force will be along the line of action
            Vector2D f = new Vector2D.Double( collisionSpec.getLoa() ).normalize().scale( fMag );

            // Accelerate each of the bodies with the force
            mA.applyForce( f, collisionSpec );
            mB.applyForce( f.scale( -1 ), collisionSpec );
        }
    }

    private void doReaction( Molecule moleculeA, Molecule moleculeB, MoleculeMoleculeCollisionSpec collisionSpec ) {
        SimpleMolecule simpleMolecule = null;
        CompositeMolecule compositeMolecule = null;

        if( moleculeA instanceof SimpleMolecule && moleculeB instanceof CompositeMolecule ) {
            simpleMolecule = (SimpleMolecule)moleculeA;
            compositeMolecule = (CompositeMolecule)moleculeB;
        }
        else if( moleculeB instanceof SimpleMolecule && moleculeA instanceof CompositeMolecule ) {
            simpleMolecule = (SimpleMolecule)moleculeB;
            compositeMolecule = (CompositeMolecule)moleculeA;
        }
        else {
            throw new RuntimeException( "unexpected situation" );
        }

        A_AB_BC_C_Reaction reaction = (A_AB_BC_C_Reaction)model.getReaction();
        reaction.doReaction( compositeMolecule, simpleMolecule );
    }

    public double getInteractionDistance() {
        return spring.getRestingLength();
    }


    /**
     *
     */
    public static class Spring {
        double k;
        double restingLength;

        public Spring( double k, double restingLength ) {
            this.k = k;
            this.restingLength = restingLength;
        }

        public double getRestingLength() {
            return restingLength;
        }

        public double getK() {
            return k;
        }
    }
}
