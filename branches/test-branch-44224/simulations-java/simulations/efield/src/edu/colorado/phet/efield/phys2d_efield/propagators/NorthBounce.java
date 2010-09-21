// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

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
