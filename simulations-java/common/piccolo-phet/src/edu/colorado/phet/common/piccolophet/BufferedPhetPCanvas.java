package edu.colorado.phet.common.piccolophet;

import edu.colorado.phet.common.phetcommon.util.PhetUtilities;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * This class explicitly buffers the graphics to resolve some issues related to piccolo (pswing) scene graph rendering.
 *
 * @author Sam Reid
 */
public class BufferedPhetPCanvas extends PhetPCanvas {
    private BufferedImage bufferedImage;

    public void paintComponent( Graphics g ) {
        if( bufferedImage == null || bufferedImage.getWidth() != getWidth() || bufferedImage.getHeight() != getHeight() ) {
//            bufferedImage = new BufferedImage( getWidth(), getHeight(),  );
            bufferedImage = new BufferedImage( getWidth(), getHeight(), getBufferedImageType() );
        }

        Graphics2D bufferedGraphics = bufferedImage.createGraphics();
        bufferedGraphics.setClip( g.getClipBounds() );//todo is this correct?
        super.paintComponent( bufferedGraphics );
        ( (Graphics2D)g ).drawRenderedImage( bufferedImage, new AffineTransform() );
        bufferedGraphics.dispose();
    }

    //Using INT_RGB on a mac doesn't allow any transparency to appear
    //INT_ARGB_PRE seems to resolve this issue
    private int getBufferedImageType() {
        return PhetUtilities.isMacintosh() ? BufferedImage.TYPE_INT_ARGB_PRE
               : BufferedImage.TYPE_INT_RGB;
    }
}
