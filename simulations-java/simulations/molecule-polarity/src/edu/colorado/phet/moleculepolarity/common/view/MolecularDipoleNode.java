// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.moleculepolarity.MPColors;
import edu.colorado.phet.moleculepolarity.common.model.Molecule2D;

/**
 * Visual representation of a molecular dipole.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MolecularDipoleNode extends DipoleNode {

    private static final double OFFSET = 55; // offset in the direction that the dipole points

    public MolecularDipoleNode( final Molecule2D molecule, final double scale ) {
        super( molecule.dipole, MPColors.MOLECULAR_DIPOLE, scale );

        // position the dipole with some radial offset from the molecule's location
        SimpleObserver observer = new SimpleObserver() {
            public void update() {

                // offset vector relative to molecule location
                ImmutableVector2D v = ImmutableVector2D.parseAngleAndMagnitude( OFFSET, molecule.dipole.get().getAngle() );

                // offset in world coordinate frame
                setOffset( molecule.location.getX() + v.getX(), molecule.location.getY() + v.getY() );
            }
        };
        molecule.dipole.addObserver( observer );
    }
}