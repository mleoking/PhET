// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.view;

import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGConstants;
import edu.colorado.phet.linegraphing.common.view.EquationControls;
import edu.colorado.phet.linegraphing.common.view.LineFormsViewProperties;
import edu.colorado.phet.linegraphing.slopeintercept.model.SlopeInterceptModel;

/**
 * Control panel for interacting with a line's equation in slope-intercept form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class SlopeInterceptEquationControls extends EquationControls {

    public SlopeInterceptEquationControls( SlopeInterceptModel model, LineFormsViewProperties viewProperties ) {
        super( SlopeInterceptEquationNode.createGeneralFormNode(),
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
