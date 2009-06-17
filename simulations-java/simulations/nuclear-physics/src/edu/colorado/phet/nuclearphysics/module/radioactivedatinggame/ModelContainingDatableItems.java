/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Point2D;

/**
 * This interface is used to interact with the datable items contained within
 * a model.
 * 
 * @author John Blanco
 */
public interface ModelContainingDatableItems {

	/**
	 * Get the datable item at the specified model location, or null if there
	 * isn't anything there.
	 */
	public abstract DatableItem getDatableItemAtLocation(Point2D probeLocation);

}