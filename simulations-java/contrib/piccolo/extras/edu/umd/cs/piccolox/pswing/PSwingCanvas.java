/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.umd.cs.piccolox.pswing;

import edu.umd.cs.piccolo.IgnorableEventSource;
import edu.umd.cs.piccolo.PCanvas;

import javax.swing.*;
import java.awt.*;

/**
 * The <b>PSwingCanvas</b> is a PCanvas that can display Swing components with the PSwing adapter.
 *
 * @author Benjamin B. Bederson
 * @author Sam R. Reid
 * @author Lance E. Good
 */

public class PSwingCanvas extends PCanvas implements IgnorableEventSource {
    public static final String SWING_WRAPPER_KEY = "Swing Wrapper";

    private SwingWrapper swingWrapper;
    private PSwingEventHandler swingEventHandler;
    private volatile boolean ignoringEvents;

    /**
     * Construct a new PSwingCanvas.
     */
    public PSwingCanvas() {
        swingWrapper = new SwingWrapper( this );
        add( swingWrapper );
        //allow a client application to set a subclass of PSwingRepaintManager instead of using the default provided in previous version of PSwingCanvas
        if (!(RepaintManager.currentManager( this) instanceof PSwingRepaintManager)){
            RepaintManager.setCurrentManager( new PSwingRepaintManager());
        }
        ((PSwingRepaintManager)RepaintManager.currentManager( this )).addPSwingCanvas( this );

        swingEventHandler = new PSwingEventHandler( this, getCamera() );//todo or maybe getCameraLayer() or getRoot()?
        swingEventHandler.setActive( true );
    }

    JComponent getSwingWrapper() {
        return swingWrapper;
    }

    /*
     * For internal use only.
     */
    public void setIgnoringEvents(boolean flag) {
        ignoringEvents = flag;
    }

    /*
     * For internal use only.
     */
    public boolean isIgnoringEvents() {
        return ignoringEvents;
    }

    public void addPSwing( PSwing pSwing ) {
        swingWrapper.add( pSwing.getComponent() );
    }

    public void removePSwing( PSwing pSwing ) {
        swingWrapper.remove( pSwing.getComponent() );
    }

    private static class SwingWrapper extends JComponent {
        private PSwingCanvas pSwingCanvas;

        public SwingWrapper( PSwingCanvas pSwingCanvas ) {
            this.pSwingCanvas = pSwingCanvas;
            setSize( new Dimension( 0, 0 ) );
            setPreferredSize( new Dimension( 0, 0 ) );
            putClientProperty( SWING_WRAPPER_KEY, SWING_WRAPPER_KEY );
        }

        public PSwingCanvas getpSwingCanvas() {
            return pSwingCanvas;
        }
    }

}