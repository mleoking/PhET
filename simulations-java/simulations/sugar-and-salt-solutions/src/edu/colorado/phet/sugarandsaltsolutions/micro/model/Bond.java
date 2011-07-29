package edu.colorado.phet.sugarandsaltsolutions.micro.model;

/**
 * The bond is the edge in the graph representation of the lattice.
 *
 * @author Sam Reid
 */
public class Bond<T> {

    //Point of origination for the bond
    public final T source;

    //Vertex where the bond terminates
    public final T destination;

    //Type of the bond, for distinguishing bonds to make sure lattices can have the right topology
    public final BondType type;

    public Bond( T source, T destination, BondType type ) {
        this.source = source;
        this.destination = destination;
        this.type = type;
    }

    public Bond<T> reverse() {
        return new Bond<T>( destination, source, type.reverse() );
    }

    @Override public String toString() {
        return source + " --" + type + "--> " + destination;
    }

    public boolean contains( T component ) {
        return source == component || destination == component;
    }
}