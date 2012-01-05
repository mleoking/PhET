// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.modules;

import java.awt.Color;
import java.awt.Cursor;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.lwjglphet.LWJGLCanvas;
import edu.colorado.phet.lwjglphet.math.ImmutableMatrix4F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.lwjglphet.nodes.GuiNode;
import edu.colorado.phet.lwjglphet.nodes.OrthoComponentNode;
import edu.colorado.phet.lwjglphet.nodes.OrthoPiccoloNode;
import edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;
import edu.colorado.phet.platetectonics.control.CrustChooserPanel;
import edu.colorado.phet.platetectonics.control.CrustPieceNode;
import edu.colorado.phet.platetectonics.control.MotionTypeChooserPanel;
import edu.colorado.phet.platetectonics.control.OptionsPanel;
import edu.colorado.phet.platetectonics.control.PlayModePanel;
import edu.colorado.phet.platetectonics.control.TectonicsTimeControl;
import edu.colorado.phet.platetectonics.model.PlateMotionModel;
import edu.colorado.phet.platetectonics.model.PlateMotionModel.MotionType;
import edu.colorado.phet.platetectonics.model.PlateMotionModel.PlateType;
import edu.colorado.phet.platetectonics.util.Bounds3D;
import edu.colorado.phet.platetectonics.util.Grid3D;
import edu.colorado.phet.platetectonics.view.BoxHighlightNode;
import edu.colorado.phet.platetectonics.view.HandleNode;
import edu.colorado.phet.platetectonics.view.PlateView;

/**
 * Displays two main plates that the user can direct to move towards, away from, or along each other.
 */
public class PlateMotionTab extends PlateTectonicsTab {

    public final Property<Boolean> isAutoMode = new Property<Boolean>( false );
    private CrustChooserPanel crustChooserPanel;
    private OrthoPiccoloNode crustChooserNode;
    private GuiNode crustPieceLayer;

    private final Property<Boolean> showLabels = new Property<Boolean>( false );
    private final Property<Boolean> showWater = new Property<Boolean>( false );

    private final List<OrthoPiccoloNode> placedPieces = new ArrayList<OrthoPiccoloNode>();

    private OrthoPiccoloNode motionTypeChooserPanel = null;

    private boolean draggingPlate = false;
    private ImmutableVector2F draggingPlateStartMousePosition = null;

    public PlateMotionTab( LWJGLCanvas canvas ) {
        super( canvas, Strings.PLATE_MOTION_TAB, 0.5f );
    }

