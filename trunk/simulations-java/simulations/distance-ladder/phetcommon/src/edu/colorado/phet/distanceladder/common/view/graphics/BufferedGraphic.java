/*, 2003.*/
package edu.colorado.phet.distanceladder.common.view.graphics;

import edu.colorado.phet.distanceladder.common.view.ApparatusPanel;
import edu.colorado.phet.distanceladder.common.view.BasicGraphicsSetup;
import edu.colorado.phet.distanceladder.common.view.GraphicsSetup;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Feb 21, 2004
 * Time: 1:15:56 AM
 */
public class BufferedGraphic implements Graphic {
    BufferedImage buffer;
    Graphic graphic;
    Graphics2D bufferGraphics;
    private AffineTransform transform = new AffineTransform();
    private Color backgroundColor;
    private GraphicsSetup setup;

    public BufferedGraphic( BufferedImage buffer, Graphic graphic, Color backgroundColor, GraphicsSetup setup ) {
        this.buffer = buffer;
        this.graphic = graphic;
        this.backgroundColor = backgroundColor;
        this.setup = setup;
        this.bufferGraphics = buffer.createGraphics();
    }

    public void repaintBuffer() {
        setup.setup( bufferGraphics );
        bufferGraphics.setColor( backgroundColor );
        bufferGraphics.fillRect( 0, 0, buffer.getWidth(), buffer.getHeight() );
        graphic.paint( bufferGraphics );
    }

    public void paint( Graphics2D g ) {
        g.drawRenderedImage( buffer, transform );
    }

    public void setTransform( AffineTransform transform ) {
        this.transform = transform;
    }
}
