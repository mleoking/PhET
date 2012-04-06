// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.linegraphing.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.NumberFormat;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.linegraphing.LGColors;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
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
    private static final double BRACKET_END_Y_OFFSET = 3;
    private static final double BRACKET_END_HEIGHT = 15;
    private static final double BRACKET_CORNER_RADIUS = 0.5 * BRACKET_END_HEIGHT;
    private static final double BRACKET_TIP_WIDTH = 6;
    private static final double BRACKET_TIP_HEIGHT = 6;

    // label
    private static final PhetFont LABEL_FONT = new PhetFont( Font.BOLD, 16 );
    private static final Color LABEL_COLOR = Color.BLACK;
    private static final Color LABEL_BACKGROUND_COLOR = ColorUtils.createColor( LGColors.SLOPE_COLOR, 120 );
    private static final NumberFormat LABEL_FORMAT = new DefaultDecimalFormat( "0" );
    private final double MINUS_SIGN_WIDTH = new PhetPText( "-", LABEL_FONT ).getFullBoundsReference().getWidth();
    private static final double LABEL_SPACING = 2;
    private static final double LABEL_BACKGROUND_MARGIN = 3;

    // Constructor that uses default look
    public BracketValueNode( Direction direction, double length, double value ) {
        this( direction, length, BRACKET_COLOR, BRACKET_STROKE, value, LABEL_FONT, LABEL_COLOR, LABEL_BACKGROUND_COLOR );
    }

    public BracketValueNode( Direction direction, double length, Color bracketColor, Stroke bracketStroke, double value, PhetFont font, Color textColor, Color backgroundColor ) {

        PNode bracketNode = new BracketNode( Math.abs( length ), bracketColor, bracketStroke );
        addChild( bracketNode );

        PText labelNode = new PText( LABEL_FORMAT.format( value ) );
        labelNode.setFont( font );
        labelNode.setTextPaint( textColor );
        addChild( labelNode );

        PPath backgroundNode = new PPath( new RoundRectangle2D.Double( 0, 0,
                                                                  labelNode.getFullBoundsReference().getWidth() + ( 2 * LABEL_BACKGROUND_MARGIN ),
                                                                  labelNode.getFullBoundsReference().getHeight() + ( 2 * LABEL_BACKGROUND_MARGIN ),
                                                                  5, 5 ) );
        backgroundNode.setPaint( backgroundColor );
        backgroundNode.setStroke( null );
        addChild( backgroundNode );
        backgroundNode.moveInBackOf( labelNode );

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

            // background behind label
            backgroundNode.setOffset( labelNode.getFullBoundsReference().getMinX() - LABEL_BACKGROUND_MARGIN,
                                      labelNode.getFullBoundsReference().getMinY() - LABEL_BACKGROUND_MARGIN );
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
            path.moveTo( 0, BRACKET_END_Y_OFFSET );
            path.quadTo( 0, (float) BRACKET_END_HEIGHT, (float) BRACKET_CORNER_RADIUS, (float) BRACKET_END_HEIGHT );
            path.lineTo( (float) ( ( width - bracketTipWidth ) / 2 ), (float) BRACKET_END_HEIGHT );
            path.lineTo( (float) ( width / 2 ), (float) ( BRACKET_END_HEIGHT + BRACKET_TIP_HEIGHT ) );
            path.lineTo( (float) ( ( width + bracketTipWidth ) / 2 ), (float) BRACKET_END_HEIGHT );
            path.lineTo( (float) ( width - BRACKET_CORNER_RADIUS ), (float) BRACKET_END_HEIGHT );
            path.quadTo( (float) width, (float)BRACKET_END_HEIGHT, (float) width, BRACKET_END_Y_OFFSET );
            setPathTo( path );
        }
    }

    // Return an icon that represents this feature.
    public static Icon createIcon( double width ) {

        PNode parentNode = new PNode();

        // rise
        PNode riseNode = new BracketValueNode( Direction.LEFT, 40, BRACKET_COLOR, new BasicStroke( 1f ), 2, new PhetFont( Font.BOLD,18 ), LABEL_COLOR, LABEL_BACKGROUND_COLOR ) {
            // WORKAROUND for #558 (image returned by PPath.toImage is clipped on right and bottom edges)
            @Override public PBounds getFullBoundsReference() {
                PBounds b = super.getFullBoundsReference();
                return new PBounds( b.getX(), b.getY(), b.getWidth() + 2, b.getHeight() + 2 );
            }
        };
        parentNode.addChild( riseNode );
        riseNode.setOffset( 0, 0 );

        // run
        PNode runNode = new BracketValueNode( Direction.UP, 60, BRACKET_COLOR, new BasicStroke( 1f ), 3, new PhetFont( Font.BOLD,18 ), LABEL_COLOR, LABEL_BACKGROUND_COLOR ) {
            // WORKAROUND for #558 (image returned by PPath.toImage is clipped on right and bottom edges)
            @Override public PBounds getFullBoundsReference() {
                PBounds b = super.getFullBoundsReference();
                return new PBounds( b.getX(), b.getY(), b.getWidth() + 2, b.getHeight() + 2 );
            }
        };
        parentNode.addChild( runNode );
        runNode.setOffset( riseNode.getFullBoundsReference().getMinX() - runNode.getFullBoundsReference().getWidth() - 2,
                           riseNode.getFullBoundsReference().getMaxY() + 2);

        // line connecting ends of brackets
        PPath lineNode = new PPath( new Line2D.Double( runNode.getFullBoundsReference().getMinX(), runNode.getFullBoundsReference().getMinY(),
                                                       riseNode.getFullBoundsReference().getMinX(), riseNode.getFullBoundsReference().getMinY() ) );
        lineNode.setStroke( new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 6, 6 }, 0 ) );
        parentNode.addChild( lineNode );

        parentNode.scale( width / parentNode.getFullBoundsReference().getWidth() );
        return new ImageIcon( parentNode.toImage() );
    }
}
