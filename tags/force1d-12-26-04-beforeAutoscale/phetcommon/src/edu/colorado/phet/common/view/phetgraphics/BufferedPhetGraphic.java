/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.phetgraphics;

import edu.colorado.phet.common.view.GraphicsSetup;
import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * BufferedPhetGraphic
 *
 * @author ?
 * @version $Revision$
 */
public class BufferedPhetGraphic extends PhetImageGraphic {
    private Paint background;
    private BufferedImage buffer;
    private Graphic graphic;
    private int type;
    private GraphicsSetup graphicsSetup;

    public BufferedPhetGraphic( Component component, Graphic graphic, Paint background ) {
        this( component, component.getWidth(), component.getHeight(), graphic );
        setSize( component.getWidth(), component.getHeight() );
        this.background = background;
    }

    public BufferedPhetGraphic( Component component, int width, int height, Graphic graphic ) {
        this( component, width, height, graphic, BufferedImage.TYPE_INT_RGB );
    }

    public BufferedPhetGraphic( Component component, int width, int height, Graphic graphic, int type ) {
        super( component );
        this.graphic = graphic;
        if( width > 0 && height > 0 ) {
            buffer = new BufferedImage( width, height, type );
            setImage( buffer );
        }
        else {
            buffer = null;
        }
        this.type = type;
    }

    public void repaintBuffer() {
        Graphics2D g2 = buffer.createGraphics();
        if( graphicsSetup != null ) {
            graphicsSetup.setup( g2 );
        }
        if( background != null ) {
            g2.setPaint( background );
            g2.fillRect( 0, 0, buffer.getWidth(), buffer.getHeight() );
        }
        if( buffer != null && graphic != null ) {
            graphic.paint( buffer.createGraphics() );
            forceRepaint();
        }
    }

    public void setSize( int width, int height ) {
        if( width > 0 && height > 0 ) {
            buffer = new BufferedImage( width, height, type );
            repaintBuffer();
        }
    }

    public BufferedImage getBuffer() {
        return buffer;
    }

}
