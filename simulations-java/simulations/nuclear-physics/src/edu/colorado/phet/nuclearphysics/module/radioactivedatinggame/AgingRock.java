/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * This class implements the behavior of a model element that represents a
 * rock that can be dated by radiometric means, and that starts off looking
 * hot and then cools down.
 * 
 * @author John Blanco
 */
public class AgingRock extends DatableItem {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
	
	private static final String NAME = "Aging Rock"; // For debugging, no need to translate. 
	private static final String HOT_ROCK_IMAGE_NAME = "rock_1.png";
	private static final String COOL_ROCK_IMAGE_NAME = "rock_1.png";
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
	
	private final ConstantDtClock _clock;
	
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
	
	public AgingRock(ConstantDtClock clock, Point2D center, double width) {
		super(NAME, HOT_ROCK_IMAGE_NAME, center, width, 0, 0);
		_clock = clock;
	}
	
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

}
