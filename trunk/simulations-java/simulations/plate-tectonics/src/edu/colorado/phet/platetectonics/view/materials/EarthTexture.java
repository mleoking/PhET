// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view.materials;

import java.nio.ByteBuffer;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.glu.GLU;

import edu.colorado.phet.lwjglphet.utils.GLDisplayList;

import static org.lwjgl.opengl.GL11.*;

public class EarthTexture {
    private static final int WIDTH = 512;
    private static final int HEIGHT = 512;

    private static final Random random = new Random( 2501 );

    private static final GLDisplayList textureAction;

    static {
        textureAction = new GLDisplayList( new Runnable() {
            public void run() {
                ByteBuffer buffer = BufferUtils.createByteBuffer( WIDTH * HEIGHT * 3 );
                for ( int i = 0; i < buffer.capacity(); i += 3 ) {
                    final byte b = (byte) ( random.nextInt() % 256 );
                    buffer.put( new byte[] { b, b, b } );
                }
                buffer.rewind();

                glTexEnvf( GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE );

                glTexEnvf( GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE );

                glTexParameterf( GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,
//                         GL_NEAREST );
                                 GL_LINEAR_MIPMAP_NEAREST );
                glTexParameterf( GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR );

                // repeat
                glTexParameterf( GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT );
                glTexParameterf( GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT );

                GLU.gluBuild2DMipmaps( GL_TEXTURE_2D, 3, WIDTH, HEIGHT, GL_RGB, GL_UNSIGNED_BYTE, buffer );
            }
        } );

    }

    public static void begin() {
        glEnable( GL_TEXTURE_2D );
        textureAction.run();
    }

    public static void end() {
        glDisable( GL_TEXTURE_2D );
    }
}
