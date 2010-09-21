package edu.colorado.phet.batteryvoltage.common.electron.paint.particle;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import edu.colorado.phet.batteryvoltage.common.phys2d.DoublePoint;
import edu.colorado.phet.batteryvoltage.common.phys2d.Particle;

/*Centers the image.*/

public class ImagePainter implements ParticlePainter {
    BufferedImage im;

    public ImagePainter( BufferedImage im ) {
        this.im = im;
    }

    public void paint( Particle p, Graphics2D g ) {
        DoublePoint dp = p.getPosition();
        int x = (int) dp.getX() - im.getWidth() / 2;
        int y = (int) dp.getY() - im.getHeight() / 2;
        g.drawRenderedImage( im, AffineTransform.getTranslateInstance( x, y ) );
    }

    public boolean contains( Particle p, Point pt ) {
        DoublePoint dp = p.getPosition();
        int x = (int) dp.getX() - im.getWidth() / 2;
        int y = (int) dp.getY() - im.getHeight() / 2;
        Rectangle rect = new Rectangle( x, y, im.getWidth(), im.getHeight() );
        return rect.contains( pt.x, pt.y );
    }
}
