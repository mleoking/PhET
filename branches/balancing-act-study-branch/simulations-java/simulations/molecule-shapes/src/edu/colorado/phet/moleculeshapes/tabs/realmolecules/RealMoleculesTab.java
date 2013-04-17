// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.moleculeshapes.tabs.realmolecules;

import java.awt.*;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Matrix4F;
import edu.colorado.phet.common.phetcommon.math.Permutation;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3D;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.util.FunctionalUtils;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.lwjglphet.LWJGLCanvas;
import edu.colorado.phet.lwjglphet.nodes.OrthoPiccoloNode;
import edu.colorado.phet.moleculeshapes.MoleculeShapesApplication;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Strings;
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
import edu.colorado.phet.moleculeshapes.view.LonePairNode;
import edu.colorado.phet.moleculeshapes.view.MoleculeModelNode;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.moleculeshapes.MoleculeShapesConstants.OUTSIDE_PADDING;
import static edu.colorado.phet.moleculeshapes.MoleculeShapesSimSharing.UserComponents.realMoleculesTabWithComboBox;
import static edu.colorado.phet.moleculeshapes.MoleculeShapesSimSharing.UserComponents.realMoleculesTabWithKit;

/**
 * Module that shows the difference between the model and the real shapes the molecules make
 */
public class RealMoleculesTab extends MoleculeViewTab {

    private final boolean useKit;
    private final boolean isBasicsVersion;

    /*---------------------------------------------------------------------------*
    * model
    *----------------------------------------------------------------------------*/

    public Property<RealMoleculeShape> realMolecule = new Property<RealMoleculeShape>( null );

    // change this to toggle between the "Real" and "Model" views
    public Property<Boolean> showRealView = new Property<Boolean>( true );

    private OrthoPiccoloNode namePanel;

    /*---------------------------------------------------------------------------*
    * graphics/control
    *----------------------------------------------------------------------------*/

    private OrthoPiccoloNode controlPanel;

    private static final Random random = new Random( System.currentTimeMillis() );


    public RealMoleculesTab( LWJGLCanvas canvas, String name, boolean useKit, boolean isBasicsVersion ) {
        super( canvas, name );

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
        super.initialize();
        initializeResources();

        moleculeNode = new MoleculeModelNode( getMolecule(), readoutLayer, this );
        sceneLayer.addChild( moleculeNode );

        showRealView.addObserver( new SimpleObserver() {
            public void update() {
                rebuildMolecule( false );
            }
        }, false );

        // TODO: control panels!
        /*---------------------------------------------------------------------------*
        * main control panel
        *----------------------------------------------------------------------------*/
        Property<Vector2D> controlPanelPosition = new Property<Vector2D>( new Vector2D() );
        final Function0<Double> getControlPanelXPosition = new Function0<Double>() {
            public Double apply() {
                return controlPanel.position.get().getX();
            }
        };
        RealMoleculesControlPanel controlPanelNode = new RealMoleculesControlPanel( this, getControlPanelXPosition, isBasicsVersion );
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

            OrthoPiccoloNode realModelSelectionOrthoNode = new OrthoPiccoloNode( realModelSelectionNode, this, canvasTransform,
                                                          new Property<Vector2D>( new Vector2D(
                                                                  ( (int) ( getStageSize().width - realModelSelectionNode.getFullBounds().getWidth() - controlPanelNode.getFullBounds().getWidth() ) / 2 ),
                                                                  ( (int) ( OUTSIDE_PADDING ) )
                                                          ) ), mouseEventNotifier ) {{
                updateOnEvent( beforeFrameRender );
            }};
            guiLayer.addChild( realModelSelectionOrthoNode );
            guiNodes.add( realModelSelectionOrthoNode );
        }

        /*---------------------------------------------------------------------------*
        * "geometry name" panel
        *----------------------------------------------------------------------------*/
        namePanel = new OrthoPiccoloNode( new MoleculeShapesPanelNode( new GeometryNameNode( getMoleculeProperty(), !isBasicsVersion ), Strings.CONTROL__GEOMETRY_NAME ) {{
            // TODO fix (temporary offset since PiccoloJMENode isn't checking the "origin")
//            setOffset( 0, 10 );
        }}, this, canvasTransform, new Property<Vector2D>( new Vector2D() ), mouseEventNotifier ) {{
            onResize.addUpdateListener( new UpdateListener() {
                public void update() {
                    position.set( new Vector2D( OUTSIDE_PADDING, (int) ( getStageSize().getHeight() - getComponentHeight() - OUTSIDE_PADDING ) ) );
                }
            }, true );
            updateOnEvent( beforeFrameRender );
        }};
        guiLayer.addChild( namePanel );
        guiNodes.add( namePanel );
    }

    public void switchToMolecule( RealMoleculeShape selectedRealMolecule ) {
        realMolecule.set( selectedRealMolecule );
        rebuildMolecule( true );
    }

    private void rebuildMolecule( final boolean switchedRealMolecule ) {
        moleculeNode.detachReadouts();
        sceneLayer.removeChild( moleculeNode );

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
                            FunctionalUtils.map( LocalShape.sortedLonePairsFirst( groups ), new Function1<PairGroup, Vector3D>() {
                                public Vector3D apply( PairGroup pair ) {
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
            final List<Vector3D> idealUnitVectors = vseprConfiguration.getAllUnitVectors();

            setMolecule( new VSEPRMolecule() {{
                PairGroup newCentralAtom = new PairGroup( new Vector3D(), false, false );
                addCentralAtom( newCentralAtom );
                for ( int i = 0; i < numRadialAtoms + numRadialLonePairs; i++ ) {
                    Vector3D unitVector = mapping.rotateVector( idealUnitVectors.get( i ) );
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
        moleculeNode = new MoleculeModelNode( getMolecule(), readoutLayer, RealMoleculesTab.this );
        sceneLayer.addChild( moleculeNode );
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
        }
    }

    public void startOverlayMoleculeDrag() {
        dragging = true;
        dragMode = DragMode.REAL_MOLECULE_ROTATE;
        draggingChanged();
    }

    public boolean shouldUseKit() {
        return useKit;
    }

    public void updateLayout( Dimension canvasSize ) {
        resizeDirty = true;
    }

    @Override public boolean allowTogglingLonePairs() {
        return !isBasicsVersion;
    }

    @Override public boolean allowTogglingAllLonePairs() {
        return false;
    }

    @Override public boolean allowMiddleMouseClickModifications() {
        return false;
    }

    @Override public boolean isRealTab() {
        return true;
    }

    private void initializeResources() {
        // pre-load the lone pair geometry, so we don't get that delay
        LonePairNode.getGeometry();
    }

    public boolean canAutoRotateRealMolecule() {
        return !( dragging && dragMode == DragMode.REAL_MOLECULE_ROTATE );
    }

    public IUserComponent getUserComponent() {
        return useKit ? realMoleculesTabWithKit : realMoleculesTabWithComboBox;
    }
}
