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

    /*
    * This controls the vertical offset of the slope's sign.
    * Zero is vertically centered on the equals sign, positive values move it down, negative move it up.
    * This was created because there was a great deal of discussion and disagreement about where the sign should be placed.
    */
    protected final double slopeSignYOffset;

    // fudge factors for horizontal lines, to vertically center them with equals sign (set by visual inspection)
    protected final double slopeSignYFudgeFactor;
    protected final double operatorYFudgeFactor;
    protected final double fractionLineYFudgeFactor;

    protected final PDimension operatorLineSize;  // size of the lines used to create + and - operators
    protected final PDimension signLineSize; // size of the lines used to create + and - signs
    protected final double fractionLineThickness; // thickness of the fraction divisor line

    // spacing between components of an equation (set by visual inspection)
    protected final double integerSignXSpacing; // spacing between a sign and the integer to the right of it
    protected final double fractionSignXSpacing; // spacing between a sign and the fraction to the right of it
    protected final double slopeXSpacing; // spacing between the slope and what's to the right of it
    protected final double operatorXSpacing; // space around an operator (eg, +)
    protected final double relationalOperatorXSpacing; // space around the relational operator (eg, =)
    protected final double parenXSpacing; // space between a parenthesis and the thing it encloses
    protected final double ySpacing; // all y spacing

    public EquationNode( int pointSize ) {
        setPickable( false );

        // Compute dimensions and layout offsets as percentages of the font's point size.
        slopeSignYOffset = 0;
        slopeSignYFudgeFactor = 0.08 * pointSize;
        operatorYFudgeFactor = 0.08 * pointSize;
        fractionLineYFudgeFactor = 0.08 * pointSize;
        operatorLineSize = new PDimension( 0.42 * pointSize, 0.08 * pointSize );
        signLineSize = new PDimension( 0.54 * pointSize, 0.1 * pointSize );
        fractionLineThickness = 0.06 * pointSize;
        integerSignXSpacing = 0.0625 * pointSize;
        fractionSignXSpacing = 0.36 * pointSize;
        slopeXSpacing = 0.15 * pointSize; //TODO have separate spacing for fraction vs integer slope, this is a bit big for integer slopes
        operatorXSpacing = 0.25 * pointSize;
        relationalOperatorXSpacing = 0.35 * pointSize;
        parenXSpacing = 0.08 * pointSize;
        ySpacing = 0.1 * pointSize;
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
                child.setPaint( paint );
            }
            setPaintDeep( child, paint );
        }
    }

    // Creates a plus or minus sign. Sign is distinct from operator, so that they can have different looks.
    protected PNode createSignNode( double value, Color color ) {
        if ( value >= 0 ) {
            return new PlusNode( signLineSize, color );
        }
        else {
            return new MinusNode( signLineSize, color );
        }
    }

    // Creates a plus or minus operator. Operator is distinct from sign, so that they can have different looks.
    protected PNode createOperatorNode( double value, Color color ) {
        if ( value >= 0 ) {
            return new PlusNode( operatorLineSize, color );
        }
        else {
            return new MinusNode( operatorLineSize, color );
        }
    }

    // Creates the horizontal line that separates numerator and denominator in a fraction (slope, in our case.)
    protected PNode createFractionLineNode( double width, Color color ) {
        PPath node = new PPath( new Rectangle2D.Double( 0, 0, width, fractionLineThickness ) );
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
            xNode.setOffset( leftParenNode.getFullBoundsReference().getMaxX() + parenXSpacing, leftParenNode.getYOffset() );
            operatorNode.setOffset( xNode.getFullBoundsReference().getMaxX() + operatorXSpacing,
                                    leftParenNode.getFullBoundsReference().getCenterY() - ( operatorNode.getFullBoundsReference().getHeight() / 2 ) + operatorYFudgeFactor );
            x1Node.setOffset( operatorNode.getFullBoundsReference().getMaxX() + operatorXSpacing, leftParenNode.getYOffset() );
            rightParenNode.setOffset( x1Node.getFullBoundsReference().getMaxX() + parenXSpacing, leftParenNode.getYOffset() );

            return parentNode;
        }
    }

    // Converts a double to an integer string, using nearest-neighbor rounding.
    protected static String toIntString( double d ) {
        return String.valueOf( MathUtil.roundHalfUp( d ) );
    }
}
