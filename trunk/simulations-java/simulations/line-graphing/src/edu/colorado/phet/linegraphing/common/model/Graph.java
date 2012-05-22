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

    public final IntegerRange xRange, yRange;

    public Graph( IntegerRange xRange, IntegerRange yRange ) {
        this.xRange = xRange;
        this.yRange = yRange;
    }

    public int getWidth() {
        return xRange.getLength();
    }

    public int getHeight() {
        return yRange.getLength();
    }

    public boolean contains( ImmutableVector2D point ) {
        return xRange.contains( point.getX() ) && yRange.contains( point.getY() );
    }
}
