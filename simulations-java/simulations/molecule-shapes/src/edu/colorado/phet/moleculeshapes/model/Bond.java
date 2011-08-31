// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.model;

import edu.colorado.phet.chemistry.model.Atom;

/**
 * Generic Bond
 */
public class Bond<AtomT extends Atom> {
    public final AtomT a;
    public final AtomT b;
    public final int order;

    public Bond( AtomT a, AtomT b, int order ) {
        this.a = a;
        this.b = b;
        this.order = order;
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

    public boolean contains( AtomT atom ) {
        return a == atom || b == atom;
    }

    public AtomT getOtherAtom( AtomT atom ) {
        assert contains( atom );
        return a == atom ? b : a;
    }

}
