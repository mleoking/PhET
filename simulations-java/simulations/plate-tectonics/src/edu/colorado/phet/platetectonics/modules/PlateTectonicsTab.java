// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.platetectonics.modules;

import java.awt.*;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2F;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.model.event.VoidNotifier;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.lwjglphet.CanvasTransform;
import edu.colorado.phet.lwjglphet.CanvasTransform.StageCenteringCanvasTransform;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.GLOptions.RenderPass;
import edu.colorado.phet.lwjglphet.LWJGLCanvas;
import edu.colorado.phet.lwjglphet.LWJGLTab;
import edu.colorado.phet.lwjglphet.math.ImmutableMatrix4F;
import edu.colorado.phet.lwjglphet.math.LWJGLTransform;
import edu.colorado.phet.lwjglphet.math.PlaneF;
import edu.colorado.phet.lwjglphet.math.Ray3F;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.lwjglphet.nodes.GuiNode;
import edu.colorado.phet.lwjglphet.nodes.OrthoComponentNode;
import edu.colorado.phet.lwjglphet.nodes.OrthoPiccoloNode;
import edu.colorado.phet.lwjglphet.nodes.ThreadedPlanarPiccoloNode;
import edu.colorado.phet.lwjglphet.utils.LWJGLUtils;
import edu.colorado.phet.platetectonics.PlateTectonicsApplication;
import edu.colorado.phet.platetectonics.PlateTectonicsSimSharing;
import edu.colorado.phet.platetectonics.PlateTectonicsSimSharing.ParameterKeys;
import edu.colorado.phet.platetectonics.PlateTectonicsSimSharing.UserComponents;
import edu.colorado.phet.platetectonics.control.CrustPieceNode;
import edu.colorado.phet.platetectonics.control.DensitySensorNode3D;
import edu.colorado.phet.platetectonics.control.DraggableTool2D;
import edu.colorado.phet.platetectonics.control.RulerNode3D;
import edu.colorado.phet.platetectonics.control.ThermometerNode3D;
import edu.colorado.phet.platetectonics.control.ToolDragHandler;
import edu.colorado.phet.platetectonics.control.ToolboxNode;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.model.TectonicsClock;
import edu.colorado.phet.platetectonics.model.ToolboxState;
import edu.colorado.phet.platetectonics.view.ColorMode;

import static edu.colorado.phet.common.phetcommon.math.vector.Vector3F.X_UNIT;
import static edu.colorado.phet.platetectonics.PlateTectonicsConstants.framesPerSecondLimit;
import static org.lwjgl.opengl.GL11.*;

/**
 * Abstract plate tectonics module that consolidates common behavior between the tabs
 */
public abstract class PlateTectonicsTab extends LWJGLTab {

    // tool z-coordinates, so that they do not intersect (causes visual artifacts due to the rounding error in the OpenGL depth map)
    private static final float RULER_Z = 0;
    private static final float THERMOMETER_Z = 1;
    private static final float DENSITY_SENSOR_Z = 2;

    // frustum (camera) properties
    public static final float fieldOfViewDegrees = 20;
    public static final float nearPlane = 1;
    public static final float farPlane = 21000;

    // how the cross-section of the earth should be colored
    public final Property<ColorMode> colorMode = new Property<ColorMode>( ColorMode.DENSITY );

    // for interpolation between both zooming extremes. 1 == fully zoomed in, 0 == fully zoomed out
    public final Property<Double> zoomRatio = new Property<Double>( 1.0 );

    // OpenGL standard transformations
    public final LWJGLTransform sceneProjectionTransform = new LWJGLTransform();
    public final LWJGLTransform sceneModelViewTransform = new LWJGLTransform();

    private Dimension stageSize;

    // event notifications
    public final VoidNotifier mouseEventNotifier = new VoidNotifier();
    public final VoidNotifier keyboardEventNotifier = new VoidNotifier();
    public final VoidNotifier beforeFrameRender = new VoidNotifier();
    public final VoidNotifier timeChangeNotifier = new VoidNotifier();

