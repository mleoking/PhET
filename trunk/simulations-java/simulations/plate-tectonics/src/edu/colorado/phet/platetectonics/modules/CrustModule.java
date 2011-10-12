// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.modules;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.jmephet.JMEUtils;
import edu.colorado.phet.jmephet.JMEView;
import edu.colorado.phet.jmephet.PhetJMEApplication.RenderPosition;
import edu.colorado.phet.jmephet.hud.HUDNode;
import edu.colorado.phet.jmephet.hud.HUDNode.HUDNodeCollision;
import edu.colorado.phet.jmephet.hud.PiccoloJMENode;
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
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PText;

import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.math.Vector2f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.system.JmeCanvasContext;

import static edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings.*;

/**
 * Represents the 1st tab, which has a modifiable section of crust surrounded by oceanic and continental crusts, all
 * on top of the mantle.
 */
public class CrustModule extends PlateTectonicsModule {

    private CrustModel model;

    private JMEView guiView; // view (layer) for our control panels
    private JMEView toolView; // view (layer) for our tools

    private ToolboxState toolboxState = new ToolboxState();
    private ToolDragHandler toolDragHandler = new ToolDragHandler( toolboxState );
    private Toolbox toolbox;
    private static final float RULER_Z = 0;
    private static final float THERMOMETER_Z = 1;

    public CrustModule( Frame parentFrame ) {
        super( parentFrame, CRUST_TAB, 2 ); // 0.5 km => 1 distance in view
    }

    @Override public void updateState( float tpf ) {
        super.updateState( tpf );
        model.update( tpf );
    }

