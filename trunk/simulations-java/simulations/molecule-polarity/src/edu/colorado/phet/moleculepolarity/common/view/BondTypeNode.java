// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.model.TwoAtomMolecule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Displays the bond type, by placing a marker on a continuum whose
 * extremes are "more covalent" and "more ionic".
 * Range is absolute value of the difference in electronegativity between the 2 atoms,
 * from 0.0 (non-polar covalent) to 3.3 (mostly iconic)
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BondTypeNode extends PComposite {

    private static final double TRACK_LENGTH = 350;
    private static final double TRACK_HEIGHT = 5;
    private static final Font LABEL_FONT = new PhetFont( 12 );
    private static final double LABEL_Y_SPACING = 4;
    private static final double THUMB_WIDTH = 15;
    private static final double THUMB_HEIGHT = 30;

    private static final LinearFunction X_OFFSET_FUNCTION = new LinearFunction( 0, MPConstants.ELECTRONEGATIVITY_RANGE.getLength(), 0, TRACK_LENGTH );

    public BondTypeNode( TwoAtomMolecule molecule ) {

        PNode trackNode = new PPath( new Rectangle2D.Double( 0, 0, TRACK_LENGTH, TRACK_HEIGHT ) ) {{
            setPaint( Color.BLACK );
        }};
        PNode maxLabelNode = new PText( MPStrings.IONIC ) {{
            setFont( LABEL_FONT );
        }};
        PNode minLabelNode = new PText( MPStrings.COVALENT ) {{
            setFont( LABEL_FONT );
        }};
        final ArrowNode thumbNode = new ArrowNode( new Point2D.Double( 0, -THUMB_HEIGHT ), new Point2D.Double( 0, 0 ), THUMB_WIDTH, THUMB_WIDTH, THUMB_WIDTH / 3 );
        thumbNode.setPaint( Color.LIGHT_GRAY );

        // rendering order
        addChild( maxLabelNode );
        addChild( minLabelNode );
        addChild( trackNode );
        addChild( thumbNode );

        // layout
        minLabelNode.setOffset( trackNode.getFullBoundsReference().getMinX(),
                                trackNode.getFullBoundsReference().getMaxY() + LABEL_Y_SPACING );
        maxLabelNode.setOffset( trackNode.getFullBoundsReference().getMaxX() - maxLabelNode.getFullBoundsReference().getWidth(),
                                trackNode.getFullBoundsReference().getMaxY() + LABEL_Y_SPACING );

        molecule.bond.deltaElectronegativity.addObserver( new VoidFunction1<Double>() {
            public void apply( Double deltaElectronegativity ) {
                thumbNode.setOffset( X_OFFSET_FUNCTION.evaluate( Math.abs( deltaElectronegativity ) ), thumbNode.getYOffset() );
            }
        } );
    }
}
