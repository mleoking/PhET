package edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice;

/**
 * The bond is the edge in the graph representation of the lattice.
 *
 * @author Sam Reid
 */
public class Bond {
    public final Ion source;
    public final Ion destination;
    public final BondType type;

    public Bond( Ion source, Ion destination, BondType type ) {
        this.source = source;
        this.destination = destination;
        this.type = type;
    }

    public Bond reverse() {
        return new Bond( destination, source, type.reverse() );
    }

    @Override public String toString() {
        return source + " --" + type + "--> " + destination;
    }
}
