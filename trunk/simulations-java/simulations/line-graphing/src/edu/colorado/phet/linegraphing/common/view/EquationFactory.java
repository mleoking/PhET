// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.model.LineFactory;
import edu.colorado.phet.linegraphing.common.model.PointSlopeLine;

/**
 * Base class for all factories that create equation nodes for lines.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class EquationFactory<T extends PointSlopeLine> {

    // spacing between components of an equation
    protected static final double X_SPACING = 3;
    protected static final double Y_SPACING = 0;

    // Subclasses implement this to create the equation in the correct form.
    public abstract EquationNode createNode( T line, PhetFont font );

    // Convenience method that simplifies the line before creating the equation.
    public EquationNode createSimplifiedNode( T line, LineFactory<T> lineFactory, PhetFont font ) {
        return createNode( lineFactory.simplify( line ), font );
    }

    // When slope is undefined, we display "undefined" in place of an equation.
    protected static class UndefinedSlopeNode extends EquationNode {
        public UndefinedSlopeNode( PointSlopeLine line, PhetFont font ) {
            assert( line.run == 0 );
            setPickable( false );
            addChild( new PhetPText( Strings.SLOPE_UNDEFINED, font, line.color ) );
        }
    }

    // Converts a double to an integer string, using nearest-neighbor rounding.
    protected static String toIntString( double d ) {
        return String.valueOf( MathUtil.roundHalfUp( d ) );
    }
}
