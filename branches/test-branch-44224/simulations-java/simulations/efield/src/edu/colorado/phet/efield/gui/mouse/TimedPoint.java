// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.efield.gui.mouse;

import edu.colorado.phet.efield.phys2d_efield.DoublePoint;

public class TimedPoint {

    public TimedPoint( DoublePoint doublepoint, long l ) {
        dp = doublepoint;
        time = l;
    }

    public long getAge( long l ) {
        return l - time;
    }

    public DoublePoint getDoublePoint() {
        return dp;
    }

    DoublePoint dp;
    long time;
}
