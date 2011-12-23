// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GLActionListener implements ActionListener {
    private Runnable runnable;

    public GLActionListener( Runnable runnable ) {
        this.runnable = runnable;
    }

    public void actionPerformed( ActionEvent e ) {
        LWJGLUtils.invoke( runnable );
    }
}
