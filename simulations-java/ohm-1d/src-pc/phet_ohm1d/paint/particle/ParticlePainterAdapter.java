package phet_ohm1d.paint.particle;

import phet_ohm1d.paint.Painter;
import phet_ohm1d.phys2d.Particle;

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

    public void paint( Graphics2D g ) {
        p.paint( part, g );
    }

}
