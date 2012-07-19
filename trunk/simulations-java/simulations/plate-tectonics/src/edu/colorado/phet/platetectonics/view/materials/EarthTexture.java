// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view.materials;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.glu.GLU;

import static edu.colorado.phet.platetectonics.PlateTectonicsResources.RESOURCES;
import static org.lwjgl.opengl.GL11.*;

public class EarthTexture {
    private static final int WIDTH = 512;
    private static final int HEIGHT = 512;

    private static final Random random = new Random( 250134503L );

    private static int textureId;
    private static boolean textureInitialized = false;

    private static ByteBuffer buffer;

    static {
        buffer = BufferUtils.createByteBuffer( WIDTH * HEIGHT * 4 );
        try {
            BufferedImage image = ImageIO.read( RESOURCES.getResourceAsStream( "images/textures/noise.png" ) );
            byte data[] = (byte[]) image.getRaster().getDataElements( 0, 0, image.getWidth(), image.getHeight(), null );
            for ( int i = 0; i < data.length; i += 4 ) {
                data[i] = (byte) ( data[i] & 0x7F | 0x80 );
                data[i + 1] = (byte) ( data[i + 1] & 0x7F | 0x80 );
                data[i + 2] = (byte) ( data[i + 2] & 0x7F | 0x80 );
                data[i + 3] = (byte) ( 0xFF );

                /* Used for debugging texture issues. makes the unit texture square be composed of 4 uniquely-identifiable colored quads
                int idx = i / 4;

                data[i + 1] = (byte) ( idx % ( image.getWidth() ) > 256 ? 0 : 255 );
                data[i + 2] = (byte) ( idx / ( image.getHeight() ) > 256 ? 0 : 255 );
                */
            }
            buffer.clear();
            buffer.put( data, 0, data.length );
        }
        catch( IOException e ) {
            throw new RuntimeException( "failure to read noise file", e );
        }
        buffer.rewind();
    }

    public static void begin() {
        glEnable( GL_TEXTURE_2D );
        if ( !textureInitialized ) {
            textureInitialized = true;
            textureId = glGenTextures();
            glBindTexture( GL_TEXTURE_2D, textureId );

            glTexEnvf( GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE );
            glTexParameterf( GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,
                             GL_LINEAR_MIPMAP_NEAREST );
            glTexParameterf( GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR );

            // repeat
            glTexParameterf( GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT );
            glTexParameterf( GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT );

            GLU.gluBuild2DMipmaps( GL_TEXTURE_2D, 4, WIDTH, HEIGHT, GL_RGBA, GL_UNSIGNED_BYTE, buffer );
        }

        glBindTexture( GL_TEXTURE_2D, textureId );
    }

    public static void end() {
        glDisable( GL_TEXTURE_2D );
    }
}
