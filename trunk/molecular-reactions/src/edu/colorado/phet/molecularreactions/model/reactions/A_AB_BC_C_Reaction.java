/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model.reactions;

import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.model.collision.MoleculeMoleculeCollisionAgent;
import edu.colorado.phet.molecularreactions.model.collision.MoleculeMoleculeCollisionSpec;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.common.math.Vector2D;

/**
 * A_AB_BC_C_Reaction
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class A_AB_BC_C_Reaction extends Reaction {
    private static EnergyProfile energyProfile = new EnergyProfile( 0,
                                                                    MRConfig.DEFAULT_REACTION_THRESHOLD,
                                                                    0,
                                                                    60 );

    public A_AB_BC_C_Reaction() {
        super( energyProfile, new Criteria( energyProfile ) );
    }

    private static double getRelKE( Molecule m1, Molecule m2 ) {
        // Determine the kinetic energy in the collision. We consider this to be the
        // kinetic energy of an object whose mass is equal to the total masses of
        // the two molecules, moving at a speed equal to the magnitude of the
        // relative velocity of the two molecules
        Vector2D loa = new Vector2D.Double( m2.getPosition().getX() - m1.getPosition().getX(),
                                            m2.getPosition().getY() - m1.getPosition().getY() ).normalize();
        double sRel = Math.max( m1.getVelocity().dot( loa ) - m2.getVelocity().dot( loa ), 0 );
        double ke = 0.5 * ( m1.getMass() + m2.getMass() ) * sRel * sRel;
        return ke;
    }

    private static class Criteria implements Reaction.ReactionCriteria {

        private EnergyProfile energyProfile;

        public Criteria( EnergyProfile energyProfile ) {
            this.energyProfile = energyProfile;
        }

        public boolean criteriaMet( Molecule m1, Molecule m2, MoleculeMoleculeCollisionSpec collisionSpec ) {
            boolean result = false;

            // We need to have one simple molecule and one composite molecule
            boolean firstClassificationCriterionMet = false;
            CompositeMolecule cm = null;
            SimpleMolecule sm = null;
            if( m1 instanceof CompositeMolecule ) {
                cm = (CompositeMolecule)m1;
                if( m2 instanceof SimpleMolecule ) {
                    sm = (SimpleMolecule)m2;
                    firstClassificationCriterionMet = true;
                }
            }
            else {
                sm = (SimpleMolecule)m1;
                if( m2 instanceof CompositeMolecule ) {
                    cm = (CompositeMolecule)m2;
                    firstClassificationCriterionMet = true;
                }
            }

            // The simple molecule must be of a type not contained in the
            // composite molecule
            boolean secondClassificationCriterionMet = false;
            if( firstClassificationCriterionMet ) {
                if( cm.getType() == CompositeMolecule.AB
                    && sm instanceof MoleculeC ) {
                    secondClassificationCriterionMet = true;
                }
                else if( cm.getType() == CompositeMolecule.BC
                         && sm instanceof MoleculeA ) {
                    secondClassificationCriterionMet = true;
                }
            }

            // The simple molecule must have collided with the non-B simple
            // molecule in the composite molecule
            boolean thirdClassificationCriterionMet = false;
            if( secondClassificationCriterionMet
                && (!( collisionSpec.getMoleculeA() instanceof MoleculeA
                      && collisionSpec.getMoleculeB() instanceof MoleculeC ) )
                && (!( collisionSpec.getMoleculeA() instanceof MoleculeC
                      && collisionSpec.getMoleculeB() instanceof MoleculeA  ) )
                    ) {
                thirdClassificationCriterionMet = true;
            }

            // The relative kinetic energy of the collision must be above the
            // energy profile threshold
            if( thirdClassificationCriterionMet ) {
                result = getRelKE( m1, m2 ) > energyProfile.getPeakLevel();
            }
            return result;
        }
    }
}
