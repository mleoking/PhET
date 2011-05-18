//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.model;

import edu.colorado.phet.chemistry.model.Atom;

/**
 * Base class for a molecular bond between two atoms. Order of the bond and other details are
 * not included, but could be put into a subclass
 */
public class Bond<T extends Atom> {
    public T a;
    public T b;

    public Bond( T a, T b ) {
        this.a = a;
        this.b = b;
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

    public boolean contains( T atom ) {
        return atom == a || atom == b;
    }

    public T getOtherAtom( T atom ) {
        assert ( contains( atom ) );
        return ( a == atom ? b : a );
    }
}
