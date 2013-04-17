// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.moleculeshapes.tabs;

import java.awt.*;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import edu.colorado.phet.common.phetcommon.math.Matrix4F;
import edu.colorado.phet.common.phetcommon.math.PlaneF;
import edu.colorado.phet.common.phetcommon.math.QuaternionF;
import edu.colorado.phet.common.phetcommon.math.Ray3F;
import edu.colorado.phet.common.phetcommon.math.SphereF;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2F;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;
import edu.colorado.phet.common.phetcommon.math.vector.Vector4F;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.model.event.VoidNotifier;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction2;
import edu.colorado.phet.lwjglphet.CanvasTransform;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.LWJGLCanvas;
import edu.colorado.phet.lwjglphet.LWJGLTab;
import edu.colorado.phet.lwjglphet.math.LWJGLTransform;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.lwjglphet.nodes.GuiNode;
import edu.colorado.phet.lwjglphet.nodes.OrthoSwingNode;
import edu.colorado.phet.lwjglphet.utils.LWJGLUtils;
import edu.colorado.phet.moleculeshapes.MoleculeShapesColor;
import edu.colorado.phet.moleculeshapes.MoleculeShapesProperties;
import edu.colorado.phet.moleculeshapes.MoleculeShapesSimSharing;
import edu.colorado.phet.moleculeshapes.model.Molecule;
import edu.colorado.phet.moleculeshapes.model.PairGroup;
import edu.colorado.phet.moleculeshapes.view.AtomNode;
import edu.colorado.phet.moleculeshapes.view.LonePairNode;
import edu.colorado.phet.moleculeshapes.view.MoleculeModelNode;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet.parameterSet;
import static edu.colorado.phet.lwjglphet.GLOptions.RenderPass;
import static edu.colorado.phet.moleculeshapes.MoleculeShapesConstants.FRAMES_PER_SECOND_LIMIT;
import static edu.colorado.phet.moleculeshapes.MoleculeShapesSimSharing.UserComponents.mouseMiddleButton;
import static org.lwjgl.opengl.GL11.*;

/**
 * Abstract class for modules that show a single molecule view
 */
public abstract class MoleculeViewTab extends LWJGLTab {

    public static final float ROTATION_MOUSE_SENSITIVITY = 0.005f;

    private Property<Molecule> molecule = new Property<Molecule>( null );

    // list of all orthographic user interfaces (stored here so we can handle mouse events correctly)
    protected final List<OrthoSwingNode> guiNodes = new ArrayList<OrthoSwingNode>();

    // whether bond angles should be shown
    public final Property<Boolean> showBondAngles = new Property<Boolean>( false );

    // whether lone pairs should be shown
    public final Property<Boolean> showLonePairs = new Property<Boolean>( true );

    // whether terminal lone pairs should also be shown
    public final Property<Boolean> showAllLonePairs = new Property<Boolean>( false );

    // event notifications
    public final VoidNotifier mouseEventNotifier = new VoidNotifier();
    public final VoidNotifier keyboardEventNotifier = new VoidNotifier();
    public final VoidNotifier beforeFrameRender = new VoidNotifier();
    public final VoidNotifier timeChangeNotifier = new VoidNotifier();

    // OpenGL standard transformations
    public final LWJGLTransform sceneProjectionTransform = new LWJGLTransform();
    public final LWJGLTransform sceneModelViewTransform = new LWJGLTransform();

    // whether everything should be displayed as wireframe (we can do this basically globally)
    private boolean showWireframe = false;

    private Dimension stageSize;

    // transform from the "stage" to the "canvas", that handles the proper centering transform as in other simulations
    protected CanvasTransform canvasTransform;

    // we want to wait until LWJGL has loaded fully before initializing
    private boolean initialized = false;

    // timestamp for calculation of elapsed time between frames
    private long lastSeenTime;

    // in seconds
    private float timeElapsed;

    // recorded amount
    public final Property<Double> framesPerSecond = new Property<Double>( 0.0 );
    private final LinkedList<Long> timeQueue = new LinkedList<Long>();

    // the root GL node that underlies everything rendered in 3D
    public final GLNode rootNode = new GLNode();

