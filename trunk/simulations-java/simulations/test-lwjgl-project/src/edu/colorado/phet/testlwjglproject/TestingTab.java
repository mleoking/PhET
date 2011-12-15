// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.testlwjglproject;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.event.VoidNotifier;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.Spacer;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.testlwjglproject.lwjgl.CanvasTransform;
import edu.colorado.phet.testlwjglproject.lwjgl.CanvasTransform.StageCenteringCanvasTransform;
import edu.colorado.phet.testlwjglproject.lwjgl.LWJGLCanvas;
import edu.colorado.phet.testlwjglproject.lwjgl.LWJGLTab;
import edu.colorado.phet.testlwjglproject.lwjgl.OrthoComponentNode;
import edu.colorado.phet.testlwjglproject.lwjgl.OrthoPiccoloNode;
import edu.umd.cs.piccolo.PNode;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;

public class TestingTab extends LWJGLTab {
    private long timeElapsed = 0;
    private long lastTime = 0;
    private OrthoComponentNode testSwingNode;
    private OrthoPiccoloNode testPiccoloNode;
    private CanvasTransform canvasTransform;
    private Dimension stageSize;

    // TODO: consider a different way of handling this, or moving this up to LWJGLTab
    public final VoidNotifier mouseEventNotifier = new VoidNotifier();

    public TestingTab( LWJGLCanvas canvas, String title ) {
        super( canvas, title );
    }

