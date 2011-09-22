// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.jmephet.input;

import com.jme3.input.RawInputListener;
import com.jme3.input.controls.InputListener;
import com.jme3.input.controls.Trigger;
import com.jme3.math.Vector2f;

/**
 * Mirrors some of the functionality of JME's InputManager. See InputManager for documentation
 */
public interface JMEInputHandler {
    public void addListener( InputListener listener, String... mappingNames );

    public void removeListener( InputListener listener );

    public void addMapping( String mappingName, Trigger... triggers );

    public void addRawInputListener( RawInputListener listener );

    public void removeRawInputListener( RawInputListener listener );

    public Vector2f getCursorPosition();
}
