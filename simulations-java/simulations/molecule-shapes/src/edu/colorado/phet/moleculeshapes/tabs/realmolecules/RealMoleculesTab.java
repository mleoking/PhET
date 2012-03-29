// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.tabs.realmolecules;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector3D;
import edu.colorado.phet.common.phetcommon.math.Permutation;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.FunctionalUtils;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction2;
import edu.colorado.phet.jmephet.CanvasTransform.CenteredStageCanvasTransform;
import edu.colorado.phet.jmephet.JMEUtils;
import edu.colorado.phet.jmephet.JMEView;
import edu.colorado.phet.jmephet.PhetCamera;
import edu.colorado.phet.jmephet.PhetJMEApplication;
import edu.colorado.phet.jmephet.PhetJMEApplication.RenderPosition;
import edu.colorado.phet.jmephet.hud.HUDNode;
import edu.colorado.phet.jmephet.hud.PiccoloJMENode;
import edu.colorado.phet.jmephet.input.JMEInputHandler;
import edu.colorado.phet.moleculeshapes.MoleculeShapesApplication;
import edu.colorado.phet.moleculeshapes.MoleculeShapesColor;
import edu.colorado.phet.moleculeshapes.MoleculeShapesProperties;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Strings;
import edu.colorado.phet.moleculeshapes.MoleculeShapesSimSharing;
import edu.colorado.phet.moleculeshapes.MoleculeShapesSimSharing.ModelActions;
import edu.colorado.phet.moleculeshapes.MoleculeShapesSimSharing.ModelComponentTypes;
import edu.colorado.phet.moleculeshapes.MoleculeShapesSimSharing.ModelObjects;
import edu.colorado.phet.moleculeshapes.MoleculeShapesSimSharing.ModelParameterKeys;
import edu.colorado.phet.moleculeshapes.MoleculeShapesSimSharing.UserComponents;
import edu.colorado.phet.moleculeshapes.control.GeometryNameNode;
import edu.colorado.phet.moleculeshapes.control.MoleculeShapesPanelNode;
import edu.colorado.phet.moleculeshapes.control.PropertyRadioButtonNode;
import edu.colorado.phet.moleculeshapes.model.AttractorModel;
import edu.colorado.phet.moleculeshapes.model.AttractorModel.ResultMapping;
import edu.colorado.phet.moleculeshapes.model.Bond;
import edu.colorado.phet.moleculeshapes.model.LocalShape;
import edu.colorado.phet.moleculeshapes.model.Molecule;
import edu.colorado.phet.moleculeshapes.model.PairGroup;
import edu.colorado.phet.moleculeshapes.model.RealMolecule;
import edu.colorado.phet.moleculeshapes.model.RealMoleculeShape;
import edu.colorado.phet.moleculeshapes.model.VSEPRMolecule;
import edu.colorado.phet.moleculeshapes.model.VseprConfiguration;
import edu.colorado.phet.moleculeshapes.tabs.MoleculeViewTab;
import edu.colorado.phet.moleculeshapes.view.AtomNode;
import edu.colorado.phet.moleculeshapes.view.LonePairNode;
import edu.colorado.phet.moleculeshapes.view.MoleculeModelNode;
import edu.umd.cs.piccolo.PNode;

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
import com.jme3.math.FastMath;
import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.system.JmeCanvasContext;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet.parameterSet;
import static edu.colorado.phet.moleculeshapes.MoleculeShapesConstants.OUTSIDE_PADDING;
import static edu.colorado.phet.moleculeshapes.MoleculeShapesSimSharing.UserComponents.realMoleculesTabWithComboBox;
import static edu.colorado.phet.moleculeshapes.MoleculeShapesSimSharing.UserComponents.realMoleculesTabWithKit;

/**
 * Module that shows the difference between the model and the real shapes the molecules make
 */
public class RealMoleculesTab extends MoleculeViewTab {

    private PhetJMEApplication app;

    private final boolean useKit;
    private final boolean isBasicsVersion;

