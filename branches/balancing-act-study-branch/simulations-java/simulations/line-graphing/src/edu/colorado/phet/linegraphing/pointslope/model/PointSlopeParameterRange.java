// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.pointslope.model;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;

/**
 * Methods for computing ranges of line parameters for point-slope form,
 * so that point and slope are within the visible range of the graph.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PointSlopeParameterRange {

    // Range for the x component of the point (x1,y1)
    public DoubleRange x1( Line line, Graph graph ) {
        final double min = Math.max( graph.xRange.getMin(), graph.xRange.getMin() - line.run );
        final double max = Math.min( graph.xRange.getMax(), graph.xRange.getMax() - line.run );
        return new DoubleRange( min, max );
    }

    // Range for the y component of the point (x1,y1)
    public DoubleRange y1( Line line, Graph graph ) {
        final double min = Math.max( graph.yRange.getMin(), graph.yRange.getMin() - line.rise );
        final double max = Math.min( graph.yRange.getMax(), graph.yRange.getMax() - line.rise );
        return new DoubleRange( min, max );
    }

    // Range for the vertical component of the slope
    public DoubleRange rise( Line line, Graph graph ) {
        final double min = graph.yRange.getMin() - line.y1;
        final double max = graph.yRange.getMax() - line.y1;
        return new DoubleRange( min, max );
    }

    // Range for the horizontal component of the slope
    public DoubleRange run( Line line, Graph graph ) {
        final double min = graph.xRange.getMin() - line.x1;
        final double max = graph.xRange.getMax() - line.x1;
        return new DoubleRange( min, max );
    }
}
