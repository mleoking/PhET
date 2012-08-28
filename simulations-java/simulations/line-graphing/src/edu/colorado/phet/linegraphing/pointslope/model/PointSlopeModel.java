// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.pointslope.model;

import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.model.LineFormsModel;
import edu.colorado.phet.linegraphing.common.model.PointSlopeLine;

/**
 * Model for the "Point-Slope" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PointSlopeModel extends LineFormsModel<PointSlopeLine> {
    public PointSlopeModel() {
        super( new PointSlopeLine( 1, 2, 3, 4, LGColors.INTERACTIVE_LINE ) );
    }
}
