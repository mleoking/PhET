// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.Pbuffer;
import org.lwjgl.opengl.PixelFormat;

/**
 * Responsible for extracting the native libraries into the correct place, if needed.
 */
public class StartupUtils {

    private static final Logger logger = Logger.getLogger( StartupUtils.class.getName() );

    public static void setupLibraries() throws IOException {

        JPopupMenu.setDefaultLightWeightPopupEnabled( false ); // potential solution for menu-over-OpenGL canvas issue. see http://lwjgl.org/forum/index.php?topic=2365.0;wap2

        boolean webstart = System.getProperty( "javawebstart.version" ) != null;

        if ( !webstart ) {
            // libraries not loaded via JNLP, so we need to load them here.

            // create a temporary directory to hold native libs
            final File tempDir = new File( System.getProperty( "java.io.tmpdir" ), "phet-lwjgl-libs" );
            tempDir.mkdirs();
            final String path = tempDir.getAbsolutePath();

            logger.log( Level.INFO, "Extracting native JME3 libraries to: " + path );

            LWJGLStartupImplementation.extractNativeLibs( tempDir, LWJGLStartupImplementation.getPlatform(), false, false );
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
        catch( LWJGLException e ) {
            //e.printStackTrace();
        }
        return result;
    }
}
