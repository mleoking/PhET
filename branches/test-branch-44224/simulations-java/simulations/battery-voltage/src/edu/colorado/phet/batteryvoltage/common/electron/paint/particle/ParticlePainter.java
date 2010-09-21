package edu.colorado.phet.batteryvoltage.common.electron.paint.particle;

import java.awt.*;

import edu.colorado.phet.batteryvoltage.common.phys2d.Particle;

public interface ParticlePainter {
    public void paint( Particle p, Graphics2D g );

    public boolean contains( Particle p, Point pt );
}
