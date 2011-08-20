package edu.colorado.phet.moleculeshapes.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Mouse;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option.None;
import edu.colorado.phet.common.phetcommon.util.Option.Some;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.PhetOptionPane;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.moleculeshapes.MoleculeShapesApplication;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Strings;
import edu.colorado.phet.moleculeshapes.control.MoleculeShapesControlPanel;
import edu.colorado.phet.moleculeshapes.jme.Arc;
import edu.colorado.phet.moleculeshapes.jme.BaseJMEApplication;
import edu.colorado.phet.moleculeshapes.jme.HUDNode;
import edu.colorado.phet.moleculeshapes.jme.JmeUtils;
import edu.colorado.phet.moleculeshapes.jme.PiccoloJMENode;
import edu.colorado.phet.moleculeshapes.math.ImmutableVector3D;
import edu.colorado.phet.moleculeshapes.model.MoleculeModel;
import edu.colorado.phet.moleculeshapes.model.MoleculeModel.Adapter;
import edu.colorado.phet.moleculeshapes.model.MoleculeModel.Listener;
import edu.colorado.phet.moleculeshapes.model.PairGroup;
import edu.umd.cs.piccolo.nodes.PText;

import com.jme3.bounding.BoundingSphere;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
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
 */
public class MoleculeJMEApplication extends BaseJMEApplication {

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

    private List<AtomNode> atomNodes = new ArrayList<AtomNode>();
    private List<LonePairNode> lonePairNodes = new ArrayList<LonePairNode>();
    private List<BondNode> bondNodes = new ArrayList<BondNode>();
    private List<Spatial> angleNodes = new ArrayList<Spatial>();

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
    private PiccoloJMENode showGeometryButtonNode;

    private PiccoloJMENode moleculeShapeReadout;
    private PiccoloJMENode electronShapeReadout;

    private Node moleculeNode; // The molecule to display and rotate

    private static final Random random = new Random( System.currentTimeMillis() );

