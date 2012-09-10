// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.pointslope.model;

import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.model.LineFormsModel;

/**
 * Model for the "Point-Slope" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PointSlopeModel extends LineFormsModel {
    public PointSlopeModel() {
        super( Line.createPointSlope( 1, 2, 3, 4, LGColors.INTERACTIVE_LINE ) );
    }
}
