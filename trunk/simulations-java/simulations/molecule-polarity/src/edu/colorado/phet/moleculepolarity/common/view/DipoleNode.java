// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.PolarCartesianConverter;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.Vector2DNode;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.colorado.phet.moleculepolarity.common.model.Bond;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for visual representation of dipoles.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class DipoleNode extends PComposite {

    private static final double REF_MAGNITUDE = MPConstants.ELECTRONEGATIVITY_RANGE.getLength();
    private static final double REF_LENGTH = 200;

    private static final double PERPENDICULAR_OFFSET = 100; // offset perpendicular to the axis of the endpoints

    private final Vector2DNode vectorNode;

    protected DipoleNode( Color color ) {
        vectorNode = new Vector2DNode( 1, 0, REF_MAGNITUDE, REF_LENGTH ); // origin is at tail of VectorNode
        vectorNode.setArrowFillPaint( color );
        addChild( vectorNode );
    }

    protected void setMagnitude( double magnitude ) {
        vectorNode.setXY( magnitude, 0 );
    }

    // Visual representation of a bond dipole.
    public static class BondDipoleNode extends DipoleNode {
        public BondDipoleNode( final Bond bond ) {
            super( Color.BLACK );

            // align the dipole to be parallel with the bond, with some perpendicular offset
            SimpleObserver update = new SimpleObserver() {
                public void update() {

                    setMagnitude( bond.dipole.get().getMagnitude() * ( bond.isDipoleInPhase() ? 1 : -1 ) );

                    // compute location of dipole, with offset
                    double dipoleX = PolarCartesianConverter.getX( PERPENDICULAR_OFFSET, bond.getAngle() - Math.PI / 2 );
                    double dipoleY = PolarCartesianConverter.getY( PERPENDICULAR_OFFSET, bond.getAngle() - Math.PI / 2 );

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
                    if ( bond.isDipoleInPhase() ) {
                        translate( -length / 2, 0 );
                    }
                    else {
                        translate( +length / 2, 0 );
                    }
                }
            };
            bond.endpoint1.addObserver( update );
            bond.endpoint2.addObserver( update );
            bond.dipole.addObserver( update );
        }
    }
}
