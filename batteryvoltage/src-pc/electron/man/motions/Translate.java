package electron.man.motions;

import electron.man.Man;
import electron.man.Motion;

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
