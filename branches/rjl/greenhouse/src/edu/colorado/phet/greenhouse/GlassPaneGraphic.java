/**
 * Class: GlassPaneGraphic
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 30, 2003
 */
package edu.colorado.phet.greenhouse;

import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class GlassPaneGraphic implements Graphic {
    private GlassPane glassPane;
    private Rectangle2D.Double graphic;
    private Paint glassPaint = Color.white;
    private float glassAlpha = 0.3f;
    private Stroke glassStroke = new BasicStroke( 0.01f );

    public GlassPaneGraphic( GlassPane glassPane ) {
        this.glassPane = glassPane;
        graphic = new Rectangle2D.Double( glassPane.getBounds().getMinX(),
                                          glassPane.getBounds().getMinY(),
                                          glassPane.getWidth(),
                                          glassPane.getHeight() );
    }

    public void paint( Graphics2D g2 ) {
        g2.setPaint( glassPaint );
        g2.setStroke( glassStroke );
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, glassAlpha));
        g2.fill( graphic );
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
        g2.draw( graphic );
    }
}
