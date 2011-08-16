// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.PolarCartesianConverter;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.moleculepolarity.common.model.IMolecule;

/**
 * Visual representation of a molecular dipole.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MolecularDipoleNode extends DipoleNode {

    private static final double OFFSET = 200; // offset in the direction that the dipole points

    public MolecularDipoleNode( final IMolecule molecule ) {
        super( Color.ORANGE );

        // align the dipole to be parallel with the bond, with some perpendicular offset
        SimpleObserver update = new SimpleObserver() {
            public void update() {

                ImmutableVector2D dipole = molecule.getMolecularDipole();
                System.out.println( "MolecularDipoleNode.update magnitude=" + dipole.getMagnitude() + " angle=" + Math.toDegrees( dipole.getAngle() ) );

                setComponents( dipole.getX(), dipole.getY() );

                // compute offset, in direction that dipole points
                final double angle = dipole.getAngle();
                double offsetX = PolarCartesianConverter.getX( OFFSET, angle );
                double offsetY = PolarCartesianConverter.getY( OFFSET, angle );

                // translate relative to molecule's location
                translate( molecule.getLocation().getX() + offsetX, molecule.getLocation().getY() + offsetY );
            }
        };
        molecule.addMolecularDipoleObserver( update );
    }
}