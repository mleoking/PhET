// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.testlwjglproject;

import java.awt.Canvas;
import java.awt.GridLayout;

import javax.swing.JPanel;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

/**
 * An empty module, mainly used for testing tabs.
 */
public class TestModule extends PiccoloModule {
    public TestModule( String s ) {
        super( s, new ConstantDtClock( 30, 1 ) );
        setSimulationPanel( new PhetPCanvas() );
        setClockControlPanel( null );

        final Canvas canvas = new Canvas() {
            private boolean running;
            private Thread gameThread;

            {
                setFocusable( true );
                requestFocus();
                setIgnoreRepaint( true );
            }

            public final void addNotify() {
                super.addNotify();
                final Canvas canvas = this;
                gameThread = new Thread() {
                    public void run() {
                        running = true;
                        try {
                            Display.setParent( canvas );
                            Display.create();

                            // init OpenGL
                            GL11.glMatrixMode( GL11.GL_PROJECTION );
                            GL11.glLoadIdentity();
                            GL11.glOrtho( 0, 800, 600, 0, 1, -1 );
                            GL11.glMatrixMode( GL11.GL_MODELVIEW );

                            GL11.glEnable( GL11.GL_BLEND );
                            GL11.glBlendFunc( GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA );
                        }
                        catch ( LWJGLException e ) {
                            e.printStackTrace();
                        }

                        GL11.glTranslatef( 100, 100, 0 );

                        long start = System.currentTimeMillis();

                        // game loop
                        while ( running ) {
                            Display.sync( 60 );

                            // show both sides
                            GL11.glPolygonMode( GL11.GL_FRONT, GL11.GL_FILL );
                            GL11.glPolygonMode( GL11.GL_BACK, GL11.GL_FILL );

                            // Clear the screen and depth buffer
                            GL11.glClear( GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT );

                            // Reset the transform
                            GL11.glMatrixMode( GL11.GL_MODELVIEW );
                            GL11.glLoadIdentity();

                            // translate our stuff a bit (can deal with centering after we get resizing working properly
                            GL11.glTranslatef( 400, 200, 0 );

                            float angle = (float) ( System.currentTimeMillis() - start ) / 200;

                            fractalThing( angle, 12, 0, 2 );

                            Display.update();
                        }

                        Display.destroy();
                    }
                };
                gameThread.start();
            }

            public final void removeNotify() {
                running = false;
                try {
                    gameThread.join();
                }
                catch ( InterruptedException e ) {
                    e.printStackTrace();
                }
                super.removeNotify();
            }
        };
        setSimulationPanel( new JPanel( new GridLayout( 1, 1 ) ) {{
            add( canvas );
        }} );
    }

    private void fractalThing( float angle, int num, int depth, int limit ) {
        GL11.glPushMatrix();
        for ( int i = 0; i < num; i++ ) {
            GL11.glTranslatef( 0, 100, 0 );
            GL11.glRotatef( angle, 0, 0, 1 );

            // set the color of the quad (R,G,B,A)
            float n = ( (float) ( i ) ) / ( num - 1 );
            GL11.glColor4f( 1 - n, 0.0f, n, 0.5f - 0.4f * ( (float) depth ) / ( (float) limit ) );

            // draw quad
            GL11.glBegin( GL11.GL_QUADS );
            GL11.glVertex3f( -50, -50, 0 );
            GL11.glVertex3f( 50, -50, 0 );
            GL11.glVertex3f( 50, 50, 0 );
            GL11.glVertex3f( -50, 50, 0 );
            GL11.glEnd();

            if ( depth < limit ) {
                GL11.glPushMatrix();
                GL11.glScalef( 0.5f, 0.5f, 1f );
                fractalThing( angle, num, depth + 1, limit );
                GL11.glPopMatrix();
            }
        }
        GL11.glPopMatrix();
    }
}