// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;

/**
 * Model of a simple 2D graph.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Graph {

    public final int minX, maxX, minY, maxY;

    public Graph( IntegerRange xRange, IntegerRange yRange ) {
        this( xRange.getMin(), xRange.getMax(), yRange.getMin(), yRange.getMax() );
    }

    public Graph( int minX, int maxX, int minY, int maxY ) {
        assert ( minX < maxX && minY < maxY ); // min is less than max
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
