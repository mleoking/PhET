// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.jme;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

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
public abstract class PhetJMEApplication extends Application {

    public final Property<ColorRGBA> backgroundColor = new Property<ColorRGBA>( ColorRGBA.Black );

    public final Property<Dimension> canvasSize = new Property<Dimension>( null ); // updated on the Swing EDT

    private List<SimpleObserver> updateObservers = new ArrayList<SimpleObserver>();

    // statistics that can be shown on the screen
    public JMEStatistics statistics = new JMEStatistics();

    // our main "root" nodes for different layers
    private Node sceneNode = new Node( "Scene Node" );
    private Node guiNode = new Node( "Gui Node" );
    private Node backgroundGuiNode = new Node( "Background Gui Node" );

    private JMEView gui; // in front of the main viewports
    private JMEView backgroundGui; // behind the main viewports

    // nodes that will get updated every frame
    private List<Node> liveNodes = new ArrayList<Node>();

    private volatile Dimension stageSize = null;

    public PhetJMEApplication() {
        super();

        // let everyone know that this is the one unique global instance
        JMEUtils.setApplication( this );
    }

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

        // setup a GUI behind the main scene
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
        backgroundGui = new JMEView( this, backgroundGuiViewPort, backgroundGuiCam, backgroundGuiNode );

        // make the "main" viewport not clear what is behind it
        viewPort.setClearFlags( false, true, true );
        viewPort.attachScene( sceneNode );
        liveNodes.add( sceneNode );

        // hook up the main "gui"
        guiNode.setQueueBucket( Bucket.Gui );
        guiNode.setCullHint( CullHint.Never );
        guiViewPort.attachScene( guiNode );
        liveNodes.add( guiNode );
        gui = new JMEView( this, guiViewPort, guiViewPort.getCamera(), guiNode );

        statistics.initialize( this, guiNode );

        if ( inputManager != null ) {
            inputManager.setCursorVisible( true );
        }
    }

    public JMEView createView( String name ) {
        return createView( name, new Camera( settings.getWidth(), settings.getHeight() ) );
    }

    public JMEView createView( String name, Camera camera ) {
        Node scene = new Node( name + " Scene Node" );

        final ViewPort viewport = renderManager.createMainView( name + " Viewport", camera );
        viewport.attachScene( scene );
        addLiveNode( scene );
        return new JMEView( this, viewport, camera, scene );
    }

    public Timer getTimer() {
        return timer;
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

    public JMEView getGui() {
        return gui;
    }

    public JMEView getBackgroundGui() {
        return backgroundGui;
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
    }

    public void simpleRender( RenderManager rm ) {
    }

    public void onResize( Dimension canvasSize ) {
        if ( stageSize == null ) {
            stageSize = canvasSize;
        }

        this.canvasSize.set( canvasSize );
    }

    public Dimension getStageSize() {
        return stageSize;
    }
}
