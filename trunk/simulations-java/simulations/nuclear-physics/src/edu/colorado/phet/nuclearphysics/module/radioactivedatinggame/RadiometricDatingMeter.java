/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import edu.colorado.phet.nuclearphysics.model.Carbon14Nucleus;
import edu.colorado.phet.nuclearphysics.model.Uranium238Nucleus;

/**
 * This class encapsulates a meter that supplies information about the amount
 * of a radiometric substance that has decayed in a given sample.
 * 
 * @author John Blanco
 */
public class RadiometricDatingMeter {

	// Static - Not intended for instantiation.
	
	public static double getPercentageCarbon14Remaining( DatableObject item ){
		return calculatePercentageRemaining(item.getAge(), Carbon14Nucleus.HALF_LIFE);
	}
	
	public static double getPercentageUranium238Remaining( DatableObject item ){
		return calculatePercentageRemaining(item.getAge(), Uranium238Nucleus.HALF_LIFE);
	}
	
	/**
	 * Get the amount of a substance that would be left based on the age of an
	 * item and the half life of the nucleus of the radiometric material being
	 * tested.
	 * 
	 * @param item
	 * @param customNucleusHalfLife
	 * @return
	 */
	public static double getPercentageCustomNucleusRemaining( DatableObject item, double customNucleusHalfLife ){
		return calculatePercentageRemaining(item.getAge(), customNucleusHalfLife);
	}
	
	private static double calculatePercentageRemaining( double age, double halfLife ){
		if ( age <= 0 ){
			return 100;
		}
		else{
			return 100 * Math.exp( -0.693 * age / halfLife );
		}
	}
}
