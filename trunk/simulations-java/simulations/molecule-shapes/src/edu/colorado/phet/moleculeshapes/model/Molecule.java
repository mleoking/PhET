// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a general molecular structure.
 * <p/>
 * TODO: potentially make more generic and combine with BAM's molecule handling!!!
 */
public class Molecule {
    private List<Atom3D> atoms = new ArrayList<Atom3D>();
    private List<Bond<Atom3D>> bonds = new ArrayList<Bond<Atom3D>>();

    public Molecule() {
    }

    public void addAtom( Atom3D atom ) {
        assert !atoms.contains( atom );
        atoms.add( atom );
    }

    public void addBond( Atom3D a, Atom3D b, int order ) {
        bonds.add( new Bond<Atom3D>( a, b, order ) );
    }

    public Collection<Atom3D> getAtoms() {
        return atoms;
    }

    public Collection<Bond<Atom3D>> getBonds() {
        return bonds;
    }
}
