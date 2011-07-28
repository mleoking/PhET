package edu.colorado.phet.sugarandsaltsolutions.micro.model;

/**
 * Represents a location within a lattice where a new component could be added.  This is used in lattice growing.
 *
 * @author Sam Reid
 */
public abstract class LatticeSite<T> {
    public final T component;
    public final BondType type;

    public LatticeSite( T component, BondType type ) {
        this.component = component;
        this.type = type;
    }

    //Create a new lattice that has expanded from this open site
    public abstract Lattice<T> grow( Lattice<T> lattice );
}