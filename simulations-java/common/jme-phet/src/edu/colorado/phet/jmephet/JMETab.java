// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.jmephet;

import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.PhetTabbedPane.TabbedModule.Tab;
import edu.colorado.phet.jmephet.PhetJMEApplication.RenderPosition;
import edu.colorado.phet.jmephet.input.JMEInputHandler;
import edu.colorado.phet.jmephet.input.JMETabInputHandler;

import com.jme3.app.state.AppState;
import com.jme3.asset.AssetManager;
import com.jme3.renderer.Camera;

public abstract class JMETab implements Tab {
    private final PhetJMEApplication app;
    private final String title;
    public final Property<Boolean> active = new Property<Boolean>( false );

    private JMETabInputHandler inputHandler;
    private List<AppState> states = new ArrayList<AppState>();
    private List<JMEView> views = new ArrayList<JMEView>();

    private boolean hasInitialized = false;

    public JMETab( PhetJMEApplication app, String title ) {
        this.app = app;
        this.title = title;

        app.addTab( this );

        assureInitialized();
    }

    public String getTitle() {
        return title;
    }

    public void setActive( boolean active ) {
        if ( active != this.active.get() ) {
            this.active.set( active );
            if ( active ) {
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
            else {
                // detach states from our application
                for ( AppState state : states ) {
                    app.getStateManager().detach( state );
                }

                for ( JMEView view : views ) {
                    view.setVisible( false );
                }
            }
        }
    }

    private void assureInitialized() {
        if ( !hasInitialized ) {
            hasInitialized = true;
            // locking for the JME thread
            JMEUtils.invokeLater( new Runnable() {
                public void run() {
                    inputHandler = new JMETabInputHandler( JMETab.this, app.getInputManager() ); // TODO: we are passing so many partially-constructed objects...
                    try {
                        SwingUtilities.invokeAndWait( new Runnable() {
                            public void run() {
                                initialize();

                                if ( getCanvasSize() != null ) { // sanity check
                                    updateLayout( getCanvasSize() );
                                }
                            }
                        } );
                    }
                    catch ( InterruptedException e ) {
                        e.printStackTrace();
                    }
                    catch ( InvocationTargetException e ) {
                        e.printStackTrace();
                    }
                }
            } );
        }
    }

    public abstract void initialize();

    // this gets called whenever the state should be updated TODO better docs
    public void updateState( final float tpf ) {

    }

    public void updateLayout( Dimension canvasSize ) {
    }

    public boolean isActive() {
        return active.get();
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

    public AssetManager getAssetManager() {
        return app.getAssetManager();
    }

    public JMEInputHandler getInputHandler() {
        return inputHandler;
    }
}
