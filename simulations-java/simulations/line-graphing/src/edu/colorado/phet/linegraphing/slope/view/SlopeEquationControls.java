// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slope.view;

import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGConstants;
import edu.colorado.phet.linegraphing.common.view.EquationControls;
import edu.colorado.phet.linegraphing.common.view.LineFormsViewProperties;
import edu.colorado.phet.linegraphing.slope.model.SlopeModel;

/**
 * Control panel for interacting with a line's equation in slope form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class SlopeEquationControls extends EquationControls {

    public SlopeEquationControls( SlopeModel model, LineFormsViewProperties viewProperties ) {
        super( SlopeEquationNode.createGeneralFormNode(),
               model.interactiveLine,
               model.savedLines,
               viewProperties.interactiveEquationVisible,
               viewProperties.linesVisible,
               new SlopeEquationNode( model.interactiveLine, model.x1Range, model.y1Range,
                                      LGConstants.INTERACTIVE_EQUATION_FONT, LGConstants.STATIC_EQUATION_FONT,
                                      LGColors.STATIC_EQUATION_ELEMENT ) );
    }
}
