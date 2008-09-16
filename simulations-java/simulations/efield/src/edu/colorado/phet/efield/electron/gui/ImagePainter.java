// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.efield.electron.gui;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import edu.colorado.phet.efield.electron.phys2d_efield.DoublePoint;
import edu.colorado.phet.efield.electron.phys2d_efield.Particle;

// Referenced classes of package edu.colorado.phet.efield.electron.gui:
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