    /*---------------------------------------------------------------------------*
    * input mapping constants
    *----------------------------------------------------------------------------*/
    public static final String MAP_LEFT = "CameraLeft";
    public static final String MAP_RIGHT = "CameraRight";
    public static final String MAP_UP = "CameraUp";
    public static final String MAP_DOWN = "CameraDown";
    public static final String MAP_LMB = "CameraDrag";
    public static final String MAP_MMB = "RightMouseButton";

    private static final float ROTATION_MOUSE_SENSITIVITY = 5.0f;

    /*---------------------------------------------------------------------------*
    * model
    *----------------------------------------------------------------------------*/

    public Property<RealMoleculeShape> realMolecule = new Property<RealMoleculeShape>( null );

    // change this to toggle between the "Real" and "Model" views
    public Property<Boolean> showRealView = new Property<Boolean>( true );

    private JMEView readoutView;
    private PiccoloJMENode namePanel;

    /*---------------------------------------------------------------------------*
    * dragging
    *----------------------------------------------------------------------------*/

    public static enum DragMode {
        MODEL_ROTATE, // rotate the VSEPR model molecule
        PAIR_FRESH_PLANAR, // drag an atom/lone pair on the z=0 plane
        PAIR_EXISTING_SPHERICAL, // drag an atom/lone pair across the surface of a sphere
        REAL_MOLECULE_ROTATE // rotate the "real" molecule in the display
    }

    private volatile boolean dragging = false; // keeps track of the drag state
    private volatile DragMode dragMode = DragMode.MODEL_ROTATE;
    private volatile PairGroup draggedParticle = null;
    private volatile boolean globalLeftMouseDown = false; // keep track of the LMB state, since we need to deal with a few synchronization issues

    /*---------------------------------------------------------------------------*
    * positioning
    *----------------------------------------------------------------------------*/

    private volatile boolean resizeDirty = false;

    private Quaternion rotation = new Quaternion(); // The angle about which the molecule should be rotated, changes as a function of time

    /*---------------------------------------------------------------------------*
    * graphics/control
    *----------------------------------------------------------------------------*/

    private CenteredStageCanvasTransform canvasTransform;
    private PiccoloJMENode controlPanel;

    private JMEView guiView;

    private JMEView moleculeView;
    private Camera moleculeCamera;

    private MoleculeModelNode moleculeNode;

    private JMEInputHandler inputHandler;

    private static final Random random = new Random( System.currentTimeMillis() );


    public RealMoleculesTab( String name, boolean useKit, boolean isBasicsVersion ) {
        super( name );

        this.useKit = useKit;
        this.isBasicsVersion = isBasicsVersion;

        realMolecule.addObserver( new SimpleObserver() {
            public void update() {
                if ( realMolecule.get() != null ) {
                    SimSharingManager.sendModelMessage( ModelObjects.molecule, ModelComponentTypes.moleculeModel, ModelActions.realMoleculeChanged, new ParameterSet( new Parameter( ModelParameterKeys.realMolecule, realMolecule.get().getDisplayName() ) ) );
                }
            }
        } );

        // TODO: improve initialization here
        RealMoleculeShape startingMolecule = isBasicsVersion ? RealMoleculeShape.TAB_2_BASIC_MOLECULES[0] : RealMoleculeShape.TAB_2_MOLECULES[0];
        RealMolecule startingMoleculeModel = new RealMolecule( startingMolecule );
        setMolecule( startingMoleculeModel );
        realMolecule.set( startingMolecule );
    }

