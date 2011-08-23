// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import edu.colorado.phet.common.phetcommon.math.PolarCartesianConverter;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.moleculepolarity.MPColors;
import edu.colorado.phet.moleculepolarity.common.model.Bond;

/**
 * Visual representation of a bond dipole.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BondDipoleNode extends DipoleNode {

    private static final double PERPENDICULAR_OFFSET = 75; // offset perpendicular to the axis of the endpoints

    public BondDipoleNode( final Bond bond, final double scale ) {
        super( MPColors.BOND_DIPOLE );

        // align the dipole to be parallel with the bond, with some perpendicular offset
        SimpleObserver update = new SimpleObserver() {
            public void update() {

                setComponentX( scale * bond.deltaElectronegativity.get() ); // for a dipole with angle=0

                // compute location of dipole, with offset
                final double angle = bond.getAngle() - Math.PI / 2; // above the bond
                double dipoleX = PolarCartesianConverter.getX( PERPENDICULAR_OFFSET, angle );
                double dipoleY = PolarCartesianConverter.getY( PERPENDICULAR_OFFSET, angle );

                // clear the transform
                setOffset( 0, 0 );
                setRotation( 0 );

                // compute length before transforming
                final double length = getFullBoundsReference().getWidth();

                // offset from bond
                translate( bond.getCenter().getX() + dipoleX, bond.getCenter().getY() + dipoleY );

                // parallel to bond
                rotate( bond.getAngle() );

                // center vector on bond
                if ( bond.deltaElectronegativity.get() > 0 ) {
                    translate( -length / 2, 0 );
                }
                else {
                    translate( +length / 2, 0 );
                }
            }
        };
        bond.endpoint1.addObserver( update );
        bond.endpoint2.addObserver( update );
        bond.deltaElectronegativity.addObserver( update );
    }
}