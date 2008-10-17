/**
 * Class: GlassPaneGraphic
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 30, 2003
 */
package edu.colorado.phet.greenhouse;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

import edu.colorado.phet.greenhouse.coreadditions.graphics.ImageGraphic;
import edu.colorado.phet.greenhouse.phetcommon.view.graphics.Graphic;

/**
 * GlassPaneGraphic
 * <p/>
 * A graphic that fakes a glass pane. Rather than using alpha compositing in the Graphics2D,
 * we paint it with an opaque paint that is computed to look like the glass pane being
 * composited with alpha. This is done so that photons can appear to disappear into the glass
 * and emerge again from it.
 */
public class GlassPaneGraphic implements Graphic {
    private Rectangle2D.Double graphic;
    private Paint glassPaint = Color.white;
    private float glassAlpha = 0.3f;
    private Stroke glassStroke = new BasicStroke( 0.01f );

    public GlassPaneGraphic( GlassPane glassPane, ImageGraphic backgroundGraphic, Rectangle2D modelBounds ) {
        graphic = new Rectangle2D.Double( glassPane.getBounds().getMinX(),
                                          glassPane.getBounds().getMinY(),
                                          glassPane.getWidth(),
                                          glassPane.getHeight() );

        // compute the paint that we will need for the glass pane to look right over the
        // background
        BufferedImage bi = backgroundGraphic.getBufferedImage();
        int y = (int) ( bi.getHeight() - ( glassPane.getBounds().getY() * bi.getHeight() / modelBounds.getHeight() ) );
        int rgb = bi.getRGB( bi.getWidth() / 2, y );
        ColorModel colorModel = bi.getColorModel();
        int whiteComponent = (int) ( glassAlpha * 255 );
        int red = getSrcOver( colorModel.getRed( rgb ), whiteComponent, glassAlpha );
        int green = getSrcOver( colorModel.getGreen( rgb ), whiteComponent, glassAlpha );
        int blue = getSrcOver( colorModel.getBlue( rgb ), whiteComponent, glassAlpha );
        glassPaint = new Color( red, green, blue );
    }

    private int getSrcOver( int cd, int cs, float as ) {
        return (int) ( ( 1 - as ) * (float) cd ) + cs;
    }

    public void paint( Graphics2D g2 ) {
        g2.setPaint( glassPaint );
        g2.setStroke( glassStroke );
//        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, glassAlpha));
        g2.fill( graphic );
//        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
//        g2.draw( graphic );
    }
}
