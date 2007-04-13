package edu.colorado.phet.bernoulli.spline;

/**
 * User: Sam Reid
 * Date: Aug 23, 2003
 * Time: 1:33:12 AM
 * Copyright (c) Aug 23, 2003 by Sam Reid
 */
public class SplineLocation {
    int segment;
    double offset;

    public int getSegment() {
        return segment;
    }

    public double getOffset() {
        return offset;
    }

    public SplineLocation( int segment, double offset ) {
        this.segment = segment;
        this.offset = offset;
    }
}
