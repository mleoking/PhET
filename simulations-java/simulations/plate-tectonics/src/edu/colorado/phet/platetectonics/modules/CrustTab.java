// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.modules;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.lwjglphet.GLNode;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.LWJGLCanvas;
import edu.colorado.phet.lwjglphet.OrthoPiccoloNode;
import edu.colorado.phet.lwjglphet.math.ImmutableMatrix4F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.nodes.GuiNode;
import edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;
import edu.colorado.phet.platetectonics.control.DensitySensorNode3D;
import edu.colorado.phet.platetectonics.control.MyCrustPanel;
import edu.colorado.phet.platetectonics.control.RulerNode3D;
import edu.colorado.phet.platetectonics.control.ThermometerNode3D;
import edu.colorado.phet.platetectonics.control.ToolDragHandler;
import edu.colorado.phet.platetectonics.control.Toolbox;
import edu.colorado.phet.platetectonics.model.CrustModel;
import edu.colorado.phet.platetectonics.model.ToolboxState;
import edu.colorado.phet.platetectonics.util.Bounds3D;
import edu.colorado.phet.platetectonics.util.Grid3D;
import edu.colorado.phet.platetectonics.view.PlateView;
import edu.umd.cs.piccolo.nodes.PText;

import static edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings.CONTINENTAL_CRUST;
import static edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings.OCEANIC_CRUST;
import static org.lwjgl.opengl.GL11.*;

/**
 * Represents the 1st tab, which has a modifiable section of crust surrounded by oceanic and continental crusts, all
 * on top of the mantle.
 */
public class CrustTab extends PlateTectonicsTab {

    private CrustModel model;

    private ToolboxState toolboxState = new ToolboxState();
    private ToolDragHandler toolDragHandler = new ToolDragHandler( toolboxState );
    private Toolbox toolbox;
    private static final float RULER_Z = 0;
    private static final float THERMOMETER_Z = 1;
    private static final float DENSITY_SENSOR_Z = 2;

    public CrustTab( LWJGLCanvas canvas ) {
        super( canvas, Strings.CRUST_TAB, 2 ); // 0.5 km => 1 distance in view
    }

