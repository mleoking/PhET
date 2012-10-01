// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.model;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;

/**
 * Methods for computing ranges of line parameters, so that the points
 * that define the line are within the visible range of the graph.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LineParameterRange {

    // Range for the x component of the point (x1,y1)
    public static DoubleRange x1( Line line, Graph graph ) {
        final double x1Min = Math.max( graph.xRange.getMin(), graph.xRange.getMin() - line.run );
        final double x1Max = Math.min( graph.xRange.getMax(), graph.xRange.getMax() - line.run );
        return new DoubleRange( x1Min, x1Max );
    }

    // Range for the y component of the point (x1,y1)
    public static DoubleRange y1( Line line, Graph graph ) {
        final double y1Min = Math.max( graph.yRange.getMin(), graph.yRange.getMin() - line.rise );
        final double y1Max = Math.min( graph.yRange.getMax(), graph.yRange.getMax() - line.rise );
        return new DoubleRange( y1Min, y1Max );
    }

    // Range for the vertical component of the slope
    public static DoubleRange rise( Line line, Graph graph ) {
        final double riseMin = graph.yRange.getMin() - line.y1;
        final double riseMax = graph.yRange.getMax() - line.y1;
        return new DoubleRange( riseMin, riseMax );
    }

    // Range for the horizontal component of the slope
    public static DoubleRange run( Line line, Graph graph ) {
        final double runMin = graph.xRange.getMin() - line.x1;
        final double runMax = graph.xRange.getMax() - line.x1;
        return new DoubleRange( runMin, runMax );
    }
}
