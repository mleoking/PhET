// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.moleculeshapes.tabs.moleculeshapes;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Matrix4F;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3D;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.LWJGLCanvas;
import edu.colorado.phet.lwjglphet.math.LWJGLTransform;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.lwjglphet.nodes.GuiNode;
import edu.colorado.phet.lwjglphet.nodes.OrthoPiccoloNode;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Strings;
import edu.colorado.phet.moleculeshapes.control.BondTypeOverlayNode;
import edu.colorado.phet.moleculeshapes.control.GeometryNameNode;
import edu.colorado.phet.moleculeshapes.control.MoleculeShapesPanelNode;
import edu.colorado.phet.moleculeshapes.model.Bond;
import edu.colorado.phet.moleculeshapes.model.PairGroup;
import edu.colorado.phet.moleculeshapes.model.VSEPRMolecule;
import edu.colorado.phet.moleculeshapes.tabs.MoleculeViewTab;
import edu.colorado.phet.moleculeshapes.view.LonePairNode;
import edu.colorado.phet.moleculeshapes.view.MoleculeModelNode;
import edu.umd.cs.piccolo.util.PBounds;

import static edu.colorado.phet.moleculeshapes.MoleculeShapesConstants.OUTSIDE_PADDING;
import static edu.colorado.phet.moleculeshapes.MoleculeShapesSimSharing.UserComponents.moleculeShapesTab;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_VIEWPORT_BIT;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glViewport;

/**
 * Main module for Molecule Shapes
 */
public class MoleculeShapesTab extends MoleculeViewTab {

    private final boolean isBasicsVersion;

    /*---------------------------------------------------------------------------*
    * model
    *----------------------------------------------------------------------------*/

    public final Property<Boolean> addSingleBondEnabled = new Property<Boolean>( true );
    public final Property<Boolean> addDoubleBondEnabled = new Property<Boolean>( true );
    public final Property<Boolean> addTripleBondEnabled = new Property<Boolean>( true );
    public final Property<Boolean> addLonePairEnabled = new Property<Boolean>( true );

    /*---------------------------------------------------------------------------*
    * positioning
    *----------------------------------------------------------------------------*/

    private Property<Rectangle2D> realMoleculeOverlayStageBounds = new Property<Rectangle2D>( new PBounds( 0, 0, 1, 1 ) ); // initialized to technically valid state
    private Property<Rectangle2D> singleBondOverlayStageBounds;
    private Property<Rectangle2D> doubleBondOverlayStageBounds;
    private Property<Rectangle2D> tripleBondOverlayStageBounds;
    private Property<Rectangle2D> lonePairOverlayStageBounds;

    /*---------------------------------------------------------------------------*
    * graphics/control
    *----------------------------------------------------------------------------*/

    private OrthoPiccoloNode controlPanel;
    private OrthoPiccoloNode namePanel;

    private MoleculeShapesControlPanel controlPanelNode;

    // TODO: uncomment with controls
//    private MoleculeShapesControlPanel controlPanelNode;

    private static final Random random = new Random( System.currentTimeMillis() );


    public MoleculeShapesTab( LWJGLCanvas canvas, String name, boolean isBasicsVersion ) {
        super( canvas, name );
        this.isBasicsVersion = isBasicsVersion;
        setMolecule( new VSEPRMolecule() );
    }

