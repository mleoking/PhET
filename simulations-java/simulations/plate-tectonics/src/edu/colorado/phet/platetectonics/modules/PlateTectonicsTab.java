// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.modules;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.util.LinkedList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.model.event.VoidNotifier;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.lwjglphet.CanvasTransform;
import edu.colorado.phet.lwjglphet.CanvasTransform.StageCenteringCanvasTransform;
import edu.colorado.phet.lwjglphet.GLNode;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.LWJGLCanvas;
import edu.colorado.phet.lwjglphet.LWJGLTab;
import edu.colorado.phet.lwjglphet.OrthoComponentNode;
import edu.colorado.phet.lwjglphet.math.ImmutableMatrix4F;
import edu.colorado.phet.lwjglphet.math.LWJGLTransform;
import edu.colorado.phet.platetectonics.util.LWJGLModelViewTransform;

import static edu.colorado.phet.platetectonics.PlateTectonicsConstants.framesPerSecondLimit;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;

/**
 * General plate tectonics module that consolidates common behavior between the various tabs
 * <p/>
 * TODO: implement different cameras somehow?
 */
public abstract class PlateTectonicsTab extends LWJGLTab {
    public static final String MAP_LEFT = "CameraLeft";
    public static final String MAP_RIGHT = "CameraRight";
    public static final String MAP_UP = "CameraUp";
    public static final String MAP_DOWN = "CameraDown";
    public static final String MAP_LMB = "CameraDrag";

    private Dimension stageSize;

    public final VoidNotifier mouseEventNotifier = new VoidNotifier();
    public final VoidNotifier keyboardEventNotifier = new VoidNotifier();
    public final VoidNotifier beforeFrameRender = new VoidNotifier();

    private LWJGLTransform debugCameraTransform = new LWJGLTransform();
    protected CanvasTransform canvasTransform;
    private LWJGLModelViewTransform modelViewTransform;
    private long lastSeenTime;
    public final GLNode rootNode = new GLNode();

    // in seconds
    private float timeElapsed;

    // recorded amount
    public final Property<Double> framesPerSecond = new Property<Double>( 0.0 );
    private final LinkedList<Long> timeQueue = new LinkedList<Long>();

    public PlateTectonicsTab( LWJGLCanvas canvas, String title, float kilometerScale ) {
        super( canvas, title );

        // TODO: better initialization for this model view transform (for each module)
        modelViewTransform = new LWJGLModelViewTransform( ImmutableMatrix4F.scaling( kilometerScale / 1000 ) );
    }

    @Override public void start() {
        lastSeenTime = System.currentTimeMillis();

        stageSize = initialCanvasSize;
        canvasTransform = new StageCenteringCanvasTransform( canvasSize, stageSize );

        // show both sides
        glPolygonMode( GL_FRONT, GL_FILL );
        glPolygonMode( GL_BACK, GL_FILL );

        /*---------------------------------------------------------------------------*
        * debug camera controls
        *----------------------------------------------------------------------------*/
        mouseEventNotifier.addUpdateListener(
                new UpdateListener() {
                    public void update() {
                        if ( Keyboard.isKeyDown( Keyboard.KEY_Q ) ) {
                            int dx = Mouse.getEventDX();
                            if ( Mouse.isButtonDown( 0 ) ) {
                                int dy = Mouse.getEventDY();
                                debugCameraTransform.prepend( ImmutableMatrix4F.rotateY( (float) dx / 100 ) );
                                debugCameraTransform.prepend( ImmutableMatrix4F.rotateX( (float) -dy / 100 ) );
                            }
                            else if ( Mouse.isButtonDown( 1 ) ) {
                                debugCameraTransform.prepend( ImmutableMatrix4F.rotateZ( (float) dx / 100 ) );
                            }
                        }
                    }
                }, false );
        beforeFrameRender.addUpdateListener(
                new UpdateListener() {
                    public void update() {
                        if ( Keyboard.isKeyDown( Keyboard.KEY_W ) ) {
                            debugCameraTransform.prepend( ImmutableMatrix4F.translation( 0, 0, 1 ) );
                        }
                        if ( Keyboard.isKeyDown( Keyboard.KEY_S ) ) {
                            debugCameraTransform.prepend( ImmutableMatrix4F.translation( 0, 0, -1 ) );
                        }
                        if ( Keyboard.isKeyDown( Keyboard.KEY_A ) ) {
                            debugCameraTransform.prepend( ImmutableMatrix4F.translation( 1, 0, 0 ) );
                        }
                        if ( Keyboard.isKeyDown( Keyboard.KEY_D ) ) {
                            debugCameraTransform.prepend( ImmutableMatrix4F.translation( -1, 0, 0 ) );
                        }
                    }
                }, false );

        // TODO: add in lighting for consistency
    }

