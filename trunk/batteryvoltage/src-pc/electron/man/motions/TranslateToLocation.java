package electron.man.motions;

import electron.man.Man;
import electron.man.Motion;
import phys2d.DoublePoint;

public class TranslateToLocation implements Motion {
    Location loc;
    double speed;

    public TranslateToLocation( Location loc, double speed ) {
        this.speed = speed;
        this.loc = loc;
    }

    public void setLocation( Location loc ) {
        this.loc = loc;
    }

    public boolean update( double dt, Man m ) {
        DoublePoint x = loc.getPosition();
        try {
            DoublePoint dir = x.subtract( m.getNeck().getPosition() ).normalize();
            double dx = dt * dir.getX() * speed;
            double dy = dt * dir.getY() * speed;

            m.getNeck().translate( dx, dy );
            return true;
        }
        catch( Exception e ) {
            return true;
        }//got too close.
    }
}
