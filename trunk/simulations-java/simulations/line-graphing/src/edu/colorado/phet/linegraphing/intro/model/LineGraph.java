// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.model;

/**
 * Model of a graph that displays lines.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LineGraph {

    //TODO add more responsibilities to the graph?

    public final int minX, maxX, minY, maxY;

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
}
