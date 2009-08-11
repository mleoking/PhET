package edu.colorado.phet.nuclearphysics.model;

import edu.colorado.phet.nuclearphysics.common.NucleusType;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus.MultiNucleusDecayModel;

/**
 * This very simple class acts as a central location where the half life of
 * various nuclei may be obtained.
 * 
 * @author John Blanco
 */
public class HalfLifeInfo {

	private HalfLifeInfo(){
		// Not meant to be instantiated.
	}
	
	/**
	 * Get the half life for the specified nucleus type.
	 * 
	 * @param nucleusType
	 * @return half life in milliseconds (might be a really big number)
	 */
	public static double getHalfLifeForNucleusType(NucleusType nucleusType){

		double halfLife;
		
		switch (nucleusType){
		
		case HYDROGEN_3:
			halfLife = MultiNucleusDecayModel.convertDaysToMs( 4500 );
			break;
			
		case CARBON_14:
			halfLife = MultiNucleusDecayModel.convertYearsToMs( 5730 );
			break;
			
		case URANIUM_238:
			halfLife = MultiNucleusDecayModel.convertYearsToMs( 4.46E9 );
			break;
			
		default:
			System.err.println("Warning: No half life info for nucleus type " + nucleusType);
			assert false;
			halfLife = 0;
			break;
		}
		
		return halfLife;
	}
	
	public static double getHalfLifeForNucleusConfig(int numProtons, int numNeutrons){
		return getHalfLifeForNucleusType(AtomicNucleus.identifyNucleus(numProtons, numNeutrons));
	}
}
