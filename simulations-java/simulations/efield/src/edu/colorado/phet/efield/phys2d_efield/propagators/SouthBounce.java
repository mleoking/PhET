// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

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
