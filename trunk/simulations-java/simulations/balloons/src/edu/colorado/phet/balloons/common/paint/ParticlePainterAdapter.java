// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balloons.common.paint;

import java.awt.*;

import edu.colorado.phet.balloons.common.phys2d.Particle;

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
