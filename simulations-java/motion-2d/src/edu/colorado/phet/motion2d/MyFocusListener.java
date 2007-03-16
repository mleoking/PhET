package edu.colorado.phet.motion2d;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class MyFocusListener implements FocusListener {
    Component myComponent;

    public MyFocusListener( Component myComponent ) {
        this.myComponent = myComponent;
    }

    public void focusGained( FocusEvent fe ) {
    }

    public void focusLost( FocusEvent fe ) {
        myComponent.requestFocus();
    }
}