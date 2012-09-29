// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.pointslope.view;

import java.text.MessageFormat;

import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGConstants;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.model.LineFormsModel;
import edu.colorado.phet.linegraphing.common.view.EquationControls;
import edu.colorado.phet.linegraphing.common.view.LineFormsViewProperties;

/**
 * Control panel for interacting with a line's equation in point-slope form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class PointSlopeEquationControls extends EquationControls {

    public PointSlopeEquationControls( LineFormsModel model, LineFormsViewProperties viewProperties ) {
        super( MessageFormat.format( "({0} - {1}) = {2}({3} - {4})", /* (y - y1) = m(x - x1) */
                                     Strings.SYMBOL_Y, Strings.SYMBOL_Y1, Strings.SYMBOL_SLOPE, Strings.SYMBOL_X, Strings.SYMBOL_X1 ),
               model.interactiveLine,
               model.savedLines,
               viewProperties.interactiveEquationVisible,
               viewProperties.linesVisible,
               new PointSlopeInteractiveEquationNode( model.interactiveLine, model.x1Range, model.y1Range, model.riseRange, model.runRange,
                                                      true, true, true,
                                                      LGConstants.INTERACTIVE_EQUATION_FONT, LGConstants.STATIC_EQUATION_FONT,
                                                      LGColors.STATIC_EQUATION_ELEMENT ) );
    }
}
