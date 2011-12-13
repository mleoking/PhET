// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.testlwjglproject.lwjgl;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

public abstract class TextureImage {
    private BufferedImage paintableImage;
    private ByteBuffer buffer;
    private final AffineTransform imageTransform;
    private final int width;
    private final int height;

    public TextureImage( int width, int height, boolean hasAlpha ) {
        this( width, height, hasAlpha, new AffineTransform() );
    }

    public TextureImage( int width, int height, boolean hasAlpha, AffineTransform imageTransform ) {
        super();
        assert LWJGLUtils.isPowerOf2( width );
        assert LWJGLUtils.isPowerOf2( height );
        this.width = width;
        this.height = height;
        buffer = ByteBuffer.allocateDirect( 4 * width * height );
        this.imageTransform = imageTransform;
        paintableImage = new BufferedImage( width, height, hasAlpha ? BufferedImage.TYPE_4BYTE_ABGR : BufferedImage.TYPE_3BYTE_BGR );
    }

    /**
     * Call from the EDT thread
     */
    public void refreshImage() {
        // paint within the EDT thread
        Graphics2D g = paintableImage.createGraphics();

        // transform the subsequent calls
        g.transform( imageTransform );

        // paint onto the graphics (on the BufferedImage)
        paint( g );
        g.dispose();

        final byte data[] = (byte[]) paintableImage.getRaster().getDataElements( 0, 0, paintableImage.getWidth(), paintableImage.getHeight(), null );

        // then transfer the image data during the LWJGL thread
        LWJGLCanvas.addTask( new Runnable() {
            public void run() {
                // TODO: speed on this could be improved?
                buffer.clear();
                buffer.put( data, 0, data.length );
                buffer.rewind();
            }
        } );
    }

    public void useTexture() {
        glTexImage2D( GL_TEXTURE_2D, 0, 4, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer );
    }

    // override this to define how to paint the image
    public abstract void paint( Graphics2D graphicsContext );

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
