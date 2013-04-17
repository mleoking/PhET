// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.model;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.pointslope.model.PointSlopeParameterRange;

/**
 * Methods for computing ranges of line parameters for slope-intercept form,
 * so that slope and intercept are within the visible range of the graph.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeInterceptParameterRange extends PointSlopeParameterRange {

    // x1 is fixed at 0 for slope-intercept.
    @Override public DoubleRange x1( Line line, Graph graph ) {
        return new DoubleRange( 0, 0 );
    }
}
