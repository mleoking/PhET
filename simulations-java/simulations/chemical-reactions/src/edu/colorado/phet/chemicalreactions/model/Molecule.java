// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.chemicalreactions.model.MoleculeShape.AtomSpot;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;

public class Molecule {

    private final List<Atom> atoms = new ArrayList<Atom>();

    public Molecule() {

    }

    public Molecule( MoleculeShape shape ) {
        for ( AtomSpot spot : shape.spots ) {
            addAtom( new Atom( spot.element, new Property<ImmutableVector2D>( spot.position ) ) );
        }
    }

    public void addAtom( Atom atom ) {
        atoms.add( atom );
    }

    public List<Atom> getAtoms() {
        return atoms;
    }
}
