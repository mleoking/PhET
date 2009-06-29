/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * This class implements the behavior of a model element that represents a
 * tree that can be dated by radiometric means, and that grows, dies, and
 * falls over as time goes by.
 * 
 * @author John Blanco
 */
public class AgingTree extends DatableItem {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
	
	private static final String NAME = "Aging Tree"; // For debugging, no need to translate. 
	private static final String LIVING_TREE_IMAGE_NAME = "tree_1.png";
	private static final String DEAD_TREE_IMAGE_NAME = "dead_tree.png";
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
	
	private final ConstantDtClock _clock;
	
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
	
	public AgingTree(ConstantDtClock clock, Point2D center, double width, double rotationAngle) {
		super(NAME, LIVING_TREE_IMAGE_NAME, center, width, rotationAngle, 0);
		_clock = clock;
	}
	
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

}
