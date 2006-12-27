package electron.paint.animate;

import phys2d.DoublePoint;
import phys2d.Particle;

import java.awt.*;

public class ParticlePoint implements PointSource {
    Particle p;

    public ParticlePoint( Particle p ) {
        this.p = p;
    }

    public Point getPoint() {
        DoublePoint dp = p.getPosition();
        int x = (int)dp.getX();
        int y = (int)dp.getY();
        return new Point( x, y );
    }
}
