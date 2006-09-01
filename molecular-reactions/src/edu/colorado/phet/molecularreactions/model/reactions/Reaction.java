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

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.model.collision.MoleculeMoleculeCollisionAgent;
import edu.colorado.phet.molecularreactions.model.collision.MoleculeMoleculeCollisionSpec;
import edu.colorado.phet.molecularreactions.MRConfig;

/**
 * Reaction
 * <p>
 * This class encapsulates all the criteria for whether a reaction will
 * occur or not.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Reaction {
    private EnergyProfile energyProfile;
    private ReactionCriteria reactionCriteria;

    public Reaction( ) {
        energyProfile = new EnergyProfile( 0, MRConfig.DEFAULT_REACTION_THRESHOLD, 0, 60 );
        reactionCriteria = new SimpleMoleculeCompoundMoleculeCriteria( energyProfile );
    }

    protected Reaction( EnergyProfile energyProfile, ReactionCriteria reactionCriteria ) {
        this.energyProfile = energyProfile;
        this.reactionCriteria = reactionCriteria;
    }

    public EnergyProfile getEnergyProfile() {
        return energyProfile;
    }

    public ReactionCriteria getReactionCriteria() {
        return reactionCriteria;
    }

    //--------------------------------------------------------------------------------------------------
    // Reaction criteria
    //--------------------------------------------------------------------------------------------------

    public interface ReactionCriteria {
        boolean criteriaMet( Molecule bodyA, Molecule bodyB, MoleculeMoleculeCollisionSpec collisionSpec );
    }

    /**
     * Combines two simple molecules of different types into one compound molecule
     */
    class SimpleMoleculeSimpleMoleculeReactionCriteria implements ReactionCriteria {
        public boolean criteriaMet( Molecule m1, Molecule m2, MoleculeMoleculeCollisionSpec collisionSpec ) {
            return m1.getKineticEnergy() + m2.getKineticEnergy() > energyProfile.getPeakLevel()
                   && m1 instanceof SimpleMolecule && m2 instanceof SimpleMolecule
                   && m1.getClass() != m2.getClass();
        }
    }

    /**
     * Combines any two molecules together
     */
    class SimpleMoleculeReactionCriteria implements ReactionCriteria {
        public boolean criteriaMet( Molecule m1, Molecule m2, MoleculeMoleculeCollisionSpec collisionSpec ) {
            return m1.getKineticEnergy() + m2.getKineticEnergy() > energyProfile.getPeakLevel();
        }
    }

    /**
     * If a simple molecule hits a compound molecule, combines
     */
    static class SimpleMoleculeCompoundMoleculeCriteria implements ReactionCriteria {
        private EnergyProfile energyProfile;

        public SimpleMoleculeCompoundMoleculeCriteria( EnergyProfile energyProfile ) {
            this.energyProfile = energyProfile;
        }

        public boolean criteriaMet( Molecule m1, Molecule m2, MoleculeMoleculeCollisionSpec  collisionSpec ) {

            // Determine the kinetic energy in the collision. We consider this to be the
            // kinetic energy of an object whose mass is equal to the total masses of
            // the two molecules, moving at a speed equal to the magnitude of the
            // relative velocity of the two molecules
            Vector2D loa = new Vector2D.Double( m2.getPosition().getX() - m1.getPosition().getX(),
                                                m2.getPosition().getY() - m1.getPosition().getY()).normalize();
            double sRel = Math.max( m1.getVelocity().dot( loa ) - m2.getVelocity().dot( loa), 0 );
            double ke = 0.5 * (m1.getMass() + m2.getMass() ) * sRel * sRel;

            // Classify the two molecules. Note that there must be one and only one
            // simple molecule, and one and only one composite molecule
            CompositeMolecule cm = null;
            SimpleMolecule sm = null;
            if( m1 instanceof CompositeMolecule ) {
                cm = (CompositeMolecule)m1;
            }
            else {
                sm = (SimpleMolecule)m1;
            }

            if( m2 instanceof CompositeMolecule ) {
                if( cm != null ) {
                    return false;
                }
                else {
                    cm = (CompositeMolecule)m2;
                }
            }
            else {
                if( sm != null ) {
                    return false;
                }
                else {
                    sm = (SimpleMolecule)m2;
                }
            }

            // One of the molecules must be a composite molecule whose components
            // are two simple molecules, and the other molecule must be a simple molecule
            if( cm.numSimpleMolecules() == 2
                && cm.getType() == CompositeMolecule.BB
                && sm instanceof MoleculeA
                && ke >= energyProfile.getPeakLevel() ) {
                return true;
            }
            if( cm.numSimpleMolecules() == 2
                && cm.getType() == CompositeMolecule.AB
                && sm instanceof MoleculeB
                && ke >= energyProfile.getPeakLevel() ) {
                return true;
            }
            return false;
        }
    }
}
