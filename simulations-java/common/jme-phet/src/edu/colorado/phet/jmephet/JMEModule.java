// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.jmephet;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.jmephet.PhetJMEApplication.RenderPosition;
import edu.colorado.phet.jmephet.input.JMEInputHandler;
import edu.colorado.phet.jmephet.input.JMEModuleInputHandler;

import com.jme3.app.state.AppState;
import com.jme3.asset.AssetManager;
import com.jme3.renderer.Camera;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;

/**
 * Support for creating a JME application, context and canvas
 */
public abstract class JMEModule extends Module {

    private JMEModuleInputHandler inputHandler;
    private List<AppState> states = new ArrayList<AppState>();
    private List<JMEView> views = new ArrayList<JMEView>();

    private static PhetJMEApplication app = null;
    private static JmeCanvasContext context;
    private static Canvas canvas;

    private boolean hasInitialized = false;

    public JMEModule( Frame parentFrame, String name, IClock clock ) {
        super( name, clock );

        final boolean isFirstModule = app == null;

        // do the following only for the first initialization, since we can only create one application
        if ( isFirstModule ) {
            final AppSettings settings = new AppSettings( true );

            JMEUtils.initializeLibraries( settings );

            // antialiasing (use at most 4 anti-aliasing samples. more makes the UI look blurry)
            int maxSamples = JMEUtils.getMaximumAntialiasingSamples();
            settings.setSamples( Math.min( 4, maxSamples ) );

            // store settings within the properties
            JMEUtils.maxAllowedSamples = maxSamples;
            if ( JMEUtils.antiAliasingSamples.get() == null ) {
                JMEUtils.antiAliasingSamples.set( settings.getSamples() );
            }

            // limit the framerate
            settings.setFrameRate( JMEUtils.frameRate.get() );

            // TODO: better way than having each module know how to create its own canvas? (more of a global JME state?)
            app = createApplication( parentFrame );

            app.setPauseOnLostFocus( false );
            app.setSettings( settings );
            app.createCanvas();

            JMEUtils.frameRate.addObserver( new SimpleObserver() {
                public void update() {
                    AppSettings s = settings;
                    s.setFrameRate( JMEUtils.frameRate.get() );
                    app.setSettings( s );
                    app.restart();
                }
            } );

            JMEUtils.antiAliasingSamples.addObserver( new SimpleObserver() {
                public void update() {
                    AppSettings s = settings;
                    s.setSamples( JMEUtils.antiAliasingSamples.get() );
                    app.setSettings( s );
                    app.restart();
                }
            } );

            context = (JmeCanvasContext) app.getContext();
            canvas = context.getCanvas();

            addListener( new Listener() {
                public void activated() {
                    app.startCanvas();
                }

                public void deactivated() {
                }
            } );

            // listen to resize events on our canvas, so that we can update our layout
            getCanvas().addComponentListener( new ComponentAdapter() {
                @Override public void componentResized( ComponentEvent e ) {
                    app.onResize( getCanvas().getSize() );
                }
            } );
        }

        setSimulationPanel( new JPanel( new BorderLayout() ) {{
            if ( isFirstModule ) {
                // add the actual panel in, since we are the top module
                add( canvas, BorderLayout.CENTER );
            }
            else {
                // placeholder. since this will never show up, we don't need to handle anything
            }
        }} );

        addListener( new Listener() {
            public void activated() {
                // make sure we are initialized before anything else
                assureInitialized();

                // hook up states to our application
                for ( AppState state : states ) {
                    app.getStateManager().attach( state );
                }

                for ( JMEView view : views ) {
                    view.setVisible( true );
                }
            }

            public void deactivated() {
                // detach states from our application
                for ( AppState state : states ) {
                    app.getStateManager().detach( state );
                }

                for ( JMEView view : views ) {
                    view.setVisible( false );
                }
            }
        } );

        app.addModule( this );

        // hide most of the default things
        setClockControlPanel( null );
        setControlPanel( null );
        setLogoPanelVisible( false );
    }

    private void assureInitialized() {
        if ( !hasInitialized ) {
            hasInitialized = true;
            // locking for the JME thread
            JMEUtils.invokeLater( new Runnable() {
                public void run() {
                    inputHandler = new JMEModuleInputHandler( JMEModule.this, app.getInputManager() ); // TODO: we are passing so many partially-constructed objects...
                    initialize();

                    if ( getCanvasSize() != null ) { // sanity check
                        updateLayout( getCanvasSize() );
                    }
                }
            } );
        }
    }

    public abstract void initialize();

    public abstract PhetJMEApplication createApplication( Frame parentFrame );

    // this gets called whenever the state should be updated TODO better docs
    public void updateState( final float tpf ) {

    }

    public void updateLayout( Dimension canvasSize ) {
    }

    public void attachState( AppState state ) {
        states.add( state );
        if ( isActive() ) {
            app.getStateManager().attach( state );
        }
    }

    public void detachState( AppState state ) {
        states.remove( state );
        if ( isActive() ) {
            app.getStateManager().detach( state );
        }
    }

    public JMEView createRegularView( final String name, Camera camera, RenderPosition position ) {
        JMEView view = app.createRegularView( name, camera, position );
        addView( view );
        return view;
    }

    public JMEView createGUIView( final String name, RenderPosition position ) {
        JMEView view = app.createGUIView( name, position );
        addView( view );
        return view;
    }

    private void addView( JMEView view ) {
        views.add( view );
        view.setVisible( isActive() );
    }

    /*---------------------------------------------------------------------------*
    * getters
    *----------------------------------------------------------------------------*/

    public Dimension getStageSize() {
        return app.getStageSize();
    }

    public Dimension getCanvasSize() {
        return app.canvasSize.get();
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public AssetManager getAssetManager() {
        return app.getAssetManager();
    }

    public JMEInputHandler getInputHandler() {
        return inputHandler;
    }
}
