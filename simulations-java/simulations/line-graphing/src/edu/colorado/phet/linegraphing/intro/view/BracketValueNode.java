// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.linegraphing.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Bracket with a value next to it.
 * Origin is dependent on direction and sign of length.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BracketValueNode extends PComposite {

    public static enum Direction {LEFT, RIGHT, UP, DOWN} // direction that the open side of the bracket faces

    // bracket
    private static final Stroke BRACKET_STROKE = new BasicStroke( 0.5f );
    private static final Color BRACKET_COLOR = Color.BLACK;
    private static final double BRACKET_END_HEIGHT = 10;
    private static final double BRACKET_CORNER_RADIUS = 0.5 * BRACKET_END_HEIGHT;
    private static final double BRACKET_TIP_WIDTH = 6;
    private static final double BRACKET_TIP_HEIGHT = 6;

    // label
    private static final PhetFont LABEL_FONT = new PhetFont( 12 );
    private static final Color LABEL_COLOR = Color.BLACK;
    private static final NumberFormat LABEL_FORMAT = new DefaultDecimalFormat( "0" );
    private final double MINUS_SIGN_WIDTH = new PhetPText( "-", LABEL_FONT ).getFullBoundsReference().getWidth();
    private static final double LABEL_SPACING = 2;

    // Constructor that uses default look
    public BracketValueNode( Direction direction, double length, double value ) {
        this( direction, length, BRACKET_COLOR, BRACKET_STROKE, value, LABEL_FONT, LABEL_COLOR );
    }

    public BracketValueNode( Direction direction, double length, Color bracketColor, Stroke bracketStroke, double value, PhetFont font, Color textColor ) {

        PNode bracketNode = new BracketNode( Math.abs( length ), bracketColor, bracketStroke );
        addChild( bracketNode );

        PText labelNode = new PText( LABEL_FORMAT.format( value ) );
        labelNode.setFont( font );
        labelNode.setTextPaint( textColor );
        addChild( labelNode );

        // layout
        {
            final double signOffset = ( length < 0 ) ? MINUS_SIGN_WIDTH / 2 : 0;
            if ( direction == Direction.UP ) {
                if ( length > 0 ) {
                    bracketNode.setOffset( 0, 0 );
                }
                else {
                    bracketNode.setOffset( -bracketNode.getFullBoundsReference().getWidth(), 0 );
                }
                // centered below bracket
                labelNode.setOffset( bracketNode.getFullBoundsReference().getCenterX() - ( labelNode.getFullBoundsReference().getWidth() / 2 ) - signOffset,
                                    bracketNode.getFullBoundsReference().getMaxY() + LABEL_SPACING );
            }
            else if ( direction == Direction.DOWN ) {
                bracketNode.setRotation( Math.PI );
                if ( length > 0 ) {
                    bracketNode.setOffset( bracketNode.getFullBoundsReference().getWidth(), 0 );
                }
                // centered above bracket
                labelNode.setOffset( bracketNode.getFullBoundsReference().getCenterX() - ( labelNode.getFullBoundsReference().getWidth() / 2 ) - signOffset,
                                    bracketNode.getFullBoundsReference().getMinY() - labelNode.getFullBoundsReference().getHeight() - LABEL_SPACING );
            }
            else if ( direction == Direction.LEFT ) {
                bracketNode.setRotation( -Math.PI / 2 );
                if ( length < 0 ) {
                    bracketNode.setOffset( 0, bracketNode.getFullBoundsReference().getHeight() );
                }
                // centered to right of bracket
                labelNode.setOffset( bracketNode.getFullBoundsReference().getMaxX() + LABEL_SPACING,
                                     bracketNode.getFullBoundsReference().getCenterY() - ( labelNode.getFullBoundsReference().getHeight() / 2 ) );
            }
            else if ( direction == Direction.RIGHT ) {
                bracketNode.setRotation( Math.PI / 2 );
                if ( length > 0 ) {
                    bracketNode.setOffset( 0, -bracketNode.getFullBoundsReference().getHeight() );
                }
                // centered to left of bracket
                labelNode.setOffset( bracketNode.getFullBoundsReference().getMinX() - labelNode.getFullBoundsReference().getWidth() - LABEL_SPACING,
                                    bracketNode.getFullBoundsReference().getCenterY() - ( labelNode.getFullBoundsReference().getHeight() / 2 ) );
            }
            else {
                throw new UnsupportedOperationException( "unsupported direction: " + direction );
            }
        }
    }

    // Bracket is drawn in the UP direction.
    private static class BracketNode extends PPath {

        public BracketNode( double width, Paint paint, Stroke stroke ) {
            assert ( width > 0 );

            setStroke( stroke );
            setStrokePaint( paint );

            final double bracketTipWidth = Math.min( BRACKET_TIP_WIDTH, 0.5 * width );
            GeneralPath path = new GeneralPath();
            path.moveTo( 0, 0 );
            path.quadTo( 0, (float) BRACKET_END_HEIGHT, (float) BRACKET_CORNER_RADIUS, (float) BRACKET_END_HEIGHT );
            path.lineTo( (float) ( ( width - bracketTipWidth ) / 2 ), (float) BRACKET_END_HEIGHT );
            path.lineTo( (float) ( width / 2 ), (float) ( BRACKET_END_HEIGHT + BRACKET_TIP_HEIGHT ) );
            path.lineTo( (float) ( ( width + bracketTipWidth ) / 2 ), (float) BRACKET_END_HEIGHT );
            path.lineTo( (float) ( width - BRACKET_CORNER_RADIUS ), (float) BRACKET_END_HEIGHT );
            path.quadTo( (float) width, (float)BRACKET_END_HEIGHT, (float) width, 0 );
            setPathTo( path );
        }
    }
}