    // should be called from stable positions in the JME and Swing EDT threads
    @Override public void initialize() {
        super.initialize();
        initializeResources();

        // when the molecule is made empty, make sure to show lone pairs again (will allow us to drag out new ones)
        getMolecule().onBondChanged.addListener( new VoidFunction1<Bond<PairGroup>>() {
            public void apply( Bond<PairGroup> bond ) {
                if ( getMolecule().getRadialLonePairs().isEmpty() ) {
                    showLonePairs.set( true );
                }
            }
        } );

        moleculeNode = new MoleculeModelNode( getMolecule(), readoutLayer, this );
        sceneLayer.addChild( moleculeNode );

        /*---------------------------------------------------------------------------*
        * molecule setup
        *----------------------------------------------------------------------------*/

        // start with two single bonds
        PairGroup centralAtom = new PairGroup( new Vector3D(), false, false );
        getMolecule().addCentralAtom( centralAtom );
        getMolecule().addGroup( new PairGroup( new Vector3D( 8, 0, 3 ).normalized().times( PairGroup.BONDED_PAIR_DISTANCE ), false, false ), centralAtom, 1 );
        Vector3D vector3D = new Vector3D( 2, 8, -5 );
        getMolecule().addGroup( new PairGroup( vector3D.normalized().times( PairGroup.BONDED_PAIR_DISTANCE ), false, false ), centralAtom, 1 );

        /*---------------------------------------------------------------------------*
        * bond overlays
        *----------------------------------------------------------------------------*/

        singleBondOverlayStageBounds = new Property<Rectangle2D>( new PBounds( 0, 0, getStageSize().width, getStageSize().height ) );
        doubleBondOverlayStageBounds = new Property<Rectangle2D>( new PBounds( 0, 0, getStageSize().width, getStageSize().height ) );
        tripleBondOverlayStageBounds = new Property<Rectangle2D>( new PBounds( 0, 0, getStageSize().width, getStageSize().height ) );
        lonePairOverlayStageBounds = new Property<Rectangle2D>( new PBounds( 0, 0, getStageSize().width, getStageSize().height ) );

        // TODO: bond overlays
        Function2<String, Property<Rectangle2D>, GLNode> createBondOverlayView = new Function2<String, Property<Rectangle2D>, GLNode>() {
            public GLNode apply( String name, final Property<Rectangle2D> rectangle2DProperty ) {
                return new GuiNode( MoleculeShapesTab.this ) {

                    private LWJGLTransform projectionTransform = new LWJGLTransform();

                    {
                        requireEnabled( GL_DEPTH_TEST );
                        requireEnabled( GL_BLEND );

                        // push/pop handling for the viewport calculations
                        addResetAttrib( GL_VIEWPORT_BIT );
                    }

                    @Override protected void preRender( GLOptions options ) {
                        super.preRender( options );

                        // TODO: fix crazy hacks below from the porting. my apologies.

                        Rectangle2D viewportBounds = canvasTransform.getTransformedBounds( rectangle2DProperty.get() );

                        // position the overlay viewport over this region
                        glViewport( (int) viewportBounds.getMinX(),
                                    (int) ( getCanvasHeight() - viewportBounds.getMaxY() ),
                                    (int) viewportBounds.getWidth(),
                                    (int) viewportBounds.getHeight() );

                        // TODO: adjust positioning here?
                        glMatrixMode( GL_PROJECTION );
                        glLoadIdentity();
                        float fieldOfViewRadians = (float) ( fieldOfViewDegrees / 180f * Math.PI );
                        float correctedFieldOfViewRadians = (float) Math.atan( Math.tan( fieldOfViewRadians ) ); // like the normal function, without the correction factor

                        Matrix4F perspectiveMatrix = getGluPerspective( correctedFieldOfViewRadians,
                                                                        (float) ( rectangle2DProperty.get().getWidth() / rectangle2DProperty.get().getHeight() ),
                                                                        nearPlane, farPlane );
                        projectionTransform.set( perspectiveMatrix );
                        projectionTransform.apply();

                        glMatrixMode( GL_MODELVIEW );
                        glLoadIdentity();
                        // extra translation so it looks "behind"
                        glTranslatef( 0, 0, -45 );

                        loadLighting();
                    }
                };
            }
        };

        // TODO: refactor

        GLNode singleBondOverlay = createBondOverlayView.apply( "Single Bond", singleBondOverlayStageBounds );
        singleBondOverlay.addChild( new BondTypeOverlayNode( new VSEPRMolecule() {{
            PairGroup centralAtom = new PairGroup( new Vector3D(), false, false );
            addCentralAtom( centralAtom );
            addGroup( new PairGroup( Vector3D.X_UNIT.times( PairGroup.BONDED_PAIR_DISTANCE ), false, false ), centralAtom, 1 );
        }}, this, addSingleBondEnabled ) );
        overlayLayer.addChild( singleBondOverlay );

        GLNode doubleBondOverlay = createBondOverlayView.apply( "Double Bond", doubleBondOverlayStageBounds );
        doubleBondOverlay.addChild( new BondTypeOverlayNode( new VSEPRMolecule() {{
            PairGroup centralAtom = new PairGroup( new Vector3D(), false, false );
            addCentralAtom( centralAtom );
            addGroup( new PairGroup( Vector3D.X_UNIT.times( PairGroup.BONDED_PAIR_DISTANCE ), false, false ), centralAtom, 2 );
        }}, this, addDoubleBondEnabled ) );
        overlayLayer.addChild( doubleBondOverlay );

        GLNode tripleBondOverlay = createBondOverlayView.apply( "Triple Bond", tripleBondOverlayStageBounds );
        tripleBondOverlay.addChild( new BondTypeOverlayNode( new VSEPRMolecule() {{
            PairGroup centralAtom = new PairGroup( new Vector3D(), false, false );
            addCentralAtom( centralAtom );
            addGroup( new PairGroup( Vector3D.X_UNIT.times( PairGroup.BONDED_PAIR_DISTANCE ), false, false ), centralAtom, 3 );
        }}, this, addTripleBondEnabled ) );
        overlayLayer.addChild( tripleBondOverlay );

        if ( !isBasicsVersion() ) {
            GLNode lonePairOverlay = createBondOverlayView.apply( "Lone Pair", lonePairOverlayStageBounds );
            lonePairOverlay.addChild( new BondTypeOverlayNode( new VSEPRMolecule() {{
                PairGroup centralAtom = new PairGroup( new Vector3D(), false, false );
                addCentralAtom( centralAtom );
                addGroup( new PairGroup( Vector3D.X_UNIT.times( PairGroup.LONE_PAIR_DISTANCE ), true, false ), centralAtom, 0 );
            }}, this, addLonePairEnabled ) );
            overlayLayer.addChild( lonePairOverlay );
        }

        /*---------------------------------------------------------------------------*
        * main control panel
        *----------------------------------------------------------------------------*/
        Property<Vector2D> controlPanelPosition = new Property<Vector2D>( new Vector2D() );
        controlPanelNode = new MoleculeShapesControlPanel( this );
        controlPanel = new OrthoPiccoloNode( controlPanelNode, this, canvasTransform, controlPanelPosition, mouseEventNotifier ) {{
            updateOnEvent( beforeFrameRender );
        }};
        guiLayer.addChild( controlPanel );
        guiNodes.add( controlPanel );
        controlPanel.onResize.addUpdateListener(
                new UpdateListener() {
                    public void update() {
                        if ( controlPanel != null ) {
                            controlPanel.position.set( new Vector2D(
                                    getStageSize().width - controlPanel.getComponentWidth() - OUTSIDE_PADDING,
                                    OUTSIDE_PADDING ) );
                        }
                        resizeDirty = true; // TODO: better way of getting this dependency?
                    }
                }, true );

        /*---------------------------------------------------------------------------*
        * "geometry name" panel
        *----------------------------------------------------------------------------*/
        namePanel = new OrthoPiccoloNode( new MoleculeShapesPanelNode( new GeometryNameNode( getMoleculeProperty(), !isBasicsVersion() ), Strings.CONTROL__GEOMETRY_NAME ) {{
            // TODO fix (temporary offset since PiccoloJMENode isn't checking the "origin")
            setOffset( 0, 10 );
        }}, this, canvasTransform, new Property<Vector2D>( new Vector2D() ), mouseEventNotifier ) {{
            updateOnEvent( beforeFrameRender );
            onResize.addUpdateListener( new UpdateListener() {
                public void update() {
                    position.set( new Vector2D( OUTSIDE_PADDING, getStageSize().getHeight() - getComponentHeight() - OUTSIDE_PADDING ) );
                }
            }, true );
        }};
        guiLayer.addChild( namePanel );
        guiNodes.add( namePanel );
    }

