// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.jme;

import edu.colorado.phet.common.phetcommon.model.event.AbstractNotifier;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

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
 * TODO rework
 * from SimpleApplication
 */
public abstract class BaseJMEApplication extends Application {

    public final Property<ColorRGBA> backgroundColor = new Property<ColorRGBA>( ColorRGBA.Black );

    protected Node rootNode = new Node( "Root Node" );
    protected Node guiNode = new Node( "Gui Node" );
    protected Node preGuiNode = new Node( "Pre Gui Node" );

    // event notifier for the update loop
    public AbstractNotifier<VoidFunction0> updateNotifier = new AbstractNotifier<VoidFunction0>();

    // statistics that can be shown on the screen
    public JMEStatistics statistics = new JMEStatistics();

    public BaseJMEApplication() {
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
        viewPort.attachScene( rootNode );
        guiViewPort.attachScene( guiNode );

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
        super.update(); // makes sure to execute AppTasks
        if ( speed == 0 || paused ) {
            return;
        }

        float tpf = timer.getTimePerFrame() * speed;

        updateNotifier.updateListeners( new VoidFunction1<VoidFunction0>() {
            public void apply( VoidFunction0 fun ) {
                fun.apply();
            }
        } );

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

    public void simpleUpdate( float tpf ) {
    }

    public void simpleRender( RenderManager rm ) {
    }
}
