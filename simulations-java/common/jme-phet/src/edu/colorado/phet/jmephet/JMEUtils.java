// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.jmephet;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.Pbuffer;
import org.lwjgl.opengl.PixelFormat;

import edu.colorado.phet.common.phetcommon.math.vector.Vector3D;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeSystem;
import com.jme3.util.BufferUtils;

/**
 * Utilities for dealing with JME3
 */
public class JMEUtils {
    /*---------------------------------------------------------------------------*
    * global properties
    *----------------------------------------------------------------------------*/

    // global application. since we only should have one per process, this is acceptable as global information
    private static PhetJMEApplication instance_app;

    public static synchronized void setApplication( PhetJMEApplication application ) {
        if ( instance_app != null ) {
            throw new RuntimeException( "Only one Application should be used" );
        }
        instance_app = application;
    }

    public static synchronized PhetJMEApplication getApplication() {
        return instance_app;
    }

    // the running framerate
    public static final Property<Integer> frameRate = new Property<Integer>( 60 );
    // number of antialiasing samples to use, or null to use the default
    public static final Property<Integer> antiAliasingSamples = new Property<Integer>( null );
    public static int maxAllowedSamples = 0; // should be written on startup

    /*---------------------------------------------------------------------------*
    * Vector3f conversion
    *----------------------------------------------------------------------------*/
    public static Vector3f convertVector( Vector3D vec ) {
        return new Vector3f( (float) vec.getX(), (float) vec.getY(), (float) vec.getZ() );
    }

    public static Vector3D convertVector( Vector3f vec ) {
        return new Vector3D( vec.getX(), vec.getY(), vec.getZ() );
    }

    /*---------------------------------------------------------------------------*
    * ColorRGBA conversion
    *----------------------------------------------------------------------------*/
    public static Color convertColor( ColorRGBA color ) {
        return new Color( color.r, color.g, color.b, color.a );
    }

    public static ColorRGBA convertColor( Color color ) {
        return new ColorRGBA( scaleColor( color.getRed() ), scaleColor( color.getGreen() ), scaleColor( color.getBlue() ), scaleColor( color.getAlpha() ) );
    }

    private static float scaleColor( int x ) {
        return ( (float) x ) / 255f;
    }

