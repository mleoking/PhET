package edu.colorado.phet.batteryvoltage.common.electron.paint.animate;

import java.awt.*;

import edu.colorado.phet.batteryvoltage.common.phys2d.DoublePoint;
import edu.colorado.phet.batteryvoltage.common.phys2d.Particle;

public class ParticlePoint implements PointSource {
    Particle p;

    public ParticlePoint( Particle p ) {
        this.p = p;
    }

    public Point getPoint() {
        DoublePoint dp = p.getPosition();
        int x = (int) dp.getX();
        int y = (int) dp.getY();
        return new Point( x, y );
    }
}
