/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.common.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;

/**
 * Abstract base class for nuclei that will autonomously decay after some
 * period of time.
 * 
 * @author John Blanco
 */
public abstract class AbstractDecayNucleus extends AtomicNucleus implements NuclearDecayControl {

	protected double _decayTime = 0;
	protected double _activatedLifetime = 0;
	protected double _halfLife = 0;
	protected boolean _paused = false;

	public AbstractDecayNucleus(NuclearPhysicsClock clock, Point2D position,
			int numProtons, int numNeutrons) {
		super(clock, position, numProtons, numNeutrons);
	}

	public double getDecayTime() {
	    return _decayTime;
	}

	public double getHalfLife() {
	    return _halfLife;
	}

	/**
	 * Set the half life for this nucleus.
	 * 
	 * @param halfLife - Half life in milliseconds.
	 */
	public void setHalfLife(double halfLife) {
	    _halfLife = halfLife;
	}

	public boolean isPaused() {
		return _paused;
	}

	public void setPaused(boolean paused) {
		_paused = paused;
	}

	public abstract void activateDecay();

	public abstract boolean hasDecayed();

	/**
	 * Return true if decay is currently active and false if not.
	 */
	public boolean isDecayActive() {
		if (_decayTime != 0){
			return true;
		}
		else{
			return false;
		}
	}

	/**
	 * Returns a value indicating how long the nucleus has been active without
	 * having decayed.
	 */
	public double getActivatedTime() {
		return _activatedLifetime;
	}

	public void reset() {
	    // Reset the decay time to 0, indicating that it shouldn't occur
	    // until something changes.
	    _decayTime = 0;
	    _activatedLifetime = 0;
	
	    // Make sure we are not paused.
		_paused = false;
	}
}
