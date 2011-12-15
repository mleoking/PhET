// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.testlwjglproject.lwjgl;

import java.awt.Dimension;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector3D;

import static org.lwjgl.opengl.GL11.*;

public class LWJGLUtils {

    public static int toPowerOf2( int n ) {
        int result = 1;
        while ( result < n ) {
            result *= 2;
        }
        return result;
    }

    public static Dimension toPowerOf2( Dimension dim ) {
        return new Dimension( toPowerOf2( dim.width ), toPowerOf2( dim.height ) );
    }

    public static boolean isPowerOf2( int n ) {
        return n == toPowerOf2( n );
    }

    public static void vertex3d( ImmutableVector3D v ) {
        glVertex3d( v.getX(), v.getY(), v.getZ() );
    }

    public static void normal3d( ImmutableVector3D normal ) {
        glNormal3d( normal.getX(), normal.getY(), normal.getZ() );
    }

    public static void texCoord2d( ImmutableVector2D coord ) {
        glTexCoord2d( coord.getX(), coord.getY() );
    }

    public static FloatBuffer floatBuffer( float[] floats ) {
        FloatBuffer result = BufferUtils.createFloatBuffer( floats.length );
        result.put( floats );
        result.rewind();
        return result;
    }
}
