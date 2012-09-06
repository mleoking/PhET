// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.moleculeshapes.model;

/**
 * Generic Bond
 */
public class Bond<AtomT> {
    public final AtomT a;
    public final AtomT b;
    public final int order;
    public final double length; // in angstroms, or 0 if there is no data

    public Bond( AtomT a, AtomT b, int order, double length ) {
        this.a = a;
        this.b = b;
        this.order = order;
        this.length = length;
        assert ( a != b );
    }

    @Override
    public int hashCode() {
        return a.hashCode() + b.hashCode();
    }

    @Override
    public boolean equals( Object ob ) {
        if ( ob instanceof Bond/*, James Bond*/ ) {
            Bond other = (Bond) ob;
            return ( this.a == other.a && this.b == other.b ) || ( this.a == other.b && this.b == other.a );
        }
        else {
            return false;
        }
    }

    @Override public String toString() {
        return "{" + a.toString() + " => " + b.toString() + "}";
    }

    public boolean contains( AtomT atom ) {
        return a == atom || b == atom;
    }

    public AtomT getOtherAtom( AtomT atom ) {
        assert contains( atom );
        return a == atom ? b : a;
    }

}
