package edu.colorado.phet.nuclearphysics.model;

import edu.colorado.phet.nuclearphysics.common.NucleusType;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;

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

		if (nucleusType == null){
			return Double.POSITIVE_INFINITY;
		}
		
		double halfLife;
		
		switch (nucleusType){
		
		case HYDROGEN_3:
			halfLife = HalfLifeInfo.convertDaysToMs( 4500 );
			break;
			
		case CARBON_14:
			halfLife = HalfLifeInfo.convertYearsToMs( 5730 );
			break;
			
		case POLONIUM_211:
			halfLife = 516;
			break;
			
		case URANIUM_235:
			halfLife = HalfLifeInfo.convertYearsToMs( 703800000 );
			break;
			
		case URANIUM_238:
			halfLife = HalfLifeInfo.convertYearsToMs( 4.46E9 );
			break;
			
		case HEAVY_CUSTOM:
			halfLife = 900;
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

	/**
	 * Convenience method for converting years to milliseconds, since
	 * milliseconds is used throughout the simulation for timing.
	 */
	static public double convertYearsToMs( double years ){
		return years * 3.1556926E10;
	}

	/**
	 * Convenience method for converting milliseconds to years, since
	 * milliseconds is used throughout the simulation for timing.
	 */
	static public double convertMsToYears( double milliseconds ){
		return milliseconds * 3.16887646E-11;
	}

	/**
	 * Convenience method for converting days to milliseconds, since
	 * milliseconds is used throughout the simulation for timing.
	 */
	static public double convertDaysToMs( double days ){
		return days * 86400000;
	}

	/**
	 * Convenience method for converting hours to milliseconds, since
	 * milliseconds is used throughout the simulation for timing.
	 */
	static public double convertHoursToMs( double hours ){
		return hours * 3600000;
	}
}
