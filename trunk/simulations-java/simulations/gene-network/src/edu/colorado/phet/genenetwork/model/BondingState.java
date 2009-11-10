package edu.colorado.phet.genenetwork.model;

/**
 * This enum defines the possible states of a bond that exists between two
 * model elements.
 * 
 * @author John Blanco
 */
public enum BondingState {
	UNBOUND_AND_AVAILABLE,
	MOVING_TOWARDS_BOND,
	BONDED,
	UNBONDED_BUT_UNAVALABLE
}
