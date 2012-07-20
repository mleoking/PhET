// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.moleculepolarity.twoatoms;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.moleculepolarity.common.model.DiatomicMolecule;
import edu.colorado.phet.moleculepolarity.common.model.MPModel2D;

/**
 * Model for the "Two Atoms" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TwoAtomsModel extends MPModel2D {

    public final DiatomicMolecule molecule;

    public TwoAtomsModel( IClock clock ) {
        // all other layout is based on molecule location, so choose a visually-pleasing location for the molecule
        super( clock, new DiatomicMolecule( new Vector2D( 350, 390 ), 0 ) );
        molecule = (DiatomicMolecule) getMolecule(); // hate to cast, but it facilitates moving shared code to base class
    }
}
