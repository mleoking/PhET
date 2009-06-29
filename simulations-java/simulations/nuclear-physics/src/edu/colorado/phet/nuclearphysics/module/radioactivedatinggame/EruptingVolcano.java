/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * This class implements the behavior of a model element that represents a
 * volcano that can can be dated by radiometric means, and that can erupt,
 * and that sends out the appropriate animation notifications when it does.
 * 
 * @author John Blanco
 */
public class EruptingVolcano extends DatableItem {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
	
	private static final String NAME = "Volcano"; // For debugging, no need to translate. 
	private static final String HOT_VOLCANO_IMAGE_NAME = "volcano_hot.png";
	private static final String DORMANT_VOLCANO_IMAGE_NAME = "volcano_cool.png";
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
	
	private final ConstantDtClock _clock;
	
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
	
	public EruptingVolcano(ConstantDtClock clock, Point2D center, double width) {
		super(NAME, HOT_VOLCANO_IMAGE_NAME, center, width, 0, 0);
		_clock = clock;
	}
	
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

}