    @Override public void initialize() {
        super.initialize();

        crustPieceLayer = new GuiNode( this );
        rootNode.addChild( crustPieceLayer );

        // grid centered X, with front Z at 0
        Grid3D grid = new Grid3D(
                Bounds3D.fromMinMax( -700000, 700000,
                                     -400000, 15000,
                                     -250000, 0 ),
                256, 256, 32 );

        // create the model and terrain
//        model = new AnimatedPlateModel( grid );
        setModel( new PlateMotionModel( grid.getBounds() ) );

        guiLayer.addChild( createFPSReadout( Color.BLACK ) );

        sceneLayer.addChild( new PlateView( getModel(), this, showWater ) );
        sceneLayer.addChild( new HandleNode( new Property<ImmutableVector3F>( new ImmutableVector3F( -150, 15, -125 / 2 ) ), this ) );
        sceneLayer.addChild( new HandleNode( new Property<ImmutableVector3F>( new ImmutableVector3F( 150, 15, -125 / 2 ) ), this ) );

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
        final OrthoPiccoloNode modeSwitchPanel = new OrthoPiccoloNode( new ControlPanelNode( new PlayModePanel( isAutoMode, getPlateMotionModel().animationStarted ) ), this, getCanvasTransform(), new Property<ImmutableVector2D>( new ImmutableVector2D( 10, 10 ) ), mouseEventNotifier ) {{
            updateOnEvent( beforeFrameRender );
        }};
        addGuiNode( modeSwitchPanel );

        /*---------------------------------------------------------------------------*
         * crust chooser
         *----------------------------------------------------------------------------*/
        crustChooserPanel = new CrustChooserPanel();
        crustChooserNode = new OrthoPiccoloNode( new ControlPanelNode( crustChooserPanel ), this, getCanvasTransform(),
                                                 new Property<ImmutableVector2D>( new ImmutableVector2D() ), mouseEventNotifier ) {{
            canvasSize.addObserver( new SimpleObserver() {
                public void update() {
                    position.set( new ImmutableVector2D(
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

        // young oceanic piece
        addCrustPieceGLNode( new CrustPieceGLNode(
                new CrustPieceNode( PlateType.YOUNG_OCEANIC, 35, 0.5f ),
                getYoungOceanicOffset() ) );

        // old oceanic piece
        addCrustPieceGLNode( new CrustPieceGLNode(
                new CrustPieceNode( PlateType.OLD_OCEANIC, 35, 0.4f ),
                getOldOceanicOffset() ) );

        /*---------------------------------------------------------------------------*
        * options panel
        *----------------------------------------------------------------------------*/
        addGuiNode( new OrthoPiccoloNode(
                new ControlPanelNode( new OptionsPanel( showLabels, true, showWater, getPlateMotionModel().hasBothPlates, new Runnable() {
                    public void run() {
                        resetAll();
                    }
                } ) ),
                this, getCanvasTransform(),
                new Property<ImmutableVector2D>( new ImmutableVector2D() ), mouseEventNotifier ) {{
            canvasSize.addObserver( new SimpleObserver() {
                public void update() {
                    int center = (int) ( ( toolbox.position.get().getX() + toolbox.getComponentWidth() )
                                         + ( crustChooserNode.position.get().getX() ) ) / 2;
                    position.set( new ImmutableVector2D( center - getComponentWidth() / 2,
                                                         getStageSize().height - getComponentHeight() - 10 ) );
                }
            } );
            updateOnEvent( beforeFrameRender );
        }} );

        /*---------------------------------------------------------------------------*
         * time control
         *----------------------------------------------------------------------------*/
        final OrthoComponentNode timeControlPanelNode = new OrthoComponentNode( new TectonicsTimeControl( getClock() ),
                                                                                this, getCanvasTransform(), new Property<ImmutableVector2D>( new ImmutableVector2D() ),
                                                                                mouseEventNotifier ) {{
            position.set( new ImmutableVector2D( getStageSize().width - getComponentWidth(),
                                                 0 ) );
            updateOnEvent( beforeFrameRender );

            // enable this time control when we can run AND we are in auto mode
            SimpleObserver visibilityObserver = new SimpleObserver() {
                public void update() {
                    setVisible( getPlateMotionModel().canRun.get() && isAutoMode.get() );
                }
            };
            getPlateMotionModel().canRun.addObserver( visibilityObserver );
            isAutoMode.addObserver( visibilityObserver );
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
                                                                   new Property<ImmutableVector2D>( new ImmutableVector2D() ), mouseEventNotifier ) {{
                        double left = modeSwitchPanel.position.get().getX() + modeSwitchPanel.getComponentWidth();
                        double right = timeControlPanelNode.position.get().getX();
                        position.set( new ImmutableVector2D( ( ( left + right ) - getComponentWidth() ) * 0.5,
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
                            ImmutableVector2F currentPosition = getMousePositionOnZPlane();
                            ImmutableVector2F deltaXY = currentPosition.minus( draggingPlateStartMousePosition );
                            MotionType startingMotionType = getPlateMotionModel().motionType.get();

                            if ( startingMotionType == null ) {
                                // TODO: handle starting of motion type
                            }
                            else {
                                boolean draggingRightPlate = draggingPlateStartMousePosition.x > 0;
                                boolean pullingLeft = deltaXY.x < 0;
                                switch( startingMotionType ) {
                                    case CONVERGENT:
                                        // comparison works for opposite direction
                                        if ( draggingRightPlate == pullingLeft ) {
                                            getClock().stepByWallSecondsForced( getTimeElapsed() * Math.abs( deltaXY.x ) / 10 );
                                        }
                                        break;
                                    case DIVERGENT:
                                        if ( draggingRightPlate != pullingLeft ) {
                                            getClock().stepByWallSecondsForced( getTimeElapsed() * Math.abs( deltaXY.x ) / 10 );
                                        }
                                        break;
                                    case TRANSFORM:
                                        break;
                                }
                            }
                        }
                    }
                }, false );
    }

    private ImmutableVector2F getCrustOffset( ImmutableVector2F pieceOffset ) {
        ImmutableVector2D nodeOffset = crustChooserNode.position.get();
        return new ImmutableVector2F( (float) nodeOffset.getX() + pieceOffset.x,
                                      (float) nodeOffset.getY() + pieceOffset.y );
    }

    private ImmutableVector2F getContinentalOffset() {
        return getCrustOffset( crustChooserPanel.getContinentalCenter() );
    }

    private ImmutableVector2F getYoungOceanicOffset() {
        return getCrustOffset( crustChooserPanel.getYoungOceanicCenter() );
    }

    private ImmutableVector2F getOldOceanicOffset() {
        return getCrustOffset( crustChooserPanel.getOldOceanicCenter() );
    }

    public PlateMotionModel getPlateMotionModel() {
        return (PlateMotionModel) getModel();
    }

    @Override public void droppedCrustPiece( OrthoPiccoloNode crustPieceNode ) {
        PlateMotionModel model = getPlateMotionModel();
        CrustPieceNode piece = (CrustPieceNode) crustPieceNode.getNode();

        boolean droppedBackInPanel = isMouseOverCrustChooser();

        if ( droppedBackInPanel ) {
            crustPieceNode.position.reset();
        }
        else {
            if ( isMouseOverLeftSide() ) {
                if ( !model.hasLeftPlate() ) {
                    model.dropLeftCrust( piece.type );
                    crustPieceNode.getParent().removeChild( crustPieceNode );
                    placedPieces.add( crustPieceNode );
                    guiNodes.remove( crustPieceNode );
                }
                else {
                    crustPieceNode.position.reset();
                }
            }
            else {
                if ( !model.hasRightPlate() ) {
                    model.dropRightCrust( piece.type );
                    crustPieceNode.getParent().removeChild( crustPieceNode );
                    placedPieces.add( crustPieceNode );
                    guiNodes.remove( crustPieceNode );
                }
                else {
                    crustPieceNode.position.reset();
                }
            }
        }
    }

    public void addCrustPieceGLNode( CrustPieceGLNode crustPieceGLNode ) {
        crustPieceLayer.addChild( crustPieceGLNode );
        guiNodes.add( 0, crustPieceGLNode );
    }

    @Override public void resetAll() {
        super.resetAll();

        // TODO: this is probably buggy?
        showLabels.reset();
        showWater.reset();
        isAutoMode.reset();
        getClock().pause();
        getClock().resetSimulationTime();

        for ( OrthoPiccoloNode placedPiece : placedPieces ) {
            // add in to the front
            guiNodes.add( 0, placedPiece );
            placedPiece.position.reset();
            crustPieceLayer.addChild( placedPiece );
        }
    }

    private boolean isMouseOverLeftSide() {
        return getMousePositionOnZPlane().x < 0;
    }

    private boolean isMouseOverCrustChooser() {
        return isGuiUnder( crustChooserNode, Mouse.getEventX(), Mouse.getEventY() );
    }

    @Override public ImmutableMatrix4F getSceneModelViewMatrix() {
        ImmutableMatrix4F regularView = super.getSceneModelViewMatrix();

        // calculated this as a debug matrix by camera manipulation, then dumping and inputting here
        return ImmutableMatrix4F.rowMajor( 0.9939557f, 7.9941313E-4f, -0.109775305f, -58.643387f,
                                           -0.0018950196f, 0.9999494f, -0.0098764775f, 0.11180614f,
                                           0.109761804f, 0.010024817f, 0.9939069f, -6.4759464f,
                                           0.0f, 0.0f, 0.0f, 1.0f ).times( regularView );
    }

    // 3D GLNode responsible for showing the CrustPiece
    private class CrustPieceGLNode extends OrthoPiccoloNode {
        private CrustPieceGLNode( CrustPieceNode crustPiece, final ImmutableVector2F offset ) {
            super( crustPiece, PlateMotionTab.this, getCanvasTransform(),
                   new Property<ImmutableVector2D>(
                           new ImmutableVector2D(
                                   offset.x - crustPiece.getFullBounds().getWidth() / 2,
                                   offset.y - crustPiece.getFullBounds().getHeight() / 2 ) ),
                   mouseEventNotifier );

            // hide piece when we go into the running mode
            getPlateMotionModel().hasBothPlates.addObserver( new ChangeObserver<Boolean>() {
                public void update( Boolean newValue, Boolean oldValue ) {
                    setVisible( !newValue );
                }
            } );
        }
    }

    // do not allow automatic clock progress when in manual mode
    @Override public boolean allowClockTickOnFrame() {
        return isAutoMode.get();
    }

    // show the hand cursor instead of the "default" when over the play area (AND plates exist)
    @Override protected void uncaughtCursor() {
        // TODO: use a closed-hand grab cursor instead?
        getCanvas().setCursor( Cursor.getPredefinedCursor( getPlateMotionModel().hasBothPlates.get() ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR ) );
    }

    @Override protected void uncaughtMouseButton() {
        if ( !isAutoMode.get() ) {
            draggingPlate = Mouse.getEventButtonState();
            draggingPlateStartMousePosition = getMousePositionOnZPlane();
        }
    }
}
