// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.efield.gui;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import edu.colorado.phet.efield.phys2d_efield.DoublePoint;
import edu.colorado.phet.efield.phys2d_efield.Particle;

// Referenced classes of package edu.colorado.phet.efield.gui:
//            ParticlePainter

public class ImagePainter
        implements ParticlePainter {

    public ImagePainter( BufferedImage bufferedimage ) {
        im = bufferedimage;
    }

    public void paint( Particle particle, Graphics2D graphics2d ) {
        DoublePoint doublepoint = particle.getPosition();
        int i = (int) doublepoint.getX() - im.getWidth() / 2;
        int j = (int) doublepoint.getY() - im.getHeight() / 2;
        graphics2d.drawRenderedImage( im, AffineTransform.getTranslateInstance( i, j ) );
    }

    public boolean contains( Particle particle, Point point ) {
        DoublePoint doublepoint = particle.getPosition();
        int i = (int) doublepoint.getX() - im.getWidth() / 2;
        int j = (int) doublepoint.getY() - im.getHeight() / 2;
        Rectangle rectangle = new Rectangle( i, j, im.getWidth(), im.getHeight() );
        return rectangle.contains( point.x, point.y );
    }

    BufferedImage im;
}
