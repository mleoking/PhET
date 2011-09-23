// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.jmephet;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.Module.Listener;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.common.phetcommon.view.util.PhetOptionPane;
import edu.colorado.phet.jmephet.input.JMEInputHandler;
import edu.colorado.phet.jmephet.input.WrappedInputManager;

import com.jme3.app.Application;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.system.AppSettings;
import com.jme3.system.Timer;

/**
 * PhET-specific behavior needed instead of the generic SimpleApplication JME3 class.
 * <p/>
 * Has a "background" GUI in the back, a scene in the middle, and the main GUI in front.
 * TODO: further cleanup on the exported interface
 */
public class PhetJMEApplication extends Application {

    /*---------------------------------------------------------------------------*
    * modules
    *----------------------------------------------------------------------------*/

    private final List<JMEModule> modules = new ArrayList<JMEModule>();
    public final Property<JMEModule> activeModule = new Property<JMEModule>( null );

    /*---------------------------------------------------------------------------*
    * global properties
    *----------------------------------------------------------------------------*/

    public final Property<ColorRGBA> backgroundColor = new Property<ColorRGBA>( ColorRGBA.Black );
    public final Property<Dimension> canvasSize = new Property<Dimension>( null ); // updated on the Swing EDT

    private List<SimpleObserver> updateObservers = new ArrayList<SimpleObserver>();

    // statistics that can be shown on the screen
    public JMEStatistics statistics = new JMEStatistics();

    // our main "root" nodes for different layers
    private Node sceneNode = new Node( "Scene Node" );
    private Node guiNode = new Node( "Gui Node" );
    private Node backgroundGuiNode = new Node( "Background Gui Node" );

    // nodes that will get updated every frame
    private List<Node> liveNodes = new ArrayList<Node>();

    private volatile Dimension stageSize = null;
    private final Frame parentFrame;

    private JMEInputHandler directInputHandler;

    /*---------------------------------------------------------------------------*
    * construction
    *----------------------------------------------------------------------------*/

    public PhetJMEApplication( Frame parentFrame ) {
        super();
        this.parentFrame = parentFrame;

        // let everyone know that this is the one unique global instance
        JMEUtils.setApplication( this );
    }

    /*---------------------------------------------------------------------------*
    * modules
    *----------------------------------------------------------------------------*/

    public void addModule( final JMEModule module ) {
        modules.add( module );

        // when modules are made active, record that so we can update our active modules
        module.addListener( new Listener() {
            public void activated() {
                activeModule.set( module );
            }

            public void deactivated() {
            }
        } );
    }

    /*---------------------------------------------------------------------------*
    * JMonkeyEngine overrides
    *----------------------------------------------------------------------------*/

    @Override
    public void start() {
        // setup default settings, in case
        if ( settings == null ) {
            setSettings( new AppSettings( true ) );
        }

        //re-setting settings they can have been merged from the registry.
        setSettings( settings );
        super.start();
    }

    @Override
    public void initialize() {
        super.initialize();
//        guiViewPort.getCamera().resize( canvasSize.get().width, canvasSize.get().height, true );
//        guiViewPort.getCamera().update();
//        cam.resize( canvasSize.get().width, canvasSize.get().height, true );
//        cam.update();

        directInputHandler = new WrappedInputManager( inputManager );

        // setup a GUI behind the main scene
        // TODO: consider removing this and just adding a background-color layer!
        backgroundGuiNode.setQueueBucket( Bucket.Gui );
        backgroundGuiNode.setCullHint( CullHint.Never );
        Camera backgroundGuiCam = new Camera( settings.getWidth(), settings.getHeight() );
        final ViewPort backgroundGuiViewPort = renderManager.createPreView( "Background GUI", backgroundGuiCam );
        backgroundGuiViewPort.setClearFlags( true, true, true );
        backgroundGuiViewPort.attachScene( backgroundGuiNode );
        backgroundColor.addObserver( new SimpleObserver() {
            public void update() {
                backgroundGuiViewPort.setBackgroundColor( backgroundColor.get() );
            }
        } );
        liveNodes.add( backgroundGuiNode );
        JMEView backgroundGui = new JMEView( this, backgroundGuiViewPort, backgroundGuiCam, backgroundGuiNode );

        // make the "main" viewport not clear what is behind it
        viewPort.setClearFlags( false, true, true );
        viewPort.attachScene( sceneNode );
        liveNodes.add( sceneNode );

        // hook up the main "gui"
        guiNode.setQueueBucket( Bucket.Gui );
        guiNode.setCullHint( CullHint.Never );
        guiViewPort.attachScene( guiNode );
        liveNodes.add( guiNode );
        JMEView gui = new JMEView( this, guiViewPort, guiViewPort.getCamera(), guiNode );

        statistics.initialize( this, guiNode );

        if ( inputManager != null ) {
            inputManager.setCursorVisible( true );
        }
    }

