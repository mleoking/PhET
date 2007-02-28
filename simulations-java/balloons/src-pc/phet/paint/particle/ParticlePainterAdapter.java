package phet.paint.particle;

import phet.paint.Painter;
import phet.phys2d.Particle;

import java.awt.*;

public class ParticlePainterAdapter implements Painter {
    ParticlePainter p;
    Particle part;

    public ParticlePainterAdapter( ParticlePainter p, Particle part ) {
        this.p = p;
        this.part = part;
    }

    public String toString() {
        return getClass().getName() + ", Particle=" + part + ", Paint=" + p;
    }

    public boolean contains( Point px ) {
        return p.contains( part, px );
    }

    public void paint( Graphics2D g ) {
        p.paint( part, g );
    }

    public Particle getParticle() {
        return part;
    }
}
