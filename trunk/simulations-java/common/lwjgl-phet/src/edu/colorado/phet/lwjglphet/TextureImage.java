// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.utils.LWJGLUtils;

import static org.lwjgl.opengl.GL11.*;

/**
 * Creates an OpenGL texture that is backed by a BufferedImage, and is updated with a
 * paint() call that allows arbitrary Graphics2D calls.
 */
public abstract class TextureImage {
    private BufferedImage paintableImage;
    private ByteBuffer buffer;
    private final AffineTransform imageTransform;
    private final int width;
    private final int height;

    private int textureId;
    private boolean textureInitialized = false;
    private int magFilter;
    private int minFilter;

    public TextureImage( int width, int height, boolean hasAlpha, int magFilter, int minFilter ) {
        this( width, height, hasAlpha, magFilter, minFilter, new AffineTransform() );
    }

    public TextureImage( int width, int height, boolean hasAlpha, int magFilter, int minFilter, AffineTransform imageTransform ) {
        super();
        this.magFilter = magFilter;
        this.minFilter = minFilter;
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

        g.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
        g.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
        g.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g.setRenderingHint( RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY );

        // transform the subsequent calls
        // TODO: the full transform (including the translation) is causing jittery unexplained behavior. only using the scale part for now
        g.scale( imageTransform.getScaleX(), imageTransform.getScaleY() );
//        g.transform( imageTransform );

        // paint onto the graphics (on the BufferedImage)
        paint( g );
        g.dispose();

        // TODO: speed on this could be improved?
        final byte data[] = (byte[]) paintableImage.getRaster().getDataElements( 0, 0, paintableImage.getWidth(), paintableImage.getHeight(), null );

        // then transfer the image data during the LWJGL thread
        Runnable updateTextureRunnable = new Runnable() {
            public void run() {
                // make sure to lock this instance so we don't read the buffer while it is being written to
                synchronized ( TextureImage.this ) {
                    buffer.clear();
                    buffer.put( data, 0, data.length );
                    buffer.rewind();
                    if ( !textureInitialized ) {
                        textureId = glGenTextures();
                        textureInitialized = true;
                    }
                    glBindTexture( GL_TEXTURE_2D, textureId );
                    glTexEnvf( GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE );
                    glTexParameterf( GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP );
                    glTexParameterf( GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP );
                    glTexParameterf( GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magFilter );
                    glTexParameterf( GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter );
                    glTexImage2D( GL_TEXTURE_2D, 0, 4, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer );
                }
            }
        };

        // only delay execution if we are in the Swing EDT.
        if ( LWJGLUtils.isLWJGLRendererThread() ) {
            // in LWJGL, so we can update the texture without causing threading issues
            updateTextureRunnable.run();
        }
        else {
            // must delay execution
            LWJGLCanvas.addTask( updateTextureRunnable );
        }
    }

    public void useTexture() {
        // lock the instance to prevent concurrent buffer modification
        synchronized ( this ) {
            if ( textureInitialized ) {
                glBindTexture( GL_TEXTURE_2D, textureId );
                glColor4f( 1, 1, 1, 1 );
            }
        }
    }

    // override this to notify the image that it needs to be repainted before being drawn again
    public abstract void repaint();

    // override this to define how to paint the image
    public abstract void paint( Graphics2D graphicsContext );

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public AffineTransform getImageTransform() {
        return imageTransform;
    }

    public void dispose() {
        if ( textureInitialized ) {
            textureInitialized = false;
            glDeleteTextures( textureId );
        }
    }

    public ImmutableVector2F localToComponentCoordinates( ImmutableVector2F localCoordinates ) {
        Point2D swingPoint;
        try {
            swingPoint = getImageTransform().inverseTransform( new Point2D.Float( localCoordinates.x, localCoordinates.y ), new Point2D.Float() );
        }
        catch ( NoninvertibleTransformException e ) {
            throw new RuntimeException( e );
        }
        final double x = swingPoint.getX();
        final double y = swingPoint.getY();
        return new ImmutableVector2F( x, y );
    }
}
