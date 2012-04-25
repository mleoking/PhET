// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.modules;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
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
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.lwjglphet.math.LWJGLTransform;
import edu.colorado.phet.lwjglphet.math.PlaneF;
import edu.colorado.phet.lwjglphet.math.Ray3F;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.lwjglphet.nodes.GuiNode;
import edu.colorado.phet.lwjglphet.nodes.OrthoComponentNode;
import edu.colorado.phet.lwjglphet.nodes.OrthoPiccoloNode;
import edu.colorado.phet.lwjglphet.nodes.PlanarComponentNode;
import edu.colorado.phet.lwjglphet.shapes.UnitMarker;
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
import edu.colorado.phet.platetectonics.control.Toolbox;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.model.TectonicsClock;
import edu.colorado.phet.platetectonics.model.ToolboxState;
import edu.colorado.phet.platetectonics.view.ColorMode;

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

    private static final float RULER_Z = 0;
    private static final float THERMOMETER_Z = 1;
    private static final float DENSITY_SENSOR_Z = 2;

    // frustum properties
    public static final float fieldOfViewDegrees = 20;
    public static final float nearPlane = 1;
    public static final float farPlane = 21000;

    public final Property<ColorMode> colorMode = new Property<ColorMode>( ColorMode.DENSITY );

    public final Property<Double> zoomRatio = new Property<Double>( 1.0 );

    public final LWJGLTransform sceneProjectionTransform = new LWJGLTransform();
    public final LWJGLTransform sceneModelViewTransform = new LWJGLTransform();

    private Dimension stageSize;

    public final VoidNotifier mouseEventNotifier = new VoidNotifier();
    public final VoidNotifier keyboardEventNotifier = new VoidNotifier();
    public final VoidNotifier beforeFrameRender = new VoidNotifier();
    public final VoidNotifier timeChangeNotifier = new VoidNotifier();

    private LWJGLTransform debugCameraTransform = new LWJGLTransform();
    protected CanvasTransform canvasTransform;
    private LWJGLTransform modelViewTransform;
    private long lastSeenTime;

    // TODO: why sceneNode and sceneLayer?
    public final GLNode rootNode = new GLNode();
    public final GLNode sceneNode = new GLNode();
    protected GLNode sceneLayer;
    protected GLNode guiLayer;
    protected GLNode toolLayer;
    private boolean showWireframe = false;

    private PlateModel model;

    protected final List<OrthoComponentNode> guiNodes = new ArrayList<OrthoComponentNode>();
    protected ToolboxState toolboxState = new ToolboxState();
    protected ToolDragHandler toolDragHandler = new ToolDragHandler( toolboxState );
    protected Toolbox toolbox;
    protected OrthoPiccoloNode draggedCrustPiece = null;

    private final TectonicsClock clock = new TectonicsClock( 1 ); // TODO: work on time multiplier

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

        // commented out code made for picking transformation matrices by using in-sim "flying" controls
