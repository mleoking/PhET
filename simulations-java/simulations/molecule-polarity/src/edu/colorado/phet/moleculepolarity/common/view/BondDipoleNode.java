// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import edu.colorado.phet.common.phetcommon.math.PolarCartesianConverter;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.moleculepolarity.MPColors;
import edu.colorado.phet.moleculepolarity.common.model.Bond;

/**
 * Visual representation of a bond dipole.
 * Controls its own offset in world coordinates, so clients should not call setOffset.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BondDipoleNode extends DipoleNode {

    private static final double PERPENDICULAR_OFFSET = 55; // offset perpendicular to the axis of the bond

    public BondDipoleNode( final Bond bond ) {
        super( bond.dipole, MPColors.BOND_DIPOLE );

        // position the dipole to be parallel with the bond, with some perpendicular offset
        bond.dipole.addObserver( new SimpleObserver() {
            public void update() {
                // location of tail in polar coordinates, relative to center of bond
                double offsetX = isInPhase( bond, bond.dipole.get() ) ? ( getDipoleViewLength() / 2 ) : -( getDipoleViewLength() / 2 );
                double offsetAngle = Math.atan( offsetX / PERPENDICULAR_OFFSET );
                double tailDistance = PERPENDICULAR_OFFSET / Math.cos( offsetAngle );
                double tailAngle = bond.getAngle() - ( Math.PI / 2 ) - offsetAngle;

                // location of tail in Cartesian coordinates, relative to center of bond
                double tailX = PolarCartesianConverter.getX( tailDistance, tailAngle );
                double tailY = PolarCartesianConverter.getY( tailDistance, tailAngle );

                // location of tail in world coordinate frame
                setOffset( bond.getCenter().getX() + tailX, bond.getCenter().getY() + tailY );
            }
        } );
    }

    // True if the dipole points in the same direction as a vector from bond.endpoint1 to bond.endpoint2.
    // Direction will never be precisely the same due to round-off error, so test with a coarse angle.
    private static boolean isInPhase( Bond bond, Vector2D dipole ) {
        return Math.abs( bond.getAngle() - dipole.getAngle() ) < ( Math.PI / 4 );
    }
}