    /**
     * Copied from JME3 source, since apparently it wasn't in this newer version!
     * <p/>
     * A function for creating a rotation matrix that rotates a vector called
     * "start" into another vector called "end".
     *
     * @param matrix The matrix to modify
     * @param a      normalized non-zero starting vector
     * @param b      normalized non-zero ending vector
     * @see "Tomas M�ller, John Hughes \"Efficiently Building a Matrix to Rotate \
     *      One Vector to Another\" Journal of Graphics Tools, 4(4):1-4, 1999"
     */
    public static void fromStartEndVectors( Matrix3f matrix, Vector3D a, Vector3D b ) {
        Vector3f start = convertVector( a );
        Vector3f end = convertVector( b );

        // adding copyright for this function (and the documentation) so we don't violate copyright
        /*
         * Copyright (c) 2009-2010 jMonkeyEngine
         * All rights reserved.
         *
         * Redistribution and use in source and binary forms, with or without
         * modification, are permitted provided that the following conditions are
         * met:
         *
         * * Redistributions of source code must retain the above copyright
         *   notice, this list of conditions and the following disclaimer.
         *
         * * Redistributions in binary form must reproduce the above copyright
         *   notice, this list of conditions and the following disclaimer in the
         *   documentation and/or other materials provided with the distribution.
         *
         * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
         *   may be used to endorse or promote products derived from this software
         *   without specific prior written permission.
         *
         * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
         * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
         * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
         * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
         * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
         * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
         * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
         * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
         * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
         * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
         * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
         */
        Vector3f v = new Vector3f();
        float e, h, f;

        start.cross( end, v );
        e = start.dot( end );
        f = ( e < 0 ) ? -e : e;

        // if "from" and "to" vectors are nearly parallel
        if ( f > 1.0f - FastMath.ZERO_TOLERANCE ) {
            Vector3f u = new Vector3f();
            Vector3f x = new Vector3f();
            float c1, c2, c3; /* coefficients for later use */
            int i, j;

            x.x = ( start.x > 0.0 ) ? start.x : -start.x;
            x.y = ( start.y > 0.0 ) ? start.y : -start.y;
            x.z = ( start.z > 0.0 ) ? start.z : -start.z;

            if ( x.x < x.y ) {
                if ( x.x < x.z ) {
                    x.x = 1.0f;
                    x.y = x.z = 0.0f;
                }
                else {
                    x.z = 1.0f;
                    x.x = x.y = 0.0f;
                }
            }
            else {
                if ( x.y < x.z ) {
                    x.y = 1.0f;
                    x.x = x.z = 0.0f;
                }
                else {
                    x.z = 1.0f;
                    x.x = x.y = 0.0f;
                }
            }

            u.x = x.x - start.x;
            u.y = x.y - start.y;
            u.z = x.z - start.z;
            v.x = x.x - end.x;
            v.y = x.y - end.y;
            v.z = x.z - end.z;

            c1 = 2.0f / u.dot( u );
            c2 = 2.0f / v.dot( v );
            c3 = c1 * c2 * u.dot( v );

            for ( i = 0; i < 3; i++ ) {
                for ( j = 0; j < 3; j++ ) {
                    float val = -c1 * u.get( i ) * u.get( j ) - c2 * v.get( i )
                                                                * v.get( j ) + c3 * v.get( i ) * u.get( j );
                    matrix.set( i, j, val );
                }
                float val = matrix.get( i, i );
                matrix.set( i, i, val + 1.0f );
            }
        }
        else {
            // the most common case, unless "start"="end", or "start"=-"end"
            float hvx, hvz, hvxy, hvxz, hvyz;
            h = 1.0f / ( 1.0f + e );
            hvx = h * v.x;
            hvz = h * v.z;
            hvxy = hvx * v.y;
            hvxz = hvx * v.z;
            hvyz = hvz * v.y;
            matrix.set( 0, 0, e + hvx * v.x );
            matrix.set( 0, 1, hvxy - v.z );
            matrix.set( 0, 2, hvxz + v.y );

            matrix.set( 1, 0, hvxy + v.z );
            matrix.set( 1, 1, e + h * v.y * v.y );
            matrix.set( 1, 2, hvyz - v.x );

            matrix.set( 2, 0, hvxz - v.y );
            matrix.set( 2, 1, hvyz + v.x );
            matrix.set( 2, 2, e + hvz * v.z );
        }

    }

    /**
     * Find a quaternion that transforms a unit vector A into a unit vector B. There
     * are technically multiple solutions, so this only picks one.
     *
     * @param a Unit vector A
     * @param b Unit vector B
     * @return A quaternion s.t. Q * A = B
     */
    public static Quaternion getRotationQuaternion( Vector3f a, Vector3f b ) {
        Matrix3f rotationMatrix = new Matrix3f();
        fromStartEndVectors( rotationMatrix, convertVector( a ), convertVector( b ) );
        return new Quaternion().fromRotationMatrix( rotationMatrix );
    }

    /**
     * Spherical linear interpolation between two unit vectors.
     *
     * @param start Start unit vector
     * @param end   End unit vector
     * @param ratio Between 0 (at start vector) and 1 (at end vector)
     * @return Spherical linear interpolation between the start and end
     */
    public static Vector3f slerp( Vector3f start, Vector3f end, float ratio ) {
        // assumes normalized. TODO doc
        return new Quaternion().slerp( Quaternion.IDENTITY, getRotationQuaternion( start, end ), ratio ).mult( start );
    }

    /**
     * Returns a ray that is in the coordinate system of the spatial passed in.
     *
     * @param ray     A ray in the world coordinate system
     * @param spatial Any spatial
     * @return A new Ray in the coordinate system of the spatial
     */
    public static Ray transformWorldRayToLocalCoordinates( Ray ray, Spatial spatial ) {
        Vector3f transformedPosition = spatial.getWorldTransform().transformInverseVector( ray.getOrigin(), new Vector3f() );
        Vector3f transformedDirection = spatial.getLocalToWorldMatrix( new Matrix4f() ).transpose().mult( ray.getDirection() ).normalize(); // transpose trick to transform a unit vector
        return new Ray( transformedPosition, transformedDirection );
    }

