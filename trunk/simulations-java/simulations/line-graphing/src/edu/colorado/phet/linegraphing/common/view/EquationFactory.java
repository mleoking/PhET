// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Base class for all factories that create equation nodes for lines.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class EquationFactory {

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
    protected static final Dimension OPERATOR_LINE_SIZE = new Dimension( 10, 2 );

    // size of the lines used to create + and - signs
    protected static final Dimension SIGN_LINE_SIZE = new Dimension( 10, 2 );

    // thickness of the fraction divisor line
    protected static final int FRACTION_LINE_THICKNESS = 2;

    // spacing between components of an equation (set by visual inspection)
    protected static final double X_SPACING = 3;
    protected static final double Y_SPACING = 0;

    // Subclasses implement this to create the equation in the correct form.
    public abstract EquationNode createNode( Line line, PhetFont font );

    // Convenience method that simplifies the line before creating the equation.
    public EquationNode createSimplifiedNode( Line line, PhetFont font ) {
        return createNode( line.simplified(), font );
    }

    // When slope is undefined, we display "undefined" in place of an equation.
    protected static class UndefinedSlopeNode extends EquationNode {
        public UndefinedSlopeNode( Line line, PhetFont font ) {
            assert ( line.run == 0 );
            setPickable( false );
            addChild( new PhetPText( MessageFormat.format( Strings.SLOPE_UNDEFINED, Strings.SYMBOL_X, line.x1 ), font, line.color ) );
        }
    }

    // Converts a double to an integer string, using nearest-neighbor rounding.
    protected static String toIntString( double d ) {
        return String.valueOf( MathUtil.roundHalfUp( d ) );
    }


    // Creates a plus or minus sign. Sign is distinct from operator, so that they can have different looks.
    protected static PNode createSignNode( double value, Color color ) {
        if ( value >= 0 ) {
            return new PlusNode( SIGN_LINE_SIZE.width, SIGN_LINE_SIZE.height, color );
        }
        else {
            return new MinusNode( SIGN_LINE_SIZE.width, SIGN_LINE_SIZE.height, color );
        }
    }

    // Creates a plus or minus operator. Operator is distinct from sign, so that they can have different looks.
    protected static PNode createOperatorNode( double value, Color color ) {
        if ( value >= 0 ) {
            return new PlusNode( OPERATOR_LINE_SIZE.width, OPERATOR_LINE_SIZE.height, color );
        }
        else {
            return new MinusNode( OPERATOR_LINE_SIZE.width, OPERATOR_LINE_SIZE.height, color );
        }
    }

    // Creates the horizontal line that separates numerator and denominator in a fraction (slope, in our case.)
    protected static PNode createFractionLineNode( double width, Color color ) {
        PPath node = new PPath( new Rectangle2D.Double( 0, 0, width, FRACTION_LINE_THICKNESS ) );
        node.setStroke( null );
        node.setPaint( color );
        return node;
    }
}
