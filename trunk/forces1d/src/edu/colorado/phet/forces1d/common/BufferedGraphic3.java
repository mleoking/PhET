/** Sam Reid*/
package edu.colorado.phet.forces1d.common;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Nov 14, 2004
 * Time: 3:11:38 PM
 * Copyright (c) Nov 14, 2004 by Sam Reid
 */
public class BufferedGraphic3 extends PhetImageGraphic {
    private BufferedImage buffer;
    private Graphic graphic;

    public BufferedGraphic3( Component component, int width, int height, Graphic graphic ) {
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
