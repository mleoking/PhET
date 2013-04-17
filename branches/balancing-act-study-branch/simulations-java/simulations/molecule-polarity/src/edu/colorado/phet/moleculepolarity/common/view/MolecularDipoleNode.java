// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.moleculepolarity.MPColors;
import edu.colorado.phet.moleculepolarity.common.model.Molecule2D;

/**
 * Visual representation of a molecular dipole.
 * Controls its own offset in world coordinates, so clients should not call setOffset.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MolecularDipoleNode extends DipoleNode {

    private static final double OFFSET = 55; // offset in the direction that the dipole points

    public MolecularDipoleNode( final Molecule2D molecule ) {
        super( molecule.dipole, MPColors.MOLECULAR_DIPOLE );

        // position the dipole with some radial offset from the molecule's location
        molecule.dipole.addObserver( new SimpleObserver() {
            public void update() {
                // offset vector relative to molecule location
                Vector2D v = Vector2D.createPolar( OFFSET, molecule.dipole.get().getAngle() );
                // offset in world coordinate frame
                setOffset( molecule.location.getX() + v.getX(), molecule.location.getY() + v.getY() );
            }
        } );
    }
}