// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.genenetwork.model;


/**
 * Base class for strategies that are used by messenger RNA (mRNA) to create
 * or "spawn" enzymes or proteins.
 * 
 * @author John Blanco
 */
public abstract class MessengerRnaSpawningStrategy {

	public abstract boolean isSpawningComplete();
	
	public abstract void stepInTime(double dt, SimpleModelElement parentModelElement);
}
