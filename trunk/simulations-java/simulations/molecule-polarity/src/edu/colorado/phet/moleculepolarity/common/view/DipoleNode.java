// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.PolarCartesianConverter;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.colorado.phet.moleculepolarity.common.model.Bond;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Base class for visual representation of dipoles.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DipoleNode extends PhetPNode {

    private static final double REFERENCE_MAGNITUDE = MPConstants.ELECTRONEGATIVITY_RANGE.getLength(); // model value
    private static final double REFERENCE_LENGTH = 150; // view size
    private static final double PERPENDICULAR_OFFSET = 75; // offset perpendicular to the axis of the endpoints
    private static final Dimension HEAD_SIZE = new Dimension( 12, 25 ); // similar to Jmol
    private static final double TAIL_WIDTH = 4; // similar to Jmol
    private static final double FRACTIONAL_HEAD_HEIGHT = 0.5; // when the head size is less than fractionalHeadHeight * arrow length, the head will be scaled.

    private double x;
    private final PPath arrowNode;

    private Point2D somePoint; // reusable point

    public DipoleNode( Color color ) {
        super();

        setPickable( false );
        setChildrenPickable( false );

        arrowNode = new PPath();
        arrowNode.setPaint( color );
        addChild( arrowNode );

        somePoint = new Point2D.Double();

        update();
    }

    protected void setComponentX( double x ) {
        if ( x != this.x ) {
            this.x = x;
            update();
        }
    }

    // Updates the arrow to match the node's state.
    private void update() {
        final double y = 0;
        final double magnitude = PolarCartesianConverter.getRadius( x, y );
        if ( magnitude == 0 ) {
            arrowNode.setPathTo( new Rectangle2D.Double() ); // because Arrow doesn't handle zero-length arrows
        }
        else {
            somePoint.setLocation( x * ( REFERENCE_LENGTH / REFERENCE_MAGNITUDE ), y );
            Arrow arrow = new Arrow( new Point2D.Double( 0, 0 ), somePoint, HEAD_SIZE.height, HEAD_SIZE.width, TAIL_WIDTH, FRACTIONAL_HEAD_HEIGHT, true /* scaleTailToo */ );
            arrowNode.setPathTo( arrow.getShape() );
        }
    }

    // Visual representation of a bond dipole.
    public static class BondDipoleNode extends DipoleNode {
        public BondDipoleNode( final Bond bond ) {
            super( Color.BLACK );

            // align the dipole to be parallel with the bond, with some perpendicular offset
            SimpleObserver update = new SimpleObserver() {
                public void update() {

                    setComponentX( bond.deltaElectronegativity.get() );

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
}
