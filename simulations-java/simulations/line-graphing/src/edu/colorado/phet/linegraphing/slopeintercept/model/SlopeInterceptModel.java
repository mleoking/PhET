// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.model;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.model.StraightLine;
import edu.colorado.phet.linegraphing.pointslope.model.PointSlopeModel;

/**
 * Model for the "Slope-Intercept" module.
 * Note that slope-intercept is a specialization of point-slope form, having x1 fixed at zero.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeInterceptModel extends PointSlopeModel {
    public SlopeInterceptModel() {
        super( new StraightLine( 2, 3, 1, LGColors.INTERACTIVE_LINE ),
               new DoubleRange( 0, 0 ) ); // x1 is fixed at zero
    }
}
