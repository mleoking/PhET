/*, 2003.*/
package edu.colorado.phet.semiconductor.common;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.semiconductor.oldphetgraphics.graphics.Graphic;

/**
 * User: Sam Reid
 * Date: Oct 8, 2003
 * Time: 11:58:25 PM
 */
public class TargetedImageGraphic2 implements Graphic {
    BufferedImage image;
    private Rectangle2D modelBounds;

    public TargetedImageGraphic2( BufferedImage image, Rectangle2D modelBounds ) {
        this.image = image;
        this.modelBounds = modelBounds;
    }

    public void paint( Graphics2D g ) {
        g.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );
//        g.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED );
//        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        AffineTransform at = AffineTransform.getTranslateInstance( modelBounds.getX(), modelBounds.getY() );
        double sx = modelBounds.getWidth() / image.getWidth();
        double sy = modelBounds.getHeight() / image.getHeight();
        at.scale( sx, sy );
        g.drawRenderedImage( image, at );
    }

}