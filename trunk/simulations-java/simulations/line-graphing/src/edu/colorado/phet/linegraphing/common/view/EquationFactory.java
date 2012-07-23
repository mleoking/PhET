// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.model.StraightLine;

/**
 * Base class for all factories that create equation nodes for lines.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class EquationFactory {

    protected static final double X_SPACING = 3;
    protected static final double Y_SPACING = 0;

    public abstract EquationNode createNode( StraightLine line, PhetFont font );

    public EquationNode createSimplifiedNode( StraightLine line, PhetFont font ) {
        return createNode( line.simplified(), font );
    }

    /*
     * Slope is undefined.
     */
    protected static class UndefinedSlopeNode extends EquationNode {
        public UndefinedSlopeNode( StraightLine line, PhetFont font ) {
            setPickable( false );
            addChild( new PhetPText( Strings.SLOPE_UNDEFINED, font, line.color ) );
        }
    }

    // Converts a double to an integer string, using nearest-neighbor rounding.
    protected static String toIntString( double d ) {
        return String.valueOf( MathUtil.roundHalfUp( d ) );
    }
}