    @Override
    public void update() {
        super.update(); // make sure to call this
        if ( speed == 0 || paused ) {
            return;
        }

        float tpf = timer.getTimePerFrame() * speed;

        // update states
        stateManager.update( tpf );

        // simple update and root node
        updateState( tpf );

        for ( SimpleObserver observer : updateObservers ) {
            observer.update();
        }

        for ( Node node : liveNodes ) {
            node.updateLogicalState( tpf );
        }

        for ( Node node : liveNodes ) {
            node.updateGeometricState();
        }

        // render states
        stateManager.render( renderManager );
        if ( context.isRenderable() ) {
            renderManager.render( tpf );
        }
        simpleRender( renderManager );
        stateManager.postRender();
    }

    /*---------------------------------------------------------------------------*
    * view and node utilities
    *----------------------------------------------------------------------------*/

    public Camera createDefaultCamera() {
        return new Camera( canvasSize.get().width, canvasSize.get().height );
    }

    public JMEView createMainView( final String name, Camera camera ) {
        return createView( name, camera, new Function2<RenderManager, Camera, ViewPort>() {
            public ViewPort apply( RenderManager renderManager, Camera camera ) {
                return renderManager.createMainView( name + " Viewport", camera );
            }
        } );
    }

    public JMEView createBackGUIView( final String name ) {
        return createGUIView( name, new Function2<RenderManager, Camera, ViewPort>() {
            public ViewPort apply( RenderManager renderManager, Camera camera ) {
                return renderManager.createPreView( name + " Viewport", camera );
            }
        } );
    }

    public JMEView createFrontGUIView( final String name ) {
        return createGUIView( name, new Function2<RenderManager, Camera, ViewPort>() {
            public ViewPort apply( RenderManager renderManager, Camera camera ) {
                return renderManager.createPostView( name + " Viewport", camera );
            }
        } );
    }

    private JMEView createView( String name, Camera camera, Function2<RenderManager, Camera, ViewPort> viewportFactory ) {
        Node scene = new Node( name + " Node" );

        final ViewPort viewport = viewportFactory.apply( renderManager, camera );
        viewport.attachScene( scene );
        addLiveNode( scene );
        return new JMEView( this, viewport, camera, scene );
    }

    private JMEView createGUIView( String name, Function2<RenderManager, Camera, ViewPort> viewportFactory ) {
        JMEView view = createView( name, createDefaultCamera(), viewportFactory );

        view.getScene().setQueueBucket( Bucket.Gui );
        view.getScene().setCullHint( CullHint.Never );

        return view;
    }

    public Timer getTimer() {
        return timer;
    }

    public void addLiveNode( Node node ) {
        liveNodes.add( node );
    }

    public void removeLiveNode( Node node ) {
        liveNodes.remove( node );
    }

    public void addUpdateObserver( SimpleObserver observer ) {
        updateObservers.add( observer );
    }

    public void removeUpdateObserver( SimpleObserver observer ) {
        updateObservers.remove( observer );
    }

    public void updateState( float tpf ) {
        if ( activeModule.get() != null ) {
            activeModule.get().updateState( tpf );
        }
    }

    public void simpleRender( RenderManager rm ) {
    }

    public void onResize( Dimension canvasSize ) {
        if ( stageSize == null ) {
            stageSize = canvasSize;
        }

        this.canvasSize.set( canvasSize );

        // notify all of our modules about the size change. they might even be hidden
        for ( JMEModule module : modules ) {
            module.updateLayout( canvasSize );
        }
    }

    public Dimension getStageSize() {
        return stageSize;
    }

    @Override public void handleError( String errMsg, final Throwable t ) {
        super.handleError( errMsg, t );
        if ( errMsg.equals( "Failed to initialize OpenGL context" ) ) {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    // TODO: i18n?
                    PhetOptionPane.showMessageDialog( getParentFrame(), "The simulation was unable to start.\nUpgrading your video card's drivers may fix the problem.\nError information:\n" + t.getMessage() );
                }
            } );
        }
    }

    public Frame getParentFrame() {
        return parentFrame;
    }

    public JMEInputHandler getDirectInputHandler() {
        return directInputHandler;
    }
}
