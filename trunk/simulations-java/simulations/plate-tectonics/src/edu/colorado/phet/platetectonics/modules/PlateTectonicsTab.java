// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.modules;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import edu.colorado.phet.common.phetcommon.model.event.VoidNotifier;
import edu.colorado.phet.lwjglphet.CanvasTransform;
import edu.colorado.phet.lwjglphet.CanvasTransform.StageCenteringCanvasTransform;
import edu.colorado.phet.lwjglphet.GLNode;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.LWJGLCanvas;
import edu.colorado.phet.lwjglphet.LWJGLTab;
import edu.colorado.phet.lwjglphet.math.ImmutableMatrix4F;
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
    public final VoidNotifier beforeFrameRender = new VoidNotifier();

    protected CanvasTransform canvasTransform;
    private LWJGLModelViewTransform modelViewTransform;
    private long lastSeenTime;
    public final GLNode rootNode = new GLNode();

    // in seconds
    private float timeElapsed;

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

        // TODO: add in lighting for consistency
    }

    @Override public void loop() {
        // delay if we need to, limiting our FPS
        Display.sync( framesPerSecondLimit.get() );

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
//        glRotatef( 90, 0, 1, 0 );
        glTranslatef( 0, -20, -500 );
    }
}
