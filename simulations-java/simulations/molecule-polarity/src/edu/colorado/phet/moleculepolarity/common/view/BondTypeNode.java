// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.moleculepolarity.MPConstants;
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
public class BondTypeNode extends PComposite {

    private static final Dimension TRACK_SIZE = new Dimension( 350, 5 );
    private static final Font TITLE_FONT = new PhetFont( 12 );
    private static final Font LABEL_FONT = new PhetFont( 12 );
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color TRACK_COLOR = Color.WHITE;
    private static final double X_INSET = 3;
    private static final double Y_INSET = 3;

    private static final LinearFunction X_OFFSET_FUNCTION = new LinearFunction( 0, MPConstants.ELECTRONEGATIVITY_RANGE.getLength(), 0, TRACK_SIZE.width );

    public BondTypeNode( DiatomicMolecule molecule ) {

        final PText titleNode = new PText( MPStrings.BOND_TYPE ) {{
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

        // compute a track height that fits the title and labels
        double trackHeight = Math.max( TRACK_SIZE.height, PNodeLayoutUtils.getMaxFullHeight( new ArrayList<PNode>() {{
            add( titleNode );
            add( maxLabelNode );
            add( minLabelNode );
        }} ) + ( 2 * Y_INSET ) );
        // the track that represents the continuum
        PNode trackNode = new PPath( new Rectangle2D.Double( 0, 0, TRACK_SIZE.width, trackHeight ) ) {{
            setPaint( TRACK_COLOR );
        }};

        // pointer that moves along the track, not interactive
        final PointerNode pointerNode = new PointerNode( trackHeight, molecule.atomA, molecule.atomB );

        // rendering order
        addChild( trackNode );
        addChild( pointerNode );
        addChild( titleNode );
        addChild( maxLabelNode );
        addChild( minLabelNode );

        // layout
        trackNode.setOffset( 0, 0 );
        titleNode.setOffset( trackNode.getFullBoundsReference().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 ),
                             trackNode.getYOffset() + Y_INSET );
        pointerNode.setOffset( trackNode.getOffset() );
        minLabelNode.setOffset( trackNode.getFullBoundsReference().getMinX() + X_INSET,
                                trackNode.getYOffset() + Y_INSET );
        maxLabelNode.setOffset( trackNode.getFullBoundsReference().getMaxX() - maxLabelNode.getFullBoundsReference().getWidth() - X_INSET,
                                trackNode.getYOffset() + Y_INSET );

        // when difference in electronegativity changes, move the thumb
        molecule.bond.dipole.addObserver( new VoidFunction1<ImmutableVector2D>() {
            public void apply( ImmutableVector2D dipole ) {
                pointerNode.setOffset( X_OFFSET_FUNCTION.evaluate( dipole.getMagnitude() ), pointerNode.getYOffset() );
            }
        } );
    }

    // Pointer looks like a vertical bond, connecting 2 atoms.
    private static class PointerNode extends PComposite {

        private static final double ATOM_DIAMETER = 10;

        public PointerNode( final double trackHeight, final Atom atom1, final Atom atom2 ) {

            PPath atom1Node = new PPath( new Ellipse2D.Double( -ATOM_DIAMETER / 2, -ATOM_DIAMETER - 3, ATOM_DIAMETER, ATOM_DIAMETER ) ) {{
                setPaint( atom1.getColor() );
            }};

            PPath atom2Node = new PPath( new Ellipse2D.Double( -ATOM_DIAMETER / 2, trackHeight + 3, ATOM_DIAMETER, ATOM_DIAMETER ) ) {{
                setPaint( atom2.getColor() );
            }};

            PPath bondNode = new PPath( new Line2D.Double( 0, -ATOM_DIAMETER, 0, trackHeight + ATOM_DIAMETER ) ) {{
                setStroke( new BasicStroke( 2f ) );
                setStrokePaint( Color.GRAY );
            }};

            addChild( bondNode );
            addChild( atom1Node );
            addChild( atom2Node );
        }
    }
}
