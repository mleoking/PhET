package edu.colorado.phet.balloons.common.paint;

import java.awt.*;

import edu.colorado.phet.balloons.common.phys2d.Particle;

public interface ParticlePainter {
    public void paint( Particle p, Graphics2D g );
}
