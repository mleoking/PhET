package edu.colorado.phet.balloons.common.paint;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class DoubleBufferPainter implements Painter {
    Painter p;
    BufferedImage buffer;
    Graphics2D bufGraphics;
    static final AffineTransform at = new AffineTransform();

    public DoubleBufferPainter( Painter p, int w, int h ) {
        this( p, new BufferedImage( w, h, BufferedImage.TYPE_INT_RGB ) );
    }

    public DoubleBufferPainter( Painter p, BufferedImage buffer ) {
        this.p = p;
        this.buffer = buffer;
        this.bufGraphics = (Graphics2D) buffer.getGraphics();
    }

    public void paint( Graphics2D g ) {
        p.paint( bufGraphics );
        g.drawRenderedImage( buffer, at );
    }
}
