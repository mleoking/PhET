// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.ImageButtonNode;

/**
 * Button for moving forward or backward through the kits
 *
 * @author Sam Reid
 */
public class ArrowButton extends ImageButtonNode {
    public static final int inset = 5;

    //Create an arrow button for moving forward or background through the kits
    //Create images that translate down and right when pressed so they look like button presses
    //And pad the original image so that both have the same bounds, so no layout problems are caused (such as control panels resizing when the button is pressed)
    public ArrowButton( BufferedImage image ) {
        super( pad( image, inset, 0 ), pad( image, inset, inset ) );
        addInputEventListener( new CursorHandler() );
    }

    protected static BufferedImage pad( final BufferedImage bufferedImage, int inset, final int offset ) {
        return new BufferedImage( bufferedImage.getWidth() + inset, bufferedImage.getHeight() + inset, bufferedImage.getType() ) {{
            Graphics2D g2 = createGraphics();
            g2.drawRenderedImage( bufferedImage, AffineTransform.getTranslateInstance( offset, offset ) );
            g2.dispose();
        }};
    }
}