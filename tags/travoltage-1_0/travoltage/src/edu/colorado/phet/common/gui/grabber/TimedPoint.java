package edu.colorado.phet.common.gui.grabber;

import edu.colorado.phet.common.phys2d.DoublePoint;

public class TimedPoint {
    DoublePoint dp;
    long time;

    public TimedPoint( DoublePoint dp, long time ) {
        this.dp = dp;
        this.time = time;
    }

    public long getAge( long now ) {
        return now - time;
    }

    public DoublePoint getDoublePoint() {
        return dp;
    }
}
