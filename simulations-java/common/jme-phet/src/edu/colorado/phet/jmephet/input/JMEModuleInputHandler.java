// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.jmephet.input;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.application.Module.Listener;
import edu.colorado.phet.common.phetcommon.util.FunctionalUtils;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.jmephet.JMEModule;
import edu.colorado.phet.jmephet.JMEUtils;

import com.jme3.input.InputManager;
import com.jme3.input.RawInputListener;
import com.jme3.input.controls.InputListener;
import com.jme3.input.controls.Trigger;
import com.jme3.math.Vector2f;

/**
 * Allows us to hook a group of module-specific listeners together so that they are added
 * when the module is activated and removed when the module is deactivated.
 * <p/>
 * Not thread-safe. Assumes it is accessed in the JME thread
 */
public class JMEModuleInputHandler implements JMEInputHandler {
    private JMEModule module;
    private final InputManager inputManager;

    private List<ListenerWrapper> wrappers = new ArrayList<ListenerWrapper>();

    public JMEModuleInputHandler( JMEModule module, InputManager inputManager ) {
        this.module = module;
        this.inputManager = inputManager;

        // update our input listeners based on whether we are active or inactive
        module.addListener( new Listener() {
            public void activated() {
                JMEUtils.invoke( new Runnable() {
                    public void run() {
                        for ( ListenerWrapper wrapper : wrappers ) {
                            wrapper.activate();
                        }
                    }
                } );
            }

            public void deactivated() {
                JMEUtils.invoke( new Runnable() {
                    public void run() {
                        for ( ListenerWrapper wrapper : wrappers ) {
                            wrapper.deactivate();
                        }
                    }
                } );
            }
        } );
    }

    public void addListener( InputListener listener, String... mappingNames ) {
        addListenerWrapper( new InputListenerWrapper( listener, mappingNames ) );
    }

    public void removeListener( InputListener listener ) {
        removeListenerWrapper( listener );
    }

    public void addMapping( String mappingName, Trigger... triggers ) {
        inputManager.addMapping( mappingName, triggers );
    }

    public void addRawInputListener( RawInputListener listener ) {
        addListenerWrapper( new RawInputListenerWrapper( listener ) );
    }

    public void removeRawInputListener( RawInputListener listener ) {
        removeListenerWrapper( listener );
    }

    public Vector2f getCursorPosition() {
        return inputManager.getCursorPosition();
    }

    // Add a wrapper, and activate if necessary
    private void addListenerWrapper( ListenerWrapper wrapper ) {
        wrappers.add( wrapper );
        if ( module.isActive() ) {
            wrapper.activate();
        }
    }

    private ListenerWrapper findWrapperWithListenerObject( final Object listener ) {
        return FunctionalUtils.firstOrNull( wrappers, new Function1<ListenerWrapper, Boolean>() {
            public Boolean apply( ListenerWrapper listenerWrapper ) {
                // TODO: should we consider equals() instead?
                return listenerWrapper.getListener() == listener;
            }
        } );
    }

    // Remove a wrapper, and deactivate if necessary
    private void removeListenerWrapper( Object listener ) {
        ListenerWrapper wrapper = findWrapperWithListenerObject( listener );
        if ( wrapper != null ) {
            wrappers.remove( wrapper );
            if ( module.isActive() ) {
                wrapper.deactivate();
            }
        }
    }

    // Allows us to consolidate various types of wrappers
    private static interface ListenerWrapper {
        // called to add the listener to the input manager
        public void activate();

        // called to remove the listener from the input manager
        public void deactivate();

        // get the actual listener object. used for finding the wrapper during removal
        public Object getListener();
    }

    public class InputListenerWrapper implements ListenerWrapper {
        private final InputListener inputListener;
        private final String[] mappingNames;

        public InputListenerWrapper( InputListener inputListener, String[] mappingNames ) {
            this.inputListener = inputListener;
            this.mappingNames = mappingNames;
        }

        public void activate() {
            inputManager.addListener( inputListener, mappingNames );
        }

        public void deactivate() {
            inputManager.removeListener( inputListener );
        }

        public Object getListener() {
            return inputListener;
        }
    }

    public class RawInputListenerWrapper implements ListenerWrapper {
        private final RawInputListener rawInputListener;

        public RawInputListenerWrapper( RawInputListener rawInputListener ) {
            this.rawInputListener = rawInputListener;
        }

        public void activate() {
            inputManager.addRawInputListener( rawInputListener );
        }

        public void deactivate() {
            inputManager.removeRawInputListener( rawInputListener );
        }

        public Object getListener() {
            return rawInputListener;
        }
    }
}
