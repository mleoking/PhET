/*, 2003.*/
package edu.colorado.phet.common_microwaves.view.graphics;

import edu.colorado.phet.common_microwaves.view.graphics.Graphic;

import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Oct 8, 2003
 * Time: 11:58:25 PM
 *
 */
public class TargetedImageGraphic implements Graphic {
    BufferedImage image;
    private Rectangle2D.Double modelBounds;

    public TargetedImageGraphic( BufferedImage image, Rectangle2D.Double modelBounds ) {
        this.image = image;
        this.modelBounds = modelBounds;
    }

    public void paint( Graphics2D g ) {
        g.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        AffineTransform at = AffineTransform.getTranslateInstance( modelBounds.x, modelBounds.y );
        double sx = modelBounds.getWidth() / image.getWidth();
        double sy = modelBounds.getHeight() / image.getHeight();
        at.scale( sx, -sy );
        at.translate( 0, -image.getHeight() );

        g.drawRenderedImage( image, at );
    }

    public void setRect( Rectangle2D.Double rect ) {
        this.modelBounds = rect;
    }

}