    @Override public void updateState( final float tpf ) {
        // add an offset to the left, since we have a control panel on the right, and add in the rotation
        moleculeNode.transform.set(
                Matrix4F.translation( new Vector3F( -4.5f, 1.5f, 0 ) )
                        .times( rotation.get().toRotationMatrix().toMatrix4f() ) );

        getMolecule().update( tpf );
        moleculeNode.updateView();

        // update the overlay viewport
        if ( resizeDirty && controlPanel != null ) {
            // TODO: refactoring here into generic viewport handling? (just tell it to be at X/Y for stage and it sticks there?)
            resizeDirty = false;

            double bondScaledWidth = getStageSize().getWidth() / 2.3;
            double bondScaledHeight = getStageSize().getHeight() / 2.3;

            // TODO: overlays handling
            // bond overlays
            Rectangle2D singleBondTargetStageBounds = controlPanel.transformBoundsToStage( controlPanelNode.getSingleBondTargetBounds() );
            Rectangle2D doubleBondTargetStageBounds = controlPanel.transformBoundsToStage( controlPanelNode.getDoubleBondTargetBounds() );
            Rectangle2D tripleBondTargetStageBounds = controlPanel.transformBoundsToStage( controlPanelNode.getTripleBondTargetBounds() );
            Rectangle2D lonePairTargetStageBounds = controlPanel.transformBoundsToStage( controlPanelNode.getLonePairTargetBounds() );
            // TODO: refactor
            singleBondOverlayStageBounds.set(
                    new PBounds(
                            // position the center of these bounds at the middle-left edge of the target bounds
                            singleBondTargetStageBounds.getMinX() - bondScaledWidth / 2,
                            singleBondTargetStageBounds.getCenterY() - bondScaledHeight / 2,
                            bondScaledWidth,
                            bondScaledHeight ) );
            doubleBondOverlayStageBounds.set(
                    new PBounds(
                            // position the center of these bounds at the middle-left edge of the target bounds
                            doubleBondTargetStageBounds.getMinX() - bondScaledWidth / 2,
                            doubleBondTargetStageBounds.getCenterY() - bondScaledHeight / 2,
                            bondScaledWidth,
                            bondScaledHeight ) );
            tripleBondOverlayStageBounds.set(
                    new PBounds(
                            // position the center of these bounds at the middle-left edge of the target bounds
                            tripleBondTargetStageBounds.getMinX() - bondScaledWidth / 2,
                            tripleBondTargetStageBounds.getCenterY() - bondScaledHeight / 2,
                            bondScaledWidth,
                            bondScaledHeight ) );
            lonePairOverlayStageBounds.set(
                    new PBounds(
                            // position the center of these bounds at the middle-left edge of the target bounds
                            lonePairTargetStageBounds.getMinX() - bondScaledWidth / 2,
                            lonePairTargetStageBounds.getCenterY() - bondScaledHeight / 2,
                            bondScaledWidth,
                            bondScaledHeight ) );
        }
    }

