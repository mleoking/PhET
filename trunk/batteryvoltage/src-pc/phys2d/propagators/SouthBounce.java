package phys2d.propagators;

import phys2d.DoublePoint;

public class SouthBounce extends BoundsBounce {
    double yMax;
    double distFromWall;

    public SouthBounce( double yMax, double distFromWall ) {
        this.yMax = yMax;
        this.distFromWall = distFromWall;
    }

    public boolean isOutOfBounds( DoublePoint x ) {
        return x.getY() > yMax;
    }

    public DoublePoint getNewVelocity( DoublePoint v ) {
        double y = -Math.abs( v.getY() );
        return new DoublePoint( v.getX(), y );
    }

    public DoublePoint getPointAtBounds( DoublePoint x ) {
        return new DoublePoint( x.getX(), yMax - distFromWall );
    }
}
