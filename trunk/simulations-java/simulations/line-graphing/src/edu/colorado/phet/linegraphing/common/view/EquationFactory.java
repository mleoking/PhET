// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.model.Line;

/**
 * Base class for all factories that create equation nodes for lines.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class EquationFactory {

    // spacing between components of an equation
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
}
