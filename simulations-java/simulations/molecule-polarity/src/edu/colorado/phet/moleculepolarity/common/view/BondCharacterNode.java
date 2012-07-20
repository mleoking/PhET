// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.simsharing.NonInteractiveEventHandler;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.colorado.phet.moleculepolarity.MPSimSharing.UserComponents;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.model.Atom;
import edu.colorado.phet.moleculepolarity.common.model.DiatomicMolecule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Displays the bond type, by placing a marker on a continuum whose extremes are "covalent" and "ionic".
 * Origin is at the upper-left corner of the "track".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BondCharacterNode extends PComposite {

    private static final double TRACK_WIDTH = 360;
    private static final Font TITLE_FONT = new PhetFont( 12 );
    private static final Font LABEL_FONT = new PhetFont( 12 );
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color TRACK_COLOR = Color.WHITE;
    private static final double LABEL_X_INSET = 4;
    private static final double POINTER_X_INSET = 10;
    private static final double Y_MARGIN = 3;
    private static final double Y_SPACING = 3;

    private final LinearFunction xOffsetFunction;

    public BondCharacterNode( DiatomicMolecule molecule ) {

        final PText titleNode = new PText( MPStrings.BOND_CHARACTER ) {{
            setFont( TITLE_FONT );
            setTextPaint( TEXT_COLOR );
        }};

        // label at the max end
        final PNode maxLabelNode = new PText( MPStrings.IONIC ) {{
            setFont( LABEL_FONT );
            setTextPaint( TEXT_COLOR );
        }};

        // label at the min end
        final PNode minLabelNode = new PText( MPStrings.COVALENT ) {{
            setFont( LABEL_FONT );
            setTextPaint( TEXT_COLOR );
        }};

        // pointer that moves along the track, not interactive
        final PointerNode pointerNode = new PointerNode( molecule.atomA, molecule.atomB );

        // compute the track
        double trackHeight = ( 2 * Y_MARGIN ) + titleNode.getFullBoundsReference().getHeight() + Y_SPACING + pointerNode.getFullBoundsReference().getHeight();
        PNode trackNode = new PPath( new Rectangle2D.Double( 0, 0, TRACK_WIDTH, trackHeight ) ) {{
            setPaint( TRACK_COLOR );
        }};

        // rendering order
        addChild( trackNode );
        addChild( pointerNode );
        addChild( titleNode );
        addChild( maxLabelNode );
        addChild( minLabelNode );

        // layout
        trackNode.setOffset( 0, 0 );
        titleNode.setOffset( trackNode.getFullBoundsReference().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 ),
                             trackNode.getYOffset() + Y_MARGIN );
        pointerNode.setOffset( 0, titleNode.getFullBoundsReference().getMaxY() + Y_SPACING );
        minLabelNode.setOffset( trackNode.getFullBoundsReference().getMinX() + LABEL_X_INSET,
                                trackNode.getYOffset() + Y_MARGIN );
        maxLabelNode.setOffset( trackNode.getFullBoundsReference().getMaxX() - maxLabelNode.getFullBoundsReference().getWidth() - LABEL_X_INSET,
                                trackNode.getYOffset() + Y_MARGIN );

        xOffsetFunction = new LinearFunction( 0, MPConstants.ELECTRONEGATIVITY_RANGE.getLength(),
                                              POINTER_X_INSET, TRACK_WIDTH - pointerNode.getFullBoundsReference().getWidth() - POINTER_X_INSET );

        // when difference in electronegativity changes, move the thumb
        molecule.bond.dipole.addObserver( new VoidFunction1<Vector2D>() {
            public void apply( Vector2D dipole ) {
                pointerNode.setOffset( xOffsetFunction.evaluate( dipole.getMagnitude() ), pointerNode.getYOffset() );
            }
        } );

        //Report when the user tries to interactive with this non-interactive control
        addInputEventListener( new NonInteractiveEventHandler( UserComponents.bondCharacterNode ) );
    }

    // Pointer looks like a horizontally aligned diatomic molecule.
    private static class PointerNode extends PComposite {

        private static final double ATOM_DIAMETER = 10;

        public PointerNode( final Atom atom1, final Atom atom2 ) {

            PPath bondNode = new PPath( new Line2D.Double( 0, 0, ATOM_DIAMETER + 3, 0 ) ) {{
                setStroke( new BasicStroke( 3f ) );
                setStrokePaint( Color.GRAY );
            }};
            PPath atom1Node = new TinyAtomNode( atom1, ATOM_DIAMETER );
            PPath atom2Node = new TinyAtomNode( atom2, ATOM_DIAMETER );

            addChild( bondNode );
            addChild( atom1Node );
            addChild( atom2Node );

            bondNode.setOffset( atom1Node.getFullBoundsReference().getCenterX(),
                                atom1Node.getFullBounds().getCenterY() );
            atom2Node.setOffset( bondNode.getFullBoundsReference().getMaxX() - ( atom2Node.getFullBoundsReference().getWidth() / 2 ),
                                 atom1Node.getXOffset() );
        }
    }

    private static class TinyAtomNode extends PPath {
        public TinyAtomNode( Atom atom, double diameter ) {
            setPathTo( new Ellipse2D.Double( 0, 0, diameter, diameter ) );
            setPaint( atom.getColor() );
        }
    }
}