    // stores the additional camera transformation created by using the debugging camera controls
    private LWJGLTransform debugCameraTransform = new LWJGLTransform();

    // transform from the "stage" to the "canvas", that handles the proper centering transform as in other simulations
    protected CanvasTransform canvasTransform;

    // transform from the model to the view "stage" size
    private LWJGLTransform modelViewTransform;

    // timestamp for calculation of elapsed time between frames
    private long lastSeenTime;

    // the root GL node that underlies everything rendered in 3D
    public final GLNode rootNode = new GLNode();

    // separate layers under the root note that helps us keep things in the correct z-order
    protected GLNode sceneLayer;
    protected GLNode guiLayer;
    protected GLNode toolLayer;

    // whether everything should be displayed as wireframe (we can do this basically globally)
    private boolean showWireframe = false;

    // reference to the model. subclasses of this tab will have references to the specific model subclasses, but this is used for tab-common behavior
    private PlateModel model;

    // list of all orthographic user interfaces (stored here so we can handle mouse events correctly)
    protected final List<OrthoComponentNode> guiNodes = new ArrayList<OrthoComponentNode>();

    // separate "model" state of the toolbox
    protected ToolboxState toolboxState = new ToolboxState();

    // state handling for dragging the tools (sensors / ruler)
    protected ToolDragHandler toolDragHandler = new ToolDragHandler( toolboxState );

    protected ToolboxNode toolboxNode;

    // if we are dragging a crust piece, this will reference it. otherwise, null
    protected OrthoPiccoloNode draggedCrustPiece = null;

    private final TectonicsClock clock = new TectonicsClock( 1 );

    // in seconds
    private float timeElapsed;

    // recorded amount
    public final Property<Double> framesPerSecond = new Property<Double>( 0.0 );
    private final LinkedList<Long> timeQueue = new LinkedList<Long>();

    // we want to wait until LWJGL has loaded fully before initializing
    private boolean initialized = false;

    public PlateTectonicsTab( LWJGLCanvas canvas, String title, float kilometerScale ) {
        super( canvas, title );

        // TODO: better initialization for this model view transform (for each module)
        modelViewTransform = new LWJGLTransform( ImmutableMatrix4F.scaling( kilometerScale / 1000 ) );

        // commented out code made for picking transformation matrices by using in-sim "flying" controls
        // if we want to slightly change the initial angle of view, this code will be extremely helpful
//        debugCameraTransform.changed.addListener( new VoidFunction1<LWJGLTransform>() {
//            public void apply( LWJGLTransform debugTransform ) {
//                System.out.println( "debug matrix:\n" + debugTransform.getMatrix() );
//            }
//        } );
    }

