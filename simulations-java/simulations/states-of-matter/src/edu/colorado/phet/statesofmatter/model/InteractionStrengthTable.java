/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model;

import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.model.particle.ConfigurableStatesOfMatterAtom;

/**
 * This class provides the interaction strength value between a number of
 * different pairs of atoms.  To do them all would be too much, so this is a
 * sparse table.  Feel free to fill them in as more are needed.
 * 
 * At the time of this writing, it is my understanding that the interaction
 * potential is not a property of an individual atom or molecule, but of two
 * interacting atoms or molecules.  That is why this is being created as a
 * separate class.  If this turns out to be a misunderstanding, it would make
 * sense to consolidate this information into the individual atom objects.
 * 
 * @author John Blanco
 *
 */
public class InteractionStrengthTable {

	/**
	 * Get the interaction potential between two atoms.  Units are such that
	 * the value divided by k-boltzmann is in Kelvin.  This is apparently how
	 * it is generally done.  Note that this value is used as the "epsilon"
	 * parameter in Lennard-Jones potential calculations.
	 * 
	 * @param atom1
	 * @param atom2
	 * @return
	 */
	static double getInteractionPotential( AtomType atom1, AtomType atom2 ){
		if (atom1 == atom2){
			// Heterogeneous pair of atoms.
			if (atom1 == AtomType.NEON){
				return 32.8;
			}
			else if (atom1 == AtomType.ARGON){
				return 111.84;
			}
			else if (atom1 == AtomType.OXYGEN){
				return 113.27;
			}
			else if (atom1 == AtomType.ADJUSTABLE){
				return ConfigurableStatesOfMatterAtom.DEFAULT_INTERACTION_POTENTIAL;
			}
			else{
				System.err.println("Error: Interaction potential not available for requested atom: " + atom1);
				assert(false);
				return StatesOfMatterConstants.MAX_EPSILON / 2;  // In the real world, default to an arbitrary value.
			}
		}
		else{
			// Heterogeneous situation.  I think that in reality each pair of
			// atoms would have a unique interaction potential, but for now we
			// just use the average of the two atoms.
			return (getInteractionPotential(atom1, atom1) + getInteractionPotential(atom2, atom2)) / 2;
		}
	}
}
