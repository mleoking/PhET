package edu.colorado.phet.moleculeshapes.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Random;

import org.lwjgl.input.Mouse;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option.None;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.PhetOptionPane;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.moleculeshapes.MoleculeShapesApplication;
import edu.colorado.phet.moleculeshapes.control.GeometryNameNode;
import edu.colorado.phet.moleculeshapes.control.MoleculeShapesControlPanel;
import edu.colorado.phet.moleculeshapes.control.MoleculeShapesPanelNode;
import edu.colorado.phet.moleculeshapes.jme.HUDNode;
import edu.colorado.phet.moleculeshapes.jme.JmeUtils;
import edu.colorado.phet.moleculeshapes.jme.PhetJMEApplication;
import edu.colorado.phet.moleculeshapes.jme.PiccoloJMENode;
import edu.colorado.phet.moleculeshapes.math.ImmutableVector3D;
import edu.colorado.phet.moleculeshapes.model.MoleculeModel;
import edu.colorado.phet.moleculeshapes.model.PairGroup;

import com.jme3.bounding.BoundingSphere;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.system.JmeCanvasContext;

/**
 * Use jme3 to show a rotating molecule
 * TODO: on JME3 loading failure, show the user some debugging information!
 * TODO: when dragging existing atoms/lone pairs and the mouse moves out of range (no sphere-hits), set its location to the CAMERA-TANGENT location
 * TODO: double-check naming with double/triple bonds
 * TODO: audit for any other synchronization issues. we have the AWT and JME threads running rampant!
 * TODO: massive hidden bug if you middle-click-drag out a molecule!!!
 * TODO: collision-lab-like button unpress failures?
 * TODO: with 6 triple bonds, damping can become an issue? can cause one to fly out of range!!!
 * TODO: potential listener leak with bond angles
 * <p/>
 * NOTES:
 * TODO: it's weird to drag out an invisible lone pair
 * TODO: consider color-coding the molecular / electron readouts?
 * TODO: Reset: how many bonds (0 or 2) should it reset to?
 * <p/>
 * Problem spatial name: null
 * at com.jme3.scene.Spatial.checkCulling(Spatial.java:217)
 * at com.jme3.renderer.RenderManager.renderScene(RenderManager.java:775)
 * at com.jme3.renderer.RenderManager.renderScene(RenderManager.java:793)
 * at com.jme3.renderer.RenderManager.renderViewPort(RenderManager.java:1116)
 * at com.jme3.renderer.RenderManager.render(RenderManager.java:1159)
 * at edu.colorado.phet.moleculeshapes.jme.BaseJMEApplication.update(BaseJMEApplication.java:194)
 * at com.jme3.system.lwjgl.LwjglAbstractDisplay.runLoop(LwjglAbstractDisplay.java:144)
 * at com.jme3.system.lwjgl.LwjglCanvas.runLoop(LwjglCanvas.java:199)
 * at com.jme3.system.lwjgl.LwjglAbstractDisplay.run(LwjglAbstractDisplay.java:218)
 * at java.lang.Thread.run(Thread.java:662)
 */
public class MoleculeJMEApplication extends PhetJMEApplication {

    /*---------------------------------------------------------------------------*
    * input mapping constants
    *----------------------------------------------------------------------------*/
    public static final String MAP_LEFT = "CameraLeft";
    public static final String MAP_RIGHT = "CameraRight";
    public static final String MAP_UP = "CameraUp";
    public static final String MAP_DOWN = "CameraDown";
    public static final String MAP_LMB = "CameraDrag";
    public static final String MAP_MMB = "RightMouseButton";

    private static final float MOUSE_SCALE = 3.0f;

    /*---------------------------------------------------------------------------*
    * model
    *----------------------------------------------------------------------------*/

    private MoleculeModel molecule = new MoleculeModel();

    public static final Property<Boolean> showLonePairs = new Property<Boolean>( true );
    private final Frame parentFrame;

    public MoleculeJMEApplication( Frame parentFrame ) {
        this.parentFrame = parentFrame;
    }

    /*---------------------------------------------------------------------------*
    * dragging
    *----------------------------------------------------------------------------*/

    public static enum DragMode {
        MOLECULE_ROTATE, // rotate the molecule
        PAIR_FRESH_PLANAR, // drag an atom/lone pair on the z=0 plane
        PAIR_EXISTING_SPHERICAL // drag an atom/lone pair across the surface of a sphere
    }

