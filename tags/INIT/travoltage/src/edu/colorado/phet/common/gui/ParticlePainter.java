package edu.colorado.phet.common.gui;

import edu.colorado.phet.common.phys2d.Particle;

import java.awt.*;

public interface ParticlePainter {
    public void paint( Particle p, Graphics2D g );

    public boolean contains( Particle p, Point pt );
}
