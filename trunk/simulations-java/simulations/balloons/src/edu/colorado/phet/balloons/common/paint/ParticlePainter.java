package edu.colorado.phet.balloons.common.paint;

import edu.colorado.phet.balloons.common.phys2d.Particle;

import java.awt.*;

public interface ParticlePainter {
    public void paint( Particle p, Graphics2D g );
}
