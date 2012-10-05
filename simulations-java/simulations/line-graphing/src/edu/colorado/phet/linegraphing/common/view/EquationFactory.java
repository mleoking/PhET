// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.linegraphing.common.model.Line;

/**
 * Base class for all factories that create equation nodes for lines.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class EquationFactory {

    // Subclasses implement this to create the equation in the correct form.
    public abstract EquationNode createNode( Line line, PhetFont font, Color color );

    // Convenience method that simplifies the line before creating the equation.
    public EquationNode createSimplifiedNode( Line line, PhetFont font, Color color ) {
        return createNode( line.simplified(), font, color );
    }
}
