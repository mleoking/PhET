package phys2d.propagators;

import phys2d.DoublePoint;

public class NorthBounce extends BoundsBounce {
    double yMin;
    double distFromWall;

    public NorthBounce( double yMin, double distFromWall ) {
        this.distFromWall = distFromWall;
        this.yMin = yMin;
    }

    public boolean isOutOfBounds( DoublePoint x ) {
        //util.Debug.traceln("Position="+x);
        return x.getY() < yMin;
    }

    public DoublePoint getPointAtBounds( DoublePoint x ) {
        return new DoublePoint( x.getX(), yMin + distFromWall );
    }

    public DoublePoint getNewVelocity( DoublePoint v ) {
        double y = Math.abs( v.getY() );
        DoublePoint a = new DoublePoint( v.getX(), y );
        //util.Debug.traceln("New velocity="+a+"\n");
        return a;
    }
}
