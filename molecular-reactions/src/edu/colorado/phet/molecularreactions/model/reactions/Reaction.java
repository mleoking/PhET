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
abstract public class Reaction {
    private EnergyProfile energyProfile;
    private ReactionCriteria reactionCriteria;

    /**
     *
     * @param energyProfile
     * @param reactionCriteria
     */
    protected Reaction( EnergyProfile energyProfile, ReactionCriteria reactionCriteria ) {
        this.energyProfile = energyProfile;
        this.reactionCriteria = reactionCriteria;
    }

    //--------------------------------------------------------------------------------------------------
    // Abstract methods
    //--------------------------------------------------------------------------------------------------

    abstract public boolean moleculesAreProperTypes( Molecule molecule1, Molecule molecule2 );

    abstract public SimpleMolecule getMoleculeToRemove( CompositeMolecule compositeMolecule,  SimpleMolecule moleculeAdded );

    abstract public SimpleMolecule getMoleculeToKeep( CompositeMolecule compositeMolecule,  SimpleMolecule moleculeAdded );

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
}
