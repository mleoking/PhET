package edu.colorado.phet.ohm1d.common.paint.particle;

import java.awt.*;

import edu.colorado.phet.ohm1d.common.paint.Painter;
import edu.colorado.phet.ohm1d.common.phys2d.Particle;

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
