package edu.colorado.phet.signalcircuit.paint.particle;

import edu.colorado.phet.signalcircuit.paint.Painter;
import edu.colorado.phet.signalcircuit.phys2d.Particle;

import java.awt.*;

public class ParticlePainterAdapter implements Painter {
    ParticlePainter p;
    Particle part;

    public ParticlePainterAdapter( ParticlePainter p, Particle part ) {
        this.p = p;
        this.part = part;
    }

    public void paint( Graphics2D g ) {
        p.paint( part, g );
    }

}
