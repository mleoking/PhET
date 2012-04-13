// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;

/**
 * Model of a graph that displays lines.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LineGraph {

    //TODO add more responsibilities to the graph?

    public final int minX, maxX, minY, maxY;

    public LineGraph( IntegerRange xRange, IntegerRange yRange ) {
        this( xRange.getMin(), xRange.getMax(), yRange.getMin(), yRange.getMax() );
    }

    public LineGraph( int minX, int maxX, int minY, int maxY ) {
        assert ( minX < maxX && minY < maxY ); // min is less than max
        assert ( maxX > 0 && minX <= 0 && maxY > 0 && minY <= 0 ); // (0,0) and quadrant 1 is visible
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    public int getWidth() {
        return maxX - minX;
    }

    public int getHeight() {
        return maxY - minY;
    }

    public boolean contains( ImmutableVector2D point ) {
        return ( point.getX() >= minX && point.getX() <= maxX && point.getY() >= minY && point.getY() <= maxX );
    }
}