    @Override public void start() {
        super.start();

        /*---------------------------------------------------------------------------*
        * mouse-button presses
        *----------------------------------------------------------------------------*/
//        getInputHandler().addListener(
//                new ActionListener() {
//                    public void onAction( String name, boolean isMouseDown, float tpf ) {
//                        // on left mouse button change
//                        if ( name.equals( MAP_LMB ) ) {
//                            final HUDNodeCollision toolCollision = HUDNode.getHUDCollisionUnderPoint( toolView, getInputHandler().getCursorPosition() );
//                            final HUDNodeCollision guiCollision = HUDNode.getHUDCollisionUnderPoint( guiView, getInputHandler().getCursorPosition() );
//
//                            if ( isMouseDown ) {
//                                if ( toolCollision != null ) {
//                                    Node parentNode = toolCollision.hudNode.getParent();
//
//                                    if ( parentNode instanceof DraggableTool2D ) {
//                                        toolDragHandler.mouseDownOnTool( (DraggableTool2D) parentNode, getMousePositionOnZPlane() );
//                                    }
//                                }
//                            }
//                            else {
//                                boolean isMouseOverToolbox = guiCollision != null && guiCollision.hudNode.getParent() == toolbox;
//                                toolDragHandler.mouseUp( isMouseOverToolbox );
//                            }
//                        }
//                    }
//                }, MAP_LMB );

        /*---------------------------------------------------------------------------*
        * mouse motion
        *----------------------------------------------------------------------------*/
//        getInputHandler().addListener(
//                new AnalogListener() {
//                    public void onAnalog( final String name, final float value, float tpf ) {
//                        //By always updating the cursor at every mouse move, we can be sure it is always correct.
//                        //Whenever there is a mouse move event, make sure the cursor is in the right state.
//                        updateCursor();
//
//                        toolDragHandler.mouseMove( getMousePositionOnZPlane() );
//                    }
//                }, MAP_LEFT, MAP_RIGHT, MAP_UP, MAP_DOWN );

        // grid centered X, with front Z at 0
        Grid3D grid = new Grid3D(
                Bounds3D.fromMinMax( -1500000, 1500000,
                                     -150000, 15000,
                                     -2000000, 0 ),
                512, 512, 64 );

        // create the model and terrain
        model = new CrustModel( grid );

        // layers
        final GLNode sceneLayer = new GLNode() {
            @Override protected void preRender( GLOptions options ) {
                loadCameraMatrices();
                glEnable( GL_DEPTH_TEST );
            }

            @Override protected void postRender( GLOptions options ) {
                glDisable( GL_DEPTH_TEST );
            }
        };
        final GLNode guiLayer = new GuiNode( this );
        final GLNode toolLayer = new GLNode() {
            @Override protected void preRender( GLOptions options ) {
                loadCameraMatrices();
            }
        };
        rootNode.addChild( sceneLayer );
        rootNode.addChild( guiLayer );
        rootNode.addChild( toolLayer );

        // TODO: improve the plate view
        sceneLayer.addChild( new PlateView( model, this, grid ) );

        /*---------------------------------------------------------------------------*
        * toolbox
        *----------------------------------------------------------------------------*/
        toolbox = new Toolbox( this, toolboxState ) {{
            // layout the panel if its size changes (and on startup)
            canvasSize.addObserver( new SimpleObserver() {
                public void update() {
                    position.set( new ImmutableVector2D(
                            10, // left side
                            getStageSize().height - getComponentHeight() - 10 ) ); // offset from bottom
                }
            } );
            updateOnEvent( beforeFrameRender );
        }};
        guiLayer.addChild( toolbox );


        //TODO: factor out duplicated code in tools
        toolboxState.rulerInToolbox.addObserver( new SimpleObserver() {
            public void update() {
                if ( !toolboxState.rulerInToolbox.get() ) {

                    // we just "removed" the ruler from the toolbox, so add it to our scene
                    RulerNode3D ruler = new RulerNode3D( getModelViewTransform(), CrustTab.this );
                    toolLayer.addChild( ruler );

                    // offset the ruler slightly from the mouse, and start the drag
                    ImmutableVector2F mousePosition = getMousePositionOnZPlane();
                    ImmutableVector2F initialMouseOffset = ruler.getInitialMouseOffset();
                    ruler.setTransform( ImmutableMatrix4F.translation( mousePosition.x - initialMouseOffset.x,
                                                                       mousePosition.y - initialMouseOffset.y,
                                                                       RULER_Z ) ); // on Z=0 plane
                    toolDragHandler.startDragging( ruler, mousePosition );
                }
            }
        } );

        toolboxState.thermometerInToolbox.addObserver( new SimpleObserver() {
            public void update() {
                if ( !toolboxState.thermometerInToolbox.get() ) {

                    // we just "removed" the ruler from the toolbox, so add it to our scene
                    ThermometerNode3D thermometer = new ThermometerNode3D( getModelViewTransform(), CrustTab.this, model );
                    toolLayer.addChild( thermometer );

                    // offset the ruler slightly from the mouse, and start the drag
                    ImmutableVector2F mousePosition = getMousePositionOnZPlane();
                    ImmutableVector2F initialMouseOffset = thermometer.getInitialMouseOffset();
                    thermometer.setTransform( ImmutableMatrix4F.translation( mousePosition.x - initialMouseOffset.x,
                                                                             mousePosition.y - initialMouseOffset.y,
                                                                             THERMOMETER_Z ) ); // on Z=0 plane

                    toolDragHandler.startDragging( thermometer, mousePosition );
                }
            }
        } );

        toolboxState.densitySensorInToolbox.addObserver( new SimpleObserver() {
            public void update() {
                if ( !toolboxState.densitySensorInToolbox.get() ) {

                    // we just "removed" the ruler from the toolbox, so add it to our scene
                    DensitySensorNode3D sensorNode = new DensitySensorNode3D( getModelViewTransform(), CrustTab.this, model );
                    toolLayer.addChild( sensorNode );

                    // offset the ruler slightly from the mouse, and start the drag
                    ImmutableVector2F mousePosition = getMousePositionOnZPlane();
                    ImmutableVector2F initialMouseOffset = sensorNode.getInitialMouseOffset();
                    sensorNode.setTransform( ImmutableMatrix4F.translation( mousePosition.x - initialMouseOffset.x,
                                                                            mousePosition.y - initialMouseOffset.y,
                                                                            DENSITY_SENSOR_Z ) );

                    toolDragHandler.startDragging( sensorNode, mousePosition );
                }
            }
        } );

        /*---------------------------------------------------------------------------*
        * my crust
        *----------------------------------------------------------------------------*/
        guiLayer.addChild( new OrthoPiccoloNode( new ControlPanelNode( new MyCrustPanel( model ) ), this, getCanvasTransform(), new Property<ImmutableVector2D>( new ImmutableVector2D() ), mouseEventNotifier ) {{
            // layout the panel if its size changes (and on startup)
            canvasSize.addObserver( new SimpleObserver() {
                public void update() {
                    position.set( new ImmutableVector2D(
                            Math.ceil( ( getStageSize().width - getComponentWidth() ) / 2 ), // center horizontally
                            10 ) ); // offset from top
                }
            } );

            updateOnEvent( beforeFrameRender );
        }} );

        /*---------------------------------------------------------------------------*
        * labels
        *----------------------------------------------------------------------------*/

        // "oceanic crust" label
        guiLayer.addChild(
                new OrthoPiccoloNode( new PText( OCEANIC_CRUST ) {{
                    setFont( new PhetFont( 16, true ) );
                }},
                                      this,
                                      getCanvasTransform(),
                                      new Property<ImmutableVector2D>( new ImmutableVector2D( 30, getStageSize().getHeight() * 0.4 ) ),
                                      mouseEventNotifier ) );

        // "continental crust" label
        guiLayer.addChild( new OrthoPiccoloNode( new PText( CONTINENTAL_CRUST ) {{
            setFont( new PhetFont( 16, true ) );
        }}, this, getCanvasTransform(), new Property<ImmutableVector2D>( new ImmutableVector2D() ), mouseEventNotifier ) {{
            // TODO: improve positioning to handle i18n?
            position.set( new ImmutableVector2D( getStageSize().getWidth() - getComponentWidth() - 30,
                                                 getStageSize().getHeight() * 0.4 ) );
        }} );

        // earth mesh
//        final float viewRadius = getModelViewTransform().modelToViewDeltaY( PlateModel.EARTH_RADIUS );
//        mainView.getScene().attachChild( new Geometry( "Earth Mesh", new Sphere( 50, 50, viewRadius ) ) {{
//            setMaterial( new Material( getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md" ) {{
//                setColor( "Color", new ColorRGBA( 0.5f, 0.5f, 0.5f, 0.5f ) );
//                getAdditionalRenderState().setWireframe( true );
//                getAdditionalRenderState().setFaceCullMode( FaceCullMode.Off );
//
//                // allow transparency
//                getAdditionalRenderState().setBlendMode( BlendMode.Alpha );
//                setTransparent( true );
//            }} );
//
//            setLocalTranslation( 0, -viewRadius, 0 );
//            setQueueBucket( Bucket.Transparent );
//        }} );
    }