//        debugCameraTransform.changed.addListener( new VoidFunction1<LWJGLTransform>() {
//            public void apply( LWJGLTransform debugTransform ) {
//                System.out.println( "debug matrix:\n" + debugTransform.getMatrix() );
//            }
//        } );
    }

    public void initialize() {
        stageSize = initialCanvasSize;
        canvasTransform = new StageCenteringCanvasTransform( canvasSize, stageSize );

        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
                if ( getModel() != null ) {
                    double timeChange = clockEvent.getSimulationTimeChange();
                    getModel().update( timeChange );
                }
            }
        } );

        // show both sides
        glPolygonMode( GL_FRONT, GL_FILL );
        glPolygonMode( GL_BACK, GL_FILL );

        // basic blending function used many places. this is the default
        glBlendFunc( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA );

        // layers
        sceneLayer = new GLNode() {
            {
                requireEnabled( GL_DEPTH_TEST );
            }

            @Override protected void preRender( GLOptions options ) {
                super.preRender( options );

                loadCameraMatrices();
                loadLighting();
            }
        };
        guiLayer = new GuiNode( this );
        toolLayer = new GLNode() {
            {
                requireEnabled( GL_BLEND );
            }

            @Override protected void preRender( GLOptions options ) {
                super.preRender( options );

                loadCameraMatrices();
            }
        };
        rootNode.addChild( sceneLayer );
        rootNode.addChild( guiLayer );
        rootNode.addChild( toolLayer );

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

        /*---------------------------------------------------------------------------*
        * mouse motion
        *----------------------------------------------------------------------------*/
        mouseEventNotifier.addUpdateListener(
                new UpdateListener() {
                    public void update() {
                        updateCursor();

                        if ( Mouse.getEventButton() == -1 ) {
                            // ok, not a button press event

                            if ( draggedCrustPiece != null ) {
                                // scaleX and scaleY should be identical in this case
                                float s = (float) getCanvasTransform().transform.get().getScaleX();
                                draggedCrustPiece.position.set( draggedCrustPiece.position.get().plus(
                                        new ImmutableVector2D( Mouse.getEventDX() / s, -Mouse.getEventDY() / s ) ) );
                                movedCrustPiece( draggedCrustPiece );
                            }
                            else {
                                toolDragHandler.mouseMove( getMousePositionOnZPlane() );
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
                            PlanarComponentNode toolCollision = getToolUnder( Mouse.getEventX(), Mouse.getEventY() );
                            OrthoComponentNode guiCollision = getGuiUnder( Mouse.getEventX(), Mouse.getEventY() );

                            // if mouse is down
                            if ( Mouse.getEventButtonState() ) {
                                if ( guiCollision instanceof OrthoPiccoloNode && ( (OrthoPiccoloNode) guiCollision ).getNode() instanceof CrustPieceNode ) {
                                    draggedCrustPiece = (OrthoPiccoloNode) guiCollision;
                                    pickedCrustPiece( draggedCrustPiece );
                                }
                                else if ( toolCollision != null ) {
                                    toolDragHandler.mouseDownOnTool( (DraggableTool2D) toolCollision, getMousePositionOnZPlane() );
                                }
                                else if ( guiCollision == null ) {
                                    uncaughtMouseButton();
                                }
                            }
                            else {
                                if ( draggedCrustPiece != null ) {
                                    droppedCrustPiece( draggedCrustPiece );
                                    draggedCrustPiece = null;
                                }
                                else if ( toolDragHandler.isDragging() ) {

                                    boolean isMouseOverToolbox = guiCollision != null && guiCollision == toolbox;
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
        toolbox = new Toolbox( this, toolboxState ) {{
            // layout the panel if its size changes (and on startup)
            canvasSize.addObserver( new SimpleObserver() {
                public void update() {
                    position.set( new ImmutableVector2D(
                            10, // left side
                            getStageSize().height - getComponentHeight() - 10 ) ); // offset from bottom
                }
            } );
            updateOnEvent( beforeFrameRender );
        }};
        addGuiNode( toolbox );

        // TODO: handle removal of listeners from

        //TODO: factor out duplicated code in tools
        toolboxState.rulerInToolbox.addObserver( new SimpleObserver() {
            public void update() {
                if ( !toolboxState.rulerInToolbox.get() ) {

                    SimSharingManager.sendUserMessage( UserComponents.ruler, UserComponentTypes.sprite, PlateTectonicsSimSharing.UserActions.removedFromToolbox );

                    // we just "removed" the ruler from the toolbox, so add it to our scene
                    RulerNode3D ruler = new RulerNode3D( getModelViewTransform(), PlateTectonicsTab.this );
                    toolLayer.addChild( ruler );

                    // offset the ruler slightly from the mouse, and start the drag
                    ImmutableVector2F mousePosition = getMousePositionOnZPlane();
                    ImmutableVector2F initialMouseOffset = ruler.getInitialMouseOffset();
                    final float initialX = mousePosition.x - initialMouseOffset.x;
                    final float initialY = mousePosition.y - initialMouseOffset.y;
                    ruler.draggedPosition = new ImmutableVector2F( initialX, initialY );
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
                    ImmutableVector2F mousePosition = getMousePositionOnZPlane();
                    ImmutableVector2F initialMouseOffset = thermometer.getInitialMouseOffset();
                    final float initialX = mousePosition.x - initialMouseOffset.x;
                    final float initialY = mousePosition.y - initialMouseOffset.y;
                    thermometer.draggedPosition = new ImmutableVector2F( initialX, initialY );
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
                    ImmutableVector2F mousePosition = getMousePositionOnZPlane();
                    ImmutableVector2F initialMouseOffset = sensorNode.getInitialMouseOffset();
                    final float initialX = mousePosition.x - initialMouseOffset.x;
                    final float initialY = mousePosition.y - initialMouseOffset.y;
                    sensorNode.draggedPosition = new ImmutableVector2F( initialX, initialY );
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

    @Override public void start() {
        if ( !initialized ) {
            initialize();
            initialized = true;
        }

        lastSeenTime = System.currentTimeMillis();
    }

    public boolean allowClockTickOnFrame() {
        return true;
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
        // NOTE: keep for reference, however the stage centering in this case is being done (for now) exclusively by using the fieldOfViewYFactor
//        AffineTransform affineCanvasTransform = canvasTransform.transform.get();
//
//        ImmutableMatrix4F scalingMatrix = ImmutableMatrix4F.scaling(
//                (float) affineCanvasTransform.getScaleX(),
//                (float) affineCanvasTransform.getScaleY(),
//                1 );

        float fieldOfViewRadians = (float) ( fieldOfViewDegrees / 180f * Math.PI );
        float correctedFieldOfViewRadians = (float) Math.atan( canvasTransform.getFieldOfViewYFactor() * Math.tan( fieldOfViewRadians ) );

        ImmutableMatrix4F perspectiveMatrix = getGluPerspective( correctedFieldOfViewRadians,
                                                                 (float) canvasSize.get().width / (float) canvasSize.get().height,
                                                                 nearPlane, farPlane );
        return perspectiveMatrix;
    }

    public float getSceneDistanceZoomFactor() {
        float minDistance = 1;
        float maxDistance = 35;
        return minDistance + ( getZoomRatio() ) * ( maxDistance - minDistance );
    }

    // compute the camera position, essentially
    public ImmutableMatrix4F getSceneModelViewMatrix() {
        // min == close up
        // max == far away

        float minAngle = 13;
        float maxAngle = 0;
        float minY = -80;
        float maxY = modelViewTransform.transformDeltaY( -PlateModel.CENTER_OF_EARTH_Y / 2 );
        float minZ = -400;
        float maxZ = -400 * 45;
        float ratio = getZoomRatio();
        float angle = minAngle + ratio * ( maxAngle - minAngle );
        return debugCameraTransform.getMatrix()
                .times( ImmutableMatrix4F.rotation( X_UNIT, angle / 180f * (float) Math.PI ) )
                .times( ImmutableMatrix4F.translation( 0, minY + ratio * ratio * ( maxY - minY ), minZ + ratio * ( maxZ - minZ ) ) );
    }

    private float getZoomRatio() {
        final float r = 1 - zoomRatio.get().floatValue();
        return r * r;
    }

    public ImmutableMatrix4F getGluPerspective( float fovYRadians, float aspect, float zNear, float zFar ) {
        float cotangent = (float) Math.cos( fovYRadians ) / (float) Math.sin( fovYRadians );

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
    private FloatBuffer sunDirection = LWJGLUtils.floatBuffer( new float[] { 1, 3, 2, 0 } );
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
                                       new Property<ImmutableVector2D>(
                                               new ImmutableVector2D( stageSize.getWidth() - fpsPanel.getPreferredSize().getWidth() - 200,
                                                                      10 ) ), mouseEventNotifier ) {{
            PlateTectonicsApplication.showFPSMeter.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( PlateTectonicsApplication.showFPSMeter.get() );
                }
            } );
            updateOnEvent( beforeFrameRender );
        }};
    }

    public ImmutableVector2F getMousePositionOnZPlane() {
        Ray3F cameraRay = getCameraRay( Mouse.getEventX(), Mouse.getEventY() );
        final ImmutableVector3F intersection = PlaneF.XY.intersectWithRay( cameraRay );
        return new ImmutableVector2F( intersection.x, intersection.y );
    }

    public ImmutableVector2F getBottomCenterPositionOnZPlane() {
        Ray3F cameraRay = getCameraRay( canvasSize.get().width / 2, 0 );
        final ImmutableVector3F intersection = PlaneF.XY.intersectWithRay( cameraRay );
        return new ImmutableVector2F( intersection.x, intersection.y );
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
        ImmutableVector2F screenPosition = new ImmutableVector2F( screenX, screenY );
        if ( guiNode.isReady() ) {
            ImmutableVector2F componentPoint = guiNode.screentoComponentCoordinates( screenPosition );
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

    public PlanarComponentNode getToolUnder( int x, int y ) {
        // iterate through the tools in reverse (front-to-back) order
        for ( GLNode node : getToolNodes() ) {
            PlanarComponentNode tool = (PlanarComponentNode) node;
            Ray3F cameraRay = getCameraRay( x, y );
            Ray3F localRay = tool.transform.inverseRay( cameraRay );
            if ( tool.doesLocalRayHit( localRay ) ) {
                // TODO: don't hit on the "transparent" parts, like corners
                return tool;
            }
        }
        return null;
    }

    public void updateCursor() {
        final Canvas canvas = getCanvas();

        final PlanarComponentNode toolCollision = getToolUnder( Mouse.getEventX(), Mouse.getEventY() );
        final OrthoComponentNode guiCollision = getGuiUnder( Mouse.getEventX(), Mouse.getEventY() );

        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                Component toolComponent = toolCollision == null ? null : toolCollision.getComponent().getComponent( 0 );
                if ( toolComponent != null ) {
                    canvas.setCursor( toolComponent.getCursor() );
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

    // called when a mouse click is detected that isn't dragging a tool or manipulating a GUI
    protected void uncaughtMouseButton() {

    }
}
