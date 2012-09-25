// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Base class for all equation nodes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class EquationNode extends PhetPNode {

    //TODO all offsets and dimensions should be computed based on the font's point size.
    /*
    * This controls the vertical offset of the slope's sign.
    * Zero is vertically centered on the equals sign, positive values move it down, negative move it up.
    * This was created because there was a great deal of discussion and disagreement about where the sign should be placed.
    */
    protected static final int SLOPE_SIGN_Y_OFFSET = 0;

    // fudge factors for horizontal lines, to vertically center them with equals sign (set by visual inspection)
    protected static final int SLOPE_SIGN_Y_FUDGE_FACTOR = 2;
    protected static final int OPERATOR_Y_FUDGE_FACTOR = 2;
    protected static final int FRACTION_LINE_Y_FUDGE_FACTOR = 2;

    // size of the lines used to create + and - operators
    protected static final PDimension OPERATOR_LINE_SIZE = new PDimension( 10, 2 );

    // size of the lines used to create + and - signs
    protected static final PDimension SIGN_LINE_SIZE = new PDimension( 10, 2 );

    // thickness of the fraction divisor line
    protected static final int FRACTION_LINE_THICKNESS = 2;

    // spacing between components of an equation (set by visual inspection)
    protected static final double X_SPACING = 3;
    protected static final double INTEGER_SIGN_X_SPACING = 1.5; // spacing between a sign and the integer to the right of it
    protected static final double FRACTION_SIGN_X_SPACING = 4; // spacing between a sign and the fraction to the right of it
    protected static final double RELATIONAL_OPERATOR_X_SPACING = 6; // space around the relational operator (eg, =)
    protected static final double PAREN_X_SPACING = 2; // space between a parenthesis and the thing it encloses
    protected static final double Y_SPACING = 0;

    public EquationNode() {
        setPickable( false );
    }

    // Changes the color of the equation by doing a deep traversal of this node's descendants.
    public void setPaintDeep( Paint paint ) {
        setPaintDeep( this, paint );
    }

    public static void setPaintDeep( PNode node, Paint paint ) {
        for ( int i = 0; i < node.getChildrenCount(); i++ ) {
            PNode child = node.getChild( i );
            if ( child instanceof PText ) {
                ( (PText) child ).setTextPaint( paint );
            }
            else if ( child instanceof PPath ) {
                ( (PPath) child ).setPaint( paint );
            }
            setPaintDeep( child, paint );
        }
    }

    // Creates a plus or minus sign. Sign is distinct from operator, so that they can have different looks.
    protected PNode createSignNode( double value, Color color ) {
        if ( value >= 0 ) {
            return new PlusNode( SIGN_LINE_SIZE, color );
        }
        else {
            return new MinusNode( SIGN_LINE_SIZE, color );
        }
    }

    // Creates a plus or minus operator. Operator is distinct from sign, so that they can have different looks.
    protected PNode createOperatorNode( double value, Color color ) {
        if ( value >= 0 ) {
            return new PlusNode( OPERATOR_LINE_SIZE, color );
        }
        else {
            return new MinusNode( OPERATOR_LINE_SIZE, color );
        }
    }

    // Creates the horizontal line that separates numerator and denominator in a fraction (slope, in our case.)
    protected PNode createFractionLineNode( double width, Color color ) {
        PPath node = new PPath( new Rectangle2D.Double( 0, 0, width, FRACTION_LINE_THICKNESS ) );
        node.setStroke( null );
        node.setPaint( color );
        return node;
    }

    //TODO This should probably be in a subclass, since it's specific to point-slope form.
    // Creates the portion of a point-slope equation that contains the x or y term.
    protected PNode createTermNode( double value, String symbol, Font font, Color color ) {
        if ( value == 0 ) {
            // x or y
            return new PhetPText( symbol, font, color );
        }
        else {
            // (x-x1) or (y-y1)
            PNode leftParenNode = new PhetPText( "(", font, color );
            PNode xNode = new PhetPText( symbol, font, color );
            PNode operatorNode = createOperatorNode( -value, color ); // flip sign on x1
            PNode x1Node = new PhetPText( toIntString( Math.abs( value ) ), font, color );
            PNode rightParenNode = new PhetPText( ")", font, color );

            PNode parentNode = new PNode();
            parentNode.addChild( leftParenNode );
            parentNode.addChild( xNode );
            parentNode.addChild( operatorNode );
            parentNode.addChild( x1Node );
            parentNode.addChild( rightParenNode );

            // layout
            leftParenNode.setOffset( 0, 0 );
            xNode.setOffset( leftParenNode.getFullBoundsReference().getMaxX() + PAREN_X_SPACING, leftParenNode.getYOffset() );
            operatorNode.setOffset( xNode.getFullBoundsReference().getMaxX() + X_SPACING,
                                    leftParenNode.getFullBoundsReference().getCenterY() - ( operatorNode.getFullBoundsReference().getHeight() / 2 ) + OPERATOR_Y_FUDGE_FACTOR );
            x1Node.setOffset( operatorNode.getFullBoundsReference().getMaxX() + X_SPACING, leftParenNode.getYOffset() );
            rightParenNode.setOffset( x1Node.getFullBoundsReference().getMaxX() + PAREN_X_SPACING, leftParenNode.getYOffset() );

            return parentNode;
        }
    }

    // Converts a double to an integer string, using nearest-neighbor rounding.
    protected static String toIntString( double d ) {
        return String.valueOf( MathUtil.roundHalfUp( d ) );
    }
}
