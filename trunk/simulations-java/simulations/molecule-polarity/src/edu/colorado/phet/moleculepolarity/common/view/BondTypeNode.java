// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.colorado.phet.moleculepolarity.MPStrings;
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
    private static final Dimension THUMB_SIZE = new Dimension( 10, 10 );
    private static final Font TITLE_FONT = new PhetFont( 12 );
    private static final Font LABEL_FONT = new PhetFont( 12 );
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color TRACK_COLOR = Color.WHITE;
    private static final Color POINTER_COLOR = Color.WHITE;
    private static final Color POINTER_LINE_COLOR = new Color( 200, 200, 200, 100 ); // translucent gray
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

        // size the track height to fit the title and labels
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
        final PointerNode pointerNode = new PointerNode( trackHeight );

        // rendering order
        addChild( trackNode );
        addChild( titleNode );
        addChild( maxLabelNode );
        addChild( minLabelNode );
        addChild( pointerNode );

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

    // A pair of triangles, positioned above and below the track, connected with a faint line.
    private static class PointerNode extends PComposite {
        public PointerNode( final double trackHeight ) {

            Shape topShape = new DoubleGeneralPath() {{
                moveTo( 0, 0 );
                lineTo( -THUMB_SIZE.width / 2, -THUMB_SIZE.height );
                lineTo( THUMB_SIZE.width / 2, -THUMB_SIZE.height );
                closePath();
            }}.getGeneralPath();
            PPath topNode = new PPath( topShape ) {{
                setPaint( POINTER_COLOR );
            }};
            addChild( topNode );

            Shape bottomShape = new DoubleGeneralPath() {{
                moveTo( 0, trackHeight );
                lineTo( -THUMB_SIZE.width / 2, THUMB_SIZE.height + trackHeight );
                lineTo( THUMB_SIZE.width / 2, THUMB_SIZE.height + trackHeight );
                closePath();
            }}.getGeneralPath();
            PPath bottomNode = new PPath( bottomShape ) {{
                setPaint( POINTER_COLOR );
            }};
            addChild( bottomNode );

            Line2D lineShape = new Line2D.Double( 0, 0, 0, trackHeight );
            PPath lineNode = new PPath( lineShape ) {{
                setStroke( new BasicStroke( 2f ) );
                setStrokePaint( POINTER_LINE_COLOR );
            }};
            addChild( lineNode );
        }
    }
}
