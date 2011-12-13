// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.testlwjglproject;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PhetTabbedPane.TabbedModule;
import edu.colorado.phet.testlwjglproject.lwjgl.LWJGLCanvas;
import edu.colorado.phet.testlwjglproject.lwjgl.LWJGLTab;
import edu.colorado.phet.testlwjglproject.lwjgl.StartupUtils;

import static org.lwjgl.opengl.GL11.*;

public class TestLWJGLApplication extends PhetApplication {

    public TestLWJGLApplication( PhetApplicationConfig config ) {
        super( config );
        final LWJGLCanvas canvas = LWJGLCanvas.getCanvasInstance();
        addModule( new TabbedModule( canvas ) {{
            Tab[] tabs = new Tab[] {
                    new TestingTab( canvas, TestProjectResources.getString( "sim1.module1" ) ),
                    new TestingTab( canvas, TestProjectResources.getString( "sim1.module2" ) )
            };
            for ( Tab tab : tabs ) {
                addTab( tab );
            }
        }} );
    }

    public static void main( String[] args ) {
        try {
            StartupUtils.setupLibraries();
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }

        new PhetApplicationLauncher().launchSim( new PhetApplicationConfig( args, "test-lwjgl-project", "sim1" ), TestLWJGLApplication.class );
    }

    private static class TestingTab extends LWJGLTab {
        private long timeElapsed = 0;
        private long lastTime = 0;

        public TestingTab( LWJGLCanvas canvas, String title ) {
            super( canvas, title );
        }

        @Override public void start() {
            lastTime = System.currentTimeMillis();

            {
                int width = 64;
                int height = 64;
                ByteBuffer buffer = BufferUtils.createByteBuffer( width * height * 4 );
                for ( int row = 0; row < height; row++ ) {
                    for ( int col = 0; col < width; col++ ) {
                        buffer.put( new byte[] {
                                row % 2 == 0 ? 0 : (byte) 255,
                                ( row + col ) % 2 == 1 ? 0 : (byte) 255,
                                col % 2 == 0 ? 0 : (byte) 255,
                                (byte) 255 } );
//                        buffer.put( new byte[] { (byte) ( row + col ), (byte) ( 255 - row - col ), 0, (byte) ( 128 - row + col ) } );
                    }
                }
                buffer.position( 0 );
                glTexImage2D( GL_TEXTURE_2D, 0, 4, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer );
                glTexParameterf( GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP );
                glTexParameterf( GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP );
                glTexParameterf( GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST );
                glTexParameterf( GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST );
                glTexEnvf( GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_DECAL );
            }
        }

        @Override public void stop() {
            // store state here?
        }

        @Override public void loop() {
            Display.sync( 60 );

            glEnable( GL_BLEND );
            glBlendFunc( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA );

            // show both sides
            glPolygonMode( GL_FRONT, GL_FILL );
            glPolygonMode( GL_BACK, GL_FILL );

            glViewport( 0, 0, getCanvasWidth(), getCanvasHeight() );
            glMatrixMode( GL_PROJECTION );
            glLoadIdentity();
            glOrtho( 0, getCanvasWidth(), getCanvasHeight(), 0, 1, -1 );
            System.out.println( getCanvasWidth() + "x" + getCanvasHeight() );
            glMatrixMode( GL_MODELVIEW );

            long currentTime = System.currentTimeMillis();
            timeElapsed += ( currentTime - lastTime );
            lastTime = currentTime;

            // Clear the screen and depth buffer
            glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT );

            // Reset the transform
            glMatrixMode( GL_MODELVIEW );
            glLoadIdentity();

            {
                glEnable( GL_TEXTURE_2D );
                glShadeModel( GL_FLAT );

                glColor4f( 1, 1, 1, 1 );

                glBegin( GL_QUADS );
                glTexCoord2d( 0.0, 0.0 );
                glVertex3d( 0, 0, 0.0 );
                glTexCoord2d( 0.0, 1.0 );
                glVertex3d( 0, 64, 0.0 );
                glTexCoord2d( 1.0, 1.0 );
                glVertex3d( 64, 64, 0.0 );
                glTexCoord2d( 1.0, 0.0 );
                glVertex3d( 64, 0, 0.0 );
                glEnd();

                glShadeModel( GL_SMOOTH );
                glDisable( GL_TEXTURE_2D );
            }

            // translate our stuff a bit (can deal with centering after we get resizing working properly
            glTranslatef( 400, 200, 0 );

            float angle = (float) ( timeElapsed ) / 200;

            // add a fractal-like thing in the background
            fractalThing( angle, 12, 0, 2 );

            // test direct image drawing functionality in the foreground (lower-left corner)
            {
                int width = 127;
                int height = 127;
                ByteBuffer buffer = BufferUtils.createByteBuffer( width * height * 4 );
                for ( int row = 0; row < height; row++ ) {
                    for ( int col = 0; col < width; col++ ) {
                        buffer.put( new byte[] { (byte) ( row + col ), (byte) ( 255 - row - col ), 0, (byte) ( 128 - row + col ) } );
                    }
                }
                buffer.position( 0 );
                glDrawPixels( width, height, GL_RGBA, GL_UNSIGNED_BYTE, buffer );
            }


            Display.update();
        }

        private void fractalThing( float angle, int num, int depth, int limit ) {
            glPushMatrix();
            for ( int i = 0; i < num; i++ ) {
                glTranslatef( 0, 100, 0 );
                glRotatef( angle, 0, 0, 1 );

                // set the color of the quad (R,G,B,A)
                float n = ( (float) ( i ) ) / ( num - 1 );
                glColor4f( 1 - n, 0.0f, n, 0.5f - 0.4f * ( (float) depth ) / ( (float) limit ) );

                // draw quad
                glBegin( GL_QUADS );
                glVertex3f( -50, -50, 0 );
                glVertex3f( 50, -50, 0 );
                glVertex3f( 50, 50, 0 );
                glVertex3f( -50, 50, 0 );
                glEnd();

                if ( depth < limit ) {
                    glPushMatrix();
                    glScalef( 0.5f, 0.5f, 1f );
                    fractalThing( angle, num, depth + 1, limit );
                    glPopMatrix();
                }
            }
            glPopMatrix();
        }
    }
}
