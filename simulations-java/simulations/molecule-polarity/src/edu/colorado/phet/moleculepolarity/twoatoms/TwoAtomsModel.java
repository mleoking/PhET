// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.twoatoms;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.moleculepolarity.common.model.MPModel;
import edu.colorado.phet.moleculepolarity.common.model.TwoAtomMolecule;

/**
 * Model for the "One Atom" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TwoAtomsModel extends MPModel {

    public final TwoAtomMolecule molecule;

    public TwoAtomsModel() {
        molecule = new TwoAtomMolecule( new ImmutableVector2D( 325, 390 ) );
    }

    @Override public void reset() {
        super.reset();
        molecule.reset();
    }

}
