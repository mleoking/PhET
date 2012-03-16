// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.efield.phys2d_efield.propagators;

import edu.colorado.phet.efield.phys2d_efield.DoublePoint;

// Referenced classes of package phys2d.propagators:
//            BoundsBounce

public class EastBounce extends BoundsBounce {

    public EastBounce( double d, double d1 ) {
        xMax = d;
        distFromWall = d1;
    }

    public boolean isOutOfBounds( DoublePoint doublepoint ) {
        return doublepoint.getX() > xMax;
    }

    public DoublePoint getPointAtBounds( DoublePoint doublepoint ) {
        return new DoublePoint( xMax, doublepoint.getY() );
    }

    public DoublePoint getNewVelocity( DoublePoint doublepoint ) {
        double d = -Math.abs( doublepoint.getX() );
        return new DoublePoint( d - distFromWall, doublepoint.getY() );
    }

    double xMax;
    double distFromWall;
}
