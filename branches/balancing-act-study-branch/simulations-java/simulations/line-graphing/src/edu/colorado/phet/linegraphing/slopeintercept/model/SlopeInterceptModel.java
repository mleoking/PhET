// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.model;

import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.pointslope.model.PointSlopeModel;

/**
 * Model for the "Slope-Intercept" tab.
 * This is a specialization of the Point-Slope model.
 * x1 is fixed at zero, so that y1 is synonymous with y-intercept.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeInterceptModel extends PointSlopeModel {

    public SlopeInterceptModel() {
        super( Line.createSlopeIntercept( 2, 3, 1, LGColors.INTERACTIVE_LINE ), new SlopeInterceptParameterRange() );
    }
}