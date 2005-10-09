/* Copyright 2004, Sam Reid */
package edu.colorado.phet.piccolo.pswing;

import edu.umd.cs.piccolo.PCanvas;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jul 12, 2005
 * Time: 8:47:08 AM
 * Copyright (c) Jul 12, 2005 by Sam Reid
 */

public class PSwingCanvas extends PCanvas {
    public static final String SWING_WRAPPER_KEY = "Swing Wrapper";

    private JComponent swingWrapper = new SwingWrapper();
    private static PSwingRepaintManager PSwingRepaintManager = new PSwingRepaintManager();
    private PSwingEventHandler swingEventHandler;

    public static class SwingWrapper extends JComponent {
        public SwingWrapper() {
            setSize( new Dimension( 0, 0 ) );
            setPreferredSize( new Dimension( 0, 0 ) );
            putClientProperty( SWING_WRAPPER_KEY, SWING_WRAPPER_KEY );
        }
    }

    public PSwingCanvas() {
        add( swingWrapper );
        RepaintManager.setCurrentManager( PSwingRepaintManager );
        PSwingRepaintManager.add( swingWrapper );

        swingEventHandler = new PSwingEventHandler( this, getCamera() );//todo or maybe getCameraLayer() or getRoot()?
        swingEventHandler.setActive( true );
    }

    public JComponent getSwingWrapper() {
        return swingWrapper;
    }

}