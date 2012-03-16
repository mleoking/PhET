// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.efield.phys2d_efield.propagators;

import edu.colorado.phet.efield.phys2d_efield.DoublePoint;

// Referenced classes of package phys2d.propagators:
//            BoundsBounce

public class NorthBounce extends BoundsBounce {

    public NorthBounce( double d, double d1 ) {
        distFromWall = d1;
        yMin = d;
    }

    public boolean isOutOfBounds( DoublePoint doublepoint ) {
        return doublepoint.getY() < yMin;
    }

    public DoublePoint getPointAtBounds( DoublePoint doublepoint ) {
        return new DoublePoint( doublepoint.getX(), yMin + distFromWall );
    }

    public DoublePoint getNewVelocity( DoublePoint doublepoint ) {
        double d = Math.abs( doublepoint.getY() );
        DoublePoint doublepoint1 = new DoublePoint( doublepoint.getX(), d );
        return doublepoint1;
    }

    double yMin;
    double distFromWall;
}
