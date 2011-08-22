// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.model.DiatomicMolecule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Displays the bond type, by placing a marker on a continuum whose
 * extremes are "more covalent" and "more ionic".
 * Range is the absolute value of the difference in electronegativity between the 2 atoms,
 * from 0.0 (non-polar covalent) to 3.3 (mostly iconic).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BondTypeNode extends PComposite {

    private static final Dimension TRACK_SIZE = new Dimension( 350, 5 );
    private static final Dimension THUMB_SIZE = new Dimension( 15, 30 );
    private static final Font TITLE_FONT = new PhetFont( 12 );
    private static final Font LABEL_FONT = new PhetFont( 12 );
    private static final double X_INSET = 0;
    private static final double Y_SPACING = 2;

    private static final LinearFunction X_OFFSET_FUNCTION = new LinearFunction( 0, MPConstants.ELECTRONEGATIVITY_RANGE.getLength(), 0, TRACK_SIZE.width );

    public BondTypeNode( DiatomicMolecule molecule ) {

        // the track that represents the continuum
        PNode trackNode = new PPath( new Rectangle2D.Double( 0, 0, TRACK_SIZE.width, TRACK_SIZE.height ) ) {{
            setPaint( Color.BLACK );
        }};

        PText titleNode = new PText( MPStrings.BOND_TYPE ) {{
            setFont( TITLE_FONT );
        }};

        // label at the max end
        PNode maxLabelNode = new PText( MPStrings.MORE_IONIC ) {{
            setFont( LABEL_FONT );
        }};

        // label at the min end
        PNode minLabelNode = new PText( MPStrings.MORE_COVALENT ) {{
            setFont( LABEL_FONT );
        }};

        // thumb that moves along the track, not interactive
        final ArrowNode thumbNode = new ArrowNode( new Point2D.Double( 0, -THUMB_SIZE.height ), new Point2D.Double( 0, 0 ), THUMB_SIZE.width, THUMB_SIZE.width, THUMB_SIZE.width / 3 );
        thumbNode.setPaint( Color.LIGHT_GRAY );

        // rendering order
        addChild( titleNode );
        addChild( maxLabelNode );
        addChild( minLabelNode );
        addChild( trackNode );
        addChild( thumbNode );

        // layout
        trackNode.setOffset( 0, 0 );
        titleNode.setOffset( trackNode.getFullBoundsReference().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 ),
                             trackNode.getFullBoundsReference().getMaxY() + Y_SPACING );
        thumbNode.setOffset( trackNode.getOffset() );
        minLabelNode.setOffset( trackNode.getFullBoundsReference().getMinX() + X_INSET,
                                trackNode.getFullBoundsReference().getMaxY() + Y_SPACING );
        maxLabelNode.setOffset( trackNode.getFullBoundsReference().getMaxX() - maxLabelNode.getFullBoundsReference().getWidth() - X_INSET,
                                trackNode.getFullBoundsReference().getMaxY() + Y_SPACING );

        // when difference in electronegativity changes, move the thumb
        molecule.bond.dipole.addObserver( new VoidFunction1<ImmutableVector2D>() {
            public void apply( ImmutableVector2D dipole ) {
                thumbNode.setOffset( X_OFFSET_FUNCTION.evaluate( dipole.getMagnitude() ), thumbNode.getYOffset() );
            }
        } );
    }
}
