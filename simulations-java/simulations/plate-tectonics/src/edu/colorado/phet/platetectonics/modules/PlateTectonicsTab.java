// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.modules;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.nio.FloatBuffer;
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
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.GLOptions.RenderPass;
import edu.colorado.phet.lwjglphet.LWJGLCanvas;
import edu.colorado.phet.lwjglphet.LWJGLTab;
import edu.colorado.phet.lwjglphet.math.ImmutableMatrix4F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.lwjglphet.math.LWJGLTransform;
import edu.colorado.phet.lwjglphet.math.PlaneF;
import edu.colorado.phet.lwjglphet.math.Ray3F;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.lwjglphet.nodes.OrthoComponentNode;
import edu.colorado.phet.lwjglphet.shapes.UnitMarker;
import edu.colorado.phet.lwjglphet.utils.LWJGLUtils;

import static edu.colorado.phet.lwjglphet.math.ImmutableVector3F.X_UNIT;
import static edu.colorado.phet.platetectonics.PlateTectonicsConstants.framesPerSecondLimit;
import static org.lwjgl.opengl.GL11.*;

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

    // frustum properties
    public static final float fieldOfViewDegrees = 40;
    public static final float nearPlane = 1;
    public static final float farPlane = 5000;

    public final LWJGLTransform sceneProjectionTransform = new LWJGLTransform();
    public final LWJGLTransform sceneModelViewTransform = new LWJGLTransform();

    private Dimension stageSize;

    public final VoidNotifier mouseEventNotifier = new VoidNotifier();
    public final VoidNotifier keyboardEventNotifier = new VoidNotifier();
    public final VoidNotifier beforeFrameRender = new VoidNotifier();

    private LWJGLTransform debugCameraTransform = new LWJGLTransform();
    protected CanvasTransform canvasTransform;
    private LWJGLTransform modelViewTransform;
    private long lastSeenTime;
    public final GLNode rootNode = new GLNode();
    public final GLNode sceneNode = new GLNode();
    private boolean showWireframe = false;

    // in seconds
    private float timeElapsed;

    // recorded amount
    public final Property<Double> framesPerSecond = new Property<Double>( 0.0 );
    private final LinkedList<Long> timeQueue = new LinkedList<Long>();

    private boolean initialized = false;

    public PlateTectonicsTab( LWJGLCanvas canvas, String title, float kilometerScale ) {
        super( canvas, title );

        // TODO: better initialization for this model view transform (for each module)
        modelViewTransform = new LWJGLTransform( ImmutableMatrix4F.scaling( kilometerScale / 1000 ) );
    }

    public void initialize() {
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
                                debugCameraTransform.prepend( ImmutableMatrix4F.rotationY( (float) dx / 100 ) );
                                debugCameraTransform.prepend( ImmutableMatrix4F.rotationX( (float) -dy / 100 ) );
                            }
                            else if ( Mouse.isButtonDown( 1 ) ) {
                                debugCameraTransform.prepend( ImmutableMatrix4F.rotationZ( (float) dx / 100 ) );
                            }
                        }
                    }
                }, false );
        keyboardEventNotifier.addUpdateListener(
                new UpdateListener() {
                    public void update() {
                        if ( Keyboard.getEventKey() == Keyboard.KEY_F ) {
                            showWireframe = Keyboard.getEventKeyState();
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

        /*---------------------------------------------------------------------------*
        * debugging marker on z=0 plane, press "M" and click
        *----------------------------------------------------------------------------*/
        mouseEventNotifier.addUpdateListener(
                new UpdateListener() {
                    public void update() {
                        // LMB down
                        if ( Mouse.getEventButton() == 0 && Mouse.getEventButtonState() && Keyboard.isKeyDown( Keyboard.KEY_M ) ) {
                            Ray3F cameraRay = getCameraRay( Mouse.getEventX(), Mouse.getEventY() );
                            final ImmutableVector3F intersection = PlaneF.XY.intersectWithRay( cameraRay );

                            sceneNode.addChild( new UnitMarker() {{
                                translate( intersection.x, intersection.y, intersection.z );
                                scale( 10 );
                            }} );
                        }
                    }
                }, false );
    }

    @Override public void start() {
        if ( !initialized ) {
            initialize();
            initialized = true;
        }

        lastSeenTime = System.currentTimeMillis();
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

        if ( showWireframe ) {
            options.forWireframe = true;
            glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
        }
        else {
            glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );
        }

        glViewport( 0, 0, getCanvasWidth(), getCanvasHeight() );
        setupGuiTransformations();

        options.renderPass = RenderPass.REGULAR;
        rootNode.render( options );
        options.renderPass = RenderPass.TRANSPARENCY;
        rootNode.render( options );

        // kind of a debugging node
        loadCameraMatrices();
        sceneNode.render( new GLOptions() );

        Display.update();
    }

    @Override public void stop() {
    }

    public LWJGLTransform getModelViewTransform() {
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
        sceneProjectionTransform.set( getSceneProjectionMatrix() );
        sceneProjectionTransform.apply();

        glMatrixMode( GL_MODELVIEW );
        glLoadIdentity();
        sceneModelViewTransform.set( getSceneModelViewMatrix() );
        sceneModelViewTransform.apply();
    }

    public ImmutableMatrix4F getSceneProjectionMatrix() {
        AffineTransform affineCanvasTransform = canvasTransform.transform.get();

        // TODO: scale is still off. examine history here
        ImmutableMatrix4F scalingMatrix = ImmutableMatrix4F.scaling(
                (float) affineCanvasTransform.getScaleX(),
                (float) affineCanvasTransform.getScaleY(),
                1 );

        ImmutableMatrix4F perspectiveMatrix = getGluPerspective( fieldOfViewDegrees,
                                                                 (float) canvasSize.get().width / (float) canvasSize.get().height,
                                                                 nearPlane, farPlane );
        return scalingMatrix.times( perspectiveMatrix );
    }

    public ImmutableMatrix4F getSceneModelViewMatrix() {
        return debugCameraTransform.getMatrix()
                .times( ImmutableMatrix4F.rotation( X_UNIT, 13 / 180f * (float) Math.PI ) )
                .times( ImmutableMatrix4F.translation( 0, -80, -400 ) );
    }

    public static ImmutableMatrix4F getGluPerspective( float fovy, float aspect, float zNear, float zFar ) {
        float fovAngle = (float) ( fovy / 2 * Math.PI / 180 );
        float cotangent = (float) Math.cos( fovAngle ) / (float) Math.sin( fovAngle );

        return ImmutableMatrix4F.rowMajor( cotangent / aspect, 0, 0, 0,
                                           0, cotangent, 0, 0,
                                           0, 0, ( zFar + zNear ) / ( zNear - zFar ), ( 2 * zFar * zNear ) / ( zNear - zFar ),
                                           0, 0, -1, 0 );
    }

    public Ray3F getCameraRay( int mouseX, int mouseY ) {
        ImmutableVector3F normalizedDeviceCoordinates = new ImmutableVector3F(
                2 * mouseX / (float) getCanvasWidth() - 1,
                2 * mouseY / (float) getCanvasHeight() - 1,
                1 );

        ImmutableVector3F eyeCoordinates = sceneProjectionTransform.getInverse().times( normalizedDeviceCoordinates );

        ImmutableVector3F objectCoordinatesA = sceneModelViewTransform.getInverse().times( eyeCoordinates );
        ImmutableVector3F objectCoordinatesB = sceneModelViewTransform.getInverse().times( eyeCoordinates.times( 2 ) );

        Ray3F cameraRay = new Ray3F( objectCoordinatesA, objectCoordinatesB.minus( objectCoordinatesA ) );
        return cameraRay;
    }

    public static String bufferString( FloatBuffer buffer ) {
        StringBuilder builder = new StringBuilder();
        buffer.rewind();
        while ( buffer.hasRemaining() ) {
            builder.append( buffer.get() + " " );
        }
        return builder.toString();
    }

    // similar to gluUnProject
    public ImmutableVector3F getNormalizedDeviceCoordinates( ImmutableVector3F screenCoordinates ) {
        return new ImmutableVector3F( 2 * screenCoordinates.x / (float) getCanvasWidth() - 1,
                                      2 * screenCoordinates.y / (float) getCanvasHeight() - 1,
                                      2 * screenCoordinates.z - 1 );
    }

    private FloatBuffer specular = LWJGLUtils.floatBuffer( new float[] { 0, 0, 0, 0 } );
    private FloatBuffer shininess = LWJGLUtils.floatBuffer( new float[] { 50 } );
    private FloatBuffer sunDirection = LWJGLUtils.floatBuffer( new float[] { 1, 3, -2, 0 } );
    private FloatBuffer moonDirection = LWJGLUtils.floatBuffer( new float[] { -2, 1, -1, 0 } );

    public void loadLighting() {
        /*
        final DirectionalLight sun = new DirectionalLight();
        sun.setDirection( new Vector3f( 1, 3f, -2 ).normalizeLocal() );
        sun.setColor( new ColorRGBA( 1, 1, 1, 1.3f ) );
        node.addLight( sun );

        final DirectionalLight moon = new DirectionalLight();
        moon.setDirection( new Vector3f( -2, 1, -1 ).normalizeLocal() );
        moon.setColor( new ColorRGBA( 1, 1, 1, 0.5f ) );
        node.addLight( moon );
         */

        glMaterial( GL_FRONT, GL_SPECULAR, specular );
//            glMaterial( GL_FRONT, GL_SHININESS, shininess );
        glLight( GL_LIGHT0, GL_POSITION, sunDirection );
        glLight( GL_LIGHT1, GL_POSITION, moonDirection );
//        glEnable( GL_LIGHTING );
        glEnable( GL_LIGHT0 );
        glEnable( GL_LIGHT1 );
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

    public ImmutableVector2F getMousePositionOnZPlane() {
        Ray3F cameraRay = getCameraRay( Mouse.getEventX(), Mouse.getEventY() );
        final ImmutableVector3F intersection = PlaneF.XY.intersectWithRay( cameraRay );
        return new ImmutableVector2F( intersection.x, intersection.y );
    }
}