    @Override public void loop() {
        // delay if we need to, limiting our FPS
        Display.sync( framesPerSecondLimit.get() );

        // calculate FPS
        int framesToCount = 10;
        long current = System.currentTimeMillis();
        timeQueue.add( current );
        if ( timeQueue.size() == framesToCount + 1 ) {
            long previous = timeQueue.poll();
            framesPerSecond.set( (double) ( 1000 * ( (float) framesToCount ) / ( (float) ( current - previous ) ) ) );
        }

        beforeFrameRender.updateListeners();

        // Clear the screen and depth buffer
        glClearColor( 0.85f, 0.95f, 1f, 1.0f );
        glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT );

        // reset the modelview matrix
        glMatrixMode( GL_MODELVIEW );
        glLoadIdentity();

        // calculate time elapsed
        long newTime = System.currentTimeMillis();
        timeElapsed = Math.min(
                1000f / (float) framesPerSecondLimit.get(), // don't let our time elapsed go over the frame rate limit value
                (float) ( newTime - lastSeenTime ) / 1000f ); // take elapsed milliseconds => seconds

        // walk through all of the mouse events that occurred
        while ( Mouse.next() ) {
            mouseEventNotifier.updateListeners();
        }
        while ( Keyboard.next() ) {
            keyboardEventNotifier.updateListeners();
        }

        // TODO: improve area where the model is updated. Should happen after mouse events (here)

        GLOptions options = new GLOptions();

        glViewport( 0, 0, getCanvasWidth(), getCanvasHeight() );
        setupGuiTransformations();

        rootNode.render( options );

        Display.update();
    }

    @Override public void stop() {
    }

    public LWJGLModelViewTransform getModelViewTransform() {
        return modelViewTransform;
    }

    public CanvasTransform getCanvasTransform() {
        return canvasTransform;
    }

    public float getTimeElapsed() {
        return timeElapsed;
    }

    public Dimension getStageSize() {
        return stageSize;
    }

    public void loadCameraMatrices() {
        glMatrixMode( GL_PROJECTION );
        glLoadIdentity();
        AffineTransform transform = canvasTransform.transform.get();
        glScaled( transform.getScaleX(), transform.getScaleY(), 1 );
        // TODO: scale is still off. examine history here
        gluPerspective( 40, (float) canvasSize.get().width / (float) canvasSize.get().height, 1, 5000 );
        glMatrixMode( GL_MODELVIEW );
        glLoadIdentity();
        debugCameraTransform.apply();
        glRotatef( 13, 1, 0, 0 );
        glTranslatef( 0, -80, -400 );
    }

    public OrthoComponentNode createFPSReadout( final Color color ) {
        JPanel fpsPanel = new JPanel() {{
            setPreferredSize( new Dimension( 100, 30 ) );
            setOpaque( true );
            add( new JLabel( "(FPS here)" ) {{
                setForeground( color );
                framesPerSecond.addObserver( new SimpleObserver() {
                    public void update() {
                        final double fps = Math.round( framesPerSecond.get() * 10 ) / 10;
                        SwingUtilities.invokeLater( new Runnable() {
                            public void run() {
                                setText( "FPS: " + fps );
                            }
                        } );
                    }
                } );
            }} );
        }};
        return new OrthoComponentNode( fpsPanel, this, canvasTransform,
                                       new Property<ImmutableVector2D>( new ImmutableVector2D( stageSize.getWidth() - fpsPanel.getPreferredSize().getWidth(), stageSize.getHeight() - fpsPanel.getPreferredSize().getHeight() ) ), mouseEventNotifier ) {{
            updateOnEvent( beforeFrameRender );
        }};
    }
}
