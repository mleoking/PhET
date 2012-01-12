// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.jmephet.input;

import com.jme3.input.InputManager;
import com.jme3.input.RawInputListener;
import com.jme3.input.controls.InputListener;
import com.jme3.input.controls.Trigger;
import com.jme3.math.Vector2f;

public class WrappedInputManager implements JMEInputHandler {

    private final InputManager inputManager;

    public WrappedInputManager( InputManager inputManager ) {
        this.inputManager = inputManager;
    }

    public void addListener( InputListener listener, String... mappingNames ) {
        inputManager.addListener( listener, mappingNames );
    }

    public void removeListener( InputListener listener ) {
        inputManager.removeListener( listener );
    }

    public void addMapping( String mappingName, Trigger... triggers ) {
        inputManager.addMapping( mappingName, triggers );
    }

    public void addRawInputListener( RawInputListener listener ) {
        inputManager.addRawInputListener( listener );
    }

    public void removeRawInputListener( RawInputListener listener ) {
        inputManager.removeRawInputListener( listener );
    }

    public Vector2f getCursorPosition() {
        return inputManager.getCursorPosition();
    }
}
