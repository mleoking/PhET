// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.platetectonics.tabs;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Mouse;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2F;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.lwjglphet.LWJGLCanvas;
import edu.colorado.phet.lwjglphet.math.ImmutableMatrix4F;
import edu.colorado.phet.common.phetcommon.math.Ray3F;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.lwjglphet.nodes.GuiNode;
import edu.colorado.phet.lwjglphet.nodes.OrthoComponentNode;
import edu.colorado.phet.lwjglphet.nodes.OrthoPiccoloNode;
import edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;
import edu.colorado.phet.platetectonics.PlateTectonicsSimSharing;
import edu.colorado.phet.platetectonics.PlateTectonicsSimSharing.ParameterKeys;
import edu.colorado.phet.platetectonics.PlateTectonicsSimSharing.UserActions;
import edu.colorado.phet.platetectonics.PlateTectonicsSimSharing.UserComponents;
import edu.colorado.phet.platetectonics.control.CrustChooserPanel;
import edu.colorado.phet.platetectonics.control.CrustPieceNode;
import edu.colorado.phet.platetectonics.control.MotionTypeChooserPanel;
import edu.colorado.phet.platetectonics.control.PlayModePanel;
import edu.colorado.phet.platetectonics.control.ResetPanel;
import edu.colorado.phet.platetectonics.control.TectonicsTimeControl;
import edu.colorado.phet.platetectonics.control.ViewOptionsPanel;
import edu.colorado.phet.platetectonics.model.PlateTectonicsModel;
import edu.colorado.phet.platetectonics.model.PlateMotionModel;
import edu.colorado.phet.platetectonics.model.PlateType;
import edu.colorado.phet.platetectonics.model.labels.RangeLabel;
import edu.colorado.phet.platetectonics.model.labels.TextLabel;
import edu.colorado.phet.platetectonics.util.Bounds3D;
import edu.colorado.phet.platetectonics.util.Grid3D;
import edu.colorado.phet.platetectonics.util.Side;
import edu.colorado.phet.platetectonics.view.BoxHighlightNode;
import edu.colorado.phet.platetectonics.view.HandleNode;
import edu.colorado.phet.platetectonics.view.PlateTectonicsView;
import edu.colorado.phet.platetectonics.view.labels.RangeLabelNode;
import edu.colorado.phet.platetectonics.view.labels.TextLabelNode;

/**
 * Displays two main plates that the user can direct to move towards, away from, or along each other.
 */
public class PlateMotionTab extends PlateTectonicsTab {

    // either auto or manual mode animations
    public final Property<Boolean> isAutoMode = new Property<Boolean>( false );

    // the panel container that is behind the crust pieces.
    private CrustChooserPanel crustChooserPanel;

    // the node that holds the panel
    private OrthoPiccoloNode crustChooserNode;

    // the layer where the crust pieces are added
    private GuiNode crustPieceLayer;

    // display options
    public final Property<Boolean> showLabels = new Property<Boolean>( false );
    public final Property<Boolean> showWater = new Property<Boolean>( false );

    private final List<OrthoPiccoloNode> placedPieces = new ArrayList<OrthoPiccoloNode>();

    // in auto mode, the user can choose what type of motion to follow
    private OrthoPiccoloNode motionTypeChooserPanel = null;

    // dragging state for the manual mode
    private boolean draggingPlate = false;
    private HandleNode draggedHandle = null;
    private Vector2F draggingPlateStartMousePosition = null;

    // now this actually contains angles!!!!
    public final Property<Vector2F> motionVectorRight = new Property<Vector2F>( new Vector2F() );
    private HandleNode leftHandle;
    private HandleNode rightHandle;
    private TectonicsTimeControl tectonicsTimeControl;

    // keep track of what nodes correspond to what labels
    public final Map<RangeLabel, RangeLabelNode> rangeLabelMap = new HashMap<RangeLabel, RangeLabelNode>();
    public final Map<TextLabel, TextLabelNode> textLabelMap = new HashMap<TextLabel, TextLabelNode>();

    public PlateMotionTab( LWJGLCanvas canvas ) {
        super( canvas, Strings.PLATE_MOTION_TAB, 0.5f );
    }

