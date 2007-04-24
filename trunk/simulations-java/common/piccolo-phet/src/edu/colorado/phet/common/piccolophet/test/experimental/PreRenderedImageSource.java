/*  */
package edu.colorado.phet.common.piccolophet.test.experimental;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Aug 7, 2005
 * Time: 9:40:38 PM
 *
 */

public class PreRenderedImageSource implements MagicPImage3.ImageSource {
    private BufferedImage image;

    public PreRenderedImageSource( BufferedImage image ) {
        this.image = image;
    }

    public Image newImage( AffineTransform transform ) {
//        return new BufferedImage( 10,10,BufferedImage.TYPE_INT_RGB);

        int newHeight = (int)( transform.getScaleY() * image.getHeight( null ) );
        return BufferedImageUtils.rescaleYMaintainAspectRatio( image, newHeight );
    }
}