    public void startOverlayMoleculeDrag() {
        dragging = true;
        dragMode = DragMode.REAL_MOLECULE_ROTATE;
        draggingChanged();
    }

    public void startNewInstanceDrag( int bondOrder ) {
        // sanity check
        if ( !getMolecule().wouldAllowBondOrder( bondOrder ) ) {
            // don't add to the molecule if it is full
            return;
        }

        Vector3F localPosition = getPlanarMoleculeCursorPosition();

        PairGroup pair = new PairGroup( localPosition.to3D(), bondOrder == 0, true );
        getMolecule().addGroup( pair, getMolecule().getCentralAtom(), bondOrder, ( bondOrder == 0 ? PairGroup.LONE_PAIR_DISTANCE : PairGroup.BONDED_PAIR_DISTANCE ) / PairGroup.REAL_TMP_SCALE );

        // set up dragging information
        dragging = true;
        dragMode = DragMode.PAIR_FRESH_PLANAR;
        draggedParticle = pair;

        /*
         * If the left mouse button is not down, simulate a mouse-up. This is needed due to threading issues,
         * since if you do an "instant" mouse down/up, they both get processed before this is called.
         */
        if ( !globalLeftMouseDown ) {
            onLeftMouseUp();
        }
        draggingChanged();
    }

    public void updateLayout( Dimension canvasSize ) {
        resizeDirty = true;
    }

    private void initializeResources() {
        // pre-load the lone pair geometry, so we don't get that delay
        LonePairNode.getGeometry();
    }

    public boolean canAutoRotateRealMolecule() {
        return !( dragging && dragMode == DragMode.REAL_MOLECULE_ROTATE );
    }

    public boolean isBasicsVersion() {
        return isBasicsVersion;
    }

    @Override public boolean allowTogglingLonePairs() {
        return !isBasicsVersion();
    }

    @Override public boolean allowTogglingAllLonePairs() {
        return false;
    }

    @Override public boolean isRealTab() {
        return false;
    }

    public IUserComponent getUserComponent() {
        return moleculeShapesTab;
    }
}
