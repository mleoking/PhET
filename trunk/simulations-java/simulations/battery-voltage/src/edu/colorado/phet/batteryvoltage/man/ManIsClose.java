// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.batteryvoltage.man;

import edu.colorado.phet.batteryvoltage.common.electron.man.Man;
import edu.colorado.phet.batteryvoltage.common.electron.man.motions.Location;
import edu.colorado.phet.batteryvoltage.common.phys2d.DoublePoint;

public class ManIsClose implements Condition {
    Man m;
    Location lx;
    double dist;

    public ManIsClose( Man m, Location lx, double dist ) {
        this.dist = dist;
        this.m = m;
        this.lx = lx;
    }

    public boolean isSatisfied() {
        DoublePoint p = m.getNeck().getPosition();
        DoublePoint target = lx.getPosition();
        double d = p.distance( target );
        return d < dist;
    }
}
