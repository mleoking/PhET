// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.batteryvoltage.common.phys2d.propagators;

import edu.colorado.phet.batteryvoltage.common.phys2d.DoublePoint;

public class EastBounce extends BoundsBounce {
    double xMax;
    double distFromWall;

    public EastBounce( double xMax, double distFromWall ) {
        this.xMax = xMax;
        this.distFromWall = distFromWall;
    }

    public boolean isOutOfBounds( DoublePoint x ) {
        return x.getX() > xMax;
    }

    public DoublePoint getPointAtBounds( DoublePoint x ) {
        return new DoublePoint( xMax, x.getY() );
    }

    public DoublePoint getNewVelocity( DoublePoint v ) {
        double x = -Math.abs( v.getX() );
        return new DoublePoint( x - distFromWall, v.getY() );
    }
}
