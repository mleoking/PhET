package edu.colorado.phet.common.batteryvoltage.electron.paint.particle;

import edu.colorado.phet.common.batteryvoltage.phys2d.Particle;

import java.awt.*;

public interface ParticlePainter {
    public void paint( Particle p, Graphics2D g );

    public boolean contains( Particle p, Point pt );
}
