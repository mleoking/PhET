package edu.colorado.phet.electron.man.motions;

import edu.colorado.phet.electron.man.Man;
import edu.colorado.phet.electron.man.Motion;

public class Translate implements Motion {
    double vx;
    double vy;

    public Translate( double vx, double vy ) {
        this.vx = vx;
        this.vy = vy;
    }

    public void setVelocity( double vx, double vy ) {
        this.vx = vx;
        this.vy = vy;
    }

    public boolean update( double dt, Man m ) {
        m.getNeck().translate( vx * dt, vy * dt );
        return true;
    }
}