    /**
     * Pseudo-constructor (JME-based)
     */
    @Override public void initialize() {
        super.initialize();

        initializeResources();

        // add an offset to the left, since we have a control panel on the right
        rootNode.setLocalTranslation( new Vector3f( -4.5f, 0, 0 ) );

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

        moleculeNode = new Node();
        rootNode.attachChild( moleculeNode );

        // update the UI when the molecule changes electron pairs
        molecule.addListener( new Adapter() {
            @Override public void onGroupAdded( PairGroup group ) {
                if ( group.isLonePair ) {
                    LonePairNode lonePairNode = new LonePairNode( group, assetManager );
                    lonePairNodes.add( lonePairNode );
                    moleculeNode.attachChild( lonePairNode );
                }
                else {
                    AtomNode atomNode = new AtomNode( new Some<PairGroup>( group ), assetManager );
                    atomNodes.add( atomNode );
                    moleculeNode.attachChild( atomNode );
                    rebuildBonds();
                    rebuildAngles();
                }

                onGeometryChange();
            }

            @Override public void onGroupRemoved( PairGroup group ) {
                if ( group.isLonePair ) {
                    for ( LonePairNode lonePairNode : new ArrayList<LonePairNode>( lonePairNodes ) ) {
                        // TODO: associate these more closely! (comparing positions for equality is bad)
                        if ( lonePairNode.position == group.position ) {
                            lonePairNodes.remove( lonePairNode );
                            moleculeNode.detachChild( lonePairNode );
                        }
                    }
                }
                else {
                    for ( AtomNode atomNode : new ArrayList<AtomNode>( atomNodes ) ) {
                        // TODO: associate these more closely! (comparing positions for equality is bad)
                        if ( atomNode.position == group.position ) {
                            atomNodes.remove( atomNode );
                            moleculeNode.detachChild( atomNode );
                        }
                    }
                }

                onGeometryChange();
            }
        } );

        /*---------------------------------------------------------------------------*
        * atoms
        *----------------------------------------------------------------------------*/

        //Create the central atom
        AtomNode center = new AtomNode( new None<PairGroup>(), assetManager );
        moleculeNode.attachChild( center );

        // start with two single bonds
        molecule.addPair( new PairGroup( new ImmutableVector3D( 10, 0, 3 ).normalized().times( PairGroup.BONDED_PAIR_DISTANCE ), 1, false ) );
        molecule.addPair( new PairGroup( new ImmutableVector3D( 2, 10, -5 ).normalized().times( PairGroup.BONDED_PAIR_DISTANCE ), 1, false ) );

        rebuildBonds();
        rebuildAngles();

        /*---------------------------------------------------------------------------*
        * lights
        *----------------------------------------------------------------------------*/
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection( new Vector3f( 1, -0.5f, -2 ).normalizeLocal() );
        sun.setColor( new ColorRGBA( 0.8f, 0.8f, 0.8f, 1f ) );
        rootNode.addLight( sun );

        DirectionalLight moon = new DirectionalLight();
        moon.setDirection( new Vector3f( -2, 1, -1 ).normalizeLocal() );
        moon.setColor( new ColorRGBA( 0.6f, 0.6f, 0.6f, 1f ) );
        rootNode.addLight( moon );

        /*---------------------------------------------------------------------------*
        * camera
        *----------------------------------------------------------------------------*/
        cam.setLocation( new Vector3f( cam.getLocation().getX(), cam.getLocation().getY(), cam.getLocation().getZ() + 30 ) );

        setDisplayFps( false );
        setDisplayStatView( false );

        /*---------------------------------------------------------------------------*
        * control panel
        *----------------------------------------------------------------------------*/

        showGeometryButtonNode = new PiccoloJMENode( new TextButtonNode( "Show Molecular Geometry", new PhetFont( 20 ), Color.ORANGE ) {
            {
                addActionListener( new java.awt.event.ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        showLonePairs.set( !showLonePairs.get() );
                        setText( showLonePairs.get() ? "Show Molecular Geometry" : "Show Electron Geometry" );
                    }
                } );
            }
        }, assetManager, inputManager );
        preGuiNode.attachChild( showGeometryButtonNode );
        onGeometryChange();

        resetAllNode = new PiccoloJMENode( new TextButtonNode( "Reset", new PhetFont( 16 ), Color.ORANGE ) {{
            addActionListener( new java.awt.event.ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    resetAll();
                }
            } );
        }}, assetManager, inputManager );
        preGuiNode.attachChild( resetAllNode );

        moleculeShapeReadout = new PiccoloJMENode( new PText( "Molecular Geometry: " ) {{
            setFont( new PhetFont( 16 ) );
            setTextPaint( Color.WHITE );
            molecule.addListener( new Listener() {
                {
                    updateText();
                }

                public void updateText() {
                    String name = molecule.getConfiguration().name;
                    setText( "Molecular Geometry: " + ( name == null ? Strings.SHAPE__EMPTY : name ) );

                    // TODO: fix this. shouldn't be necessary
                    if ( moleculeShapeReadout != null ) {
                        moleculeShapeReadout.refresh();
                    }
                }

                public void onGroupAdded( PairGroup group ) {
                    updateText();
                }

                public void onGroupRemoved( PairGroup group ) {
                    updateText();
                }
            } );
        }}, assetManager, inputManager ) {{
            MoleculeShapesApplication.showMolecularShapeName.addObserver( new SimpleObserver() {
                public void update() {
                    // TODO: refactor visibility
                    setCullHint( MoleculeShapesApplication.showMolecularShapeName.get() ? CullHint.Inherit : CullHint.Always );
                }
            } );
        }};
        preGuiNode.attachChild( moleculeShapeReadout );

        electronShapeReadout = new PiccoloJMENode( new PText( "Electron Geometry: " ) {{
            setFont( new PhetFont( 16 ) );
            setTextPaint( Color.WHITE );
            molecule.addListener( new Listener() {
                {
                    updateText();
                }

                public void updateText() {
                    String name = molecule.getConfiguration().geometry.name;
                    setText( "Electron Geometry: " + ( name == null ? Strings.GEOMETRY__EMPTY : name ) );

                    // TODO: fix this. shouldn't be necessary
                    if ( electronShapeReadout != null ) {
                        electronShapeReadout.refresh();
                    }
                }

                public void onGroupAdded( PairGroup group ) {
                    updateText();
                }

                public void onGroupRemoved( PairGroup group ) {
                    updateText();
                }
            } );
        }}, assetManager, inputManager ) {{
            MoleculeShapesApplication.showElectronShapeName.addObserver( new SimpleObserver() {
                public void update() {
                    // TODO: refactor visibility
                    setCullHint( MoleculeShapesApplication.showElectronShapeName.get() ? CullHint.Inherit : CullHint.Always );
                }
            } );
        }};
        preGuiNode.attachChild( electronShapeReadout );

        /*---------------------------------------------------------------------------*
        * "new" control panel
        *----------------------------------------------------------------------------*/
        controlPanel = new PiccoloJMENode( new ControlPanelNode( new MoleculeShapesControlPanel( this ) ), assetManager, inputManager );
        preGuiNode.attachChild( controlPanel );
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

    private void onGeometryChange() {
        if ( showGeometryButtonNode != null ) {
            showGeometryButtonNode.setCullHint( molecule.getLonePairs().isEmpty() ? CullHint.Always : CullHint.Inherit );
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
        rootNode.collideWith( ray, results );
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
        preGuiNode.collideWith( new Ray( new Vector3f( click2d.x, click2d.y, 0f ), new Vector3f( 0, 0, 1 ) ), results );
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

    public synchronized void testRemoveAtom() {
        if ( !molecule.getGroups().isEmpty() ) {
            molecule.removePair( molecule.getGroups().get( random.nextInt( molecule.getGroups().size() ) ) );
        }
    }

    @Override public synchronized void simpleUpdate( final float tpf ) {
        molecule.update( tpf );
        rebuildBonds();
        rebuildAngles();
        moleculeNode.setLocalRotation( rotation );

        if ( resizeDirty ) {
            resizeDirty = false;
            final float padding = 10;
            if ( controlPanel != null ) {
                controlPanel.setLocalTranslation( lastCanvasSize.width - controlPanel.getWidth() - padding,
                                                  lastCanvasSize.height - controlPanel.getHeight() - padding,
                                                  0 );
                resetAllNode.setLocalTranslation( controlPanel.getLocalTranslation().subtract( new Vector3f( -( controlPanel.getWidth() - resetAllNode.getWidth() ) / 2, 50, 0 ) ) );

                showGeometryButtonNode.setLocalTranslation( ( lastCanvasSize.width - showGeometryButtonNode.getWidth() ) / 2, padding, 0 );

                moleculeShapeReadout.setLocalTranslation( padding, lastCanvasSize.height - moleculeShapeReadout.getHeight() - padding, 0 );

                electronShapeReadout.setLocalTranslation( moleculeShapeReadout.getLocalTranslation().add( new Vector3f( 0, -30, 0 ) ) );
            }
        }
    }

    private void rebuildBonds() {
        // get our molecule-based camera position, so we can use that to compute orientation of double/triple bonds
        // TODO: refactor some of this duplicated code out
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = cam.getWorldCoordinates( new Vector2f( click2d.x, click2d.y ), 0f ).clone();

        // transform our position and direction into the local coordinate frame. we will do our computations there
        Vector3f transformedPosition = moleculeNode.getWorldTransform().transformInverseVector( click3d, new Vector3f() );

        // necessary for now since just updating their geometry shows significant errors
        for ( BondNode bondNode : bondNodes ) {
            moleculeNode.detachChild( bondNode );
        }
        bondNodes.clear();
        for ( PairGroup pair : molecule.getGroups() ) {
            if ( !pair.isLonePair ) {
                BondNode bondNode = new BondNode( pair.position.get(), pair.bondOrder, assetManager, transformedPosition );
                moleculeNode.attachChild( bondNode );
                bondNodes.add( bondNode );
            }
        }
    }

    private DecimalFormat angleFormat = new DecimalFormat( "##0.0" );

    private void rebuildAngles() {
        // TODO: docs and cleanup
        Vector3f dir = cam.getLocation();
        final Vector3f transformedDirection = moleculeNode.getLocalToWorldMatrix( new Matrix4f() ).transpose().mult( dir ).normalize(); // transpose trick to transform a unit vector

        for ( Spatial node : angleNodes ) {
            node.getParent().detachChild( node );
        }
        angleNodes.clear();

        if ( MoleculeShapesApplication.showBondAngles.get() ) {
            // iterate over all combinations of two pair groups
            for ( int i = 0; i < molecule.getGroups().size(); i++ ) {
                PairGroup a = molecule.getGroups().get( i );
                final ImmutableVector3D aDir = a.position.get().normalized();

                for ( int j = i + 1; j < molecule.getGroups().size(); j++ ) {
                    final PairGroup b = molecule.getGroups().get( j );
                    final ImmutableVector3D bDir = b.position.get().normalized();

                    final float brightness = calculateBrightness( aDir, bDir, transformedDirection );
                    if ( brightness == 0 ) {
                        continue;
                    }

                    final BondAngleNode bondAngleNode = new BondAngleNode( aDir, bDir, transformedDirection );
                    moleculeNode.attachChild( bondAngleNode );
                    angleNodes.add( bondAngleNode );

                    Vector3f globalCenter = moleculeNode.getWorldTransform().transformVector( bondAngleNode.getCenter(), new Vector3f() );
                    Vector3f globalMidpoint = moleculeNode.getWorldTransform().transformVector( bondAngleNode.getMidpoint(), new Vector3f() );

                    final Vector3f screenCenter = cam.getScreenCoordinates( globalCenter );
                    final Vector3f screenMidpoint = cam.getScreenCoordinates( globalMidpoint );

                    float extensionFactor = 1.3f;
                    final Vector3f displayPoint = screenMidpoint.subtract( screenCenter ).mult( extensionFactor ).add( screenCenter );

                    String labelText = angleFormat.format( Math.acos( aDir.dot( bDir ) ) * 180 / Math.PI ) + "°";
                    PiccoloJMENode label = new PiccoloJMENode( new PText( labelText ) {{
                        setFont( new PhetFont( 16 ) );
                        setTextPaint( new Color( 1f, 1f, 1f, brightness ) );
                    }}, assetManager, inputManager ) {{
                        setLocalTranslation( displayPoint.subtract( getWidth() / 2, getHeight() / 2, 0 ) );
                    }};
                    guiNode.attachChild( label );
                    angleNodes.add( label );
                }
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

    private class BondAngleNode extends Node {
        private Vector3f center;
        private Vector3f midpoint;

        // TODO: docs and cleanup, and move out if kept
        public BondAngleNode( ImmutableVector3D aDir, ImmutableVector3D bDir, Vector3f transformedDirection ) {
            float radius = 5;
            final float alpha = calculateBrightness( aDir, bDir, transformedDirection );

            Vector3f a = JmeUtils.convertVector( aDir );
            Vector3f b = JmeUtils.convertVector( bDir );

            Arc arc = new Arc( a, b, radius, 20 ) {{
                setLineWidth( 2 );
            }};

            center = new Vector3f();
            midpoint = Arc.slerp( a, b, 0.5f ).mult( radius );

            attachChild( new Geometry( "ArcTest", arc ) {{
                setMaterial( new Material( assetManager, "Common/MatDefs/Misc/Unshaded.j3md" ) {{
                    setColor( "Color", new ColorRGBA( 1, 0, 0, alpha ) );

                    getAdditionalRenderState().setBlendMode( BlendMode.Alpha );
                    setTransparent( true );
                }} );
            }} );

            setQueueBucket( Bucket.Transparent ); // allow it to be transparent
        }

        public Vector3f getCenter() {
            return center;
        }

        public Vector3f getMidpoint() {
            return midpoint;
        }
    }

    private static float calculateBrightness( ImmutableVector3D aDir, ImmutableVector3D bDir, Vector3f transformedDirection ) {
        float brightness = (float) Math.abs( aDir.cross( bDir ).dot( JmeUtils.convertVector( transformedDirection ) ) );

        brightness = brightness * 5 - 2.5f;
        if ( brightness < 0 ) {
            brightness = 0;
        }
        if ( brightness > 1 ) {
            brightness = 1;
        }
        return brightness;
    }

    @Override public void handleError( String errMsg, Throwable t ) {
        super.handleError( errMsg, t );
        if ( errMsg.equals( "Failed to initialize OpenGL context" ) ) {
            // TODO: improve the message
            PhetOptionPane.showMessageDialog( parentFrame, "Please upgrade your video card's drivers" );
        }
    }
}