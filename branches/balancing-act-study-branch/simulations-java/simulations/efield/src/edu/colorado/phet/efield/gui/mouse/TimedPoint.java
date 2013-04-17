// Copyright 2002-2011, University of Colorado

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
