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

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * BufferedPhetGraphic
 *
 * @author ?
 * @version $Revision$
 */
public class BufferedPhetGraphic extends PhetGraphic {
    private GraphicLayerSet graphicLayerSet;
    private PhetImageGraphic imageGraphic;
    private Paint background;
    private BufferedImage buffer;
    private int type;
    private GraphicsSetup graphicsSetup;
    private Rectangle r;

    public BufferedPhetGraphic( Component component, int width, int height, Paint background ) {
        this( component, width, height, BufferedImage.TYPE_INT_RGB, background );
    }

    public BufferedPhetGraphic( Component component, int width, int height, int type, Paint background ) {
        super( component );
        this.background = background;
        this.type = type;
        this.graphicLayerSet = new CompositePhetGraphic( component );
        this.imageGraphic = new PhetImageGraphic( component, buffer );
        if( width > 0 && height > 0 ) {
            buffer = new BufferedImage( width, height, type );
            imageGraphic.setImage( buffer );
        }
        repaintBuffer();
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
        if( buffer != null ) {
            graphicLayerSet.paint( g2 );
            imageGraphic.setImage( buffer );

            imageGraphic.setBoundsDirty();
            imageGraphic.autorepaint();
            setBoundsDirty();
            autorepaint();
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

    public int getType() {
        return type;
    }

    public void setType( int type ) {
        this.type = type;
    }

    public Paint getBackground() {
        return background;
    }

    public void setBackground( Paint background ) {
        this.background = background;
    }

    protected Rectangle determineBounds() {
        if( r != null ) {
            return r;
        }
        else {
            return imageGraphic.determineBounds();
        }
    }

    public void paint( Graphics2D g ) {
        imageGraphic.paint( g );
    }

    public void addGraphic( PhetGraphic graphic ) {
        graphicLayerSet.addGraphic( graphic );
    }

    public void setGraphicsSetup( GraphicsSetup graphicsSetup ) {
        this.graphicsSetup = graphicsSetup;
    }

    public void repaintBuffer( Rectangle r ) {
        Graphics2D g2 = buffer.createGraphics();
        if( graphicsSetup != null ) {
            graphicsSetup.setup( g2 );
        }
        g2.setClip( r );
        if( background != null ) {
            g2.setPaint( background );
            g2.fillRect( 0, 0, buffer.getWidth(), buffer.getHeight() );
        }
        if( buffer != null ) {

            graphicLayerSet.paint( g2 );
            imageGraphic.setAutoRepaint( false );
            imageGraphic.setImage( buffer );
            getComponent().repaint( r.x, r.y, r.width, r.height );
        }
    }

    public boolean contains( int x, int y ) {
        return graphicLayerSet.contains( x, y );
    }
}