    private boolean dragging = false; // keeps track of the drag state
    private DragMode dragMode = DragMode.MOLECULE_ROTATE;
    private PairGroup draggedParticle = null;

    /*---------------------------------------------------------------------------*
    * positioning
    *----------------------------------------------------------------------------*/

    private Dimension lastCanvasSize;
    private boolean resizeDirty = false;

    private Quaternion rotation = new Quaternion(); // The angle about which the molecule should be rotated, changes as a function of time

    /*---------------------------------------------------------------------------*
    * graphics/control
    *----------------------------------------------------------------------------*/

    private PiccoloJMENode controlPanel;
    private PiccoloJMENode resetAllNode;
    private PiccoloJMENode namePanel;

    private MoleculeNode moleculeNode; // The molecule to display and rotate

    private static final Random random = new Random( System.currentTimeMillis() );

    /**
     * Pseudo-constructor (JME-based)
     */
    @Override public void initialize() {
        super.initialize();

        initializeResources();

        // add an offset to the left, since we have a control panel on the right
        getSceneNode().setLocalTranslation( new Vector3f( -4.5f, 1.5f, 0 ) );

        // hook up mouse-move handlers
        inputManager.addMapping( MoleculeJMEApplication.MAP_LEFT, new MouseAxisTrigger( MouseInput.AXIS_X, true ) );
        inputManager.addMapping( MoleculeJMEApplication.MAP_RIGHT, new MouseAxisTrigger( MouseInput.AXIS_X, false ) );
        inputManager.addMapping( MoleculeJMEApplication.MAP_UP, new MouseAxisTrigger( MouseInput.AXIS_Y, false ) );
        inputManager.addMapping( MoleculeJMEApplication.MAP_DOWN, new MouseAxisTrigger( MouseInput.AXIS_Y, true ) );

        // hook up mouse-button handlers
        inputManager.addMapping( MAP_LMB, new MouseButtonTrigger( MouseInput.BUTTON_LEFT ) );
        inputManager.addMapping( MAP_MMB, new MouseButtonTrigger( MouseInput.BUTTON_MIDDLE ) );

        /*---------------------------------------------------------------------------*
        * mouse-button presses
        *----------------------------------------------------------------------------*/
        inputManager.addListener(
                new ActionListener() {
                    public void onAction( String name, boolean isMouseDown, float tpf ) {
                        // record whether the mouse button is down
                        synchronized ( MoleculeJMEApplication.this ) {

                            // on left mouse button change
                            if ( name.equals( MAP_LMB ) ) {

                                if ( isMouseDown ) {
                                    // for dragging, ignore mouse presses over the HUD
                                    boolean mouseOverInterface = getComponentUnderPointer( Mouse.getX(), Mouse.getY() ) != null;
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
                                            dragMode = DragMode.MOLECULE_ROTATE;
                                        }
                                    }
                                }
                                else {
                                    // not dragging.
                                    dragging = false;

                                    // release an electron pair if we were dragging it
                                    if ( dragMode == DragMode.PAIR_FRESH_PLANAR || dragMode == DragMode.PAIR_EXISTING_SPHERICAL ) {
                                        draggedParticle.userControlled.set( false );
                                    }
                                }
                            }

                            // kill any pair groups under the middle mouse button press
                            if ( isMouseDown && name.equals( MAP_MMB ) ) {
                                PairGroup pair = getElectronPairUnderPointer();
                                if ( pair != null ) {
                                    molecule.removePair( pair );
                                }
                            }
                        }
                    }
                }, MAP_LMB, MAP_MMB );

        /*---------------------------------------------------------------------------*
        * mouse motion
        *----------------------------------------------------------------------------*/
        inputManager.addListener(
                new AnalogListener() {
                    public void onAnalog( String name, float value, float tpf ) {

                        //By always updating the cursor at every mouse move, we can be sure it is always correct.
                        //Whenever there is a mouse move event, make sure the cursor is in the right state.
                        updateCursor();

                        if ( dragging ) {
                            synchronized ( MoleculeJMEApplication.this ) {
                                switch( dragMode ) {
                                    case MOLECULE_ROTATE:
                                        if ( name.equals( MoleculeJMEApplication.MAP_LEFT ) ) {
                                            rotation = new Quaternion().fromAngles( 0, -value * MOUSE_SCALE, 0 ).mult( rotation );
                                        }
                                        if ( name.equals( MoleculeJMEApplication.MAP_RIGHT ) ) {
                                            rotation = new Quaternion().fromAngles( 0, value * MOUSE_SCALE, 0 ).mult( rotation );
                                        }
                                        if ( name.equals( MoleculeJMEApplication.MAP_UP ) ) {
                                            rotation = new Quaternion().fromAngles( -value * MOUSE_SCALE, 0, 0 ).mult( rotation );
                                        }
                                        if ( name.equals( MoleculeJMEApplication.MAP_DOWN ) ) {
                                            rotation = new Quaternion().fromAngles( value * MOUSE_SCALE, 0, 0 ).mult( rotation );
                                        }
                                        break;
                                    case PAIR_FRESH_PLANAR:
                                        // put the particle on the z=0 plane
                                        draggedParticle.dragToPosition( JmeUtils.convertVector( getPlanarMoleculeCursorPosition() ) );
                                        break;
                                    case PAIR_EXISTING_SPHERICAL:
                                        draggedParticle.dragToPosition( JmeUtils.convertVector( getSphericalMoleculeCursorPosition( JmeUtils.convertVector( draggedParticle.position.get() ) ) ) );
                                        break;
                                }
                            }
                        }
                    }
                }, MAP_LEFT, MAP_RIGHT, MAP_UP, MAP_DOWN, MAP_LMB );

        moleculeNode = new MoleculeNode( molecule, this, cam );
        getSceneNode().attachChild( moleculeNode );

        /*---------------------------------------------------------------------------*
        * scene setup
        *----------------------------------------------------------------------------*/

        addLighting( getSceneNode() );
        cam.setLocation( new Vector3f( 0, 0, 40 ) );

        //Create the central atom
        AtomNode center = new AtomNode( new None<PairGroup>(), assetManager );
        moleculeNode.attachChild( center );

        // start with two single bonds
        molecule.addPair( new PairGroup( new ImmutableVector3D( 10, 0, 3 ).normalized().times( PairGroup.BONDED_PAIR_DISTANCE ), 1, false ) );
        molecule.addPair( new PairGroup( new ImmutableVector3D( 2, 10, -5 ).normalized().times( PairGroup.BONDED_PAIR_DISTANCE ), 1, false ) );

        /*---------------------------------------------------------------------------*
        * control panel
        *----------------------------------------------------------------------------*/

        resetAllNode = new PiccoloJMENode( new TextButtonNode( "Reset", new PhetFont( 16 ), Color.ORANGE ) {{
            addActionListener( new java.awt.event.ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    resetAll();
                }
            } );
        }}, assetManager, inputManager );
        getBackgroundGuiNode().attachChild( resetAllNode );

        /*---------------------------------------------------------------------------*
        * "new" control panel
        *----------------------------------------------------------------------------*/
        controlPanel = new PiccoloJMENode( new MoleculeShapesControlPanel( this ), assetManager, inputManager );
        getBackgroundGuiNode().attachChild( controlPanel );

        namePanel = new PiccoloJMENode( new MoleculeShapesPanelNode( new GeometryNameNode( molecule ), "Geometry Name" ) {{
            // TODO fix (temporary offset since PiccoloJMENode isn't checking the "origin")
            setOffset( 0, 10 );
        }}, assetManager, inputManager );
        getBackgroundGuiNode().attachChild( namePanel );
    }

    private static void addLighting( Node node ) {
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection( new Vector3f( 1, -0.5f, -2 ).normalizeLocal() );
        sun.setColor( new ColorRGBA( 0.8f, 0.8f, 0.8f, 1f ) );
        node.addLight( sun );

        DirectionalLight moon = new DirectionalLight();
        moon.setDirection( new Vector3f( -2, 1, -1 ).normalizeLocal() );
        moon.setColor( new ColorRGBA( 0.6f, 0.6f, 0.6f, 1f ) );
        node.addLight( moon );
    }

    private void updateCursor() {
        //This solves a problem that we saw that: when there was no padding or other component on the side of the canvas, the mouse would become East-West resize cursor
        //And wouldn't change back.
        JmeCanvasContext context = (JmeCanvasContext) getContext();
        Canvas canvas = context.getCanvas();

        //If the mouse is in front of a grabbable object, show a hand, otherwise show the default cursor
        PairGroup pair = getElectronPairUnderPointer();

        Component component = getComponentUnderPointer( Mouse.getX(), Mouse.getY() );

        if ( dragging && dragMode == DragMode.MOLECULE_ROTATE ) {
            // rotating the molecule. for now, trying out the "move" cursor
            canvas.setCursor( Cursor.getPredefinedCursor( MoleculeShapesApplication.useRotationCursor.get() ? Cursor.MOVE_CURSOR : Cursor.DEFAULT_CURSOR ) );
        }
        else if ( pair != null || dragging ) {
            // over a pair group, OR dragging one
            canvas.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        }
        else if ( component != null ) {
            // over a HUD node, so set the cursor to what the component would want
            canvas.setCursor( component.getCursor() );
        }
        else {
            // default to the default cursor
            canvas.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
        }
    }

    public synchronized void resetAll() {
        removeAllAtoms();
        MoleculeShapesApplication.showElectronShapeName.reset();
        MoleculeShapesApplication.showMolecularShapeName.reset();
        showLonePairs.reset();
    }

    public synchronized void startNewInstanceDrag( int bondOrder ) {
        if ( !molecule.wouldAllowBondOrder( bondOrder ) ) {
            // don't add to the molecule if it is full
            // TODO: find better way of not calling this (or not having the user attempt to drag!) grey-out the control panel bonds?
            return;
        }

        Vector3f localPosition = getPlanarMoleculeCursorPosition();

        PairGroup pair = new PairGroup( JmeUtils.convertVector( localPosition ), bondOrder, true );
        molecule.addPair( pair );

        // set up dragging information
        dragging = true;
        dragMode = DragMode.PAIR_FRESH_PLANAR;
        draggedParticle = pair;
    }

    public Vector3f getPlanarMoleculeCursorPosition() {
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = cam.getWorldCoordinates( new Vector2f( click2d.x, click2d.y ), 0f ).clone();
        Vector3f dir = cam.getWorldCoordinates( new Vector2f( click2d.x, click2d.y ), 1f ).subtractLocal( click3d );

        float t = -click3d.getZ() / dir.getZ(); // solve for below equation at z=0. assumes camera isn't z=0, which should be safe here

        Vector3f globalStartPosition = click3d.add( dir.mult( t ) );

        // transform to moleculeNode coordinates and return
        return moleculeNode.getWorldTransform().transformInverseVector( globalStartPosition, new Vector3f() );
    }

    public Vector3f getSphericalMoleculeCursorPosition( Vector3f currentLocalPosition ) {
        // decide whether to grab the closest or farthest point if possible. for now, we try to NOT move the pair at the start of the drag
        boolean returnCloseHit = moleculeNode.getLocalToWorldMatrix( new Matrix4f() ).mult( currentLocalPosition ).z >= 0;

        // override for dev option
        if ( !MoleculeShapesApplication.allowDraggingBehind.get() ) {
            returnCloseHit = true;
        }

        // set up intersection stuff
        CollisionResults results = new CollisionResults();
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = cam.getWorldCoordinates( new Vector2f( click2d.x, click2d.y ), 0f ).clone();
        Vector3f dir = cam.getWorldCoordinates( new Vector2f( click2d.x, click2d.y ), 1f ).subtractLocal( click3d );

        // transform our position and direction into the local coordinate frame. we will do our computations there
        Vector3f transformedPosition = moleculeNode.getWorldTransform().transformInverseVector( click3d, new Vector3f() );
        Vector3f transformedDirection = moleculeNode.getLocalToWorldMatrix( new Matrix4f() ).transpose().mult( dir ).normalize(); // transpose trick to transform a unit vector
        Ray ray = new Ray( transformedPosition, transformedDirection );

        // how far we will end up from the center atom
        float distance = (float) draggedParticle.getIdealDistanceFromCenter();

        // our sphere to cast our ray against
        BoundingSphere sphere = new BoundingSphere( distance, new Vector3f( 0, 0, 0 ) );

        sphere.collideWithRay( ray, results );
        if ( results.size() == 0 ) {
            // just return the closest intersection on the plane, since we are not pointing to a spot on the "sphere"
            // TODO: get rid of perspective "gap" here
            return getPlanarMoleculeCursorPosition().normalize().mult( distance );
        }
        else {
            // pick our desired hitpoint (there are only 2), and return it
            CollisionResult result = returnCloseHit ? results.getClosestCollision() : results.getFarthestCollision();
            return result.getContactPoint();
        }
    }

    /**
     * @return The closest (hit) electron pair currently under the mouse pointer, or null if there is none
     */
    public PairGroup getElectronPairUnderPointer() {
        CollisionResults results = new CollisionResults();
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = cam.getWorldCoordinates( new Vector2f( click2d.x, click2d.y ), 0f ).clone();
        Vector3f dir = cam.getWorldCoordinates( new Vector2f( click2d.x, click2d.y ), 1f ).subtractLocal( click3d );
        Ray ray = new Ray( click3d, dir );
        getSceneNode().collideWith( ray, results );
        for ( CollisionResult result : results ) {
            PairGroup pair = getElectronPairForTarget( result.getGeometry() );
            if ( pair != null ) {
                return pair;
            }
        }
        return null;
    }

    public Component getComponentUnderPointer( int x, int y ) {
        CollisionResults results = new CollisionResults();
        Vector2f click2d = inputManager.getCursorPosition();
        getBackgroundGuiNode().collideWith( new Ray( new Vector3f( click2d.x, click2d.y, 0f ), new Vector3f( 0, 0, 1 ) ), results );
        for ( CollisionResult result : results ) {

            Geometry geometry = result.getGeometry();
            if ( geometry instanceof HUDNode ) {
                HUDNode node = (HUDNode) geometry;
                Vector3f hitPoint = node.transformEventCoordinates( click2d.x, click2d.y );
                Component component = node.componentAt( (int) hitPoint.x, (int) hitPoint.y );
                return component;
            }
        }
        return null;
    }


    /**
     * Given a spatial target, return any associated electron pair, or null if there is none
     *
     * @param target JME Spatial
     * @return Electron pair, or null
     */
    private PairGroup getElectronPairForTarget( Spatial target ) {
        boolean isAtom = target instanceof AtomNode;
        boolean isLonePair = target instanceof LonePairNode;

        if ( isAtom ) {
            return ( (AtomNode) target ).pair;
        }
        else if ( isLonePair ) {
            if ( !target.getCullHint().equals( CullHint.Always ) ) {
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

    public synchronized void removePairGroup( PairGroup group ) {
        // synchronized removal, so we don't run over the display thread
        molecule.removePair( group );
    }

    public synchronized void testRemoveAtom() {
        if ( !molecule.getGroups().isEmpty() ) {
            molecule.removePair( molecule.getGroups().get( random.nextInt( molecule.getGroups().size() ) ) );
        }
    }

    @Override public synchronized void updateState( final float tpf ) {
        molecule.update( tpf );
        moleculeNode.setLocalRotation( rotation );

        if ( resizeDirty ) {
            resizeDirty = false;
            final float padding = 10;
            if ( controlPanel != null ) {
                controlPanel.setLocalTranslation( lastCanvasSize.width - controlPanel.getWidth() - padding,
                                                  lastCanvasSize.height - controlPanel.getHeight() - padding,
                                                  0 );

                namePanel.setLocalTranslation( padding, padding, 0 );

                resetAllNode.setLocalTranslation( controlPanel.getLocalTranslation().subtract( new Vector3f( -( controlPanel.getWidth() - resetAllNode.getWidth() ) / 2, 50, 0 ) ) );
            }
        }
    }

    public void removeAllAtoms() {
        while ( !molecule.getGroups().isEmpty() ) {
            testRemoveAtom();
        }
    }

    public void onResize( Dimension canvasSize ) {
        lastCanvasSize = canvasSize;
        resizeDirty = true;
    }

    private void initializeResources() {
        // pre-load the lone pair geometry, so we don't get that delay
        LonePairNode.getGeometry( assetManager );
    }

    public MoleculeModel getMolecule() {
        return molecule;
    }

    @Override public void handleError( String errMsg, Throwable t ) {
        super.handleError( errMsg, t );
        if ( errMsg.equals( "Failed to initialize OpenGL context" ) ) {
            // TODO: improve the message
            PhetOptionPane.showMessageDialog( parentFrame, "Please upgrade your video card's drivers" );
        }
    }
}