package electron.man.motions;

import electron.man.Man;
import electron.man.Motion;
import phys2d.DoublePoint;

public class TranslateTo implements Motion {
    double x;
    double y;
    double speed;

    public TranslateTo( double x, double y, double speed ) {
        this.speed = speed;
        this.x = x;
        this.y = y;
    }

    public boolean update( double dt, Man m ) {
        DoublePoint dir = new DoublePoint( x, y ).subtract( m.getNeck().getPosition() ).normalize();
        double dx = dt * dir.getX() * speed;
        double dy = dt * dir.getY() * speed;

        m.getNeck().translate( dx, dy );
        return true;
    }
}