    @Override public void loop() {
        super.loop();

        model.update( getTimeElapsed() );
    }

    public void updateCursor() {
        // TODO: cursor handling (after picking tests)
//        JmeCanvasContext context = (JmeCanvasContext) getApp().getContext();
//        final Canvas canvas = context.getCanvas();
//
//        // TODO: refactor picking to work with multiple views?
//        final HUDNodeCollision toolCollision = HUDNode.getHUDCollisionUnderPoint( toolView, getInputHandler().getCursorPosition() );
//        final HUDNodeCollision guiCollision = HUDNode.getHUDCollisionUnderPoint( guiView, getInputHandler().getCursorPosition() );
//        final HUDNodeCollision mainCollision = HUDNode.getHUDCollisionUnderPoint( mainView, getInputHandler().getCursorPosition() );
//        SwingUtilities.invokeLater( new Runnable() {
//            public void run() {
//                Component toolComponent = toolCollision == null ? null : toolCollision.hudNode.getRootComponent().getComponent( 0 );
//                if ( toolComponent != null ) {
//                    canvas.setCursor( toolComponent.getCursor() );
//                }
//                else {
//                    Component guiComponent = guiCollision == null ? null : guiCollision.getGuiPlaneComponent();
//                    if ( guiComponent != null ) {
//                        // over a HUD node, so set the cursor to what the component would want
//                        canvas.setCursor( guiComponent.getCursor() );
//                    }
//                    else {
//                        // check if we are picking a piccolo canvas in the main view
//                        if ( mainCollision != null && mainCollision.hudNode.getRootComponent().getComponent( 0 ) instanceof PCanvas ) {
//                            // use the canvas's cursor
//                            canvas.setCursor( mainCollision.hudNode.getRootComponent().getComponent( 0 ).getCursor() );
//                        }
//                        else {
//                            // default to the default cursor
//                            canvas.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
//                        }
//                    }
//                }
//            }
//        } );
    }

    private ImmutableVector2F getMousePositionOnZPlane() {
        // TODO: fix this mouse positioning code!
        return new ImmutableVector2F();
//        return LWJGLUtils.intersectZPlaneWithRay( mainView.getCameraRayUnderCursor( getInputHandler() ) );
    }

}
