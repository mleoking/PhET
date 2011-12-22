// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.modules;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingUtilities;

import org.lwjgl.input.Mouse;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.slider.VSliderNode;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.LWJGLCanvas;
import edu.colorado.phet.lwjglphet.math.ImmutableMatrix4F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.Ray3F;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.lwjglphet.nodes.GuiNode;
import edu.colorado.phet.lwjglphet.nodes.OrthoComponentNode;
import edu.colorado.phet.lwjglphet.nodes.OrthoPiccoloNode;
import edu.colorado.phet.lwjglphet.nodes.PlanarComponentNode;
import edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;
import edu.colorado.phet.platetectonics.control.DensitySensorNode3D;
import edu.colorado.phet.platetectonics.control.DraggableTool2D;
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
import edu.umd.cs.piccolo.PNode;
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

    private final List<OrthoComponentNode> guiNodes = new ArrayList<OrthoComponentNode>();
    private GLNode sceneLayer;
    private GLNode guiLayer;
    private GLNode toolLayer;

    public CrustTab( LWJGLCanvas canvas ) {
        super( canvas, Strings.CRUST_TAB, 2 ); // 0.5 km => 1 distance in view
    }

    @Override public void initialize() {
        super.initialize();

        /*---------------------------------------------------------------------------*
        * mouse-button presses
        *----------------------------------------------------------------------------*/
        mouseEventNotifier.addUpdateListener(
                new UpdateListener() {
                    public void update() {
                        // on left mouse button change
                        if ( Mouse.getEventButton() == 0 ) {
                            PlanarComponentNode toolCollision = getToolUnder( Mouse.getEventX(), Mouse.getEventY() );
                            OrthoComponentNode guiCollision = getGuiUnder( Mouse.getEventX(), Mouse.getEventY() );

                            // if mouse is down
                            if ( Mouse.getEventButtonState() ) {
                                if ( toolCollision != null ) {
                                    toolDragHandler.mouseDownOnTool( (DraggableTool2D) toolCollision, getMousePositionOnZPlane() );
                                }
                            }
                            else {
                                boolean isMouseOverToolbox = guiCollision != null && guiCollision == toolbox;
                                toolDragHandler.mouseUp( isMouseOverToolbox );
                                // TODO: remove the "removed" tool from the guiNodes list
                            }
                        }
                    }
                }, false );

        /*---------------------------------------------------------------------------*
        * mouse motion
        *----------------------------------------------------------------------------*/
        mouseEventNotifier.addUpdateListener(
                new UpdateListener() {
                    public void update() {
                        updateCursor();

                        if ( Mouse.getEventButton() == -1 ) {
                            // ok, not a button press event
                            toolDragHandler.mouseMove( getMousePositionOnZPlane() );
                        }
                    }

                }, false );

        // grid centered X, with front Z at 0
        Grid3D grid = new Grid3D(
                Bounds3D.fromMinMax( -1500000, 1500000,
                                     -150000, 15000,
                                     -2000000, 0 ),
                512, 512, 64 );

        // create the model and terrain
        model = new CrustModel( grid );

        // layers
        sceneLayer = new GLNode() {
            @Override protected void preRender( GLOptions options ) {
                loadCameraMatrices();
                loadLighting();
                glEnable( GL_DEPTH_TEST );
            }

            @Override protected void postRender( GLOptions options ) {
                glDisable( GL_DEPTH_TEST );
            }
        };
        guiLayer = new GuiNode( this );
        toolLayer = new GLNode() {
            @Override protected void preRender( GLOptions options ) {
                loadCameraMatrices();
            }
        };
        rootNode.addChild( sceneLayer );
        rootNode.addChild( guiLayer );
        rootNode.addChild( toolLayer );

        guiLayer.addChild( createFPSReadout( Color.BLACK ) );

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
        guiNodes.add( toolbox );


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
                    ruler.transform.prepend( ImmutableMatrix4F.translation( mousePosition.x - initialMouseOffset.x,
                                                                            mousePosition.y - initialMouseOffset.y,
                                                                            RULER_Z ) );
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
                    thermometer.transform.prepend( ImmutableMatrix4F.translation( mousePosition.x - initialMouseOffset.x,
                                                                                  mousePosition.y - initialMouseOffset.y,
                                                                                  THERMOMETER_Z ) );

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
                    sensorNode.transform.prepend( ImmutableMatrix4F.translation( mousePosition.x - initialMouseOffset.x,
                                                                                 mousePosition.y - initialMouseOffset.y,
                                                                                 DENSITY_SENSOR_Z ) );

                    toolDragHandler.startDragging( sensorNode, mousePosition );
                }
            }
        } );

        /*---------------------------------------------------------------------------*
        * my crust
        *----------------------------------------------------------------------------*/
        OrthoPiccoloNode myCrustNode = new OrthoPiccoloNode( new ControlPanelNode( new MyCrustPanel( model ) ), this, getCanvasTransform(), new Property<ImmutableVector2D>( new ImmutableVector2D() ), mouseEventNotifier ) {{
            // layout the panel if its size changes (and on startup)
            canvasSize.addObserver( new SimpleObserver() {
                public void update() {
                    position.set( new ImmutableVector2D(
                            Math.ceil( ( getStageSize().width - getComponentWidth() ) / 2 ), // center horizontally
                            10 ) ); // offset from top
                }
            } );

            updateOnEvent( beforeFrameRender );

            zoomRatio.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( zoomRatio.get() == 1 );
                }
            } );
        }};
        guiLayer.addChild( myCrustNode );
        guiNodes.add( myCrustNode );

        /*---------------------------------------------------------------------------*
        * temporary zoom control
        *----------------------------------------------------------------------------*/
        OrthoPiccoloNode zoomControl = new OrthoPiccoloNode( new ControlPanelNode( new PNode() {{
            final PText zoomText = new PText( "Zoom" );
            VSliderNode slider = new VSliderNode( 0, 1, zoomRatio, new Property<Boolean>( true ), 100 ) {{
                setOffset( ( zoomText.getFullBounds().getWidth() - getFullBounds().getWidth() ) / 2,
                           zoomText.getFullBounds().getMaxY() + 10
                );
            }};
            addChild( zoomText );
            addChild( slider );
        }} ), this, getCanvasTransform(), new Property<ImmutableVector2D>( new ImmutableVector2D() ), mouseEventNotifier ) {{
            canvasSize.addObserver( new SimpleObserver() {
                public void update() {
                    position.set( new ImmutableVector2D(
                            getStageSize().width - getComponentWidth() - 10,
                            10
                    ) );
                }
            } );

            updateOnEvent( beforeFrameRender );
        }};
        guiLayer.addChild( zoomControl );
        guiNodes.add( zoomControl );

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
                                      new Property<ImmutableVector2D>( new ImmutableVector2D( 30, getStageSize().getHeight() * 0.38 ) ),
                                      mouseEventNotifier ) {{
                    zoomRatio.addObserver( new SimpleObserver() {
                        public void update() {
                            setVisible( zoomRatio.get() == 1 );
                        }
                    } );
                }} );

        // "continental crust" label
        guiLayer.addChild( new OrthoPiccoloNode( new PText( CONTINENTAL_CRUST ) {{
            setFont( new PhetFont( 16, true ) );
        }}, this, getCanvasTransform(), new Property<ImmutableVector2D>( new ImmutableVector2D() ), mouseEventNotifier ) {{
            // TODO: improve positioning to handle i18n?
            position.set( new ImmutableVector2D( getStageSize().getWidth() - getComponentWidth() - 30,
                                                 getStageSize().getHeight() * 0.38 ) );
            zoomRatio.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( zoomRatio.get() == 1 );
                }
            } );
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

    @Override public void start() {
        super.start();
    }

    @Override public void loop() {
        super.loop();

        model.update( getTimeElapsed() );
    }

    public void updateCursor() {
        final Canvas canvas = getCanvas();

        final PlanarComponentNode toolCollision = getToolUnder( Mouse.getEventX(), Mouse.getEventY() );
        final OrthoComponentNode guiCollision = getGuiUnder( Mouse.getEventX(), Mouse.getEventY() );

        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                Component toolComponent = toolCollision == null ? null : toolCollision.getComponent().getComponent( 0 );
                if ( toolComponent != null ) {
                    canvas.setCursor( toolComponent.getCursor() );
                }
                else {
                    // this component is actually possibly a sub-component
                    Component guiComponent = guiCollision == null ? null : guiCollision.getComponentAt( Mouse.getX(), Mouse.getY() );
                    if ( guiComponent != null ) {
                        // over a HUD node, so set the cursor to what the component would want
                        canvas.setCursor( guiComponent.getCursor() );
                    }
                    else {
                        // default to the default cursor
                        canvas.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
                    }
                }
            }
        } );
    }

    public PlanarComponentNode getToolUnder( int x, int y ) {
        // iterate through the tools in reverse (front-to-back) order
        for ( GLNode node : new ArrayList<GLNode>( toolLayer.getChildren() ) {{
            Collections.reverse( this );
        }} ) {
            PlanarComponentNode tool = (PlanarComponentNode) node;
            Ray3F cameraRay = getCameraRay( x, y );
            Ray3F localRay = tool.transform.inverseRay( cameraRay );
            if ( tool.doesLocalRayHit( localRay ) ) {
                // TODO: don't hit on the "transparent" parts, like corners
                return tool;
            }
        }
        return null;
    }

    public OrthoComponentNode getGuiUnder( int x, int y ) {
        ImmutableVector2F screenPosition = new ImmutableVector2F( x, y );
        for ( OrthoComponentNode guiNode : guiNodes ) {
            if ( guiNode.isReady() ) {
                ImmutableVector2F componentPoint = guiNode.screentoComponentCoordinates( screenPosition );
                if ( guiNode.getComponent().contains( (int) componentPoint.x, (int) componentPoint.y ) ) {
                    return guiNode;
                }
            }
        }
        return null;
    }

}