    @Override
    public void initialize() {
        super.initialize();

        crustPieceLayer = new GuiNode( this );
        rootNode.addChild( crustPieceLayer );

        // grid centered X, with front Z at 0
        Grid3D grid = new Grid3D(
                Bounds3D.fromMinMax( -700000, 700000,
                                     -300000, 15000,
                                     -1000000, 0 ),
                256, 256, 32 );

        // create the model and terrain
//        model = new AnimatedPlateModel( grid );
        setModel( new PlateMotionModel( getClock(), grid.getBounds() ) );

        // add the main view
        sceneLayer.addChild( new PlateTectonicsView( getModel(), this, showWater ) );

        // add in the handles for manual mode
        leftHandle = new HandleNode( new Property<Vector3F>( new Vector3F( -120, 0, -125 / 2 ) ), this, false ) {{
            motionVectorRight.addObserver( new SimpleObserver() {
                public void update() {
                    updateTransform( -motionVectorRight.get().getX(), -motionVectorRight.get().getY() );
                }
            } );
        }};
        sceneLayer.addChild( leftHandle );
        rightHandle = new HandleNode( new Property<Vector3F>( new Vector3F( 120, 0, -125 / 2 ) ), this, true ) {{
            motionVectorRight.addObserver( new SimpleObserver() {
                public void update() {
                    updateTransform( motionVectorRight.get().getX(), motionVectorRight.get().getY() );
                }
            } );
        }};
        sceneLayer.addChild( rightHandle );

        final GLNode layerLabels = new GLNode() {{
            showLabels.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( showLabels.get() );
                }
            } );
        }};
        sceneLayer.addChild( layerLabels );

        /*---------------------------------------------------------------------------*
         * range labels
         *----------------------------------------------------------------------------*/
        getPlateMotionModel().rangeLabels.addElementAddedObserver( new VoidFunction1<RangeLabel>() {
            public void apply( final RangeLabel rangeLabel ) {
                // when a range label is added, create the view node for it

                // mapped locations
                final Property<Vector3F> topProperty = new Property<Vector3F>( new Vector3F() ) {{
                    beforeFrameRender.addUpdateListener( new UpdateListener() {
                        public void update() {
                            set( convertRadial( rangeLabel.top.get() ) );
                        }
                    }, true );
                }};
                final Property<Vector3F> bottomProperty = new Property<Vector3F>( new Vector3F() ) {{
                    beforeFrameRender.addUpdateListener( new UpdateListener() {
                        public void update() {
                            set( convertRadial( rangeLabel.bottom.get() ) );
                        }
                    }, true );
                }};

                final RangeLabelNode node = rangeLabel.isLimitToScreen() ? new RangeLabelNode(
                        topProperty,
                        bottomProperty,
                        rangeLabel.label,
                        new Property<Float>( 1f ),
                        colorMode, true,
                        getLabelPosition( topProperty, bottomProperty, new Property<Float>( 1f ) )
                ) : new RangeLabelNode(
                        topProperty,
                        bottomProperty,
                        rangeLabel.label,
                        new Property<Float>( 1f ),
                        colorMode, true
                );
                layerLabels.addChild( node );
                rangeLabelMap.put( rangeLabel, node );
            }
        } );

        getPlateMotionModel().rangeLabels.addElementRemovedObserver( new VoidFunction1<RangeLabel>() {
            public void apply( RangeLabel rangeLabel ) {
                layerLabels.removeChild( rangeLabelMap.get( rangeLabel ) );
                rangeLabelMap.remove( rangeLabel );
            }
        } );

        /*---------------------------------------------------------------------------*
        * text labels
        *----------------------------------------------------------------------------*/
        getPlateMotionModel().textLabels.addElementAddedObserver( new VoidFunction1<TextLabel>() {
            public void apply( TextLabel textLabel ) {
                final TextLabelNode textLabelNode = new TextLabelNode( textLabel, getModelViewTransform(), colorMode, new Property<Float>( 1f ) );
                layerLabels.addChild( textLabelNode );
                textLabelMap.put( textLabel, textLabelNode );
            }
        } );

        getPlateMotionModel().textLabels.addElementRemovedObserver( new VoidFunction1<TextLabel>() {
            public void apply( TextLabel textLabel ) {
                layerLabels.removeChild( textLabelMap.get( textLabel ) );
                textLabelMap.remove( textLabel );
            }
        } );

        final Color overHighlightColor = new Color( 1, 1, 0.5f, 0.3f );
        final Color regularHighlightColor = new Color( 0.5f, 0.5f, 0.5f, 0.3f );

        /*---------------------------------------------------------------------------*
         * left highlight box
         *----------------------------------------------------------------------------*/
        final Property<Color> leftHighlightColor = new Property<Color>( regularHighlightColor );
        sceneLayer.addChild( new BoxHighlightNode( getPlateMotionModel().getLeftDropAreaBounds(), getModelViewTransform(),
                                                   leftHighlightColor ) {{
            getPlateMotionModel().leftPlateType.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( getPlateMotionModel().leftPlateType.get() == null );
                }
            } );
        }} );

        /*---------------------------------------------------------------------------*
         * right highlight box
         *----------------------------------------------------------------------------*/
        final Property<Color> rightHighlightColor = new Property<Color>( regularHighlightColor );
        sceneLayer.addChild( new BoxHighlightNode( getPlateMotionModel().getRightDropAreaBounds(), getModelViewTransform(),
                                                   rightHighlightColor ) {{
            getPlateMotionModel().rightPlateType.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( getPlateMotionModel().rightPlateType.get() == null );
                }
            } );
        }} );

        // handle highlighting of whatever side of the earth we are on (when we are about to drop a crust piece)
        mouseEventNotifier.addUpdateListener( new UpdateListener() {
            public void update() {
                if ( draggedCrustPiece == null || isMouseOverCrustChooser() ) {
                    leftHighlightColor.set( regularHighlightColor );
                    rightHighlightColor.set( regularHighlightColor );
                }
                else {
                    boolean overLeft = isMouseOverLeftSide();
                    leftHighlightColor.set( overLeft ? overHighlightColor : regularHighlightColor );
                    rightHighlightColor.set( !overLeft ? overHighlightColor : regularHighlightColor );
                }
            }
        }, false );

        /*---------------------------------------------------------------------------*
         * manual / automatic switch
         *----------------------------------------------------------------------------*/
        final OrthoPiccoloNode modeSwitchPanel = new OrthoPiccoloNode( new ControlPanelNode( new PlayModePanel( isAutoMode ) ), this, getCanvasTransform(), new Property<Vector2D>( new Vector2D( 10, 10 ) ), mouseEventNotifier ) {{
            updateOnEvent( beforeFrameRender );
        }};
        addGuiNode( modeSwitchPanel );

        /*---------------------------------------------------------------------------*
         * crust chooser
         *----------------------------------------------------------------------------*/
        crustChooserPanel = new CrustChooserPanel();
        crustChooserNode = new OrthoPiccoloNode( new ControlPanelNode( crustChooserPanel ), this, getCanvasTransform(),
                                                 new Property<Vector2D>( new Vector2D() ), mouseEventNotifier ) {{
            canvasSize.addObserver( new SimpleObserver() {
                public void update() {
                    position.set( new Vector2D(
                            getStageSize().width - getComponentWidth() - 10, // right side
                            getStageSize().height - getComponentHeight() - 10 ) ); // offset from bottom
                }
            } );
            updateOnEvent( beforeFrameRender );

            // hide piece when we go into the running mode
            getPlateMotionModel().hasBothPlates.addObserver( new ChangeObserver<Boolean>() {
                public void update( Boolean newValue, Boolean oldValue ) {
                    setVisible( !newValue );
                }
            } );
        }};
        addGuiNode( crustChooserNode );

        // continental pieces (2 can be dragged out)
        addCrustPieceGLNode( new CrustPieceGLNode(
                new CrustPieceNode( PlateType.CONTINENTAL, CrustChooserPanel.CRUST_AREA_MAX_HEIGHT, 0.8f ),
                getContinentalOffset() ) );
        addCrustPieceGLNode( new CrustPieceGLNode(
                new CrustPieceNode( PlateType.CONTINENTAL, CrustChooserPanel.CRUST_AREA_MAX_HEIGHT, 0.8f ),
                getContinentalOffset() ) );

        // young oceanic pieces (2 can be dragged out)
        addCrustPieceGLNode( new CrustPieceGLNode(
                new CrustPieceNode( PlateType.YOUNG_OCEANIC, 35, 0.5f ),
                getYoungOceanicOffset() ) );
        addCrustPieceGLNode( new CrustPieceGLNode(
                new CrustPieceNode( PlateType.YOUNG_OCEANIC, 35, 0.5f ),
                getYoungOceanicOffset() ) );

        // old oceanic pieces (2 can be dragged out)
        addCrustPieceGLNode( new CrustPieceGLNode(
                new CrustPieceNode( PlateType.OLD_OCEANIC, 35, 0.4f ),
                getOldOceanicOffset() ) );
        addCrustPieceGLNode( new CrustPieceGLNode(
                new CrustPieceNode( PlateType.OLD_OCEANIC, 35, 0.4f ),
                getOldOceanicOffset() ) );

        /*---------------------------------------------------------------------------*
        * view panel, reset and rewind
        *----------------------------------------------------------------------------*/
        final OrthoPiccoloNode viewPanelNode = new OrthoPiccoloNode(
                new ControlPanelNode( new ViewOptionsPanel( PlateMotionTab.this, showLabels, true, showWater, getPlateMotionModel().hasBothPlates, colorMode ) ),
                this, getCanvasTransform(),
                new Property<Vector2D>( new Vector2D() ), mouseEventNotifier ) {{
            // NOTE: positioning code for this is below
            updateOnEvent( beforeFrameRender );
        }};
        addGuiNode( viewPanelNode );

        final OrthoPiccoloNode resetPanelNode = new OrthoPiccoloNode( new ResetPanel( this, new Runnable() {
            public void run() {
                resetAll();
            }
        } ), this, getCanvasTransform(), new Property<Vector2D>( new Vector2D() ), mouseEventNotifier ) {{
            // NOTE: positioning code for this is below
            updateOnEvent( beforeFrameRender );
        }};
        addGuiNode( resetPanelNode );

        // shared view and reset sizing code, because we need to compute both at the same time
        canvasSize.addObserver( new SimpleObserver() {
            public void update() {
                // lays out the toolbox far left, crust chooser far right, and the view and reset panels as evenly in-between as possible
                final double toolboxRightEdge = toolboxNode.position.get().getX() + toolboxNode.getComponentWidth();
                final double crustChooserLeftEdge = crustChooserNode.position.get().getX();

                final double viewPanelWidth = viewPanelNode.getComponentWidth();
                final double resetPanelWidth = resetPanelNode.getComponentWidth();

                // this amount of "free" width needs to be split into padding between all 4 items (so 3 strips of padding)
                final double freeWidth = crustChooserLeftEdge - toolboxRightEdge - viewPanelWidth - resetPanelWidth;

                final double padding = freeWidth / 3;

                // int casts so that we are aligned on a pixel boundary
                viewPanelNode.position.set( new Vector2D(
                        (int) ( toolboxRightEdge + padding ),
                        getStageSize().height - viewPanelNode.getComponentHeight() - 10 ) );

                resetPanelNode.position.set( new Vector2D(
                        (int) ( toolboxRightEdge + padding + viewPanelWidth + padding ),
                        getStageSize().height - resetPanelNode.getComponentHeight() - 40 ) ); // extra padding
            }
        } );

        /*---------------------------------------------------------------------------*
        * time control
        *----------------------------------------------------------------------------*/
        tectonicsTimeControl = new TectonicsTimeControl( getClock(), isAutoMode );
        final OrthoComponentNode timeControlPanelNode = new OrthoComponentNode( tectonicsTimeControl,
                                                                                this, getCanvasTransform(), new Property<Vector2D>( new Vector2D() ),
                                                                                mouseEventNotifier ) {{
            onResize.addUpdateListener( new UpdateListener() {
                public void update() {
                    position.set( new Vector2D( getStageSize().width - getComponentWidth(),
                                                0 ) );
                }
            }, true );

            updateOnEvent( beforeFrameRender );

            // enable this time control when we can run AND we are in auto mode
            SimpleObserver visibilityObserver = new SimpleObserver() {
                public void update() {
                    setVisible( getPlateMotionModel().hasBothPlates.get() );
                }
            };
            getPlateMotionModel().hasBothPlates.addObserver( visibilityObserver );
        }};
        addGuiNode( timeControlPanelNode );

        /*---------------------------------------------------------------------------*
        * motion direction chooser
        *----------------------------------------------------------------------------*/
        // TODO: refactoring here
        SimpleObserver motionTypeChooserObserver = new SimpleObserver() {
            public void update() {
                boolean leftPlaced = getPlateMotionModel().leftPlateType.get() != null;
                boolean rightPlaced = getPlateMotionModel().rightPlateType.get() != null;
                boolean automode = isAutoMode.get();
                boolean shouldShow = leftPlaced && rightPlaced && automode;

                if ( shouldShow && motionTypeChooserPanel == null ) {
                    // show the chooser panel
                    motionTypeChooserPanel = new OrthoPiccoloNode( new ControlPanelNode( new MotionTypeChooserPanel( getPlateMotionModel() ) ),
                                                                   PlateMotionTab.this, getCanvasTransform(),
                                                                   new Property<Vector2D>( new Vector2D() ), mouseEventNotifier ) {{
                        double left = modeSwitchPanel.position.get().getX() + modeSwitchPanel.getComponentWidth();
                        double right = timeControlPanelNode.position.get().getX();
                        position.set( new Vector2D( ( ( left + right ) - getComponentWidth() ) * 0.5,
                                                    10 ) );
                        updateOnEvent( beforeFrameRender );
                    }};
                    addGuiNode( motionTypeChooserPanel );
                }

                if ( !shouldShow && motionTypeChooserPanel != null ) {
                    // hide the chooser panel
                    // TODO: add a "removeGuiNode" function
                    guiNodes.remove( motionTypeChooserPanel );
                    guiLayer.removeChild( motionTypeChooserPanel );
                    motionTypeChooserPanel = null;
                    // TODO: get rid of superfluous listeners, and do cleanup
                }
            }
        };

        getPlateMotionModel().leftPlateType.addObserver( motionTypeChooserObserver, false );
        getPlateMotionModel().rightPlateType.addObserver( motionTypeChooserObserver, false );
        isAutoMode.addObserver( motionTypeChooserObserver, false );

        /*---------------------------------------------------------------------------*
        * plate drag handling
        *----------------------------------------------------------------------------*/
        timeChangeNotifier.addUpdateListener(
                new UpdateListener() {
                    public void update() {
                        if ( draggingPlate ) {
                            Ray3F ray = getCameraRay( Mouse.getX(), Mouse.getY() );
                            draggedHandle.drag( ray );

                            if ( getPlateMotionModel().motionType.get() != null ) {
                                switch( getPlateMotionModel().motionType.get() ) {
                                    case CONVERGENT:
                                        // comparison works for opposite direction
                                        if ( motionVectorRight.get().x != 0 ) {
                                            manualHandleDragTimeChange( getTimeElapsed() * Math.abs( mapDragMagnitude( motionVectorRight.get().x ) ) );
                                        }
                                        break;
                                    case DIVERGENT:
                                        if ( motionVectorRight.get().x != 0 ) {
                                            manualHandleDragTimeChange( getTimeElapsed() * Math.abs( mapDragMagnitude( motionVectorRight.get().x ) ) );
                                        }
                                        break;
                                    case TRANSFORM:
                                        if ( motionVectorRight.get().y != 0 ) {
                                            manualHandleDragTimeChange( getTimeElapsed() * Math.abs( mapDragMagnitude( motionVectorRight.get().y ) ) );
                                        }
                                        break;
                                }
                            }
                        }
                    }
                }, false );

        guiLayer.addChild( createFPSReadout( Color.BLACK ) );

        showWater.addObserver( new ChangeObserver<Boolean>() {
            public void update( Boolean newValue, Boolean oldValue ) {
                getModel().modelChanged.updateListeners();
            }
        } );
    }

    private void manualHandleDragTimeChange( float timeChange ) {
        if ( !Float.isNaN( timeChange ) ) {
            SimSharingManager.sendUserMessage( UserComponents.handle, UserComponentTypes.sprite, edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions.drag,
                                               new ParameterSet( new Parameter[]{
                                                       new Parameter( ParameterKeys.timeChangeMillionsOfYears, timeChange ),
                                                       new Parameter( ParameterKeys.motionType, getPlateMotionModel().motionType.get().toString() )
                                               } ) );
            getClock().stepByWallSecondsForced( timeChange );
        }
    }

    private float mapDragMagnitude( float mag ) {
        return mag * mag * 2.5f;
    }

    private Vector2F getCrustOffset( Vector2F pieceOffset ) {
        Vector2D nodeOffset = crustChooserNode.position.get();
        return new Vector2F( (float) nodeOffset.getX() + pieceOffset.x,
                             (float) nodeOffset.getY() + pieceOffset.y );
    }

    private Vector2F getContinentalOffset() {
        return getCrustOffset( crustChooserPanel.getContinentalCenter() );
    }

    private Vector2F getYoungOceanicOffset() {
        return getCrustOffset( crustChooserPanel.getYoungOceanicCenter() );
    }

    private Vector2F getOldOceanicOffset() {
        return getCrustOffset( crustChooserPanel.getOldOceanicCenter() );
    }

    public PlateMotionModel getPlateMotionModel() {
        return (PlateMotionModel) getModel();
    }

    @Override
    public void droppedCrustPiece( OrthoPiccoloNode crustPieceNode ) {
        PlateMotionModel model = getPlateMotionModel();
        CrustPieceNode piece = (CrustPieceNode) crustPieceNode.getNode();

        ParameterSet parameters = new ParameterSet( new Parameter( ParameterKeys.plateType, piece.type.toString() ) );

        if ( isMouseOverCrustChooser() ) {
            // user is putting the crust back in the chooser area
            crustPieceNode.position.reset();

            SimSharingManager.sendUserMessage( UserComponents.crustPiece, UserComponentTypes.sprite, UserActions.putBackInCrustPicker, parameters );
        }
        else {
            Side side = isMouseOverLeftSide() ? Side.LEFT : Side.RIGHT;
            parameters = parameters.with( new Parameter( ParameterKeys.side, side.toString() ) );

            if ( !model.hasPlate( side ) ) {
                model.dropCrust( side, piece.type );
                placedPieces.add( crustPieceNode );
                removeCrustPieceGLNode( (CrustPieceGLNode) crustPieceNode );

                SimSharingManager.sendUserMessage( UserComponents.crustPiece, UserComponentTypes.sprite, UserActions.droppedCrustPiece, parameters );
            }
            else {
                // user tried to drop crust on a side that already has crust. just move it back to the picker area
                crustPieceNode.position.reset();

                SimSharingManager.sendUserMessage( UserComponents.crustPiece, UserComponentTypes.sprite, UserActions.attemptedToDropOnExistingCrust, parameters );
            }
        }
    }

    public void addCrustPieceGLNode( CrustPieceGLNode crustPieceGLNode ) {
        crustPieceLayer.addChild( crustPieceGLNode );
        guiNodes.add( 0, crustPieceGLNode );
        crustPieceGLNode.setVisible( true );
    }

    public void removeCrustPieceGLNode( CrustPieceGLNode crustPieceGLNode ) {
        crustPieceLayer.removeChild( crustPieceGLNode );
        guiNodes.remove( crustPieceGLNode );
        crustPieceGLNode.setVisible( false );
    }

    @Override
    public void resetAll() {
        super.resetAll();

        showLabels.reset();
        showWater.reset();
        isAutoMode.reset();
        getClock().pause();
        getClock().resetTimeLimit();
        getClock().resetSimulationTime();
        tectonicsTimeControl.resetAll();

        for ( OrthoPiccoloNode placedPiece : placedPieces ) {
            // add in to the front
            addCrustPieceGLNode( (CrustPieceGLNode) placedPiece );
            placedPiece.position.reset();
        }
    }

    private boolean isMouseOverLeftSide() {
        return getMouseViewPositionOnZPlane().x < 0;
    }

    private boolean isMouseOverCrustChooser() {
        return isGuiUnder( crustChooserNode, Mouse.getEventX(), Mouse.getEventY() );
    }

    @Override
    public ImmutableMatrix4F getSceneModelViewMatrix() {
        ImmutableMatrix4F regularView = super.getSceneModelViewMatrix();

        // calculated this as a debug matrix by camera manipulation, then dumping and inputting here
        return ImmutableMatrix4F.rowMajor(
                0.99994993f, -3.9991664E-4f, -0.009994832f, 0.0f,
                1.9998333E-4f, 0.9998f, -0.019996665f, 0.0f,
                0.010000832f, 0.01999366f, 0.99975f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
        ).times( ImmutableMatrix4F.rowMajor(
                0.99955106f, -5.982099E-4f, 0.029960573f, 0,
                -2.248562E-6f, 0.9997994f, 0.02003758f, 0,
                -0.029966524f, -0.020028654f, 0.99935055f, 0,
                0.0f, 0.0f, 0.0f, 1.0f
        ).times( ImmutableMatrix4F.rowMajor(
                1, 0, 0, 12,
                0, 1, 0, 0,
                0, 0, 1, 78,
                0, 0, 0, 1
        ).times( ImmutableMatrix4F.rowMajor(
                0.9939557f, 7.9941313E-4f, -0.109775305f, -58.643387f,
                -0.0018950196f, 0.9999494f, -0.0098764775f, 0.11180614f,
                0.109761804f, 0.010024817f, 0.9939069f, -6.4759464f,
                0.0f, 0.0f, 0.0f, 1.0f
        ).times( regularView ) ) ) );
    }

    public void rewind() {
        getPlateMotionModel().rewind();
        getClock().pause();
        getClock().resetSimulationTime();
    }

    // 3D GLNode responsible for showing the CrustPiece
    private class CrustPieceGLNode extends OrthoPiccoloNode {
        private CrustPieceGLNode( CrustPieceNode crustPiece, final Vector2F offset ) {
            super( crustPiece, PlateMotionTab.this, getCanvasTransform(),
                   new Property<Vector2D>(
                           new Vector2D(
                                   offset.x - crustPiece.getFullBounds().getWidth() / 2,
                                   offset.y - crustPiece.getFullBounds().getHeight() / 2 ) ),
                   mouseEventNotifier );

            // hide piece when we go into the running mode
            getPlateMotionModel().hasBothPlates.addObserver( new ChangeObserver<Boolean>() {
                public void update( Boolean newValue, Boolean oldValue ) {
                    setVisible( !newValue );
                    setMouseEnabled( !newValue );
                }
            } );
        }
    }

    // do not allow automatic clock progress when in manual mode
    @Override
    public boolean allowClockTickOnFrame() {
        return isAutoMode.get();
    }

    // show the hand cursor instead of the "default" when over the play area (AND plates exist)
    @Override
    protected void uncaughtCursor() {
        // TODO: use a closed-hand grab cursor instead if possible?
        final Ray3F ray = getCameraRay( Mouse.getEventX(), Mouse.getEventY() );
        final boolean hitsRightHandle = isOverRightHandle( ray );
        final boolean hitsLeftHandle = isOverLeftHandle( ray );
        final Boolean showHand = ( getPlateMotionModel().hasBothPlates.get() && ( hitsRightHandle || hitsLeftHandle ) ) || draggingPlate;
        getCanvas().setCursor( Cursor.getPredefinedCursor( showHand ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR ) );
    }

    private boolean isOverLeftHandle( Ray3F ray ) {
        return leftHandle.isVisible() && leftHandle.intersectRay( ray );
    }

    private boolean isOverRightHandle( Ray3F ray ) {
        return rightHandle.isVisible() && rightHandle.intersectRay( ray );
    }

    @Override
    protected void uncaughtMouseButton() {
        if ( !isAutoMode.get() ) {
            final Ray3F ray = getCameraRay( Mouse.getEventX(), Mouse.getEventY() );
            final boolean overLeft = isOverLeftHandle( ray );
            final boolean overRight = isOverRightHandle( ray );
            if ( Mouse.getEventButtonState() && ( overLeft || overRight ) ) {
                // mouse down
                draggingPlate = true;
                draggingPlateStartMousePosition = getMouseViewPositionOnZPlane();
                if ( overLeft ) {
                    leftHandle.startDrag( ray );
                    draggedHandle = leftHandle;
                }
                else {
                    rightHandle.startDrag( ray );
                    draggedHandle = rightHandle;
                }
            }
            else {
                if ( draggedHandle != null ) {
                    draggedHandle.endDrag();
                    draggedHandle = null;
                }
                draggingPlate = false;
                motionVectorRight.reset();
            }
        }
    }

    private Vector3F convertRadial( Vector3F v ) {
        return getModelViewTransform().transformPosition( PlateTectonicsModel.convertToRadial( v ) );
    }

    public IUserComponent getUserComponent() {
        return PlateTectonicsSimSharing.UserComponents.plateMotionTab;
    }

    @Override
    public boolean isWaterVisible() {
        return showWater.get();
    }
}
