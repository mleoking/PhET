// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.pointslope.view;

import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGConstants;
import edu.colorado.phet.linegraphing.common.view.EquationControls;
import edu.colorado.phet.linegraphing.common.view.LineFormsViewProperties;
import edu.colorado.phet.linegraphing.pointslope.model.PointSlopeModel;

/**
 * Control panel for interacting with a line's equation in point-slope form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class PointSlopeEquationControls extends EquationControls {

    public PointSlopeEquationControls( PointSlopeModel model, LineFormsViewProperties viewProperties ) {
        super( PointSlopeEquationNode.createGeneralFormNode(),
               model.interactiveLine,
               model.savedLines,
               viewProperties.interactiveEquationVisible,
               viewProperties.linesVisible,
               new PointSlopeEquationNode( model.interactiveLine, model.x1Range, model.y1Range, model.riseRange, model.runRange,
                                           true, true, true,
                                           LGConstants.INTERACTIVE_EQUATION_FONT, LGConstants.STATIC_EQUATION_FONT,
                                           LGColors.STATIC_EQUATION_ELEMENT ) );
    }
}
