// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.jme;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import com.jme3.texture.Image;

/**
 * An image (JME3 texture) that can be drawn on with a Graphics2D context.
 * <p/>
 * When needed (or on construction), refreshImage() should be called to update
 * the texture.
 */
public abstract class PaintableImage extends Image {
    private BufferedImage backImg;
    private ByteBuffer scratch;

    private final double graphicsScale;

    public PaintableImage( int width, int height, boolean hasAlpha ) {
        this( width, height, hasAlpha, 1.0 );
    }

    public PaintableImage( int width, int height, boolean hasAlpha, double graphicsScale ) {
        super( hasAlpha ? Format.RGBA8 : Format.BGR8, width, height, ByteBuffer.allocateDirect( 4 * width * height ) );
        this.graphicsScale = graphicsScale;
        scratch = data.get( 0 );
        backImg = new BufferedImage( width, height, hasAlpha ? BufferedImage.TYPE_4BYTE_ABGR : BufferedImage.TYPE_3BYTE_BGR );

    }

    /**
     * Call from the EDT thread
     */
    public void refreshImage() {
        // paint within the EDT thread
        Graphics2D g = backImg.createGraphics();

        // scale up the subsequent graphics calls by the X amount
        g.scale( graphicsScale, graphicsScale );

        // paint onto the graphics (on the BufferedImage)
        paint( g );
        g.dispose();

        // then transfer the image data during the JME thread
        JMEUtils.invoke( new Runnable() {
            public void run() {
                /* get the image data */
                byte data[] = (byte[]) backImg.getRaster().getDataElements( 0, 0, backImg.getWidth(), backImg.getHeight(), null );
                scratch.clear();
                scratch.put( data, 0, data.length );
                scratch.rewind();
                setData( scratch );
            }
        } );
    }

    // override this to define how to paint the image
    public abstract void paint( Graphics2D graphicsContext );

}
