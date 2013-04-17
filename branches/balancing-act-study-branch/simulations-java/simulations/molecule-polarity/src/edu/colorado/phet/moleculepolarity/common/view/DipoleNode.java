// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Base class for visual representation of 2D dipoles.
 * The dipole is created at (0,0) with proper length and orientation, and it's up to subclasses to position the dipole.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class DipoleNode extends PPath {

    // Note: heights are parallel to dipole axis, widths are perpendicular.
    private static final double REFERENCE_MAGNITUDE = MPConstants.ELECTRONEGATIVITY_RANGE.getLength(); // model value
    private static final double REFERENCE_LENGTH = 135; // view size that corresponds to REFERENCE_MAGNITUDE
    private static final Dimension HEAD_SIZE = new Dimension( 12, 20 ); // similar to Jmol
    private static final Dimension CROSS_SIZE = new Dimension( 10, 10 ); // similar to Jmol
    private static final double REFERENCE_CROSS_OFFSET = 20; // offset from the tail of the arrow when arrow length is REFERENCE_LENGTH
    private static final double TAIL_WIDTH = 4; // similar to Jmol
    private static final double FRACTIONAL_HEAD_HEIGHT = 0.5; // when the head size is less than fractionalHeadHeight * arrow length, the head will be scaled.

    private final Property<Vector2D> dipole;

    protected DipoleNode( Property<Vector2D> dipole, Color color ) {
        super();
        setPaint( color );
        this.dipole = dipole;
        dipole.addObserver( new SimpleObserver() {
            public void update() {
                updateNode();
            }
        } );
    }

    // Updates the arrow to match the dipole's length and orientation.
    private void updateNode() {
        final double y = 0;
        if ( dipole.get().magnitude() == 0 ) {
            setPathTo( new Rectangle2D.Double() ); // because Arrow doesn't handle zero-length arrows
        }
        else {
            // arrow, in angle=0 orientation (pointing right), so we can easily add cross
            Arrow arrow = new Arrow( new Point2D.Double( 0, 0 ), new Point2D.Double( getDipoleViewLength(), y ),
                                     HEAD_SIZE.height, HEAD_SIZE.width, TAIL_WIDTH, FRACTIONAL_HEAD_HEIGHT, true /* scaleTailToo */ );
            // cross
            Shape cross = createCross( arrow );

            // Combine arrow and cross using constructive area geometry.
            Area area = new Area( arrow.getShape() );
            area.add( new Area( cross ) );
            setPathTo( area );

            // rotate into dipole orientation
            setRotation( dipole.get().getAngle() );
        }
    }

    /*
     * Creates the cross that signifies the positive end of the dipole.
     * We're attempting to make this look like Jmol's representation, which looks more like a 3D cylinder.
     * The arrow head and cross scale when the dipole is below some minimum length (as defined by FRACTIONAL_HEAD_HEIGHT).
     */
    private Shape createCross( Arrow arrow ) {

        double arrowLength = Math.abs( arrow.getTipLocation().getX() - arrow.getTailLocation().getX() );
        final double headScale = arrow.getHeadScale();

        // scale offset and height based on arrow length and whether arrow head is scaled.
        double crossOffset = headScale * REFERENCE_CROSS_OFFSET * arrowLength / REFERENCE_LENGTH;
        double crossHeight = headScale * CROSS_SIZE.height * arrowLength / REFERENCE_LENGTH;

        // scale width based on whether arrow head is scaled.
        double crossWidth = headScale * CROSS_SIZE.width;

        // arrow points left, flip sign of offset and shift it to the left
        if ( arrow.getTipLocation().getX() < arrow.getTailLocation().getX() ) {
            crossOffset = ( -1 * crossOffset ) - crossHeight;
        }

        return new Rectangle2D.Double( crossOffset, -crossWidth / 2, crossHeight, crossWidth );
    }

    // Gets the length of the dipole in view coordinates
    protected double getDipoleViewLength() {
        return dipole.get().magnitude() * ( REFERENCE_LENGTH / REFERENCE_MAGNITUDE );
    }

    // Creates a dipole icon, with arrow pointing to the right.
    public static Icon createIcon( Color color ) {
        PNode node = new DipoleNode( new Property<Vector2D>( new Vector2D( 0.5, 0 ) ), color ) {
        };
        return new ImageIcon( node.toImage() );
    }
}