    // main initialization. we want thus run AFTER LWJGL and the canvas have been initialized so we can get the stage size set (and call LWJGL functions)
    public void initialize() {

        // attempt to set the stage size to the canvas size, so we can get 1-to-1 pixel mapping for the UI (without needing to scale) if possible
        stageSize = initialCanvasSize;
        if ( Math.abs( stageSize.getWidth() - 1008 ) > 20 || Math.abs( stageSize.getHeight() - 676 ) > 20 ) {
            // if our stage size is far enough away from pixel-perfect graphics on the initial canvas size, simply set to the default
            stageSize = new Dimension( 1008, 676 );
        }
        canvasTransform = new StageCenteringCanvasTransform( canvasSize, stageSize );

        // forward clock events to the model
        clock.addClockListener( new ClockAdapter() {
            @Override
            public void clockTicked( ClockEvent clockEvent ) {
                if ( getModel() != null ) {
                    double timeChange = clockEvent.getSimulationTimeChange();
                    getModel().update( timeChange );
                }
            }
        } );

        // show both sides of polygons by default
        glPolygonMode( GL_FRONT, GL_FILL );
        glPolygonMode( GL_BACK, GL_FILL );

        // basic blending function used many places. this is the default
        glBlendFunc( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA );

        // layers
        sceneLayer = new GLNode() {
            {
                // allow shapes to intersect in the scene
                requireEnabled( GL_DEPTH_TEST );
            }

            @Override
            protected void preRender( GLOptions options ) {
                super.preRender( options );

                loadCameraMatrices();
                loadLighting();
            }
        };
        guiLayer = new GuiNode( this );
        toolLayer = new GLNode() {
            {
                // allow tools to blend with the background
                requireEnabled( GL_BLEND );
            }

            @Override
            protected void preRender( GLOptions options ) {
                super.preRender( options );

                // don't load lighting, tools use their own colors
                loadCameraMatrices();
            }
        };
        rootNode.addChild( sceneLayer );
        rootNode.addChild( guiLayer );
        rootNode.addChild( toolLayer );

        /*---------------------------------------------------------------------------*
        * debug camera controls
        *----------------------------------------------------------------------------*/

        // Q + left mouse button drag = pan
        // Q + right mouse button drag = yaw
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

        // show wireframe while F key is pressed
        keyboardEventNotifier.addUpdateListener(
                new UpdateListener() {
                    public void update() {
                        if ( Keyboard.getEventKey() == Keyboard.KEY_F ) {
                            showWireframe = Keyboard.getEventKeyState();
                        }
                    }
                }, false );

        // use standard WASD movement within the scene
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
        * mouse motion
        *----------------------------------------------------------------------------*/
        mouseEventNotifier.addUpdateListener(
                new UpdateListener() {
                    public void update() {
                        updateCursor();

                        if ( Mouse.getEventButton() == -1 ) {
                            // ok, not a button press event (== move)

                            if ( draggedCrustPiece != null ) {
                                // scaleX and scaleY should be identical in this case
                                float s = (float) getCanvasTransform().transform.get().getScaleX();
                                draggedCrustPiece.position.set( draggedCrustPiece.position.get().plus(
                                        new Vector2D( Mouse.getEventDX() / s, -Mouse.getEventDY() / s ) ) );
                                movedCrustPiece( draggedCrustPiece );
                            }
                            else {
                                toolDragHandler.mouseMove( getMouseViewPositionOnZPlane() );
                            }
                        }
                    }

                }, false );

        /*---------------------------------------------------------------------------*
        * mouse-button presses
        *----------------------------------------------------------------------------*/
        mouseEventNotifier.addUpdateListener(
                new UpdateListener() {
                    public void update() {
                        // on left mouse button change
                        if ( Mouse.getEventButton() == 0 ) {
                            ThreadedPlanarPiccoloNode toolCollision = getToolUnder( Mouse.getEventX(), Mouse.getEventY() );
                            OrthoComponentNode guiCollision = getGuiUnder( Mouse.getEventX(), Mouse.getEventY() );

                            // if mouse is down
                            if ( Mouse.getEventButtonState() ) {
                                if ( guiCollision instanceof OrthoPiccoloNode && ( (OrthoPiccoloNode) guiCollision ).getNode() instanceof CrustPieceNode ) {
                                    draggedCrustPiece = (OrthoPiccoloNode) guiCollision;
                                    pickedCrustPiece( draggedCrustPiece );
                                }
                                else if ( toolCollision != null ) {
                                    toolDragHandler.mouseDownOnTool( (DraggableTool2D) toolCollision, getMouseViewPositionOnZPlane() );
                                }
                                else if ( guiCollision == null ) {
                                    uncaughtMouseButton();
                                }
                            }
                            else {
                                // mouse being released
                                if ( draggedCrustPiece != null ) {
                                    droppedCrustPiece( draggedCrustPiece );
                                    draggedCrustPiece = null;
                                }
                                else if ( toolDragHandler.isDragging() ) {

                                    boolean isMouseOverToolbox = guiCollision != null && guiCollision == toolboxNode;
                                    toolDragHandler.mouseUp( isMouseOverToolbox );
                                    // TODO: remove the "removed" tool from the guiNodes list
                                }
                                else {
                                    uncaughtMouseButton();
                                }
                            }
                        }
                    }
                }, false );

        /*---------------------------------------------------------------------------*
        * toolbox
        *----------------------------------------------------------------------------*/
        toolboxNode = new ToolboxNode( this, toolboxState ) {{
            // layout the panel if its size changes (and on startup)
            canvasSize.addObserver( new SimpleObserver() {
                public void update() {
                    position.set( new Vector2D(
                            10, // left side
                            getStageSize().height - getComponentHeight() - 10 ) ); // offset from bottom
                }
            } );
            updateOnEvent( beforeFrameRender );
        }};
        addGuiNode( toolboxNode );

        //TODO: factor out duplicated code in tools
        toolboxState.rulerInToolbox.addObserver( new SimpleObserver() {
            public void update() {
                if ( !toolboxState.rulerInToolbox.get() ) {

                    SimSharingManager.sendUserMessage( UserComponents.ruler, UserComponentTypes.sprite, PlateTectonicsSimSharing.UserActions.removedFromToolbox );

                    // we just "removed" the ruler from the toolbox, so add it to our scene
                    RulerNode3D ruler = new RulerNode3D( getModelViewTransform(), PlateTectonicsTab.this );
                    toolLayer.addChild( ruler );

                    // offset the ruler slightly from the mouse, and start the drag
                    Vector2F mousePosition = getMouseViewPositionOnZPlane();
                    Vector2F initialMouseOffset = ruler.getInitialMouseOffset();
                    final float initialX = mousePosition.x - initialMouseOffset.x;
                    final float initialY = mousePosition.y - initialMouseOffset.y;
                    ruler.draggedPosition = new Vector2F( initialX, initialY );
                    ruler.transform.prepend( ImmutableMatrix4F.translation( initialX,
                                                                            initialY,
                                                                            RULER_Z ) );
                    toolDragHandler.startDragging( ruler, mousePosition );
                }
            }
        } );

        toolboxState.thermometerInToolbox.addObserver( new SimpleObserver() {
            public void update() {
                if ( !toolboxState.thermometerInToolbox.get() ) {

                    SimSharingManager.sendUserMessage( UserComponents.thermometer, UserComponentTypes.sprite, PlateTectonicsSimSharing.UserActions.removedFromToolbox );

                    // we just "removed" the ruler from the toolbox, so add it to our scene
                    ThermometerNode3D thermometer = new ThermometerNode3D( getModelViewTransform(), PlateTectonicsTab.this, model );
                    toolLayer.addChild( thermometer );

                    // offset the ruler slightly from the mouse, and start the drag
                    Vector2F mousePosition = getMouseViewPositionOnZPlane();
                    Vector2F initialMouseOffset = thermometer.getInitialMouseOffset();
                    final float initialX = mousePosition.x - initialMouseOffset.x;
                    final float initialY = mousePosition.y - initialMouseOffset.y;
                    thermometer.draggedPosition = new Vector2F( initialX, initialY );
                    thermometer.transform.prepend( ImmutableMatrix4F.translation( initialX,
                                                                                  initialY,
                                                                                  THERMOMETER_Z ) );

                    toolDragHandler.startDragging( thermometer, mousePosition );
                }
            }
        } );

        toolboxState.densitySensorInToolbox.addObserver( new SimpleObserver() {
            public void update() {
                if ( !toolboxState.densitySensorInToolbox.get() ) {

                    SimSharingManager.sendUserMessage( UserComponents.densityMeter, UserComponentTypes.sprite, PlateTectonicsSimSharing.UserActions.removedFromToolbox );

                    // we just "removed" the ruler from the toolbox, so add it to our scene
                    DensitySensorNode3D sensorNode = new DensitySensorNode3D( getModelViewTransform(), PlateTectonicsTab.this, model );
                    toolLayer.addChild( sensorNode );

                    // offset the ruler slightly from the mouse, and start the drag
                    Vector2F mousePosition = getMouseViewPositionOnZPlane();
                    Vector2F initialMouseOffset = sensorNode.getInitialMouseOffset();
                    final float initialX = mousePosition.x - initialMouseOffset.x;
                    final float initialY = mousePosition.y - initialMouseOffset.y;
                    sensorNode.draggedPosition = new Vector2F( initialX, initialY );
                    sensorNode.transform.prepend( ImmutableMatrix4F.translation( initialX,
                                                                                 initialY,
                                                                                 DENSITY_SENSOR_Z ) );

                    toolDragHandler.startDragging( sensorNode, mousePosition );
                }
            }
        } );
    }

