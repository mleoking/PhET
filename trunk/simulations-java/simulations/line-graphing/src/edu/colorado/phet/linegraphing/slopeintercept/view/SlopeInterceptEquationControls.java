// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.view;

import java.text.MessageFormat;

import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGConstants;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.model.LineFormsModel;
import edu.colorado.phet.linegraphing.common.view.EquationControls;
import edu.colorado.phet.linegraphing.common.view.LineFormsViewProperties;

/**
 * Control panel for interacting with a line's equation in slope-intercept form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class SlopeInterceptEquationControls extends EquationControls {

    public SlopeInterceptEquationControls( LineFormsModel model, LineFormsViewProperties viewProperties ) {
        super( MessageFormat.format( "{0} = {1}{2} + {3}", /* y = mx + b */
                                     Strings.SYMBOL_Y, Strings.SYMBOL_SLOPE, Strings.SYMBOL_X, Strings.SYMBOL_INTERCEPT ),
               model.interactiveLine,
               model.savedLines,
               viewProperties.interactiveEquationVisible,
               viewProperties.linesVisible,
               new SlopeInterceptEquationNode( model.interactiveLine, model.riseRange, model.runRange, model.y1Range,
                                               true, true,
                                               LGConstants.INTERACTIVE_EQUATION_FONT, LGConstants.STATIC_EQUATION_FONT,
                                               LGColors.STATIC_EQUATION_ELEMENT ) );
    }
}
