// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.testlwjglproject;

import java.awt.Color;
import java.awt.Dimension;
import java.nio.ByteBuffer;

import javax.swing.JButton;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.testlwjglproject.lwjgl.CanvasTransform;
import edu.colorado.phet.testlwjglproject.lwjgl.CanvasTransform.StageCenteringCanvasTransform;
import edu.colorado.phet.testlwjglproject.lwjgl.LWJGLCanvas;
import edu.colorado.phet.testlwjglproject.lwjgl.LWJGLTab;
import edu.colorado.phet.testlwjglproject.lwjgl.OrthoComponentNode;
import edu.colorado.phet.testlwjglproject.lwjgl.OrthoPiccoloNode;

import static org.lwjgl.opengl.GL11.*;

public class TestingTab extends LWJGLTab {
    private long timeElapsed = 0;
    private long lastTime = 0;
    private OrthoComponentNode testSwingNode;
    private OrthoPiccoloNode testPiccoloNode;
    private CanvasTransform canvasTransform;
    private Dimension stageSize;

    public TestingTab( LWJGLCanvas canvas, String title ) {
        super( canvas, title );
    }

    @Override public void start() {
        lastTime = System.currentTimeMillis();

        stageSize = initialCanvasSize;
        canvasTransform = new StageCenteringCanvasTransform( canvasSize, stageSize );

        testSwingNode = new OrthoComponentNode( new JButton( "Oh my, a Swing JButton!" ) {{
            setOpaque( false );
        }}, this, canvasTransform, new Property<ImmutableVector2D>( new ImmutableVector2D( 20, 20 ) ) );
        testPiccoloNode = new OrthoPiccoloNode( new TextButtonNode( "But can we do Piccolo2D also?", new PhetFont( 16 ), Color.ORANGE ),
                                                this, canvasTransform, new Property<ImmutableVector2D>( new ImmutableVector2D( 20, 100 ) ) );
    }

    @Override public void stop() {
        // store state here?
    }

    @Override public void loop() {
        Display.sync( 60 );

        // TODO: test node update
        //testingComponentImage.update();

        glEnable( GL_BLEND );
        glBlendFunc( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA );

        // show both sides
        glPolygonMode( GL_FRONT, GL_FILL );
        glPolygonMode( GL_BACK, GL_FILL );

        glViewport( 0, 0, getCanvasWidth(), getCanvasHeight() );
        glMatrixMode( GL_PROJECTION );
        glLoadIdentity();
        glOrtho( 0, getCanvasWidth(), getCanvasHeight(), 0, 1, -1 );
        glMatrixMode( GL_MODELVIEW );
        glLoadIdentity();

        long currentTime = System.currentTimeMillis();
        timeElapsed += ( currentTime - lastTime );
        lastTime = currentTime;

        // Clear the screen and depth buffer
        glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT );

        glPushMatrix();
        {
            CanvasTransform.applyAffineTransform( canvasTransform.transform.get() );

            // translate our stuff a bit (can deal with centering after we get resizing working properly
            glTranslatef( stageSize.width / 2, stageSize.height / 3, 0 );

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
        }
        glPopMatrix();

        // our test GUI
        testSwingNode.render();
        testPiccoloNode.render();

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