    // should be called from stable positions in the JME and Swing EDT threads
    @Override public void initialize() {
        initializeResources();

        app = JMEUtils.getApplication();

        inputHandler = getInputHandler();

        // hook up mouse-move handlers
        inputHandler.addMapping( MAP_LEFT, new MouseAxisTrigger( MouseInput.AXIS_X, true ) );
        inputHandler.addMapping( MAP_RIGHT, new MouseAxisTrigger( MouseInput.AXIS_X, false ) );
        inputHandler.addMapping( MAP_UP, new MouseAxisTrigger( MouseInput.AXIS_Y, false ) );
        inputHandler.addMapping( MAP_DOWN, new MouseAxisTrigger( MouseInput.AXIS_Y, true ) );

        // hook up mouse-button handlers
        inputHandler.addMapping( MAP_LMB, new MouseButtonTrigger( MouseInput.BUTTON_LEFT ) );

        /*---------------------------------------------------------------------------*
        * mouse-button presses
        *----------------------------------------------------------------------------*/
        inputHandler.addListener(
                new ActionListener() {
                    public void onAction( String name, boolean isMouseDown, float tpf ) {
                        // record whether the mouse button is down

                        // on left mouse button change
                        if ( name.equals( MAP_LMB ) ) {
                            globalLeftMouseDown = isMouseDown;

                            if ( isMouseDown ) {
                                onLeftMouseDown();
                            }
                            else {
                                onLeftMouseUp();
                            }
                        }
                    }
                }, MAP_LMB );

        /*---------------------------------------------------------------------------*
        * mouse motion
        *----------------------------------------------------------------------------*/
        inputHandler.addListener(
                new AnalogListener() {
                    public void onAnalog( final String name, final float value, float tpf ) {

                        //By always updating the cursor at every mouse move, we can be sure it is always correct.
                        //Whenever there is a mouse move event, make sure the cursor is in the right state.
                        updateCursor();

                        if ( dragging ) {

                            // function that updates a quaternion in-place, by adding the necessary rotation in, multiplied by the scale
                            final VoidFunction2<Quaternion, Float> updateQuaternion = new VoidFunction2<Quaternion, Float>() {
                                public void apply( Quaternion quaternion, Float scale ) {
                                    // if our window is smaller, rotate more
                                    float correctedScale = scale / getApproximateScale();

                                    if ( name.equals( MAP_LEFT ) ) {
                                        quaternion.set( new Quaternion().fromAngles( 0, -value * correctedScale, 0 ).mult( quaternion ) );
                                    }
                                    if ( name.equals( MAP_RIGHT ) ) {
                                        quaternion.set( new Quaternion().fromAngles( 0, value * correctedScale, 0 ).mult( quaternion ) );
                                    }
                                    if ( name.equals( MAP_UP ) ) {
                                        quaternion.set( new Quaternion().fromAngles( -value * correctedScale, 0, 0 ).mult( quaternion ) );
                                    }
                                    if ( name.equals( MAP_DOWN ) ) {
                                        quaternion.set( new Quaternion().fromAngles( value * correctedScale, 0, 0 ).mult( quaternion ) );
                                    }
                                }
                            };

                            switch( dragMode ) {
                                case MODEL_ROTATE:
                                    updateQuaternion.apply( rotation, ROTATION_MOUSE_SENSITIVITY );
                                    break;
                                case PAIR_FRESH_PLANAR:
                                    // put the particle on the z=0 plane
                                    draggedParticle.dragToPosition( JMEUtils.convertVector( getPlanarMoleculeCursorPosition() ) );
                                    break;
                                case PAIR_EXISTING_SPHERICAL:
                                    draggedParticle.dragToPosition( JMEUtils.convertVector( getSphericalMoleculeCursorPosition( JMEUtils.convertVector( draggedParticle.position.get() ) ) ) );
                                    break;
                            }
                        }
                    }
                }, MAP_LEFT, MAP_RIGHT, MAP_UP, MAP_DOWN, MAP_LMB );

        canvasTransform = new CenteredStageCanvasTransform( app.canvasSize );

        moleculeCamera = new PhetCamera( getStageSize(), canvasTransform.getCameraStrategy( 45, 1, 1000 ) );
        moleculeCamera.setLocation( new Vector3f( 0, 0, 40 ) );
        moleculeCamera.lookAt( new Vector3f( 0f, 0f, 0f ), Vector3f.UNIT_Y );

        moleculeView = createRegularView( "Main", moleculeCamera, RenderPosition.MAIN );
        guiView = createGUIView( "Back GUI", RenderPosition.BACK );
        readoutView = createGUIView( "Readout", RenderPosition.FRONT );

        // add an offset to the left, since we have a control panel on the right
        // TODO: make the offset dependent on the control panel width?
        moleculeView.getScene().setLocalTranslation( new Vector3f( -4.5f, 1.5f, 0 ) );

        // add lighting to the main scene
        addLighting( moleculeView.getScene() );

        moleculeNode = new MoleculeModelNode( getMolecule(), readoutView, this, moleculeCamera );
        moleculeView.getScene().attachChild( moleculeNode );

        showRealView.addObserver( new SimpleObserver() {
                                      public void update() {
                                          rebuildMolecule( false );
                                      }
                                  }, false );

        /*---------------------------------------------------------------------------*
        * main control panel
        *----------------------------------------------------------------------------*/
        Property<ImmutableVector2D> controlPanelPosition = new Property<ImmutableVector2D>( new ImmutableVector2D() );
        final Function0<Double> getControlPanelXPosition = new Function0<Double>() {
            public Double apply() {
                return controlPanel.position.get().getX();
            }
        };
        RealMoleculesControlPanel controlPanelNode = new RealMoleculesControlPanel( this, getControlPanelXPosition, isBasicsVersion );
        controlPanel = new PiccoloJMENode( controlPanelNode, inputHandler, this, canvasTransform, controlPanelPosition );
        guiView.getScene().attachChild( controlPanel );
        controlPanel.onResize.addUpdateListener(
                new UpdateListener() {
                    public void update() {
                        if ( controlPanel != null ) {
                            controlPanel.position.set( new ImmutableVector2D(
                                    getStageSize().width - controlPanel.getComponentWidth() - OUTSIDE_PADDING,
                                    getStageSize().height - controlPanel.getComponentHeight() - OUTSIDE_PADDING ) );
                        }
                        resizeDirty = true; // TODO: better way of getting this dependency?
                    }
                }, true );

        /*---------------------------------------------------------------------------*
        * options
        *----------------------------------------------------------------------------*/
//        final MoleculeShapesPanelNode optionsPanelNode = new MoleculeShapesPanelNode( new OptionsNode( this ), Strings.CONTROL__OPTIONS );
//        final PiccoloJMENode optionsPanel = new PiccoloJMENode( optionsPanelNode, inputHandler, this, canvasTransform, new Property<ImmutableVector2D>( new ImmutableVector2D() ) );
//        guiView.getScene().attachChild( optionsPanel );
//        optionsPanel.onResize.addUpdateListener(
//                new UpdateListener() {
//                    public void update() {
//                        if ( optionsPanel != null ) {
//                            optionsPanel.position.set( new ImmutableVector2D(
//                                    getStageSize().width - optionsPanel.getComponentWidth() - OUTSIDE_PADDING,
//                                    OUTSIDE_PADDING ) );
//                        }
//                        resizeDirty = true; // TODO: better way of getting this dependency?
//                    }
//                }, true );

        /*---------------------------------------------------------------------------*
        * real / model buttons
        *----------------------------------------------------------------------------*/
        if ( !isBasicsVersion ) {
            final PNode realModelSelectionNode = new PNode() {{
                // wrapper so our full bounds are handled correctly
                addChild( new PNode() {{
                    scale( 1.5 );
                    final PNode realRadioNode = new PropertyRadioButtonNode<Boolean>( UserComponents.realViewCheckBox, Strings.CONTROL__REAL_VIEW, showRealView, true );

                    // visibility handling (and initial adding)
                    MoleculeShapesApplication.showRealMoleculeRadioButtons.addObserver( new SimpleObserver() {
                        public void update() {
                            if ( MoleculeShapesApplication.showRealMoleculeRadioButtons.get() ) {
                                addChild( realRadioNode );
                            }
                            else {
                                removeChild( realRadioNode );
                            }
                        }
                    } );

                    final PNode modelRadioNode = new PropertyRadioButtonNode<Boolean>( UserComponents.modelViewCheckBox, Strings.CONTROL__MODEL_VIEW, showRealView, false );

                    // visibility handling (and initial adding)
                    MoleculeShapesApplication.showRealMoleculeRadioButtons.addObserver( new SimpleObserver() {
                        public void update() {
                            if ( MoleculeShapesApplication.showRealMoleculeRadioButtons.get() ) {
                                addChild( modelRadioNode );
                            }
                            else {
                                removeChild( modelRadioNode );
                            }
                        }
                    } );

                    modelRadioNode.setOffset( realRadioNode.getFullBounds().getMaxX() + 10, 0 );
                }} );
            }};

            guiView.getScene().attachChild( new PiccoloJMENode( realModelSelectionNode, inputHandler, this, canvasTransform,
                                                                new Property<ImmutableVector2D>( new ImmutableVector2D(
                                                                        ( (int) ( getStageSize().width - realModelSelectionNode.getFullBounds().getWidth() - controlPanelNode.getFullBounds().getWidth() ) / 2 ),
                                                                        ( (int) ( getStageSize().height - OUTSIDE_PADDING - realModelSelectionNode.getFullBounds().getHeight() ) )
                                                                ) ) ) );
        }

        /*---------------------------------------------------------------------------*
        * "geometry name" panel
        *----------------------------------------------------------------------------*/
        namePanel = new PiccoloJMENode( new MoleculeShapesPanelNode( new GeometryNameNode( getMoleculeProperty(), !isBasicsVersion ), Strings.CONTROL__GEOMETRY_NAME ) {{
            // TODO fix (temporary offset since PiccoloJMENode isn't checking the "origin")
            setOffset( 0, 10 );
        }}, inputHandler, this, canvasTransform );
        guiView.getScene().attachChild( namePanel );
        namePanel.position.set( new ImmutableVector2D( OUTSIDE_PADDING, OUTSIDE_PADDING ) );
    }