    public PlateModel getModel() {
        return model;
    }

    protected void setModel( PlateModel model ) {
        this.model = model;
    }

    @Override
    public void start() {
        if ( !initialized ) {
            initialize();
            initialized = true;
        }

        lastSeenTime = System.currentTimeMillis();
    }

    public boolean allowClockTickOnFrame() {
        return true;
    }

    @Override
    public void loop() {
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

        // calculate time elapsed
        long newTime = System.currentTimeMillis();
        timeElapsed = Math.min(
                1000f / (float) framesPerSecondLimit.get(), // don't let our time elapsed go over the frame rate limit value
                (float) ( newTime - lastSeenTime ) / 1000f ); // take elapsed milliseconds => seconds
        lastSeenTime = newTime;

        timeChangeNotifier.updateListeners();

        if ( allowClockTickOnFrame() ) {
            clock.stepByWallSeconds( timeElapsed );
        }

        // force updating of matrices before the callbacks are run, so we have correct ray picking
        loadCameraMatrices();

        beforeFrameRender.updateListeners();

        // Clear the screen and depth buffer
        glClearColor( 0.85f, 0.95f, 1f, 1.0f );
        glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT );

        // reset the modelview matrix
        glMatrixMode( GL_MODELVIEW );
        glLoadIdentity();

