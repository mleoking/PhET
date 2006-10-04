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
import edu.colorado.phet.molecularreactions.model.collision.MoleculeMoleculeCollisionSpec;

/**
 * Reaction
 * <p/>
 * This class encapsulates all the criteria for whether a reaction will
 * occur or not.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
abstract public class Reaction {
    private EnergyProfile energyProfile;
    private ReactionCriteria reactionCriteria;

    /**
     * @param energyProfile
     * @param reactionCriteria
     */
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

    public boolean areCriteriaMet( AbstractMolecule bodyA, AbstractMolecule bodyB, MoleculeMoleculeCollisionSpec collisionSpec ) {
        boolean result = false;

        if( this.moleculesAreProperTypes( bodyA, bodyB ) ) {
            double energyThreshold = getThresholdEnergy( bodyA, bodyB );
            result = reactionCriteria.criteriaMet( bodyA, bodyB, collisionSpec, energyThreshold );
        }
        return result;
    }

    //--------------------------------------------------------------------------------------------------
    // Abstract and template methods
    //--------------------------------------------------------------------------------------------------

    /**
     * Checks to see if two molecules are the right types for the reaction
     *
     * @param molecule1
     * @param molecule2
     * @return true if the molecules are of the correct type for the reaction
     */
    public boolean moleculesAreProperTypes( AbstractMolecule molecule1, AbstractMolecule molecule2 ) {
        return getReactionCriteria().moleculesAreProperTypes( molecule1, molecule2 );
    }

    abstract public SimpleMolecule getMoleculeToRemove( CompositeMolecule compositeMolecule, SimpleMolecule moleculeAdded );

    abstract public SimpleMolecule getMoleculeToKeep( CompositeMolecule compositeMolecule, SimpleMolecule moleculeAdded );

    // The energy needed to get from one of the flat portions of the profile to the top of the curve
    abstract public double getThresholdEnergy( AbstractMolecule mA, AbstractMolecule mB );

    // The distance between the points on two molecules that can react that are closest to each other
    abstract public double getCollisionDistance( AbstractMolecule mA, AbstractMolecule mB );

    // The vector between the points on two molecules that can react that are closest to each other, directed
    // from the first parameter molecule to the second
    abstract public Vector2D getCollisionVector( AbstractMolecule mA, AbstractMolecule mB );

    // Returns the potential energy of the reaction components. This is the energy of one of the flat
    // portion of the profile
    abstract public double getPotentialEnergy( AbstractMolecule m1, AbstractMolecule m2 );

    //--------------------------------------------------------------------------------------------------
    // Reaction criteria
    //--------------------------------------------------------------------------------------------------

    public interface ReactionCriteria {
        boolean criteriaMet( AbstractMolecule bodyA,
                             AbstractMolecule bodyB,
                             MoleculeMoleculeCollisionSpec collisionSpec,
                             double energyThreshold );

        boolean moleculesAreProperTypes( AbstractMolecule molecule1, AbstractMolecule molecule2 );
    }

    /**
     * Combines two simple molecules of different types into one compound molecule
     */
    class SimpleMoleculeSimpleMoleculeReactionCriteria implements ReactionCriteria {
        public boolean criteriaMet( AbstractMolecule m1, AbstractMolecule m2, MoleculeMoleculeCollisionSpec collisionSpec, double energyThreshold ) {
            return m1.getKineticEnergy() + m2.getKineticEnergy() > energyProfile.getPeakLevel()
                   && m1 instanceof SimpleMolecule && m2 instanceof SimpleMolecule
                   && m1.getClass() != m2.getClass();
        }

        public boolean moleculesAreProperTypes( AbstractMolecule molecule1, AbstractMolecule molecule2 ) {
            return true;
        }
    }

    /**
     * Combines any two molecules together
     */
    class SimpleMoleculeReactionCriteria implements ReactionCriteria {
        public boolean criteriaMet( AbstractMolecule m1, AbstractMolecule m2, MoleculeMoleculeCollisionSpec collisionSpec, double energyThreshold ) {
            return m1.getKineticEnergy() + m2.getKineticEnergy() > energyProfile.getPeakLevel();
        }

        public boolean moleculesAreProperTypes( AbstractMolecule molecule1, AbstractMolecule molecule2 ) {
            return true;
        }
    }
}