    public void switchToMolecule( RealMoleculeShape selectedRealMolecule ) {
        realMolecule.set( selectedRealMolecule );
        rebuildMolecule( true );
    }

    private void rebuildMolecule( final boolean switchedRealMolecule ) {
        moleculeNode.detachReadouts();
        moleculeView.getScene().detachChild( moleculeNode );

        /*---------------------------------------------------------------------------*
        * construct the new model, and rotate if we didn't switch molecules
        *----------------------------------------------------------------------------*/
        // get a "before" snapshot so that we can match rotations
        final Molecule molecule = getMolecule();

        // get a copy of our configuration, and count atoms / lone pairs
        final int numRadialAtoms = realMolecule.get().getCentralAtomCount();
        final int numRadialLonePairs = realMolecule.get().getCentralLonePairCount();
        final VseprConfiguration vseprConfiguration = new VseprConfiguration( numRadialAtoms, numRadialLonePairs );

        // get a copy of what might be the "old" molecule into whose space we need to rotate into
        final Molecule mappingMolecule;
        if ( switchedRealMolecule ) {
            // rebuild from scratch
            mappingMolecule = new RealMolecule( realMolecule.get() );
        }
        else {
            // base the rotation on our original
            mappingMolecule = molecule;
        }

        if ( showRealView.get() ) {
            setMolecule( new RealMolecule( realMolecule.get() ) {{
                if ( !switchedRealMolecule ) {
                    // NOTE: this might miss a couple improper mappings?

                    // compute the mapping from our "ideal" to our "old" molecule
                    // TODO: something in this mapping seems backwards... but it's working?
                    List<PairGroup> groups = new RealMolecule( realMolecule.get() ).getRadialGroups();
                    final ResultMapping mapping = AttractorModel.findClosestMatchingConfiguration(
                            AttractorModel.getOrientationsFromOrigin( mappingMolecule.getRadialGroups() ),
                            FunctionalUtils.map( LocalShape.sortedLonePairsFirst( groups ), new Function1<PairGroup, ImmutableVector3D>() {
                                public ImmutableVector3D apply( PairGroup pair ) {
                                    return pair.position.get().normalized();
                                }
                            } ),
                            LocalShape.vseprPermutations( mappingMolecule.getRadialGroups() ) );
                    for ( PairGroup group : getGroups() ) {
                        if ( group != getCentralAtom() ) {
                            group.position.set( mapping.rotateVector( group.position.get() ) );
                        }
                    }
                }
            }} );
        }
        else {
            final ResultMapping mapping = vseprConfiguration.getIdealGroupRotationToPositions( LocalShape.sortedLonePairsFirst( mappingMolecule.getRadialGroups() ) );
            final Permutation permutation = mapping.permutation.inverted();
            final List<ImmutableVector3D> idealUnitVectors = vseprConfiguration.getAllUnitVectors();

            setMolecule( new VSEPRMolecule() {{
                PairGroup newCentralAtom = new PairGroup( new ImmutableVector3D(), false, false );
                addCentralAtom( newCentralAtom );
                for ( int i = 0; i < numRadialAtoms + numRadialLonePairs; i++ ) {
                    ImmutableVector3D unitVector = mapping.rotateVector( idealUnitVectors.get( i ) );
                    if ( i < numRadialLonePairs ) {
                        addGroup( new PairGroup( unitVector.times( PairGroup.LONE_PAIR_DISTANCE ), true, false ), newCentralAtom, 0 );
                    }
                    else {
                        // we need to dig the bond order out of the mapping molecule, and we need to pick the right one (thus the permutation being applied, at an offset)
                        PairGroup oldRadialGroup = mappingMolecule.getRadialAtoms().get( permutation.apply( i ) - numRadialLonePairs );
                        Bond<PairGroup> bond = mappingMolecule.getParentBond( oldRadialGroup );
                        PairGroup group = new PairGroup( unitVector.times( bond.length * PairGroup.REAL_TMP_SCALE ), false, false );
                        addGroup( group, newCentralAtom, bond.order, bond.length );

                        addTerminalLonePairs( group, FunctionalUtils.count( mappingMolecule.getNeighbors( oldRadialGroup ), new Function1<PairGroup, Boolean>() {
                            public Boolean apply( PairGroup group ) {
                                return group.isLonePair;
                            }
                        } ) );
                    }
                }
            }} );
        }
        moleculeNode = new MoleculeModelNode( getMolecule(), readoutView, RealMoleculesTab.this, moleculeCamera );
        moleculeView.getScene().attachChild( moleculeNode );
    }

