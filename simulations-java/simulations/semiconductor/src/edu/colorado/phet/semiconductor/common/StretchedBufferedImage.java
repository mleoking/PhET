package edu.colorado.phet.semiconductor.common;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.semiconductor.phetcommon.view.graphics.Graphic;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.transforms.ModelViewTransform2D;

/**
 * User: Sam Reid
 * Date: Jan 16, 2004
 * Time: 2:21:07 AM
 */
public class StretchedBufferedImage implements Graphic {
    private BufferedImage battIm;
    private Rectangle rectangle;
    private boolean flipX;

    public StretchedBufferedImage( BufferedImage battIm, Rectangle rectangle ) {
        this.battIm = battIm;
        this.rectangle = new Rectangle( rectangle );
        if ( rectangle.width == 0 || rectangle.height == 0 ) {
            throw new RuntimeException( "Zero width or height rect." );
        }
    }

    public void paint( Graphics2D graphics2D ) {
        //To change body of implemented methods use Options | File Templates.
//        System.out.println("battIm = " + battIm);
        int w = battIm.getWidth();
        int h = battIm.getHeight();
        w = Math.max( 1, w );
        h = Math.max( 1, h );
        Rectangle2D.Double picBounds = new Rectangle2D.Double( 0, 0, w, h );
//        System.out.println("rectangle = " + rectangle);
        ModelViewTransform2D transform = new ModelViewTransform2D( picBounds, rectangle );
        AffineTransform at = transform.toAffineTransform();
        if ( flipX ) {
            at.concatenate( AffineTransform.getScaleInstance( -1, 1 ) );
            at.concatenate( AffineTransform.getTranslateInstance( -w, 0 ) );
        }
        graphics2D.drawRenderedImage( battIm, at );
    }

    public void setFlipX( boolean flipX ) {
        this.flipX = flipX;
    }

    public void setOutputRect( Rectangle outputRect ) {
        this.rectangle = outputRect;
    }
}