    @Override public void initialize() {
        super.initialize();

        /*---------------------------------------------------------------------------*
        * mouse-button presses
        *----------------------------------------------------------------------------*/
        getInputHandler().addListener(
                new ActionListener() {
                    public void onAction( String name, boolean isMouseDown, float tpf ) {
                        // on left mouse button change
                        if ( name.equals( MAP_LMB ) ) {
                            final HUDNodeCollision toolCollision = HUDNode.getHUDCollisionUnderPoint( toolView, getInputHandler().getCursorPosition() );
                            final HUDNodeCollision guiCollision = HUDNode.getHUDCollisionUnderPoint( guiView, getInputHandler().getCursorPosition() );

                            if ( isMouseDown ) {
                                if ( toolCollision != null ) {
                                    Node parentNode = toolCollision.hudNode.getParent();

                                    if ( parentNode instanceof DraggableTool2D ) {
                                        toolDragHandler.mouseDownOnTool( (DraggableTool2D) parentNode, getMousePositionOnZPlane() );
                                    }
                                }
                            }
                            else {
                                boolean isMouseOverToolbox = guiCollision != null && guiCollision.hudNode.getParent() == toolbox;
                                toolDragHandler.mouseUp( isMouseOverToolbox );
                            }
                        }
                    }
                }, MAP_LMB );

        /*---------------------------------------------------------------------------*
        * mouse motion
        *----------------------------------------------------------------------------*/
        getInputHandler().addListener(
                new AnalogListener() {
                    public void onAnalog( final String name, final float value, float tpf ) {
                        //By always updating the cursor at every mouse move, we can be sure it is always correct.
                        //Whenever there is a mouse move event, make sure the cursor is in the right state.
                        updateCursor();

                        toolDragHandler.mouseMove( getMousePositionOnZPlane() );
                    }
                }, MAP_LEFT, MAP_RIGHT, MAP_UP, MAP_DOWN );

        // grid centered X, with front Z at 0
        Grid3D grid = new Grid3D(
                Bounds3D.fromMinMax( -1500000, 1500000,
                                     -150000, 15000,
                                     -2000000, 0 ),
                512, 512, 64 );

        // create the model and terrain
        model = new CrustModel( grid );
        mainView.getScene().attachChild( new PlateView( model, this, grid ) );

        /*---------------------------------------------------------------------------*
        * "Test" GUI
        *----------------------------------------------------------------------------*/

        guiView = createGUIView( "GUI", RenderPosition.FRONT );
        toolView = createRegularView( "Tool", createCrustCamera(), RenderPosition.FRONT );
        toolView.getViewport().setClearDepth( true ); // allow it to draw on whatever on top!

        /*---------------------------------------------------------------------------*
        * toolbox
        *----------------------------------------------------------------------------*/
        toolbox = new Toolbox( this, toolboxState ) {{
            position.set( new ImmutableVector2D( 10, 10 ) );
        }};
        guiView.getScene().attachChild( toolbox );

        //TODO: factor out duplicated code in tools
        toolboxState.rulerInToolbox.addObserver( new SimpleObserver() {
            public void update() {
                if ( !toolboxState.rulerInToolbox.get() ) {

                    // we just "removed" the ruler from the toolbox, so add it to our scene
                    RulerNode3D ruler = new RulerNode3D( getModelViewTransform(), CrustModule.this );
                    toolView.getScene().attachChild( ruler );

                    // offset the ruler slightly from the mouse, and start the drag
                    Vector2f mousePosition = getMousePositionOnZPlane();
                    Vector2f initialMouseOffset = ruler.getInitialMouseOffset();
                    ruler.setLocalTranslation( mousePosition.x - initialMouseOffset.x,
                                               mousePosition.y - initialMouseOffset.y,
                                               RULER_Z ); // on Z=0 plane
                    toolDragHandler.startDragging( ruler, mousePosition );
                }
            }
        } );

        toolboxState.thermometerInToolbox.addObserver( new SimpleObserver() {
            public void update() {
                if ( !toolboxState.thermometerInToolbox.get() ) {

                    // we just "removed" the ruler from the toolbox, so add it to our scene
                    ThermometerNode3D thermometer = new ThermometerNode3D( getModelViewTransform(), CrustModule.this, model );
                    toolView.getScene().attachChild( thermometer );

                    // offset the ruler slightly from the mouse, and start the drag
                    Vector2f mousePosition = getMousePositionOnZPlane();
                    Vector2f initialMouseOffset = thermometer.getInitialMouseOffset();
                    thermometer.setLocalTranslation( mousePosition.x - initialMouseOffset.x,
                                                     mousePosition.y - initialMouseOffset.y,
                                                     THERMOMETER_Z ); // on Z=0 plane

                    toolDragHandler.startDragging( thermometer, mousePosition );
                }
            }
        } );


        /*---------------------------------------------------------------------------*
        * my crust
        *----------------------------------------------------------------------------*/
        guiView.getScene().attachChild( new PiccoloJMENode( new ControlPanelNode( new MyCrustPanel( model ) ), getInputHandler(), this, canvasTransform ) {{
            // layout the panel if its size changes (and on startup)
            onResize.addUpdateListener( new UpdateListener() {
                                            public void update() {
                                                position.set( new ImmutableVector2D(
                                                        Math.ceil( ( getStageSize().width - getComponentWidth() ) / 2 ), // center horizontally
                                                        getStageSize().height - getComponentHeight() - 10 ) ); // offset from top
                                            }
                                        }, true ); // TODO: default to this?
        }} );

        /*---------------------------------------------------------------------------*
        * labels
        *----------------------------------------------------------------------------*/

        // "oceanic crust" label
        guiView.getScene().attachChild( new PiccoloJMENode( new PText( OCEANIC_CRUST ) {{
            setFont( new PhetFont( 16, true ) );
        }}, getInputHandler(), this, canvasTransform ) {{
            // TODO: improve positioning to handle i18n?
            position.set( new ImmutableVector2D( 30,
                                                 getStageSize().getHeight() * 0.6 ) );
        }} );

        // "continental crust" label
        guiView.getScene().attachChild( new PiccoloJMENode( new PText( CONTINENTAL_CRUST ) {{
            setFont( new PhetFont( 16, true ) );
        }}, getInputHandler(), this, canvasTransform ) {{
            // TODO: improve positioning to handle i18n?
            position.set( new ImmutableVector2D( getStageSize().getWidth() - getComponentWidth() - 30,
                                                 getStageSize().getHeight() * 0.6 ) );
        }} );

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

    public void updateCursor() {
        JmeCanvasContext context = (JmeCanvasContext) getApp().getContext();
        final Canvas canvas = context.getCanvas();

        // TODO: refactor picking to work with multiple views?
        final HUDNodeCollision toolCollision = HUDNode.getHUDCollisionUnderPoint( toolView, getInputHandler().getCursorPosition() );
        final HUDNodeCollision guiCollision = HUDNode.getHUDCollisionUnderPoint( guiView, getInputHandler().getCursorPosition() );
        final HUDNodeCollision mainCollision = HUDNode.getHUDCollisionUnderPoint( mainView, getInputHandler().getCursorPosition() );
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                Component toolComponent = toolCollision == null ? null : toolCollision.hudNode.getRootComponent().getComponent( 0 );
                if ( toolComponent != null ) {
                    canvas.setCursor( toolComponent.getCursor() );
                }
                else {
                    Component guiComponent = guiCollision == null ? null : guiCollision.getGuiPlaneComponent();
                    if ( guiComponent != null ) {
                        // over a HUD node, so set the cursor to what the component would want
                        canvas.setCursor( guiComponent.getCursor() );
                    }
                    else {
                        // check if we are picking a piccolo canvas in the main view
                        if ( mainCollision != null && mainCollision.hudNode.getRootComponent().getComponent( 0 ) instanceof PCanvas ) {
                            // use the canvas's cursor
                            canvas.setCursor( mainCollision.hudNode.getRootComponent().getComponent( 0 ).getCursor() );
                        }
                        else {
                            // default to the default cursor
                            canvas.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
                        }
                    }
                }
            }
        } );
    }

    @Override public Camera getDebugCamera() {
        return mainView.getCamera();
    }

    private Vector2f getMousePositionOnZPlane() {
        return JMEUtils.intersectZPlaneWithRay( mainView.getCameraRayUnderCursor( getInputHandler() ) );
    }

}