    // separate layers under the root note that helps us keep things in the correct z-order
    protected GLNode sceneLayer;
    protected GLNode readoutLayer;
    protected GLNode guiLayer;
    protected GLNode overlayLayer;

    // frustum (camera) properties
    public static final float fieldOfViewDegrees = 45 / 2;
    public static final float nearPlane = 1;
    public static final float farPlane = 1000;

    protected MoleculeModelNode moleculeNode; // The molecule to display and rotate

    /*---------------------------------------------------------------------------*
    * dragging
    *----------------------------------------------------------------------------*/

    public static enum DragMode {
        MODEL_ROTATE, // rotate the VSEPR model molecule
        PAIR_FRESH_PLANAR, // drag an atom/lone pair on the z=0 plane
        PAIR_EXISTING_SPHERICAL, // drag an atom/lone pair across the surface of a sphere
        REAL_MOLECULE_ROTATE // rotate the "real" molecule in the display
    }

    protected volatile boolean dragging = false; // keeps track of the drag state
    protected volatile DragMode dragMode = DragMode.MODEL_ROTATE;
    protected volatile PairGroup draggedParticle = null;
    protected volatile boolean globalLeftMouseDown = false; // keep track of the LMB state, since we need to deal with a few synchronization issues

    /*---------------------------------------------------------------------------*
    * positioning
    *----------------------------------------------------------------------------*/

    protected volatile boolean resizeDirty = false;

    protected Property<QuaternionF> rotation = new Property<QuaternionF>( new QuaternionF() ); // The angle about which the molecule should be rotated, changes as a function of time

    public MoleculeViewTab( LWJGLCanvas canvas, String name ) {
        super( canvas, name );
    }

    protected void initialize() {
        // sanity check, in case a size change event has not occurred yet.
        if( initialCanvasSize == null ) {
            initialCanvasSize = getCanvas().getSize();
        }

        // attempt to set the stage size to the canvas size, so we can get 1-to-1 pixel mapping for the UI (without needing to scale) if possible
        stageSize = initialCanvasSize;
        if ( Math.abs( stageSize.getWidth() - 1008 ) > 20 || Math.abs( stageSize.getHeight() - 676 ) > 20 ) {
            // if our stage size is far enough away from pixel-perfect graphics on the initial canvas size, simply set to the default
            stageSize = new Dimension( 1008, 676 );
        }
        canvasTransform = new CanvasTransform.StageCenteringCanvasTransform( canvasSize, stageSize );

        // show both sides of polygons by default
        glPolygonMode( GL_FRONT, GL_FILL );
        glPolygonMode( GL_BACK, GL_FILL );

        // basic blending function used many places. this is the default
        glBlendFunc( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA );

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
        readoutLayer = new GuiNode( this, false );
        guiLayer = new GuiNode( this );
        overlayLayer = new GuiNode( this );
        rootNode.addChild( sceneLayer );
        rootNode.addChild( readoutLayer );
        rootNode.addChild( guiLayer );
        rootNode.addChild( overlayLayer );

        // show wireframe while F key is pressed
        keyboardEventNotifier.addUpdateListener(
                new UpdateListener() {
                    public void update() {
                        if ( Keyboard.getEventKey() == Keyboard.KEY_F ) {
                            showWireframe = Keyboard.getEventKeyState();
                        }
                    }
                }, false );

        mouseEventNotifier.addUpdateListener( new UpdateListener() {
            public void update() {

                // left mouse button state changed
                if ( Mouse.getEventButton() == 0 ) {
                    if ( Mouse.isButtonDown( 0 ) ) {
                        onLeftMouseDown();
                    }
                    else {
                        onLeftMouseUp();
                    }
                }
                // middle mouse button state changed
                else if ( allowMiddleMouseClickModifications() && Mouse.getEventButton() == 2 ) {
                    if ( Mouse.isButtonDown( 2 ) ) {
                        PairGroup pair = getElectronPairUnderPointer();
                        if ( pair != null && pair != getMolecule().getCentralAtom() ) {
                            getMolecule().removeGroup( pair );
                        }
                        SimSharingManager.sendUserMessage( mouseMiddleButton, UserComponentTypes.unknown, UserActions.pressed, parameterSet( MoleculeShapesSimSharing.ParamKeys.removedPair, pair != null ) );
                    }
                }
                // not a button press, so is a mouse-move
                else if ( Mouse.getEventButton() == -1 ) {
                    //By always updating the cursor at every mouse move, we can be sure it is always correct.
                    //Whenever there is a mouse move event, make sure the cursor is in the right state.
                    updateCursor();

                    if ( dragging ) {

                        // function that updates a quaternion in-place, by adding the necessary rotation in, multiplied by the scale
                        final VoidFunction2<Property<QuaternionF>, Float> updateQuaternion = new VoidFunction2<Property<QuaternionF>, Float>() {
                            public void apply( Property<QuaternionF> quaternion, Float scale ) {
                                // if our window is smaller, rotate more
                                float correctedScale = scale / getApproximateScale();

                                quaternion.set( QuaternionF.fromEulerAngles(
                                        -( Mouse.getEventDY() * correctedScale ), // yaw
                                        Mouse.getEventDX() * correctedScale, // roll
                                        0 // pitch
                                ).times( quaternion.get() ) );
                            }
                        };

                        switch( dragMode ) {
                            case MODEL_ROTATE:
                                updateQuaternion.apply( rotation, ROTATION_MOUSE_SENSITIVITY );
                                break;
                            case PAIR_FRESH_PLANAR:
                                // put the particle on the z=0 plane
                                draggedParticle.dragToPosition( getPlanarMoleculeCursorPosition().to3D() );
                                break;
                            case PAIR_EXISTING_SPHERICAL:
                                draggedParticle.dragToPosition( getSphericalMoleculeCursorPosition( draggedParticle.position.get().to3F() ).to3D() );
                                break;
                        }
                    }
                }
            }
        }, false );

        // TODO: rest of initialize()
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
        Display.sync( FRAMES_PER_SECOND_LIMIT.get() );

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
                1000f / (float) FRAMES_PER_SECOND_LIMIT.get(), // don't let our time elapsed go over the frame rate limit value
                (float) ( newTime - lastSeenTime ) / 1000f ); // take elapsed milliseconds => seconds
        lastSeenTime = newTime;

