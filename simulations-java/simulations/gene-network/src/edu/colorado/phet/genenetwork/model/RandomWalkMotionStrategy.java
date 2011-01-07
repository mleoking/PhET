// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Rectangle2D;

/**
 * Motion strategy that moves things around in a way that emulates the "random
 * walk" behavior exhibited by particles in a fluid.
 * 
 * @author John Blanco
 */
public class RandomWalkMotionStrategy extends DirectedRandomWalkMotionStrategy {
	
	public RandomWalkMotionStrategy(Rectangle2D bounds) {
		super(bounds);
	}
}
