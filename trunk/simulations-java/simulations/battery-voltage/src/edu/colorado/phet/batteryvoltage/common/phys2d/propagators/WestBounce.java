// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.batteryvoltage.common.phys2d.propagators;

import edu.colorado.phet.batteryvoltage.common.phys2d.DoublePoint;

public class WestBounce extends BoundsBounce {
    double xMin;
    double distFromWall;

    public WestBounce( double xMin, double distFromWall ) {
        this.xMin = xMin;
        this.distFromWall = distFromWall;
    }

    public boolean isOutOfBounds( DoublePoint x ) {
        return x.getX() < xMin;
    }

    public DoublePoint getNewVelocity( DoublePoint v ) {
        double x = Math.abs( v.getX() );
        return new DoublePoint( x, v.getY() );
    }

    public DoublePoint getPointAtBounds( DoublePoint x ) {
        return new DoublePoint( xMin + distFromWall, x.getY() );
    }
}
