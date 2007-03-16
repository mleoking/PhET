/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common_cck.view.graphics;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Oct 8, 2003
 * Time: 11:58:25 PM
 * Copyright (c) Oct 8, 2003 by Sam Reid
 */
public class TargetedImageGraphic extends BufferedImageGraphic {
    private Rectangle2D modelBounds;

    public TargetedImageGraphic( BufferedImage image, Rectangle2D modelBounds ) {
        super( image );
        this.modelBounds = modelBounds;
    }

    public void paint( Graphics2D g ) {
        BufferedImage image = getBufferedImage();

        AffineTransform at = AffineTransform.getTranslateInstance( modelBounds.getX(), modelBounds.getY() );
        double sx = modelBounds.getWidth() / image.getWidth();
        double sy = modelBounds.getHeight() / image.getHeight();
        at.scale( sx, -sy );
        at.translate( 0, -image.getHeight() );

        super.setTransform( at );
        super.paint( g );
    }

    public void setRect( Rectangle2D.Double rect ) {
        this.modelBounds = rect;
    }

}
