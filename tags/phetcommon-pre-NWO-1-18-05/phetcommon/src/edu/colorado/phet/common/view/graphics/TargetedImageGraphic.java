/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.graphics;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * TargetedImageGraphic
 *
 * @author Sam Reid
 * @version $Revision$
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