        // walk through all of the mouse events that occurred
        while ( Mouse.next() ) {
            mouseEventNotifier.updateListeners();
        }
        while ( Keyboard.next() ) {
            keyboardEventNotifier.updateListeners();
        }

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

        Display.update();
    }

    @Override
    public void stop() {
    }

    public LWJGLTransform getModelViewTransform() {
        return modelViewTransform;
    }

    public CanvasTransform getCanvasTransform() {
        return canvasTransform;
    }

    public Vector3F viewToPlanarModel( Vector3F position ) {
        // TODO: turn these into a sub-class of LWJGLTransform that applies the planar/radial transform
        return PlateModel.convertToPlanar( modelViewTransform.inversePosition( position ) );
    }

    public Vector3F planarModelToView( Vector3F position ) {
        return getModelViewTransform().transformPosition( PlateModel.convertToRadial( position ) );
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
        // NOTE: keep for reference, however the stage centering in this case is being done (for now) exclusively by using the fieldOfViewYFactor
//        AffineTransform affineCanvasTransform = canvasTransform.transform.get();
//
//        ImmutableMatrix4F scalingMatrix = ImmutableMatrix4F.scaling(
//                (float) affineCanvasTransform.getScaleX(),
//                (float) affineCanvasTransform.getScaleY(),
//                1 );

        // compute our field of view to match zoom
        float fieldOfViewRadians = (float) ( fieldOfViewDegrees / 180f * Math.PI );
        float correctedFieldOfViewRadians = (float) Math.atan( canvasTransform.getFieldOfViewYFactor() * Math.tan( fieldOfViewRadians ) );

        ImmutableMatrix4F perspectiveMatrix = getGluPerspective( correctedFieldOfViewRadians,
                                                                 (float) canvasSize.get().width / (float) canvasSize.get().height,
                                                                 nearPlane, farPlane );
        return perspectiveMatrix;
    }

    // multiplier for how far the camera is to the scene, depending on the zoom
    public float getSceneDistanceZoomFactor() {
        float minDistance = 1;
        float maxDistance = 35;
        return minDistance + ( getEffectiveZoomRatio() ) * ( maxDistance - minDistance );
    }

    // compute the camera position, essentially
    public ImmutableMatrix4F getSceneModelViewMatrix() {
        // min == close up
        // max == far away

        // here we interpolate nonlinearly between the "zoomed in" and "zoomed out" views, keeping the top of the crust in view at all times
        float minAngle = 13;
        float maxAngle = 0;
        float minY = -80;
        float maxY = modelViewTransform.transformDeltaY( -PlateModel.CENTER_OF_EARTH_Y / 2 );
        float minZ = -400;
        float maxZ = -400 * 45;
        float ratio = getEffectiveZoomRatio();
        float angle = minAngle + ratio * ( maxAngle - minAngle );
        return debugCameraTransform.getMatrix()
                .times( ImmutableMatrix4F.rotation( X_UNIT, angle / 180f * (float) Math.PI ) )
                .times( ImmutableMatrix4F.translation( 0, minY + ratio * ratio * ( maxY - minY ), minZ + ratio * ( maxZ - minZ ) ) );
    }

    // nonlinear function for nice zoom effect
    private float getEffectiveZoomRatio() {
        final float r = 1 - zoomRatio.get().floatValue();
        return r * r;
    }

    // the GLUT perspective function did not allow the proper FOV options that we needed, so here is an equivalent version that is simpler
    public ImmutableMatrix4F getGluPerspective( float fovYRadians, float aspect, float zNear, float zFar ) {
        float cotangent = (float) Math.cos( fovYRadians ) / (float) Math.sin( fovYRadians );

        return ImmutableMatrix4F.rowMajor( cotangent / aspect, 0, 0, 0,
                                           0, cotangent, 0, 0,
                                           0, 0, ( zFar + zNear ) / ( zNear - zFar ), ( 2 * zFar * zNear ) / ( zNear - zFar ),
                                           0, 0, -1, 0 );
    }

    // in view coordinates
    public Vector3F getCameraPosition() {
        return sceneModelViewTransform.getInverse().times( new Vector3F( 0, 0, 0 ) );
    }

    // given the mouse position by LWJGL, compute a ray from the camera to where
    public Ray3F getCameraRay( int mouseX, int mouseY ) {
        // the terms in this function should be googleable
        Vector3F normalizedDeviceCoordinates = new Vector3F(
                2 * mouseX / (float) getCanvasWidth() - 1,
                2 * mouseY / (float) getCanvasHeight() - 1,
                1 );

        Vector3F eyeCoordinates = sceneProjectionTransform.getInverse().times( normalizedDeviceCoordinates );

        Vector3F objectCoordinatesA = sceneModelViewTransform.getInverse().times( eyeCoordinates );
        Vector3F objectCoordinatesB = sceneModelViewTransform.getInverse().times( eyeCoordinates.times( 2 ) );

        Ray3F cameraRay = new Ray3F( objectCoordinatesA, objectCoordinatesB.minus( objectCoordinatesA ) );
        return cameraRay;
    }

    // debugging utility for printing out floatbuffer values
    public static String bufferString( FloatBuffer buffer ) {
        StringBuilder builder = new StringBuilder();
        buffer.rewind();
        while ( buffer.hasRemaining() ) {
            builder.append( buffer.get() + " " );
        }
        return builder.toString();
    }

    // similar to gluUnProject
    public Vector3F getNormalizedDeviceCoordinates( Vector3F screenCoordinates ) {
        return new Vector3F( 2 * screenCoordinates.x / (float) getCanvasWidth() - 1,
                             2 * screenCoordinates.y / (float) getCanvasHeight() - 1,
                             2 * screenCoordinates.z - 1 );
    }

    private FloatBuffer specular = LWJGLUtils.floatBuffer( new float[]{0, 0, 0, 0} );
    private FloatBuffer shininess = LWJGLUtils.floatBuffer( new float[]{50} );
    private FloatBuffer sunDirection = LWJGLUtils.floatBuffer( new float[]{1, 3, 2, 0} );
    private FloatBuffer moonDirection = LWJGLUtils.floatBuffer( new float[]{-2, 1, -1, 0} );

    public void loadLighting() {
        glMaterial( GL_FRONT, GL_SPECULAR, specular );
//            glMaterial( GL_FRONT, GL_SHININESS, shininess );
        glLight( GL_LIGHT0, GL_POSITION, sunDirection );
        glLight( GL_LIGHT1, GL_POSITION, moonDirection );
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
                                       new Property<Vector2D>(
                                               new Vector2D( stageSize.getWidth() - fpsPanel.getPreferredSize().getWidth() - 200,
                                                             10 ) ), mouseEventNotifier ) {{
            PlateTectonicsApplication.showFPSMeter.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( PlateTectonicsApplication.showFPSMeter.get() );
                }
            } );
            updateOnEvent( beforeFrameRender );
        }};
    }

    public Vector2F getMouseViewPositionOnZPlane() {
        Ray3F cameraRay = getCameraRay( Mouse.getEventX(), Mouse.getEventY() );
        final Vector3F intersection = PlaneF.XY.intersectWithRay( cameraRay );
        return new Vector2F( intersection.x, intersection.y );
    }

    // x and y [0,1] with 0,0 bottom left
    public Vector2F getViewPositionOnZPlane( float x, float y ) {
        Ray3F cameraRay = getCameraRay( (int) ( canvasSize.get().width * x ), (int) ( canvasSize.get().height * y ) );
        final Vector3F intersection = PlaneF.XY.intersectWithRay( cameraRay );
        return new Vector2F( intersection.x, intersection.y );
    }

    // view coordinates for where the bottom center of the screen hits the z=0 plane
    public Vector2F getBottomCenterPositionOnPlane( PlaneF plane ) {
        Ray3F cameraRay = getCameraRay( canvasSize.get().width / 2, 0 );
        final Vector3F intersection = plane.intersectWithRay( cameraRay );
        return new Vector2F( intersection.x, intersection.y );
    }

    public void addGuiNode( OrthoComponentNode node ) {
        guiLayer.addChild( node );
        guiNodes.add( node );
    }

    public OrthoComponentNode getGuiUnder( int x, int y ) {
        for ( OrthoComponentNode guiNode : guiNodes ) {
            if ( isGuiUnder( guiNode, x, y ) ) {
                return guiNode;
            }
        }
        return null;
    }

    public boolean isGuiUnder( OrthoComponentNode guiNode, int screenX, int screenY ) {
        Vector2F screenPosition = new Vector2F( screenX, screenY );
        if ( guiNode.isReady() ) {
            Vector2F componentPoint = guiNode.screentoComponentCoordinates( screenPosition );
            if ( guiNode.getComponent().contains( (int) componentPoint.x, (int) componentPoint.y ) ) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<GLNode> getToolNodes() {
        return new ArrayList<GLNode>( toolLayer.getChildren() ) {{
            Collections.reverse( this );
        }};
    }

    public ThreadedPlanarPiccoloNode getToolUnder( int x, int y ) {
        // iterate through the tools in reverse (front-to-back) order
        for ( GLNode node : getToolNodes() ) {
            ThreadedPlanarPiccoloNode tool = (ThreadedPlanarPiccoloNode) node;
            Ray3F cameraRay = getCameraRay( x, y );
            Ray3F localRay = tool.transform.inverseRay( cameraRay );
            if ( tool.doesLocalRayHit( localRay ) ) {
                return tool;
            }
        }
        return null;
    }

    public void updateCursor() {
        final Canvas canvas = getCanvas();

        final ThreadedPlanarPiccoloNode toolCollision = getToolUnder( Mouse.getEventX(), Mouse.getEventY() );
        final OrthoComponentNode guiCollision = getGuiUnder( Mouse.getEventX(), Mouse.getEventY() );

        // swing-related calls need to be done in this thread. unfortunately this DOES introduce a delay in the shown cursor.
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                if ( toolCollision != null ) {
                    canvas.setCursor( toolCollision.getCursor() );
                }
                else {
                    // this component is actually possibly a sub-component
                    Component guiComponent = guiCollision == null ? null : guiCollision.getComponentAt( Mouse.getX(), Mouse.getY() );
                    if ( guiComponent != null ) {
                        // over a HUD node, so set the cursor to what the component would want
                        canvas.setCursor( guiComponent.getCursor() );
                    }
                    else {
                        // default to the default cursor, unless it is overridden
                        uncaughtCursor();
                    }
                }
            }
        } );
    }

    protected void uncaughtCursor() {
        // default to the default cursor
        getCanvas().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
    }

    public void pickedCrustPiece( OrthoPiccoloNode crustPiece ) {
        CrustPieceNode piece = (CrustPieceNode) crustPiece.getNode();
        // overridden in PlateMotionTab
        SimSharingManager.sendUserMessage( UserComponents.crustPiece, UserComponentTypes.sprite, edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions.startDrag,
                                           new ParameterSet( new Parameter( ParameterKeys.plateType, piece.type.toString() ) ) );
    }

    public void movedCrustPiece( OrthoPiccoloNode crustPiece ) {
        // overridden in PlateMotionTab
    }

    public void droppedCrustPiece( OrthoPiccoloNode crustPiece ) {
        // overridden in PlateMotionTab
    }

    public void resetAll() {
        zoomRatio.reset();
        debugCameraTransform.set( ImmutableMatrix4F.IDENTITY );
        model.resetAll();
        colorMode.reset();

        // return any tools that are out
        for ( GLNode glNode : getToolNodes() ) {
            DraggableTool2D tool = (DraggableTool2D) glNode;

            if ( !tool.getInsideToolboxProperty( toolboxState ).get() ) {
                tool.recycle();

                // mark the toolbox as having the tool again
                tool.getInsideToolboxProperty( toolboxState ).set( true );
            }
        }
    }

    public TectonicsClock getClock() {
        return clock;
    }

    // called when a mouse click is detected that isn't dragging a tool or manipulating a GUI (subclass wants to use mouse behavior in scene)
    protected void uncaughtMouseButton() {

    }

    // if the range of a label goes off of the screen, we center it within the on-screen portion
    public Property<Vector3F> getLabelPosition( final Property<Vector3F> aProp,
                                                final Property<Vector3F> bProp,
                                                final Property<Float> scaleProperty ) {
        return new Property<Vector3F>( new Vector3F() ) {{
            final SimpleObserver observer = new SimpleObserver() {
                public void update() {
                    final PlaneF plane = new PlaneF( new Vector3F( 0, 0, 1 ), aProp.get().getZ() );
                    Vector2F screenBottom = getBottomCenterPositionOnPlane( plane );
                    if ( bProp.get().y < screenBottom.y ) {
                        // use the screen bottom, the actual bottom is too low
                        float averageY = ( screenBottom.y + aProp.get().y ) / 2;
                        float ratio = ( averageY - aProp.get().y ) / ( bProp.get().y - aProp.get().y );
                        set( aProp.get().times( 1 - ratio ).plus( bProp.get().times( ratio ) ) );
                    }
                    else {
                        set( aProp.get().plus( bProp.get() ).times( 0.5f ) );
                    }
                }
            };

            scaleProperty.addObserver( observer );
            beforeFrameRender.addUpdateListener( new UpdateListener() {
                public void update() {
                    observer.update();
                }
            }, false );
        }};
    }

    public boolean isWaterVisible() {
        return true;
    }
}