        timeChangeNotifier.updateListeners();

        // walk through all of the mouse events that occurred
        while ( Mouse.next() ) {
            mouseEventNotifier.updateListeners();
        }
        while ( Keyboard.next() ) {
            keyboardEventNotifier.updateListeners();
        }

        updateState( timeElapsed );

        // force updating of matrices before the callbacks are run, so we have correct ray picking
        loadCameraMatrices();

        beforeFrameRender.updateListeners();

        // Clear the screen and depth buffer
        LWJGLUtils.clearColor( MoleculeShapesColor.handler.get( MoleculeShapesColor.BACKGROUND ) );
        glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT );

        // reset the modelview matrix
        glMatrixMode( GL_MODELVIEW );
        glLoadIdentity();

        GLOptions options = new GLOptions();

        if ( showWireframe ) {
            options.forWireframe = true;
            glPolygonMode( GL11.GL_FRONT_AND_BACK, GL11.GL_LINE );
        }
        else {
            glPolygonMode( GL11.GL_FRONT_AND_BACK, GL11.GL_FILL );
        }

        glViewport( 0, 0, getCanvasWidth(), getCanvasHeight() );
        setupGuiTransformations();

        options.renderPass = RenderPass.REGULAR;
        rootNode.render( options );
        options.renderPass = RenderPass.TRANSPARENCY;
        rootNode.render( options );

        Display.update();
    }

    public abstract void updateState( final float tpf );

    @Override public void stop() {

    }

    /**
     * @return Our relative screen display scale compared to the stage scale
     */
    public Vector2D getScale() {
        return new Vector2D( getCanvasSize().getWidth() / getStageSize().getWidth(),
                             getCanvasSize().getHeight() / getStageSize().getHeight() );
    }

    public float getApproximateScale() {
        Vector2D scale = getScale();
        return (float) ( ( scale.getX() + scale.getY() ) / 2 );
    }

    public Molecule getMolecule() {
        return molecule.get();
    }

    public void setMolecule( Molecule molecule ) {
        this.molecule.set( molecule );
    }

    public Property<Molecule> getMoleculeProperty() {
        return molecule;
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

    // TODO: move this (and PT's version) to lwjgl-phet!
    // the GLUT perspective function did not allow the proper FOV options that we needed, so here is an equivalent version that is simpler
    public Matrix4F getGluPerspective( float fovYRadians, float aspect, float zNear, float zFar ) {
        float cotangent = (float) Math.cos( fovYRadians ) / (float) Math.sin( fovYRadians );

        return Matrix4F.rowMajor( cotangent / aspect, 0, 0, 0,
                                  0, cotangent, 0, 0,
                                  0, 0, ( zFar + zNear ) / ( zNear - zFar ), ( 2 * zFar * zNear ) / ( zNear - zFar ),
                                  0, 0, -1, 0 );
    }

    public Matrix4F getSceneProjectionMatrix() {
        // NOTE: keep for reference, however the stage centering in this case is being done (for now) exclusively by using the fieldOfViewYFactor
//        AffineTransform affineCanvasTransform = canvasTransform.transform.get();
//
//        Matrix4F scalingMatrix = Matrix4F.scaling(
//                (float) affineCanvasTransform.getScaleX(),
//                (float) affineCanvasTransform.getScaleY(),
//                1 );

        // compute our field of view to match zoom
        float fieldOfViewRadians = (float) ( fieldOfViewDegrees / 180f * Math.PI );
        float correctedFieldOfViewRadians = (float) Math.atan( canvasTransform.getFieldOfViewYFactor() * Math.tan( fieldOfViewRadians ) );

        Matrix4F perspectiveMatrix = getGluPerspective( correctedFieldOfViewRadians,
                                                        (float) canvasSize.get().width / (float) canvasSize.get().height,
                                                        nearPlane, farPlane );
        return perspectiveMatrix;
    }

    public Matrix4F getSceneModelViewMatrix() {
        return Matrix4F.translation( 0, 0, -40 );
    }

    protected void onLeftMouseDown() {
        // for dragging, ignore mouse presses over the HUD
        // TODO: handle left mouse button clicks
        boolean mouseOverInterface = getGuiUnder( Mouse.getEventX(), Mouse.getEventY() ) != null;
        if ( !mouseOverInterface ) {
            dragging = true;

            PairGroup pair = getElectronPairUnderPointer();
            if ( pair != null ) {
                // we are over a pair group, so start the drag on it
                dragMode = DragMode.PAIR_EXISTING_SPHERICAL;
                draggedParticle = pair;
                pair.userControlled.set( true );
            }
            else {
                // set up default drag mode
                dragMode = DragMode.MODEL_ROTATE;
            }
            draggingChanged();
        }
    }

    protected void onLeftMouseUp() {
        // not dragging anymore
        dragging = false;

        // release an electron pair if we were dragging it
        if ( dragMode == DragMode.PAIR_FRESH_PLANAR || dragMode == DragMode.PAIR_EXISTING_SPHERICAL ) {
            draggedParticle.userControlled.set( false );
        }
        draggingChanged();
    }

    private FloatBuffer specular = LWJGLUtils.floatBuffer( new float[]{0, 0, 0, 0} );
    private FloatBuffer shininess = LWJGLUtils.floatBuffer( new float[]{50} );
    private FloatBuffer sunDirection = LWJGLUtils.floatBuffer( new float[]{-1, 0.5f, 2, 0} );
    private FloatBuffer moonDirection = LWJGLUtils.floatBuffer( new float[]{2, -1, 1, 0} );
    private FloatBuffer blackColor = LWJGLUtils.floatBuffer( new float[]{0, 0, 0, 0} );

    private FloatBuffer sunColor = LWJGLUtils.floatBuffer( MoleculeShapesColor.SUN.get().getColorComponents( new float[4] ) );
    private FloatBuffer moonColor = LWJGLUtils.floatBuffer( MoleculeShapesColor.MOON.get().getColorComponents( new float[4] ) );

    protected void loadLighting() {
        glMaterial( GL_FRONT, GL_SPECULAR, specular );
//            glMaterial( GL_FRONT, GL_SHININESS, shininess );
        glLight( GL_LIGHT0, GL_POSITION, sunDirection );
        glLight( GL_LIGHT0, GL_DIFFUSE, sunColor );
        glLight( GL_LIGHT0, GL_AMBIENT, blackColor );
        glLight( GL_LIGHT0, GL_SPECULAR, blackColor );
        glLight( GL_LIGHT1, GL_POSITION, moonDirection );
        glLight( GL_LIGHT1, GL_DIFFUSE, moonColor );
        glLight( GL_LIGHT1, GL_AMBIENT, blackColor );
        glLight( GL_LIGHT1, GL_SPECULAR, blackColor );

        // turn off any sort of global ambient light
        glLightModel( GL_LIGHT_MODEL_AMBIENT, blackColor );

        // enable both lights
        glEnable( GL_LIGHT0 );
        glEnable( GL_LIGHT1 );
    }

    // given the mouse position by LWJGL, compute a ray from the camera to where
    public Ray3F getCameraRay( int mouseX, int mouseY ) {
        // TODO: consider moving this to lwjgl-phet. this is copied in Plate Tectonics!!
        // the terms in this function should be googleable

        // TODO: this information should be a separate query-able function
        Vector3F normalizedDeviceCoordinates = new Vector3F(
                2 * mouseX / (float) getCanvasWidth() - 1,
                2 * mouseY / (float) getCanvasHeight() - 1,
                1 );

        Vector3F eyeCoordinates = sceneProjectionTransform.getInverse().times( normalizedDeviceCoordinates );

        Vector3F objectCoordinatesA = sceneModelViewTransform.getInverse().times( eyeCoordinates );
        Vector3F objectCoordinatesB = sceneModelViewTransform.getInverse().times( eyeCoordinates.times( 2 ) );

        return new Ray3F( objectCoordinatesA, objectCoordinatesB.minus( objectCoordinatesA ) );
    }

    public Vector2F getScreenCoordinatesFromViewPoint( Vector3F viewPoint ) {
        Vector4F projected = sceneProjectionTransform.getMatrix().times( sceneModelViewTransform.getMatrix().times( new Vector4F( viewPoint ) ) );

        float s = 1 / projected.w;

        // map to normalized device coordinates?
        float x = projected.x * s;
        float y = projected.y * s;
        float z = projected.z * s;

        return getScreenCoordinatesFromNormalizedDeviceCoordinates( new Vector3F( x, y, z ) );


//        in[3] = (1.0f / in[3]) * 0.5f;
//
//        // Map x, y and z to range 0-1
//        in[0] = in[0] * in[3] + 0.5f;
//        in[1] = in[1] * in[3] + 0.5f;
//        in[2] = in[2] * in[3] + 0.5f;
//
//        // Map x,y to viewport
//        win_pos.put(0, in[0] * viewport.get(viewport.position() + 2) + viewport.get(viewport.position() + 0));
//        win_pos.put(1, in[1] * viewport.get(viewport.position() + 3) + viewport.get(viewport.position() + 1));
//        win_pos.put(2, in[2]);
//
//        return getScreenCoordinatesFromNormalizedDeviceCoordinates( sceneProjectionTransform.getMatrix().times( sceneModelViewTransform.getMatrix().times( viewPoint ) ) );
    }

    public Vector2F getScreenCoordinatesFromNormalizedDeviceCoordinates( Vector3F normalizedDeviceCoordinates ) {
        return new Vector2F(
                ( normalizedDeviceCoordinates.x + 1 ) * getCanvasWidth() / 2,
                ( normalizedDeviceCoordinates.y + 1 ) * getCanvasHeight() / 2
        );
    }

    public Vector3F getPlanarMoleculeCursorPosition() {
        Ray3F cameraRay = getCameraRay( Mouse.getEventX(), Mouse.getEventY() );
        final Vector3F intersection = PlaneF.XY.intersectWithRay( cameraRay );
        Vector3F globalStartPosition = new Vector3F( intersection.x, intersection.y, 0 );

        // TODO: verify getPlanarMoleculeCursorPosition
        // transform to moleculeNode coordinates and return
        return moleculeNode.transform.inverseDelta( globalStartPosition );
    }

    public Vector3F getSphericalMoleculeCursorPosition( Vector3F currentLocalPosition ) {
        // decide whether to grab the closest or farthest point if possible. for now, we try to NOT move the pair at the start of the drag
        boolean returnCloseHit = moleculeNode.transform.getInverse().times( currentLocalPosition ).z >= 0;

        // override for dev option
        if ( !MoleculeShapesProperties.allowDraggingBehind.get() ) {
            returnCloseHit = true;
        }

        // set up intersection stuff

        // transform our position and direction into the local coordinate frame. we will do our computations there
        // TODO: verify inverseRay works
        Ray3F ray = moleculeNode.transform.inverseRay( getCameraRay( Mouse.getEventX(), Mouse.getEventY() ) );
//        Ray3F ray = JMEUtils.transformWorldRayToLocalCoordinates( moleculeView.getCameraRay( inputHandler.getCursorPosition() ), moleculeNode );
        Vector3F localCameraPosition = ray.pos;
        Vector3F localCameraDirection = ray.dir;

        // how far we will end up from the center atom
        float finalDistance = (float) getMolecule().getIdealDistanceFromCenter( draggedParticle );

        // our sphere to cast our ray against

        SphereF sphere = new SphereF( Vector3F.ZERO, finalDistance );

        float epsilon = 0.000001f;
        List<SphereF.SphereIntersectionResult> intersections = sphere.intersections( ray, epsilon );
        if ( intersections.isEmpty() ) {
            /*
             * Compute the point where the closest line through the camera and tangent to our bounding sphere intersects the sphere
             * ie, think 2d. we have a unit sphere centered at the origin, and a camera at (d,0). Our tangent point satisfies two
             * important conditions:
             * - it lies on the sphere. x^2 + y^2 == 1
             * - vector to the point (x,y) is tangent to the vector from (x,y) to our camera (d,0). thus (x,y) . (d-y, -y) == 0
             * Solve, and we get x = 1/d  plug back in for y (call that height), and we have our 2d solution.
             *
             * Now, back to 3D. Since camera is (0,0,d), our z == 1/d and our x^2 + y^2 == (our 2D y := height), then rescale them out of the unit sphere
             */

            float distanceFromCamera = localCameraPosition.distance( Vector3F.ZERO );

            // first, calculate it in unit-sphere, as noted above
            float d = distanceFromCamera / finalDistance; // scaled distance to the camera (from the origin)
            float z = 1 / d; // our result z (down-scaled)
            float height = (float) ( Math.sqrt( d * d - 1 ) / d ); // our result (down-scaled) magnitude of (x,y,0), which is the radius of the circle composed of all points that could be tangent

            /*
             * Since our camera isn't actually on the z-axis, we need to calculate two vectors. One is the direction towards
             * the camera (planeNormal, easy!), and the other is the direction perpendicular to the planeNormal that points towards
             * the mouse pointer (planeHitDirection).
             */

            // intersect our camera ray against our perpendicular plane (perpendicular to our camera position from the origin) to determine the orientations
            Vector3F planeNormal = localCameraPosition.normalized();
            float t = -( localCameraPosition.magnitude() ) / ( planeNormal.dot( localCameraDirection ) );
            Vector3F planeHitDirection = localCameraPosition.plus( localCameraDirection.times( t ) ).normalized();

            // use the above plane hit direction (perpendicular to the camera) and plane normal (collinear with the camera) to calculate the result
            Vector3F downscaledResult = planeHitDirection.times( height ).plus( planeNormal.times( z ) );

            // scale it back to our sized sphere
            return downscaledResult.times( finalDistance );
        }
        else {
            // pick our desired hitpoint (there are only 2), and return it (now by flipping the ray)
            return returnCloseHit ? intersections.get( 0 ).getHitPoint() : intersections.get( 1 ).getHitPoint();
        }
    }

    /**
     * Given a spatial target, return any associated electron pair, or null if there is none
     *
     * @param target JME Spatial
     * @return Electron pair, or null
     */
    protected PairGroup getElectronPairForTarget( GLNode target ) {
        // TODO: electron pair from target
        boolean isAtom = target instanceof AtomNode;
        boolean isLonePair = target instanceof LonePairNode;

        if ( isAtom ) {
            return ( (AtomNode) target ).pair;
        }
        else if ( isLonePair ) {
            if ( target.isVisible() ) {
                return ( (LonePairNode) target ).pair;
            }
            else {
                return null; // lone pair invisible
            }
        }
        else {
            if ( target.getParent() != null ) {
                return getElectronPairForTarget( target.getParent() );
            }
            else {
                // failure
                return null;
            }
        }
    }

    /**
     * @return The closest (hit) electron pair currently under the mouse pointer, or null if there is none (central atom not counted)
     */
    public PairGroup getElectronPairUnderPointer() {
        // TODO: electron pair picking
        GLNode node = moleculeNode.intersect( getCameraRay( Mouse.getEventX(), Mouse.getEventY() ) );
        if ( node != null ) {
            PairGroup pair = getElectronPairForTarget( node );
            if ( pair != null ) {
                if ( !isRealTab() ) {
                    return pair;
                }

                // don't drag the central atom OR any terminal lone pairs (in real tab)
                if ( pair != getMolecule().getCentralAtom() && !getMolecule().getDistantLonePairs().contains( pair ) ) {
                    return pair;
                }
            }
        }
        return null;
    }

    // TODO: copied from Plate Tectonics, refactor!
    public OrthoSwingNode getGuiUnder( int x, int y ) {
        for ( OrthoSwingNode guiNode : guiNodes ) {
            if ( isGuiUnder( guiNode, x, y ) ) {
                return guiNode;
            }
        }
        return null;
    }

    // TODO: copied from Plate Tectonics, refactor!
    public boolean isGuiUnder( OrthoSwingNode guiNode, int screenX, int screenY ) {
        Vector2F screenPosition = new Vector2F( screenX, screenY );
        if ( guiNode.isReady() ) {
            Vector2F componentPoint = guiNode.screentoComponentCoordinates( screenPosition );
            if ( guiNode.getComponent().contains( (int) componentPoint.x, (int) componentPoint.y ) ) {
                return true;
            }
        }
        return false;
    }

    protected void updateCursor() {
        final Canvas canvas = getCanvas();

        //If the mouse is in front of a grabbable object, show a hand, otherwise show the default cursor
        final PairGroup pair = getElectronPairUnderPointer();

        final OrthoSwingNode guiCollision = getGuiUnder( Mouse.getEventX(), Mouse.getEventY() );

        final boolean isMoleculeRotating = dragging && ( dragMode == DragMode.MODEL_ROTATE || dragMode == DragMode.REAL_MOLECULE_ROTATE );
        final boolean isOverPairOrDraggingOne = pair != null || dragging;

        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                Component guiComponent = guiCollision == null ? null : guiCollision.getComponentAt( Mouse.getX(), Mouse.getY() );

                if ( isMoleculeRotating ) {
                    // rotating the molecule. for now, trying out the "move" cursor
                    canvas.setCursor( Cursor.getPredefinedCursor( MoleculeShapesProperties.useRotationCursor.get() ? Cursor.MOVE_CURSOR : Cursor.DEFAULT_CURSOR ) );
                }
                else if ( isOverPairOrDraggingOne ) {
                    // over a pair group, OR dragging one
                    canvas.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
                }
                else if ( guiComponent != null ) {
                    // over a HUD node, so set the cursor to what the component would want
                    canvas.setCursor( guiComponent.getCursor() );
                }
                else {
                    // default to the default cursor
                    canvas.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
                }

            }
        } );
    }

    boolean lastDragging = false;

    //Signify to the sim sharing feature that dragging state changed, should be called whenever dragging or dragMode changes (but batch together when both change)
    protected void draggingChanged() {

        //Hide spurious "dragging = false" messages when clicking on piccolo swing buttons
        if ( lastDragging != dragging ) {
            SimSharingManager.sendUserMessage( MoleculeShapesSimSharing.UserComponents.draggingState, UserComponentTypes.unknown, UserActions.changed,
                                               parameterSet( MoleculeShapesSimSharing.ParamKeys.dragging, dragging ).
                                                       with( MoleculeShapesSimSharing.ParamKeys.dragMode, dragMode.toString() ) );
        }
        lastDragging = dragging;
    }

    /*---------------------------------------------------------------------------*
    * options
    *----------------------------------------------------------------------------*/

    public boolean allowTogglingLonePairs() {
        return true;
    }

    public boolean allowTogglingAllLonePairs() {
        return true;
    }

    public boolean allowMiddleMouseClickModifications() {
        return true;
    }

    public abstract boolean isRealTab();
}