    @Override public void start() {
        lastTime = System.currentTimeMillis();

        stageSize = initialCanvasSize;
        canvasTransform = new StageCenteringCanvasTransform( canvasSize, stageSize );

        testSwingNode = new OrthoComponentNode( new JPanel() {{
            setOpaque( false );
            add( new JButton( "Swing Button" ) {{
                setOpaque( false );
//                setFont( new PhetFont( 16, true ) );
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        System.out.println( "Swing button pressed" );
                    }
                } );
            }} );
            add( new JCheckBox( "Swing Checkbox" ) {{
                setOpaque( false );
                setForeground( Color.WHITE );
//                setFont( new PhetFont( 16, true ) );
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        System.out.println( "Swing checkbox pressed" );
                    }
                } );
            }} );
            add( new JRadioButton( "Swing Radio Button" ) {{
                setOpaque( false );
                setForeground( Color.WHITE );
//                setFont( new PhetFont( 16, true ) );
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        System.out.println( "Swing radio button pressed" );
                    }
                } );
            }} );
        }}, this, canvasTransform, new Property<ImmutableVector2D>( new ImmutableVector2D( 20, 20 ) ), mouseEventNotifier );
        testPiccoloNode = new OrthoPiccoloNode( new PNode() {{
            final PNode button = new TextButtonNode( "Piccolo Button", new PhetFont( 14 ), Color.ORANGE ) {{
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        System.out.println( "Piccolo button pressed" );
                    }
                } );
            }};
            addChild( new Spacer( button.getFullBounds() ) );
            addChild( button );
            addChild( new PhetPPath( new RoundRectangle2D.Double( 0, 0, 150, 30, 10, 10 ), Color.BLUE ) {{
                setOffset( 20, button.getFullBounds().getMaxY() + 10 );
            }} );
            addChild( new PhetPPath( new RoundRectangle2D.Double( 0, 0, 150, 30, 10, 10 ), Color.YELLOW ) {{
                setOffset( 20.5, button.getFullBounds().getMaxY() + 50 );
            }} );
        }}, this, canvasTransform, new Property<ImmutableVector2D>( new ImmutableVector2D( 20, 100 ) ), mouseEventNotifier );
    }

    @Override public void stop() {
        // store state here?
    }

    @Override public void loop() {
        Display.sync( 60 );

        // walk through all of the mouse events that occurred
        while ( Mouse.next() ) {
            mouseEventNotifier.updateListeners();
        }

        testSwingNode.update();
        testPiccoloNode.update();

        glEnable( GL_BLEND );
        glBlendFunc( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA );

        // show both sides
        glPolygonMode( GL_FRONT, GL_FILL );
        glPolygonMode( GL_BACK, GL_FILL );

        glViewport( 0, 0, getCanvasWidth(), getCanvasHeight() );
        guiProjection();
        glMatrixMode( GL_MODELVIEW );
        glLoadIdentity();

        long currentTime = System.currentTimeMillis();
        timeElapsed += ( currentTime - lastTime );
        lastTime = currentTime;

        // Clear the screen and depth buffer
        glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT );

        glPushMatrix();
        {
            glMatrixMode( GL_PROJECTION );
            CanvasTransform.applyAffineTransform( canvasTransform.transform.get() );
            glMatrixMode( GL_MODELVIEW );

            /*---------------------------------------------------------------------------*
            * testing background
            *----------------------------------------------------------------------------*/
            glColor4f( 0f, 1, 1f, 0.1f );

            // draw quad
            glBegin( GL_QUADS );
            glVertex3f( 0, 0, 0 );
            glVertex3f( stageSize.width, 0, 0 );
            glVertex3f( stageSize.width, stageSize.height, 0 );
            glVertex3f( 0, stageSize.height, 0 );
            glEnd();

            glTranslatef( stageSize.width / 2, stageSize.height / 3, 0 );

            /*---------------------------------------------------------------------------*
            * fractal thing
            *----------------------------------------------------------------------------*/
            float angle = (float) ( timeElapsed ) / 200;

            // add a fractal-like thing in the background
            fractalThing( angle, 12, 0, 2 );
        }
        glPopMatrix();

        glEnable( GL_DEPTH_TEST );

        {
            glMatrixMode( GL_PROJECTION );
            glLoadIdentity();
            glScalef( 1 / ( (float) canvasSize.get().width ), 1 / ( (float) canvasSize.get().height ), 1 );
            AffineTransform transform = canvasTransform.transform.get();
            glScaled( transform.getScaleX(), transform.getScaleY(), 1 );
            gluPerspective( 40, 1, 1, 1000 );
            glMatrixMode( GL_MODELVIEW );
            glLoadIdentity();
            glTranslatef( 0, 0, -10 );
            glScalef( 100, 100, 100 );

            glColor4f( 1f, 1, 1f, 1f );

            float size = 100f;

            // TODO: do scene drawing here. the setup is correct

            // draw quad
//            glBegin( GL_QUADS );
//            glVertex3f( -size, -size, 0 );
//            glVertex3f( size, -size, 0 );
//            glVertex3f( size, size, 0 );
//            glVertex3f( -size, size, 0 );
//            glEnd();
//            FloatBuffer floatBuffer = BufferUtils.createFloatBuffer( 16 * 3 );
//            floatBuffer.put( new float[] {
//                    -1.5f, -1.5f, 4.0f,
//                    -0.5f, -1.5f, 2.0f,
//                    0.5f, -1.5f, -1.0f,
//                    1.5f, -1.5f, 2.0f,
//
//                    -1.5f, -0.5f, 1.0f,
//                    -0.5f, -0.5f, 3.0f,
//                    0.5f, -0.5f, 0.0f,
//                    1.5f, -0.5f, -1.0f,
//
//                    -1.5f, 0.5f, 4.0f,
//                    -0.5f, 0.5f, 0.0f,
//                    0.5f, 0.5f, 3.0f,
//                    1.5f, 0.5f, 4.0f,
//
//                    -1.5f, 1.5f, -2.0f,
//                    -0.5f, 1.5f, -2.0f,
//                    0.5f, 1.5f, 0.0f,
//                    1.5f, 1.5f, -1.0f
//            } );
//            glMap2f( GL11.GL_MAP2_VERTEX_3, 0, 1, 3, 4, 0, 1, 12, 4, floatBuffer );
//            glEnable( GL_MAP2_VERTEX_3 );
//            glEvalMesh2( GL_FILL, 0, 8, 0, 8 );
//            glDisable( GL_MAP2_VERTEX_3 );
        }

        glDisable( GL_DEPTH_TEST );


        {
            guiProjection();

            // our test GUI
            testSwingNode.render();
            testPiccoloNode.render();
        }

        Display.update();
    }

    private void guiProjection() {
        glMatrixMode( GL_PROJECTION );
        glLoadIdentity();
        glOrtho( 0, getCanvasWidth(), getCanvasHeight(), 0, 1, -1 );
        glMatrixMode( GL_MODELVIEW );
        glLoadIdentity();
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
