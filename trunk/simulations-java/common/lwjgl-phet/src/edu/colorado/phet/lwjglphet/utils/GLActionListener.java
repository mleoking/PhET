// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//REVIEW mention why it's necessary to do this in the LWJGL thread. Discussed in lwjgl-implementation-notes.txt, but best to describe close to where doc is needed.

/**
 * Convenience listener that can be added to wherever a Swing ActionListener is needed.
 * It wraps the runnable through LWJGLUtils.invoke, so that it will be run in the LWJGL thread.
 */
public class GLActionListener implements ActionListener {
    private Runnable runnable;

    public GLActionListener( Runnable runnable ) {
        this.runnable = runnable;
    }

    public void actionPerformed( ActionEvent e ) {
        LWJGLUtils.invoke( runnable );
    }
}
