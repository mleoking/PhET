// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.efield.phys2d_efield.propagators;

import edu.colorado.phet.efield.phys2d_efield.DoublePoint;

// Referenced classes of package phys2d.propagators:
//            BoundsBounce

public class SouthBounce extends BoundsBounce {

    public SouthBounce( double d, double d1 ) {
        yMax = d;
        distFromWall = d1;
    }

    public boolean isOutOfBounds( DoublePoint doublepoint ) {
        return doublepoint.getY() > yMax;
    }

    public DoublePoint getNewVelocity( DoublePoint doublepoint ) {
        double d = -Math.abs( doublepoint.getY() );
        return new DoublePoint( doublepoint.getX(), d );
    }

    public DoublePoint getPointAtBounds( DoublePoint doublepoint ) {
        return new DoublePoint( doublepoint.getX(), yMax - distFromWall );
    }

    double yMax;
    double distFromWall;
}