    @Override public void updateState( final float tpf ) {
        super.updateState( tpf );
        getMolecule().update( tpf );
        moleculeNode.updateView();
        moleculeNode.setLocalRotation( rotation );

        // update the overlay viewport
        if ( resizeDirty && controlPanel != null ) {
            // TODO: refactoring here into generic viewport handling? (just tell it to be at X/Y for stage and it sticks there?)
            resizeDirty = false;
        }
    }

    public PhetJMEApplication getApp() {
        return app;
    }

    public void startOverlayMoleculeDrag() {
        dragging = true;
        dragMode = DragMode.REAL_MOLECULE_ROTATE;
        draggingChanged();
    }


    boolean lastDragging = false;

    //Signify to the sim sharing feature that dragging state changed, should be called whenever dragging or dragMode changes (but batch together when both change)
    private void draggingChanged() {

        //Hide spurious "dragging = false" messages when clicking on piccolo swing buttons
        if ( lastDragging != dragging ) {
            SimSharingManager.sendUserMessage( MoleculeShapesSimSharing.UserComponents.draggingState, UserComponentTypes.unknown, UserActions.changed,
                                               parameterSet( MoleculeShapesSimSharing.ParamKeys.dragging, dragging ).
                                                       with( MoleculeShapesSimSharing.ParamKeys.dragMode, dragMode.toString() ) );
        }
        lastDragging = dragging;
    }