    public static Vector2f intersectZPlaneWithRay( Ray ray ) {
        float t = -ray.getOrigin().getZ() / ray.getDirection().getZ(); // solve for below equation at z=0. assumes camera isn't z=0, which should be safe here

        Vector3f hitPosition = ray.getOrigin().add( ray.getDirection().mult( t ) );
        return new Vector2f( hitPosition.x, hitPosition.y );
    }

    public static void discardTree( Spatial spatial ) {
        // remove any children, if there are any
        if ( spatial instanceof Node ) {
            for ( Spatial child : new ArrayList<Spatial>( ( (Node) spatial ).getChildren() ) ) {
                discardTree( child );
            }
        }

        // remove the node itself
        spatial.getParent().detachChild( spatial );
    }

    /**
     * Ensure that the (probably LWJGL) libraries are loaded (either from the JNLP path or extracted out to disk)
     *
     * @param settings Our current app settings
     */
    public static void initializeLibraries( AppSettings settings ) {
        // if we aren't running fron JNLP (and haven't initialized before)
        if ( !JmeSystem.isLowPermissions() ) {
            try {
                JMENatives.extractNativeLibs( JmeSystem.getPlatform(), settings );
            }
            catch ( IOException e ) {
                throw new RuntimeException( "JME3 failure", e );
            }

            // mark as initialized
            JmeSystem.setLowPermissions( true );
        }
    }

    /**
     * @return Maxiumum anti-aliasing samples supported. Required to have the LWJGL libraries loaded before calling
     */
    public static int getMaximumAntialiasingSamples() {
        int result = 0;
        try {
            Pbuffer pb = new Pbuffer( 10, 10, new PixelFormat( 32, 0, 24, 8, 0 ), null );
            pb.makeCurrent();
            boolean supported = GLContext.getCapabilities().GL_ARB_multisample;
            if ( supported ) {
                result = GL11.glGetInteger( GL30.GL_MAX_SAMPLES );
            }
            pb.destroy();
        }
        catch ( LWJGLException e ) {
            //e.printStackTrace();
        }
        return result;
    }

    /**
     * Initialize our JME setup. Should be called at the very start of the application.
     *
     * @param commandLineArgs Args, to check for flags
     */
    public static void initializeJME( String[] commandLineArgs ) {
        // don't spam the console output for every cylinder that we re-create (every frame)
        Logger.getLogger( "de.lessvoid" ).setLevel( Level.SEVERE );
        Logger.getLogger( "com.jme3" ).setLevel( Level.SEVERE );

        // since we are including the JME3 native lib dependencies in the JNLP, don't load if we are running online
        if ( System.getProperty( "javawebstart.version" ) != null ) {
            // see http://jmonkeyengine.org/wiki/doku.php/jme3:webstart
            JmeSystem.setLowPermissions( true );
        }
        else {
            // create a temporary directory to hold native libs
            final File tempDir = new File( System.getProperty( "java.io.tmpdir" ), "phet-jme3-libs" );
            tempDir.mkdirs();
            final String path = tempDir.getAbsolutePath();
            System.out.println( "Extracting native JME3 libraries to: " + path ); // TODO use common logging?
            JMENatives.setExtractionDir( path );
            tempDir.deleteOnExit();
        }

        // attempt to read a anti-aliasing samples count from the command line args. use "-samples 0" for 0 samples, etc.
        for ( int i = 0; i < commandLineArgs.length; i++ ) {
            if ( commandLineArgs[i].equals( "-samples" ) && i + 1 < commandLineArgs.length ) {
                antiAliasingSamples.set( Integer.parseInt( commandLineArgs[i + 1] ) );
                break;
            }
        }
    }

