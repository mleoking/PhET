// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.test;

import java.awt.*;

import javax.swing.*;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

//REVIEW Is this still a valid test? It throws "Exception in thread "Thread-3" java.lang.UnsatisfiedLinkError: no lwjgl in java.library.path" on Mac OS 10.7.4 + Java 1.6.0_33
//REVIEW If this is a valid test, doc.
public class CanvasExample {
    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Canvas Testing" );
        frame.setLayout( new BorderLayout() );
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
                        }
                        catch( LWJGLException e ) {
                            e.printStackTrace();
                        }

                        GL11.glTranslatef( 100, 100, 0 );

                        // game loop
                        while ( running ) {
                            Display.sync( 60 );

                            // Clear the screen and depth buffer
                            GL11.glClear( GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT );

                            // set the color of the quad (R,G,B,A)
                            GL11.glColor3f( 0.5f, 0.5f, 1.0f );

                            // show both sides
                            GL11.glPolygonMode( GL11.GL_FRONT, GL11.GL_FILL );
                            GL11.glPolygonMode( GL11.GL_BACK, GL11.GL_FILL );

                            // draw quad
                            GL11.glBegin( GL11.GL_QUADS );
                            GL11.glVertex3f( -50, -50, 0 );
                            GL11.glVertex3f( 50, -50, 0 );
                            GL11.glVertex3f( 50, 50, 0 );
                            GL11.glVertex3f( -50, 50, 0 );
                            GL11.glEnd();

                            GL11.glRotatef( 1, 0, 0, 1 );

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
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
                super.removeNotify();
            }
        };
        frame.setContentPane( new JPanel( new GridLayout( 1, 1 ) ) {{
            add( canvas );
        }} );
        frame.setSize( new Dimension( 800, 600 ) );
        frame.setVisible( true );
    }
}
