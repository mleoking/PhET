package edu.colorado.phet.moleculeshapes.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option.None;
import edu.colorado.phet.common.phetcommon.util.Option.Some;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.moleculeshapes.MoleculeShapesApplication;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Strings;
import edu.colorado.phet.moleculeshapes.control.MoleculeShapesControlPanel;
import edu.colorado.phet.moleculeshapes.model.ImmutableVector3D;
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
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;

/**
 * Use jme3 to show a rotating molecule
 * TODO: fix firefox failure (where to put libs?
 * TODO: consider allowing more electron pairs, since we can't show the double/triple bond differences much!
 * TODO: audit for any other synchronization issues. we have the AWT and JME threads running rampant!
 * TODO: cursor stuff!
 * TODO: massive hidden bug if you middle-click-drag out a molecule!!!
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

    public static final Property<Boolean> showLonePairs = new Property<Boolean>( true );

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

        rootNode.setLocalTranslation( new Vector3f( -4.5f, 0, 0 ) );

        inputManager.addMapping( MoleculeJMEApplication.MAP_LEFT, new MouseAxisTrigger( MouseInput.AXIS_X, true ) );
        inputManager.addMapping( MoleculeJMEApplication.MAP_RIGHT, new MouseAxisTrigger( MouseInput.AXIS_X, false ) );
        inputManager.addMapping( MoleculeJMEApplication.MAP_UP, new MouseAxisTrigger( MouseInput.AXIS_Y, false ) );
        inputManager.addMapping( MoleculeJMEApplication.MAP_DOWN, new MouseAxisTrigger( MouseInput.AXIS_Y, true ) );

        inputManager.addMapping( MAP_LMB, new MouseButtonTrigger( MouseInput.BUTTON_LEFT ) );
        inputManager.addMapping( MAP_MMB, new MouseButtonTrigger( MouseInput.BUTTON_MIDDLE ) );

        inputManager.addListener(
                new ActionListener() {
                    public void onAction( String name, boolean value, float tpf ) {
                        // record whether the mouse button is down
                        synchronized ( MoleculeJMEApplication.this ) {
                            if ( name.equals( MAP_LMB ) ) {
                                dragging = value;

                                if ( dragging ) {
                                    PairGroup pair = getElectronPairUnderPointer();
                                    if ( pair != null ) {
                                        dragMode = DragMode.PAIR_EXISTING_SPHERICAL;
                                        draggedParticle = pair;
                                        pair.userControlled.set( true );
                                    }
                                    else {
                                        // set up default drag mode
                                        dragMode = DragMode.MOLECULE_ROTATE;
                                    }
                                }
                                else {
                                    // not dragging.

                                    // release an electron pair if we were dragging it
                                    if ( dragMode == DragMode.PAIR_FRESH_PLANAR || dragMode == DragMode.PAIR_EXISTING_SPHERICAL ) {
                                        draggedParticle.userControlled.set( false );
                                    }
                                }
                            }
                            if ( name.equals( MAP_MMB ) ) {
                                PairGroup pair = getElectronPairUnderPointer();
                                if ( pair != null ) {
                                    molecule.removePair( pair );
                                }
                            }
                        }
                    }
                }, MAP_LMB, MAP_MMB );
        inputManager.addListener(
                new AnalogListener() {
                    public void onAnalog( String name, float value, float tpf ) {
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
                                        draggedParticle.dragToPosition( vectorConversion( getPlanarMoleculeCursorPosition() ) );
                                        break;
                                    case PAIR_EXISTING_SPHERICAL:
                                        draggedParticle.dragToPosition( vectorConversion( getSphericalMoleculeCursorPosition( vectorConversion( draggedParticle.position.get() ) ) ) );
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

        //Create the atoms that circle about the central atom
        double angle = Math.PI * 2 / 5;
        for ( double theta = 0; theta < Math.PI * 2; theta += angle ) {
            double x = 10 * Math.cos( theta );
            double y = 10 * Math.sin( theta );
            molecule.addPair( new PairGroup( new ImmutableVector3D( x, y, 0 ), 1, false ) );
        }

        rebuildBonds();

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

        PairGroup pair = new PairGroup( vectorConversion( localPosition ), bondOrder, true );
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
        if ( MoleculeShapesApplication.dragExistingInFront.get() ) {
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
        Vector3f click3d = cam.getWorldCoordinates(
                new Vector2f( click2d.x, click2d.y ), 0f ).clone();
        Vector3f dir = cam.getWorldCoordinates(
                new Vector2f( click2d.x, click2d.y ), 1f ).subtractLocal( click3d );
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

    public static Vector3f vectorConversion( ImmutableVector3D vec ) {
        // TODO: move to utilities
        return new Vector3f( (float) vec.getX(), (float) vec.getY(), (float) vec.getZ() );
    }

    public static ImmutableVector3D vectorConversion( Vector3f vec ) {
        // TODO: move to utilities
        return new ImmutableVector3D( vec.getX(), vec.getY(), vec.getZ() );
    }

    public static void main( String[] args ) throws IOException {
        new MoleculeJMEApplication().start();
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
}