    /**
     * Sets BufferUtils.trackingHash to a different hashmap that doesn't add elements, to avoid
     * the extremely significant performance overhead when there are many buffers.
     * <p/>
     * Uses reflection (thus hack-ish) because BufferUtils does not provide write access to
     * the boolean value trackDirectMemory (it is detected as a constant and thus changing it
     * via reflection probably would have no effect).
     * <p/>
     * It's also a hack because it's changing a private static final field! Although nicely,
     * if this fails it won't cause major issues, just performance degradation.
     */
    public static void disableBufferTrackingPerformanceHack() {
        try {
            Field f = BufferUtils.class.getDeclaredField( "trackingHash" );
            f.setAccessible( true );

            Field modifiersField = Field.class.getDeclaredField( "modifiers" );
            modifiersField.setAccessible( true );
            modifiersField.setInt( f, f.getModifiers() & ~Modifier.FINAL );

            f.set( f, new HashMap<Buffer, Object>() {
                @Override public Object put( Buffer key, Object value ) {
                    // empty!
                    return value;
                }
            } );
        }
        catch ( NoSuchFieldException e ) {
            e.printStackTrace();
        }
        catch ( IllegalAccessException e ) {
            e.printStackTrace();
        }
    }

    /**
     * Displays the number of buffers being tracked in memory. Increasing amounts mean
     * memory leaks (most likely)
     * <p/>
     * NOTE: only works if disableBufferTrackingPerformanceHack() has not been called
     *
     * @return Number of buffers.
     */
    public static int debugGetNumberOfBuffers() {
        // only way to get this info is to read it out of a string returned :(
        StringBuilder builder = new StringBuilder();
        BufferUtils.printCurrentDirectMemory( builder );
        String str = builder.toString();
        int start = str.indexOf( ": " ) + 2;
        int end = str.indexOf( "\n" );
        return Integer.parseInt( str.substring( start, end ) );
    }

    /*---------------------------------------------------------------------------*
    * threading
    *----------------------------------------------------------------------------*/

    /**
     * Attempt to run the code within the Swing Event Dispatch Thread (EDT)
     *
     * @param runnable Code to run
     */
    public static void swingLock( Runnable runnable ) {
        if ( SwingUtilities.isEventDispatchThread() ) {
            // we are in the dispatch thread. just run it now (we can't call invokeAndWait from here)
            runnable.run();
        }
        else {
            try {
                // wait for it to run in the EDT
                SwingUtilities.invokeAndWait( runnable );
            }
            catch ( InterruptedException e ) {
                e.printStackTrace();
            }
            catch ( InvocationTargetException e ) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Like SwingUtilities.invokeLater, but for the JME3 thread. Will be executed at the start
     * of the next application update
     *
     * @param runnable Code to run
     */
    public static void invokeLater( final Runnable runnable ) {
        getApplication().enqueue( new Callable<Object>() {
            public Object call() throws Exception {
                runnable.run();
                return null;
            }
        } );
    }

    /**
     * Like SwingUtilities.invokeAndWait, but for the JME3 thread. Will wait until the execution is done
     *
     * @param runnable Code to run
     * @throws InterruptedException Probably on shutdown
     * @throws java.util.concurrent.ExecutionException
     *                              Probably on startup
     */
    public static void invokeAndWait( final Runnable runnable ) throws ExecutionException, InterruptedException {
        Future<Object> future = getApplication().enqueue( new Callable<Object>() {
            public Object call() throws Exception {
                runnable.run();
                return null;
            }
        } );
        future.get();
    }

    /**
     * Run the code now if we are in the JME3 thread, otherwise run it at the beginning of the next JME3 update.
     *
     * @param runnable Code to run
     */
    public static void invoke( final Runnable runnable ) {
        if ( isLWJGLRendererThread() ) {
            runnable.run();
        }
        else {
            invokeLater( runnable );
        }
    }

    /**
     * Attempt to run the code within the JME3 thread
     *
     * @param runnable Code to run.
     */
    public static void jmeLock( Runnable runnable ) {
        if ( isLWJGLRendererThread() ) {
            runnable.run();
        }
        else {
            try {
                invokeAndWait( runnable );
            }
            catch ( ExecutionException e ) {
                e.printStackTrace();
            }
            catch ( InterruptedException e ) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isLWJGLRendererThread() {
        return Thread.currentThread().getName().equals( "LWJGL Renderer Thread" );
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
}
