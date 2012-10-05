// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.Color;
import java.text.MessageFormat;

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

    // Subclasses implement this to create the equation in the correct form.
    public abstract StaticEquationNode createNode( Line line, PhetFont font, Color color );

    // Convenience method that simplifies the line before creating the equation.
    public StaticEquationNode createSimplifiedNode( Line line, PhetFont font, Color color ) {
        return createNode( line.simplified(), font, color );
    }

    // When slope is undefined, we display "undefined" in place of an equation.
    protected static class UndefinedSlopeNode extends StaticEquationNode {
        public UndefinedSlopeNode( Line line, PhetFont font, Color color ) {
            super( font.getSize() );
            assert ( !line.isSlopeDefined() );
            setPickable( false );
            addChild( new PhetPText( MessageFormat.format( Strings.SLOPE_UNDEFINED, Strings.SYMBOL_X, line.x1 ), font, color ) );
        }
    }
}
