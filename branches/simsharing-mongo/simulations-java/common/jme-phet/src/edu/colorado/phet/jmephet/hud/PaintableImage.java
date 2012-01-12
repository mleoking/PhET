// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.jmephet.hud;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import edu.colorado.phet.jmephet.JMEUtils;

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
    private final AffineTransform imageTransform;

    public PaintableImage( int width, int height, boolean hasAlpha ) {
        this( width, height, hasAlpha, new AffineTransform() );
    }

    public PaintableImage( int width, int height, boolean hasAlpha, AffineTransform imageTransform ) {
        super( hasAlpha ? Format.RGBA8 : Format.BGR8, width, height, ByteBuffer.allocateDirect( 4 * width * height ) );
        this.imageTransform = imageTransform;
        scratch = data.get( 0 );
        backImg = new BufferedImage( width, height, hasAlpha ? BufferedImage.TYPE_4BYTE_ABGR : BufferedImage.TYPE_3BYTE_BGR );

    }

    /**
     * Call from the EDT thread
     */
    public void refreshImage() {
        // paint within the EDT thread
        Graphics2D g = backImg.createGraphics();

        // transform the subsequent calls
        g.transform( imageTransform );

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
