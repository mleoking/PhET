/** University of Colorado, PhET*/
package edu.colorado.phet.common.view.phetgraphics;

import edu.colorado.phet.common.view.GraphicsSetup;
import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 *
 */
public class PhetBufferedGraphic extends PhetImageGraphic {
    private Paint background;
    private BufferedImage buffer;
    private Graphic graphic;
    private int type;
    private GraphicsSetup graphicsSetup;

    public PhetBufferedGraphic( Component component, Graphic graphic, Paint background ) {
        this( component, component.getWidth(), component.getHeight(), graphic );
        setSize( component.getWidth(), component.getHeight() );
        this.background = background;
    }

    public PhetBufferedGraphic( Component component, int width, int height, Graphic graphic ) {
        this( component, width, height, graphic, BufferedImage.TYPE_INT_RGB );
    }

    public PhetBufferedGraphic( Component component, int width, int height, Graphic graphic, int type ) {
        super( component );
        this.graphic = graphic;
        buffer = new BufferedImage( width, height, type );
        setImage( buffer );
        this.type = type;
    }

    public void repaintBuffer() {
        Graphics2D g2 = buffer.createGraphics();
        if( background != null ) {
            g2.setPaint( background );
            g2.fillRect( 0, 0, buffer.getWidth(), buffer.getHeight() );
        }
        graphic.paint( buffer.createGraphics() );
        forceRepaint();
    }

    public void setSize( int width, int height ) {
        buffer = new BufferedImage( width, height, type );
        repaintBuffer();
    }

    public BufferedImage getBuffer() {
        return buffer;
    }

}
