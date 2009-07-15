/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

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
	public DatableItem getDatableItemAtLocation(Point2D probeLocation);
	
	/**
	 * Get the clock that the model is using.
	 * 
	 * @return - Clock being used by the model, null if this model is non-clocked.
	 */
	public ConstantDtClock getClock(); 

}