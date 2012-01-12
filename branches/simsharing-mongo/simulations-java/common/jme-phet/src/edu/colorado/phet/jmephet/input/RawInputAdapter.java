// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.jmephet.input;

import com.jme3.input.RawInputListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;

/**
 * Adapter form for JME's RawInputListener, for convenience so you don't have to specify everything in an implementation
 */
public class RawInputAdapter implements RawInputListener {
    public void beginInput() {
    }

    public void endInput() {
    }

    public void onJoyAxisEvent( JoyAxisEvent evt ) {
    }

    public void onJoyButtonEvent( JoyButtonEvent evt ) {
    }

    public void onMouseMotionEvent( MouseMotionEvent evt ) {
    }

    public void onMouseButtonEvent( MouseButtonEvent evt ) {
    }

    public void onKeyEvent( KeyInputEvent evt ) {
    }

    public void onTouchEvent( TouchEvent evt ) {
    }
}
