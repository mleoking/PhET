/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.forces1d.common_force1d.view.phetgraphics;

import java.awt.*;
import java.awt.image.BufferedImage;

import edu.colorado.phet.forces1d.common_force1d.view.BasicGraphicsSetup;
import edu.colorado.phet.forces1d.common_force1d.view.GraphicsSetup;

/**
 * BufferedPhetGraphic
 *
 * @author ?
 * @version $Revision$
 */
public class BufferedPhetGraphic extends PhetGraphic {
    private GraphicLayerSet graphicLayerSet;
    private Paint background;
    private BufferedImage buffer;
    private int type;
    private GraphicsSetup graphicsSetup = new BasicGraphicsSetup();

    public BufferedPhetGraphic( Component component, int width, int height, Paint background ) {
        this( component, width, height, BufferedImage.TYPE_INT_RGB, background );
    }

    public BufferedPhetGraphic( Component component, int width, int height, int type, Paint background ) {
        super( component );
        this.background = background;
        this.type = type;
        this.graphicLayerSet = new CompositePhetGraphic( component );
        if ( width > 0 && height > 0 ) {
            buffer = new BufferedImage( width, height, type );
        }
        repaintBuffer();
    }

    public void repaintBuffer() {
        repaintBuffer( 0, 0, buffer.getWidth(), buffer.getHeight() );
    }

    public void setSize( int width, int height ) {
        if ( width > 0 && height > 0 ) {
            buffer = new BufferedImage( width, height, type );
        }
        setBoundsDirty();
        autorepaint();
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
        Rectangle tx = getNetTransform().createTransformedShape( new Rectangle( buffer.getWidth(), buffer.getHeight() ) ).getBounds();
        return tx;
    }

    public void paint( Graphics2D g2 ) {
        if ( isVisible() ) {
            super.saveGraphicsState( g2 );
            super.updateGraphicsState( g2 );
            g2.drawRenderedImage( buffer, getNetTransform() );
            super.restoreGraphicsState();
        }
    }

    public void addGraphic( PhetGraphic graphic ) {
        graphicLayerSet.addGraphic( graphic );
    }

    public void setGraphicsSetup( GraphicsSetup graphicsSetup ) {
        this.graphicsSetup = graphicsSetup;
    }

    public void repaintBuffer( Rectangle r ) {
        repaintBuffer( r.x, r.y, r.width, r.height );
    }

    public void repaintBuffer( int x, int y, int width, int height ) {
        Graphics2D g2 = buffer.createGraphics();
        if ( graphicsSetup != null ) {
            graphicsSetup.setup( g2 );
        }
        g2.setClip( x, y, width, height );
        if ( background != null ) {
            g2.setPaint( background );
            g2.fillRect( x, y, width, height );
        }
        if ( buffer != null ) {
            graphicLayerSet.paint( g2 );
        }
    }

    public void clearGraphics() {
        graphicLayerSet.clear();
    }
}
