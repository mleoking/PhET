// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.jme;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

import com.jme3.app.Application;
import com.jme3.app.StatsView;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext.Type;
import com.jme3.system.JmeSystem;
import com.jme3.util.BufferUtils;

/**
 * TODO rework
 * from SimpleApplication
 */
public abstract class BaseJMEApplication extends Application {

    public static final String INPUT_MAPPING_EXIT = "SIMPLEAPP_Exit";
    public static final String INPUT_MAPPING_CAMERA_POS = "SIMPLEAPP_CameraPos";
    public static final String INPUT_MAPPING_MEMORY = "SIMPLEAPP_Memory";

    public final Property<ColorRGBA> backgroundColor = new Property<ColorRGBA>( ColorRGBA.Black );

    protected Node rootNode = new Node( "Root Node" );
    protected Node guiNode = new Node( "Gui Node" );
    protected Node preGuiNode = new Node( "Pre Gui Node" );
    protected float secondCounter = 0.0f;
    protected BitmapText fpsText;
    protected BitmapFont guiFont;
    protected StatsView statsView;
    protected boolean showSettings = true;
    private boolean showFps = true;

    public BaseJMEApplication() {
        super();
    }

    @Override
    public void start() {
        // set some default settings in-case
        // settings dialog is not shown
        boolean loadSettings = false;
        if ( settings == null ) {
            setSettings( new AppSettings( true ) );
            loadSettings = true;
        }

        // show settings dialog
        if ( showSettings ) {
            if ( !JmeSystem.showSettingsDialog( settings, loadSettings ) ) {
                return;
            }
        }
        //re-setting settings they can have been merged from the registry.
        setSettings( settings );
        super.start();
    }

    /**
     * Attaches FPS statistics to guiNode and displays it on the screen.
     */
    public void loadFPSText() {
        guiFont = assetManager.loadFont( "Interface/Fonts/Default.fnt" );
        fpsText = new BitmapText( guiFont, false );
        fpsText.setLocalTranslation( 0, fpsText.getLineHeight(), 0 );
        fpsText.setText( "Frames per second" );
        guiNode.attachChild( fpsText );
    }

    /**
     * Attaches Statistics View to guiNode and displays it on the screen
     * above FPS statistics line.
     */
    public void loadStatsView() {
        statsView = new StatsView( "Statistics View", assetManager, renderer.getStatistics() );
//         move it up so it appears above fps text
        statsView.setLocalTranslation( 0, fpsText.getLineHeight(), 0 );
        guiNode.attachChild( statsView );
    }

    @Override
    public void initialize() {
        super.initialize();

        // setup a GUI behind the main scene
        preGuiNode.setQueueBucket( Bucket.Gui );
        preGuiNode.setCullHint( CullHint.Never );
        Camera preGuiCam = new Camera( settings.getWidth(), settings.getHeight() );
        final ViewPort preGuiViewPort = renderManager.createPreView( "Gui Background", preGuiCam );
        preGuiViewPort.setClearFlags( true, true, true );
        preGuiViewPort.attachScene( preGuiNode );
        backgroundColor.addObserver( new SimpleObserver() {
            public void update() {
                preGuiViewPort.setBackgroundColor( backgroundColor.get() );
            }
        } );
        viewPort.setClearFlags( false, true, true );

        guiNode.setQueueBucket( Bucket.Gui );
        guiNode.setCullHint( CullHint.Never );
        loadFPSText();
        loadStatsView();
        viewPort.attachScene( rootNode );
        guiViewPort.attachScene( guiNode );

        if ( inputManager != null ) {
            if ( context.getType() == Type.Display ) {
                System.out.println( "Type is Type.Display" );
                inputManager.addMapping( INPUT_MAPPING_EXIT, new KeyTrigger( KeyInput.KEY_ESCAPE ) );
            }

            inputManager.setCursorVisible( true );

            inputManager.addMapping( INPUT_MAPPING_CAMERA_POS, new KeyTrigger( KeyInput.KEY_C ) );
            inputManager.addMapping( INPUT_MAPPING_MEMORY, new KeyTrigger( KeyInput.KEY_M ) );
            inputManager.addListener( new ActionListener() {
                                          public void onAction( String name, boolean value, float tpf ) {
                                              System.out.println( name );
                                              if ( !value ) {
                                                  return;
                                              }

                                              if ( name.equals( INPUT_MAPPING_EXIT ) ) {
                                                  stop();
                                              }
                                              else if ( name.equals( INPUT_MAPPING_CAMERA_POS ) ) {
                                                  if ( cam != null ) {
                                                      Vector3f loc = cam.getLocation();
                                                      Quaternion rot = cam.getRotation();
                                                      System.out.println( "Camera Position: ("
                                                                          + loc.x + ", " + loc.y + ", " + loc.z + ")" );
                                                      System.out.println( "Camera Rotation: " + rot );
                                                      System.out.println( "Camera Direction: " + cam.getDirection() );
                                                  }
                                              }
                                              else if ( name.equals( INPUT_MAPPING_MEMORY ) ) {
                                                  BufferUtils.printCurrentDirectMemory( null );
                                              }
                                          }
                                      }, INPUT_MAPPING_EXIT, INPUT_MAPPING_CAMERA_POS, INPUT_MAPPING_MEMORY );

        }
    }

    @Override
    public synchronized void update() {
        super.update(); // makes sure to execute AppTasks
        if ( speed == 0 || paused ) {
            return;
        }

        float tpf = timer.getTimePerFrame() * speed;

        if ( showFps ) {
            secondCounter += timer.getTimePerFrame();
            int fps = (int) timer.getFrameRate();
            if ( secondCounter >= 1.0f ) {
                fpsText.setText( "Frames per second: " + fps );
                secondCounter = 0.0f;
            }
        }

        // update states
        stateManager.update( tpf );

        // simple update and root node
        simpleUpdate( tpf );
        rootNode.updateLogicalState( tpf );
        guiNode.updateLogicalState( tpf );
        preGuiNode.updateLogicalState( tpf );
        rootNode.updateGeometricState();
        guiNode.updateGeometricState();
        preGuiNode.updateGeometricState();

        // render states
        stateManager.render( renderManager );
        if ( context.isRenderable() ) {
            renderManager.render( tpf );
        }
        simpleRender( renderManager );
        stateManager.postRender();
    }

    public void setDisplayFps( boolean show ) {
        showFps = show;
        fpsText.setCullHint( show ? CullHint.Never : CullHint.Always );
    }

    public void setDisplayStatView( boolean show ) {
        statsView.setEnabled( show );
        statsView.setCullHint( show ? CullHint.Never : CullHint.Always );
    }


    public void simpleUpdate( float tpf ) {
    }

    public void simpleRender( RenderManager rm ) {
    }
}
