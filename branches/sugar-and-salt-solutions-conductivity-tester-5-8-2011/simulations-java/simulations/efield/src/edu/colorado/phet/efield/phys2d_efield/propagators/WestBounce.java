// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.efield.phys2d_efield.propagators;

import edu.colorado.phet.efield.phys2d_efield.DoublePoint;

// Referenced classes of package phys2d.propagators:
//            BoundsBounce

public class WestBounce extends BoundsBounce {

    public WestBounce( double d, double d1 ) {
        xMin = d;
        distFromWall = d1;
    }

    public boolean isOutOfBounds( DoublePoint doublepoint ) {
        return doublepoint.getX() < xMin;
    }

    public DoublePoint getNewVelocity( DoublePoint doublepoint ) {
        double d = Math.abs( doublepoint.getX() );
        return new DoublePoint( d, doublepoint.getY() );
    }

    public DoublePoint getPointAtBounds( DoublePoint doublepoint ) {
        return new DoublePoint( xMin + distFromWall, doublepoint.getY() );
    }

    double xMin;
    double distFromWall;
}
