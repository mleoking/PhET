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

import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.GraphicsSetup;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * BufferedPhetGraphic
 * <p/>
 * //needs to extend graphic layer set for event handling.
 *
 * @author ?
 * @version $Revision$
 */
public class BufferedPhetGraphic2 extends GraphicLayerSet {
    private PhetImageGraphic phetImageGraphic;
    private Paint background;
    private int type = BufferedImage.TYPE_INT_RGB;
    private Rectangle clip;
    private GraphicsSetup graphicsSetup = new BasicGraphicsSetup();

    private FalseComponent falseComponent = new FalseComponent( new RepaintStrategy() {
        public void repaint( int x, int y, int width, int height ) {
            doRepaint( x, y, width, height );
        }

        public void paintImmediately() {
        }
    } );

    public BufferedPhetGraphic2( Component component, Paint background ) {
        super( component );
        this.background = background;
        this.phetImageGraphic = new PhetImageGraphic( component );
        createBuffer();
    }

    private void createBuffer() {
        int width = getComponent().getWidth();
        int height = getComponent().getHeight();
        if( width > 0 && height > 0 ) {
            phetImageGraphic.setImage( new BufferedImage( width, height, type ) );
        }
    }

    private void doRepaint( int x, int y, int width, int height ) {
        if( getImage() == null ) {
            createBuffer();
        }
        if( getImage() == null ) {
            return;
        }
        Graphics2D g2 = getImage().createGraphics();
        BufferedImage buffer = getImage();
        if( background != null ) {
            g2.setPaint( background );
            g2.fillRect( x, y, width, height );
        }

        if( buffer != null ) {
            clip = new Rectangle( x, y, width, height );
            Graphics2D bufferGraphics = buffer.createGraphics();
            bufferGraphics.setClip( clip );
            graphicsSetup.setup( bufferGraphics );
            super.paint( bufferGraphics );
            setBoundsDirty();
            getComponent().repaint( x, y, width, height );
        }
    }

    public void addGraphic( PhetGraphic graphic ) {
        super.addGraphic( graphic );
        graphic.setComponent( falseComponent );
    }

    public void addGraphic( PhetGraphic graphic, double layer ) {
        super.addGraphic( graphic, layer );
        graphic.setComponent( falseComponent );
    }

    public void repaintBuffer() {
        doRepaint( 0, 0, getComponent().getWidth(), getComponent().getHeight() );
//        if( getImage() == null ) {
//            createBuffer();
//        }
//        Graphics2D g2 = getImage().createGraphics();
//        BufferedImage buffer = getImage();
//        if( background != null ) {
//            g2.setPaint( background );
//            g2.fillRect( 0, 0, buffer.getWidth(), buffer.getHeight() );
//        }
//
//        if( buffer != null ) {
//            super.paint( buffer.createGraphics() );
//            forceRepaint();
//        }
    }

    private BufferedImage getImage() {
        return phetImageGraphic.getImage();
    }

    public void setSize( int width, int height ) {
        createBuffer();
    }

    protected Rectangle determineBounds() {
        return clip;
//        return phetImageGraphic.determineBounds();
    }

    public void paint( Graphics2D g ) {
        phetImageGraphic.paint( g );
    }
}
