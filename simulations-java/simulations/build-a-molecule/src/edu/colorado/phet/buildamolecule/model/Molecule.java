//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Represents a "Build a Molecule" molecule. Also useful as a type alias
 */
public class Molecule extends MoleculeStructure<Atom2D> {

    public PBounds getPositionBounds() {
        PBounds bounds = null;
        for ( Atom2D atom : getAtoms() ) {
            PBounds atomBounds = atom.getPositionBounds();
            if ( bounds == null ) {
                bounds = atomBounds;
            }
            else {
                bounds.add( atomBounds );
            }
        }
        return bounds;
    }

    public PBounds getDestinationBounds() {
        PBounds bounds = null;
        for ( Atom2D atom : getAtoms() ) {
            PBounds atomBounds = atom.getDestinationBounds();
            if ( bounds == null ) {
                bounds = atomBounds;
            }
            else {
                bounds.add( atomBounds );
            }
        }
        return bounds;
    }

    public void shiftDestination( ImmutableVector2D delta ) {
        for ( Atom2D atom : getAtoms() ) {
            atom.setDestination( atom.getDestination().getAddedInstance( delta ) );
        }
    }
}
