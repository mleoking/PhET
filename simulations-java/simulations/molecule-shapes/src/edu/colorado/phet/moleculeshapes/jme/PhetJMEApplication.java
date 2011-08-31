// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.jme;

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
 */
public abstract class PhetJMEApplication extends Application {

    public final Property<ColorRGBA> backgroundColor = new Property<ColorRGBA>( ColorRGBA.Black );

    private List<SimpleObserver> updateObservers = new ArrayList<SimpleObserver>();

    // statistics that can be shown on the screen
    public JMEStatistics statistics = new JMEStatistics();

    private Node sceneNode = new Node( "Scene Node" );
    private Node guiNode = new Node( "Gui Node" );
    private Node backgroundGuiNode = new Node( "Background Gui Node" );

    // nodes that will get updated every frame
    private List<Node> liveNodes = new ArrayList<Node>();

    public PhetJMEApplication() {
        super();
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

        // make the "main" viewport not clear what is behind it
        viewPort.setClearFlags( false, true, true );
        viewPort.attachScene( sceneNode );
        liveNodes.add( sceneNode );

        guiNode.setQueueBucket( Bucket.Gui );
        guiNode.setCullHint( CullHint.Never );
        guiViewPort.attachScene( guiNode );
        liveNodes.add( guiNode );

        statistics.initialize( this, guiNode );

        if ( inputManager != null ) {
            inputManager.setCursorVisible( true );
        }
    }

    public Timer getTimer() {
        return timer;
    }

    @Override
    public synchronized void update() {
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

    public Node getSceneNode() {
        return sceneNode;
    }

    public Node getGuiNode() {
        return guiNode;
    }

    public Node getBackgroundGuiNode() {
        return backgroundGuiNode;
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
}