    private void onLeftMouseDown() {
        // for dragging, ignore mouse presses over the HUD
        HUDNode.withComponentUnderPointer( guiView, inputHandler, new VoidFunction1<Component>() {
            public void apply( final Component componentUnderPointer ) {
                boolean mouseOverInterface = componentUnderPointer != null;
                if ( !mouseOverInterface ) {
                    JMEUtils.invoke( new Runnable() {
                        public void run() {
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
                    } );
                }
            }
        } );
    }

    private void onLeftMouseUp() {
        // not dragging anymore
        dragging = false;

        // release an electron pair if we were dragging it
        if ( dragMode == DragMode.PAIR_FRESH_PLANAR || dragMode == DragMode.PAIR_EXISTING_SPHERICAL ) {
            draggedParticle.userControlled.set( false );
        }
        draggingChanged();
    }

    public static void addLighting( Node node ) {
        final DirectionalLight sun = new DirectionalLight();
        sun.setDirection( new Vector3f( 1, -0.5f, -2 ).normalizeLocal() );
        MoleculeShapesColor.SUN.addColorRGBAObserver( new VoidFunction1<ColorRGBA>() {
            public void apply( ColorRGBA colorRGBA ) {
                sun.setColor( colorRGBA );
            }
        } );
//        sun.setColor( MoleculeShapesConstants.SUN_COLOR );
        node.addLight( sun );

        final DirectionalLight moon = new DirectionalLight();
        moon.setDirection( new Vector3f( -2, 1, -1 ).normalizeLocal() );
        MoleculeShapesColor.MOON.addColorRGBAObserver( new VoidFunction1<ColorRGBA>() {
            public void apply( ColorRGBA colorRGBA ) {
                moon.setColor( colorRGBA );
            }
        } );
//        moon.setColor( MoleculeShapesConstants.MOON_COLOR );
        node.addLight( moon );
    }

    private void updateCursor() {
        //This solves a problem that we saw that: when there was no padding or other component on the side of the canvas, the mouse would become East-West resize cursor
        //And wouldn't change back.
        JmeCanvasContext context = (JmeCanvasContext) app.getContext();
        final Canvas canvas = context.getCanvas();

        //If the mouse is in front of a grabbable object, show a hand, otherwise show the default cursor
        final PairGroup pair = getElectronPairUnderPointer();

        HUDNode.withComponentUnderPointer( guiView, inputHandler, new VoidFunction1<Component>() {
            public void apply( Component component ) {
                if ( dragging && ( dragMode == DragMode.MODEL_ROTATE || dragMode == DragMode.REAL_MOLECULE_ROTATE ) ) {
                    // rotating the molecule. for now, trying out the "move" cursor
                    canvas.setCursor( Cursor.getPredefinedCursor( MoleculeShapesProperties.useRotationCursor.get() ? Cursor.MOVE_CURSOR : Cursor.DEFAULT_CURSOR ) );
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
        } );
    }

    public Vector3f getPlanarMoleculeCursorPosition() {
        Vector2f click2d = inputHandler.getCursorPosition();
        Vector3f click3d = moleculeCamera.getWorldCoordinates( new Vector2f( click2d.x, click2d.y ), 0f ).clone();
        Vector3f dir = moleculeCamera.getWorldCoordinates( new Vector2f( click2d.x, click2d.y ), 1f ).subtractLocal( click3d );

        float t = -click3d.getZ() / dir.getZ(); // solve for below equation at z=0. assumes camera isn't z=0, which should be safe here

        Vector3f globalStartPosition = click3d.add( dir.mult( t ) );

        // transform to moleculeNode coordinates and return
        return moleculeNode.getWorldTransform().transformInverseVector( globalStartPosition, new Vector3f() );
    }

    public Vector3f getSphericalMoleculeCursorPosition( Vector3f currentLocalPosition ) {
        // decide whether to grab the closest or farthest point if possible. for now, we try to NOT move the pair at the start of the drag
        boolean returnCloseHit = moleculeNode.getLocalToWorldMatrix( new Matrix4f() ).mult( currentLocalPosition ).z >= 0;

        // override for dev option
        if ( !MoleculeShapesProperties.allowDraggingBehind.get() ) {
            returnCloseHit = true;
        }

        // set up intersection stuff
        CollisionResults results = new CollisionResults();

        // transform our position and direction into the local coordinate frame. we will do our computations there
        Ray ray = JMEUtils.transformWorldRayToLocalCoordinates( moleculeView.getCameraRay( inputHandler.getCursorPosition() ), moleculeNode );
        Vector3f localCameraPosition = ray.getOrigin();
        Vector3f localCameraDirection = ray.getDirection();

        // how far we will end up from the center atom
        float finalDistance = (float) getMolecule().getIdealDistanceFromCenter( draggedParticle );

        // our sphere to cast our ray against
        BoundingSphere sphere = new BoundingSphere( finalDistance, new Vector3f( 0, 0, 0 ) );

        sphere.collideWithRay( ray, results );
        if ( results.size() == 0 ) {
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

            float distanceFromCamera = localCameraPosition.distance( new Vector3f() );

            // first, calculate it in unit-sphere, as noted above
            float d = distanceFromCamera / finalDistance; // scaled distance to the camera (from the origin)
            float z = 1 / d; // our result z (down-scaled)
            float height = FastMath.sqrt( d * d - 1 ) / d; // our result (down-scaled) magnitude of (x,y,0), which is the radius of the circle composed of all points that could be tangent

            /*
             * Since our camera isn't actually on the z-axis, we need to calculate two vectors. One is the direction towards
             * the camera (planeNormal, easy!), and the other is the direction perpendicular to the planeNormal that points towards
             * the mouse pointer (planeHitDirection).
             */

            // intersect our camera ray against our perpendicular plane (perpendicular to our camera position from the origin) to determine the orientations
            Vector3f planeNormal = localCameraPosition.normalize();
            float t = -( localCameraPosition.length() ) / ( planeNormal.dot( localCameraDirection ) );
            Vector3f planeHitDirection = localCameraPosition.add( localCameraDirection.mult( t ) ).normalize();

            // use the above plane hit direction (perpendicular to the camera) and plane normal (collinear with the camera) to calculate the result
            Vector3f downscaledResult = planeHitDirection.mult( height ).add( planeNormal.mult( z ) );

            // scale it back to our sized sphere
            return downscaledResult.mult( finalDistance );
        }
        else {
            // pick our desired hitpoint (there are only 2), and return it
            CollisionResult result = returnCloseHit ? results.getClosestCollision() : results.getFarthestCollision();
            return result.getContactPoint();
        }
    }

    /**
     * @return The closest (hit) electron pair currently under the mouse pointer, or null if there is none (central atom not counted)
     */
    public PairGroup getElectronPairUnderPointer() {
        for ( CollisionResult result : moleculeView.hitsUnderCursor( getInputHandler() ) ) {
            PairGroup pair = getElectronPairForTarget( result.getGeometry() );
            if ( pair != null ) {
                // don't drag the central atom OR any terminal lone pairs
                if ( pair != getMolecule().getCentralAtom() && !getMolecule().getDistantLonePairs().contains( pair ) ) {
                    return pair;
                }
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

    public boolean shouldUseKit() {
        return useKit;
    }

    @Override public void updateLayout( Dimension canvasSize ) {
        super.updateLayout( canvasSize );
        resizeDirty = true;
    }

    @Override public boolean allowTogglingLonePairs() {
        return !isBasicsVersion;
    }

    @Override public boolean allowTogglingAllLonePairs() {
        return false;
    }

    private void initializeResources() {
        // pre-load the lone pair geometry, so we don't get that delay
        LonePairNode.getGeometry( getAssetManager() );
    }

    public boolean canAutoRotateRealMolecule() {
        return !( dragging && dragMode == DragMode.REAL_MOLECULE_ROTATE );
    }

    public IUserComponent getUserComponent() {
        return useKit ? realMoleculesTabWithKit : realMoleculesTabWithComboBox;
    }
}
