package electron.paint.particle;

import electron.paint.Painter;
import phys2d.Particle;

import java.awt.*;

public class ParticlePainterAdapter implements Painter {
    ParticlePainter p;
    Particle part;

    public ParticlePainterAdapter( ParticlePainter p, Particle part ) {
        this.p = p;
        this.part = part;
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
