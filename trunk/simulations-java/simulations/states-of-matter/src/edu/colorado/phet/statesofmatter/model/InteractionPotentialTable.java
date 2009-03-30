/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model;

/**
 * This class provides the interaction potential value between a number of
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
public class InteractionPotentialTable {

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
		return 100;
	}
}
