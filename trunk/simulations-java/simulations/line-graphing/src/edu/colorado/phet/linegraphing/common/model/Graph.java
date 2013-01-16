// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.model;

import javafx.beans.value.ObservableListValue;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.ObservableList;

/**
 * Model of a simple 2D graph.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Graph {

    public final IntegerRange xRange, yRange; // ranges of the graph's axes
    public final ObservableList<Line> lines; // lines displayed by the graph, in the order that they would be rendered

    public Graph( IntegerRange xRange, IntegerRange yRange ) {
        this.xRange = xRange;
        this.yRange = yRange;
        this.lines = new ObservableList<Line>();
    }

    public int getWidth() {
        return xRange.getLength();
    }

    public int getHeight() {
        return yRange.getLength();
    }

    public boolean contains( Vector2D point ) {
        return xRange.contains( point.getX() ) && yRange.contains( point.getY() );
    }
}
