package edu.colorado.phet.batteryvoltage.man;

import edu.colorado.phet.batteryvoltage.common.electron.man.Man;
import edu.colorado.phet.batteryvoltage.common.phys2d.DoublePoint;

public class ReadyToDrop implements Condition {
    Man m;
    double x;
    boolean greater;

    public ReadyToDrop( Man m, double x, boolean greater ) {
        this.m = m;
        this.x = x;
        this.greater = greater;
    }

    public void setState( double x, boolean greater ) {
        this.x = x;
        this.greater = greater;
    }

    public boolean isSatisfied() {
        DoublePoint p = m.getNeck().getPosition();
        double px = p.getX();
        if ( greater && px > x ) {
            return true;
        }
        if ( !greater && px < x ) {
            return true;
        }
        return false;
    }
}
