// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet.utils;

import java.awt.Color;
import java.awt.Dimension;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.swing.SwingUtilities;

import org.lwjgl.BufferUtils;

import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.lwjglphet.LWJGLCanvas;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;

import static org.lwjgl.opengl.GL11.*;

public class LWJGLUtils {

    private static int nextDisplayList = 1;

    public static synchronized int getDisplayListName() {
        return nextDisplayList++;
    }

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

    public static void color4f( Color color ) {
        glColor4f( (float) color.getRed() / 255f,
                   (float) color.getGreen() / 255f,
                   (float) color.getBlue() / 255f,
                   (float) color.getAlpha() / 255f
        );
    }

    public static void vertex3f( ImmutableVector3F v ) {
        glVertex3f( v.x, v.y, v.z );
    }

    public static void vertex2fxy( ImmutableVector2F v ) {
        glVertex3f( v.x, v.y, 0 );
    }

    public static FloatBuffer floatBuffer( float[] floats ) {
        FloatBuffer result = BufferUtils.createFloatBuffer( floats.length );
        result.put( floats );
        result.rewind();
        return result;
    }

    public static ShortBuffer shortBuffer( short[] shorts ) {
        ShortBuffer result = BufferUtils.createShortBuffer( shorts.length );
        result.put( shorts );
        result.rewind();
        return result;
    }

    public static void invoke( Runnable runnable ) {
        LWJGLCanvas.addTask( runnable );
    }

    public static boolean isLWJGLRendererThread() {
        return Thread.currentThread().getName().equals( LWJGLCanvas.LWJGL_THREAD_NAME );
    }

    public static SimpleObserver swingObserver( final Runnable runnable ) {
        return new SimpleObserver() {
            public void update() {
                SwingUtilities.invokeLater( runnable );
            }
        };
    }

    public static SimpleObserver jmeObserver( final Runnable runnable ) {
        return new SimpleObserver() {
            public void update() {
                invoke( runnable );
            }
        };
    }

    public static UpdateListener swingUpdateListener( final Runnable runnable ) {
        return new UpdateListener() {
            public void update() {
                SwingUtilities.invokeLater( runnable );
            }
        };
    }

    public static void withEnabled( int glCapability, Runnable runnable ) {
        glEnable( glCapability );
        runnable.run();
        glDisable( glCapability );
    }

    public static void withClientEnabled( int glClientCapability, Runnable runnable ) {
        glEnableClientState( glClientCapability );
        runnable.run();
        glDisableClientState( glClientCapability );
    }
}
