// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.kit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.ImageButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.toBufferedImage;

/**
 * Button for moving forward or backward through the kits
 *
 * @author Sam Reid
 */
public class ArrowButton extends ImageButtonNode {
    public static final int inset = 5;
    public static final double ARROW_HEIGHT = 24;

    //Create a yellow triangle like the one used in Build a Molecule for switching between kits
    //An image instead of PNode is used since that makes it easy to flip horizontally, pad inset and translate
    public static final BufferedImage LEFT_ARROW = toBufferedImage( new PhetPPath( new DoubleGeneralPath() {{
        final double arrowWidth = 17;
        moveTo( 0, ARROW_HEIGHT / 2 );
        lineTo( arrowWidth, 0 );
        lineTo( arrowWidth, ARROW_HEIGHT );
        closePath();
    }}.getGeneralPath(), Color.yellow, new BasicStroke( 1 ), Color.black ).toImage() );

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