// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.genenetwork.model;


/**
 * This is basically a null spawning strategy.
 * 
 * @author John Blanco
 */
public class SpawnNothingStrategy extends MessengerRnaSpawningStrategy {

	@Override
	public boolean isSpawningComplete() {
		return true;
	}

	@Override
	public void stepInTime(double dt, SimpleModelElement parentModelElement) {
		// Does nothing.
	}

}
