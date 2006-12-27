package electron.paint.particle;

import phys2d.Particle;

import java.awt.*;

public interface ParticlePainter {
    public void paint( Particle p, Graphics2D g );

    public boolean contains( Particle p, Point pt );
}
