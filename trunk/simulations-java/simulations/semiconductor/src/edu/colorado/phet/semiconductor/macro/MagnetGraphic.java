package edu.colorado.phet.semiconductor.macro;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.semiconductor.common.TargetedImageGraphic2;
import edu.colorado.phet.semiconductor.oldphetgraphics.graphics.Graphic;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;


/**
 * User: Sam Reid
 * Date: Apr 27, 2004
 * Time: 10:17:55 PM
 */
public class MagnetGraphic implements Graphic {
    private Magnet magnet;
    private ModelViewTransform2D transform;
    private BufferedImage image;
    boolean visible = true;

    public MagnetGraphic( Magnet magnet, ModelViewTransform2D transform, BufferedImage image ) {
        this.magnet = magnet;
        this.transform = transform;
        this.image = image;
    }

    public void paint( Graphics2D g ) {
        if ( visible ) {
            Rectangle2D r = transform.createTransformedShape( magnet.getBounds() ).getBounds2D();
            TargetedImageGraphic2 tig = new TargetedImageGraphic2( image, r );
            tig.paint( g );
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible( boolean visible ) {
        this.visible = visible;
    }
}
