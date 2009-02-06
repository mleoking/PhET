package edu.colorado.phet.ohm1d.volt;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import edu.colorado.phet.ohm1d.common.paint.Painter;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jan 24, 2003
 * Time: 8:05:23 PM
 * To change this template use Options | File Templates.
 */
public class BlackbodyScale implements Painter, PowerListener {
    BufferedImage spectrum;
    BufferedImage arrow;
    AffineTransform at;
    int y;
    int x0;
    AffineTransform arrowTransform = new AffineTransform();

    public BlackbodyScale( BufferedImage spectrum, int x, int y, BufferedImage arrow ) {
        this.x0 = x;
        this.spectrum = spectrum;
        this.arrow = arrow;
        this.at = AffineTransform.getTranslateInstance( x, y );
        this.y = spectrum.getHeight() + y;
    }

    public void paint( Graphics2D graphics2D ) {
        graphics2D.drawRenderedImage( spectrum, at );
        graphics2D.drawRenderedImage( arrow, arrowTransform );
    }

    public void powerChanged( double ratio ) {
        double x = -arrow.getWidth() / 2 + x0 + ratio * spectrum.getWidth();
        this.arrowTransform = AffineTransform.getTranslateInstance( x, y );
    }
}
