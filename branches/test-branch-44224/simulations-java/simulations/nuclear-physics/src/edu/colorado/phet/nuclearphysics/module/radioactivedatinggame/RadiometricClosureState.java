package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

/**
 * This enum defines the possible states with respect to closure, which
 * is the time at which the item begins aging radiometrically and its
 * radioactive elements start decreasing.  For example, if the item is
 * organic, closure occurs when the item dies.
 */
public enum RadiometricClosureState {
		CLOSURE_NOT_POSSIBLE,    // Closure cannot be forced.
		CLOSURE_POSSIBLE,        // Closure has not occurred, but could be forced.
		CLOSED                   // Closure has occurred.
}
