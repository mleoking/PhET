package edu.colorado.phet.moleculeshapes.view;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option.None;
import edu.colorado.phet.common.phetcommon.util.Option.Some;
import edu.colorado.phet.common.phetcommon.view.ResetAllButton;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.moleculeshapes.MoleculeShapesApplication;
import edu.colorado.phet.moleculeshapes.control.MoleculeShapesControlPanel;
import edu.colorado.phet.moleculeshapes.model.ElectronPair;
import edu.colorado.phet.moleculeshapes.model.ImmutableVector3D;
import edu.colorado.phet.moleculeshapes.model.MoleculeModel;
import edu.colorado.phet.moleculeshapes.model.MoleculeModel.Adapter;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

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
 * TODO: audit for any other synchronization issues. we have the AWT and JME threads running rampant!
 * TODO: cursor stuff!
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
    private ElectronPair draggedParticle = null;

    /*---------------------------------------------------------------------------*
    * positioning
    *----------------------------------------------------------------------------*/

    private Dimension lastCanvasSize;
    private boolean resizeDirty = false;

    private Quaternion rotation = new Quaternion(); // The angle about which the molecule should be rotated, changes as a function of time

    /*---------------------------------------------------------------------------*
    * graphics/control
    *----------------------------------------------------------------------------*/

    private SwingJMENode oldControlPanel;
    private PiccoloJMENode controlPanel;
    private PiccoloJMENode resetAllNode;

    private Node moleculeNode; // The molecule to display and rotate

    private static final Random random = new Random( System.currentTimeMillis() );

    /**
     * Pseudo-constructor (JME-based)
     */
    @Override public void initialize() {
        super.initialize();

        initializeResources();

        // TODO: re-center
//        rootNode.setLocalTranslation( new Vector3f( -4, 0, 0 ) );

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
                                    ElectronPair pair = getElectronPairUnderPointer();
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
                                ElectronPair pair = getElectronPairUnderPointer();
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
            @Override public void onPairAdded( ElectronPair pair ) {
                if ( pair.isLonePair ) {
                    LonePairNode lonePairNode = new LonePairNode( pair, assetManager );
                    lonePairNodes.add( lonePairNode );
                    moleculeNode.attachChild( lonePairNode );
                }
                else {
                    AtomNode atomNode = new AtomNode( new Some<ElectronPair>( pair ), assetManager );
                    atomNodes.add( atomNode );
                    moleculeNode.attachChild( atomNode );
                    rebuildBonds();
                }
            }

            @Override public void onPairRemoved( ElectronPair pair ) {
                if ( pair.isLonePair ) {
                    for ( LonePairNode lonePairNode : new ArrayList<LonePairNode>( lonePairNodes ) ) {
                        // TODO: associate these more closely! (comparing positions for equality is bad)
                        if ( lonePairNode.position == pair.position ) {
                            lonePairNodes.remove( lonePairNode );
                            moleculeNode.detachChild( lonePairNode );
                        }
                    }
                }
                else {
                    for ( AtomNode atomNode : new ArrayList<AtomNode>( atomNodes ) ) {
                        // TODO: associate these more closely! (comparing positions for equality is bad)
                        if ( atomNode.position == pair.position ) {
                            atomNodes.remove( atomNode );
                            moleculeNode.detachChild( atomNode );
                        }
                    }
                }
            }
        } );

        /*---------------------------------------------------------------------------*
        * atoms
        *----------------------------------------------------------------------------*/

        //Create the central atom
        AtomNode center = new AtomNode( new None<ElectronPair>(), assetManager );
        moleculeNode.attachChild( center );

        //Create the atoms that circle about the central atom
        double angle = Math.PI * 2 / 5;
        for ( double theta = 0; theta < Math.PI * 2; theta += angle ) {
            double x = 10 * Math.cos( theta );
            double y = 10 * Math.sin( theta );
            molecule.addPair( new ElectronPair( new ImmutableVector3D( x, y, 0 ), false, false ) );
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

        oldControlPanel = new SwingJMENode( new OldControlPanel(), assetManager, inputManager ) {{ }};
        preGuiNode.attachChild( oldControlPanel );

        preGuiNode.attachChild( new PiccoloJMENode( new TextButtonNode( "Show Molecular Geometry", new PhetFont( 20 ), Color.ORANGE ) {{
            addActionListener( new java.awt.event.ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    showLonePairs.set( !showLonePairs.get() );
                    setText( showLonePairs.get() ? "Show Molecular Geometry" : "Show Electron Geometry" );
                }
            } );
        }}, assetManager, inputManager ) );

        resetAllNode = new PiccoloJMENode( new TextButtonNode( "Reset", new PhetFont( 16 ), Color.ORANGE ) {{
            addActionListener( new java.awt.event.ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    resetAll();
                }
            } );
        }}, assetManager, inputManager );
        preGuiNode.attachChild( resetAllNode );

        /*---------------------------------------------------------------------------*
        * "new" control panel
        *----------------------------------------------------------------------------*/
        controlPanel = new PiccoloJMENode( new ControlPanelNode( new MoleculeShapesControlPanel( this ) ), assetManager, inputManager );
        preGuiNode.attachChild( controlPanel );
    }

    public synchronized void resetAll() {
        removeAllAtoms();
        // TODO: reset checkbox states, etc
    }

    public synchronized void startNewInstanceDrag( int bondOrder ) {
        if ( molecule.isFull() ) {
            // don't add to the molecule if it is full
            // TODO: find better way of not calling this (or not having the user attempt to drag!) grey-out the control panel bonds?
            return;
        }

        Vector3f localPosition = getPlanarMoleculeCursorPosition();

        ElectronPair pair = new ElectronPair( vectorConversion( localPosition ), bondOrder == 0, true );
        molecule.addPair( pair );

        if ( bondOrder == 1 || bondOrder == 0 ) {
            dragging = true;
            dragMode = DragMode.PAIR_FRESH_PLANAR;
            draggedParticle = pair;
        }
        else {
            throw new NotImplementedException(); // TODO: other bonds and lone pair dragging
        }
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
        Vector3f transformedDirection = moleculeNode.getLocalToWorldMatrix( new Matrix4f() ).invert().transpose().mult( dir ).normalize(); // transpose trick to transform a unit vector
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
    public ElectronPair getElectronPairUnderPointer() {
        CollisionResults results = new CollisionResults();
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = cam.getWorldCoordinates(
                new Vector2f( click2d.x, click2d.y ), 0f ).clone();
        Vector3f dir = cam.getWorldCoordinates(
                new Vector2f( click2d.x, click2d.y ), 1f ).subtractLocal( click3d );
        Ray ray = new Ray( click3d, dir );
        rootNode.collideWith( ray, results );
        for ( CollisionResult result : results ) {
            ElectronPair pair = getElectronPairForTarget( result.getGeometry() );
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
    private ElectronPair getElectronPairForTarget( Spatial target ) {
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

    public void testAddAtom( boolean isLonePair ) {
        if ( molecule.getPairs().size() < 6 ) {
            ImmutableVector3D initialPosition = new ImmutableVector3D( Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5 ).normalized().times( 7 );
            molecule.addPair( new ElectronPair( initialPosition, isLonePair, false ) );
        }
    }

    public synchronized void testRemoveAtom() {
        if ( !molecule.getPairs().isEmpty() ) {
            molecule.removePair( molecule.getPairs().get( random.nextInt( molecule.getPairs().size() ) ) );
        }
    }

    @Override public synchronized void simpleUpdate( final float tpf ) {
        molecule.update( tpf );
        rebuildBonds();
        moleculeNode.setLocalRotation( rotation );

        if ( resizeDirty ) {
            resizeDirty = false;
            if ( oldControlPanel != null ) {
                oldControlPanel.setLocalTranslation( lastCanvasSize.width - oldControlPanel.getWidth(),
                                                     lastCanvasSize.height - oldControlPanel.getHeight(),
                                                     0 );
            }
            if ( controlPanel != null ) {
                final float padding = 10;
                controlPanel.setLocalTranslation( padding, lastCanvasSize.height - controlPanel.getHeight() - padding, 0 );

                resetAllNode.setLocalTranslation( controlPanel.getLocalTranslation().subtract( new Vector3f( -( controlPanel.getWidth() - resetAllNode.getWidth() ) / 2, 50, 0 ) ) );
            }
        }
    }

    private void rebuildBonds() {
        // necessary for now since just updating their geometry shows significant errors
        for ( BondNode bondNode : bondNodes ) {
            moleculeNode.detachChild( bondNode );
        }
        bondNodes.clear();
        for ( ElectronPair pair : molecule.getPairs() ) {
            if ( !pair.isLonePair ) {
                BondNode bondNode = new BondNode( pair.position.get(), assetManager );
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
        while ( !molecule.getPairs().isEmpty() ) {
            testRemoveAtom();
        }
    }

    public synchronized void setState( final int X, final int E ) {
        enqueue( new Callable<Object>() {
            public Object call() throws Exception {
                removeAllAtoms();
                for ( int i = 0; i < X; i++ ) {
                    testAddAtom( false );
                }
                for ( int i = 0; i < E; i++ ) {
                    testAddAtom( true );
                }
                return null;
            }
        } );
    }

    public void onResize( Dimension canvasSize ) {
        lastCanvasSize = canvasSize;
        resizeDirty = true;
    }

    private void initializeResources() {
        // pre-load the lone pair geometry, so we don't get that delay
        LonePairNode.getGeometry( assetManager );
    }

    private class OldControlPanel extends JPanel {
        public OldControlPanel() {
            super( new GridBagLayout() );
            add(
                    new JPanel() {{
                        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
                        add( new JPanel( new GridBagLayout() ) {{
                            setBorder( new TitledBorder( "Bonding" ) );
                            add( new PCanvas() {{
                                     setPreferredSize( new Dimension( MoleculeShapesControlPanel.BOND_WIDTH, MoleculeShapesControlPanel.BOND_HEIGHT ) );
                                     getLayer().addChild( new PhetPPath( new java.awt.geom.Rectangle2D.Double( 0, 0, MoleculeShapesControlPanel.BOND_WIDTH, MoleculeShapesControlPanel.BOND_HEIGHT ) ) {{
                                         setPaint( Color.BLACK );
                                     }} );
                                     getLayer().addInputEventListener( new PBasicInputEventHandler() {
                                         @Override public void mousePressed( PInputEvent event ) {
                                             System.out.println( "Single bond!" );
                                         }
                                     } );
                                     setOpaque( false );
                                     removeInputEventListener( getZoomEventHandler() );
                                     removeInputEventListener( getPanEventHandler() );
                                 }}, new GridBagConstraints() );
                            add( new JLabel( "Single" ), new GridBagConstraints() {{
                                gridx = 0;
                                gridy = 1;
                            }} );
                            add( new PCanvas() {{
                                     setPreferredSize( new Dimension( MoleculeShapesControlPanel.BOND_WIDTH, MoleculeShapesControlPanel.BOND_HEIGHT * 2 + MoleculeShapesControlPanel.BOND_SPACING ) );
                                     getLayer().addChild( new PhetPPath( new java.awt.geom.Rectangle2D.Double( 0, 0, MoleculeShapesControlPanel.BOND_WIDTH, MoleculeShapesControlPanel.BOND_HEIGHT ) ) {{
                                         setPaint( Color.BLACK );
                                     }} );
                                     getLayer().addChild( new PhetPPath( new java.awt.geom.Rectangle2D.Double( 0, 0, MoleculeShapesControlPanel.BOND_WIDTH, MoleculeShapesControlPanel.BOND_HEIGHT ) ) {{
                                         setPaint( Color.BLACK );
                                         setOffset( 0, MoleculeShapesControlPanel.BOND_HEIGHT + MoleculeShapesControlPanel.BOND_SPACING );
                                     }} );
                                     getLayer().addInputEventListener( new PBasicInputEventHandler() {
                                         @Override public void mousePressed( PInputEvent event ) {
                                             System.out.println( "Double bond!" );
                                         }
                                     } );
                                     setOpaque( false );
                                     removeInputEventListener( getZoomEventHandler() );
                                     removeInputEventListener( getPanEventHandler() );
                                 }},
                                 new GridBagConstraints() {{
                                     gridx = 0;
                                     gridy = 2;
                                     insets = new Insets( 15, 0, 0, 0 );
                                 }}
                            );
                            add( new JLabel( "Double" ), new GridBagConstraints() {{
                                gridx = 0;
                                gridy = 3;
                            }} );
                            add( new PCanvas() {{
                                     setPreferredSize( new Dimension( MoleculeShapesControlPanel.BOND_WIDTH, MoleculeShapesControlPanel.BOND_HEIGHT * 3 + MoleculeShapesControlPanel.BOND_SPACING * 2 ) );
                                     getLayer().addChild( new PhetPPath( new java.awt.geom.Rectangle2D.Double( 0, 0, MoleculeShapesControlPanel.BOND_WIDTH, MoleculeShapesControlPanel.BOND_HEIGHT ) ) {{
                                         setPaint( Color.BLACK );
                                     }} );
                                     getLayer().addChild( new PhetPPath( new java.awt.geom.Rectangle2D.Double( 0, 0, MoleculeShapesControlPanel.BOND_WIDTH, MoleculeShapesControlPanel.BOND_HEIGHT ) ) {{
                                         setPaint( Color.BLACK );
                                         setOffset( 0, MoleculeShapesControlPanel.BOND_HEIGHT + MoleculeShapesControlPanel.BOND_SPACING );
                                     }} );
                                     getLayer().addChild( new PhetPPath( new java.awt.geom.Rectangle2D.Double( 0, 0, MoleculeShapesControlPanel.BOND_WIDTH, MoleculeShapesControlPanel.BOND_HEIGHT ) ) {{
                                         setPaint( Color.BLACK );
                                         setOffset( 0, ( MoleculeShapesControlPanel.BOND_HEIGHT + MoleculeShapesControlPanel.BOND_SPACING ) * 2 );
                                     }} );
                                     getLayer().addInputEventListener( new PBasicInputEventHandler() {
                                         @Override public void mousePressed( PInputEvent event ) {
                                             System.out.println( "Triple bond!" );
                                         }
                                     } );
                                     setOpaque( false );
                                     removeInputEventListener( getZoomEventHandler() );
                                     removeInputEventListener( getPanEventHandler() );
                                 }},
                                 new GridBagConstraints() {{
                                     gridx = 0;
                                     gridy = 4;
                                     insets = new Insets( 15, 0, 0, 0 );
                                 }}
                            );
                            add( new JLabel( "Triple" ), new GridBagConstraints() {{
                                gridx = 0;
                                gridy = 5;
                            }} );
                        }} );
                        add(
                                new JPanel( new GridBagLayout() ) {{
                                    setBorder( new TitledBorder( "Non-Bonding" ) );
                                    add( new PCanvas() {{
                                             setPreferredSize( new Dimension( 50, 20 ) );
                                             getLayer().addChild( new PhetPPath( new java.awt.geom.Ellipse2D.Double( 8, 3, 14, 14 ) ) {{
                                                 setPaint( Color.BLACK );
                                             }} );
                                             getLayer().addChild( new PhetPPath( new java.awt.geom.Ellipse2D.Double( 28, 3, 14, 14 ) ) {{
                                                 setPaint( Color.BLACK );
                                             }} );
                                             getLayer().addInputEventListener( new PBasicInputEventHandler() {
                                                 @Override public void mousePressed( PInputEvent event ) {
                                                     System.out.println( "Lone Pair!" );
                                                 }
                                             } );
                                             setOpaque( false );
                                             removeInputEventListener( getZoomEventHandler() );
                                             removeInputEventListener( getPanEventHandler() );
                                         }},
                                         new GridBagConstraints() );
                                    add( new JLabel( "Lone Pair" ), new GridBagConstraints() {{
                                        gridx = 0;
                                        gridy = 1;
                                    }} );
                                }}
                        );
                        add(
                                new JPanel() {{
                                    setLayout( new GridBagLayout() );
                                    setBorder( new TitledBorder( "Geometry Name" ) );
                                    add( new JPanel() {{
                                             setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
                                             add( new JCheckBox( "Molecular" ) );
                                             add( new JCheckBox( "Electron" ) );
                                         }}, new GridBagConstraints() );
                                }}
                        );
                        add( new JButton( "(Test) Add Atom" ) {{
                            addActionListener( new java.awt.event.ActionListener() {
                                public void actionPerformed( ActionEvent e ) {
                                    enqueue( new Callable<Object>() {
                                        public Object call() throws Exception {
                                            testAddAtom( false );
                                            return null;
                                        }
                                    } );
                                }
                            } );
                        }} );
                        add( new JButton( "(Test) Add Lone Pair" ) {{
                            addActionListener( new java.awt.event.ActionListener() {
                                public void actionPerformed( ActionEvent e ) {
                                    enqueue( new Callable<Object>() {
                                        public Object call() throws Exception {
                                            testAddAtom( true );
                                            return null;
                                        }
                                    } );
                                }
                            } );
                        }} );
                        add( new JButton( "(Test) Remove Random" ) {{
                            addActionListener( new java.awt.event.ActionListener() {
                                public void actionPerformed( ActionEvent e ) {
                                    enqueue( new Callable<Object>() {
                                        public Object call() throws Exception {
                                            testRemoveAtom();
                                            return null;
                                        }
                                    } );
                                }
                            } );
                        }} );

                        class VSEPRButton extends JButton {
                            VSEPRButton( String nickname, final int X, final int E ) {
                                super( nickname + " AX" + X + "E" + E );
                                addActionListener( new java.awt.event.ActionListener() {
                                    public void actionPerformed( ActionEvent e ) {
                                        setState( X, E );
                                    }
                                } );
                            }
                        }

                        add( new VSEPRButton( "Linear", 2, 3 ) );
                        add( new VSEPRButton( "Trigonal pyramidal", 3, 1 ) );
                        add( new VSEPRButton( "T-shaped", 3, 2 ) );
                        add( new VSEPRButton( "Seesaw", 4, 1 ) );
                        add( new VSEPRButton( "Square Planar", 4, 2 ) );
                        add( new VSEPRButton( "Square Pyramidal", 5, 1 ) );
                        add( new ResetAllButton( this ) );
                    }},
                    new GridBagConstraints() {{
                        gridx = 0;
                        gridy = 0;
                        anchor = GridBagConstraints.FIRST_LINE_END;
                        weighty = 1;
                        weightx = 1;
                    }}
            );
        }
    }
}