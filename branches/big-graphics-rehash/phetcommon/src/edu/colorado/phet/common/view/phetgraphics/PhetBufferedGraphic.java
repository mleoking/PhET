/** Sam Reid*/
package edu.colorado.phet.common.view.phetgraphics;

import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Nov 14, 2004
 * Time: 3:11:38 PM
 * Copyright (c) Nov 14, 2004 by Sam Reid
 */
public class PhetBufferedGraphic extends PhetImageGraphic {
    private BufferedImage buffer;
    private Graphic graphic;

    public PhetBufferedGraphic( Component component, int width, int height, Graphic graphic ) {
        super( component );
        this.graphic = graphic;
        buffer = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
        setImage( buffer );
    }

    public void repaintBuffer() {
        graphic.paint( buffer.createGraphics() );
        repaint();
    }

    public BufferedImage getBuffer() {
        return buffer;
    }

}
