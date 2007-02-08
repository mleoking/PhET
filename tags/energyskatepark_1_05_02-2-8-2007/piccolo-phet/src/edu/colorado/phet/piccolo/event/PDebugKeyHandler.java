/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.piccolo.event;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import edu.umd.cs.piccolo.util.PDebug;


/**
 * PDebugKeyHandler is a key handler that allows you to toggle
 * Piccolo's system-wide debugging flags (found in PDebug)
 * using the keyboard.
 * <p>
 * The mapping between keystrokes and debug flags is:
 * <code>
 * CTRL-Shift-B - Bounds
 * CTRL-Shift-C - Clear (set to false) all debug flags
 * CTRL-Shift-D - region management
 * CTRL-Shift-F - Full bounds
 * CTRL-Shift-M - used Memory
 * CTRL-Shift-P - Paint calls
 * CTRL-Shift-R - frame Rate
 * CTRL-Shift-T - Threads
 * </code>
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PDebugKeyHandler extends KeyAdapter {

    /**
     * Sole constructor.
     */
    public PDebugKeyHandler() {
        super();
    }

    /**
     * Handles key events.
     */
    public void keyReleased( KeyEvent event ) {
        if ( hasCorrectModifier( event ) ) {
            processKeyCode( event.getKeyCode() );
        }
    }
    
    /*
     * Checks for the Ctrl-Shift modifier.
     * 
     * @param event
     * @return true or false
     */
    private boolean hasCorrectModifier( KeyEvent event ) {
        int onMask = KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK;
        int offMask = KeyEvent.ALT_DOWN_MASK | KeyEvent.META_DOWN_MASK;
        return ( ( event.getModifiersEx() & ( onMask | offMask ) ) == onMask );
    }
    
    /*
     * Toggles the flag that corresponds to the keycode.
     * 
     * @param keyCode
     */
    private void processKeyCode( int keyCode ) {
        switch ( keyCode ) {
        case KeyEvent.VK_B:
            PDebug.debugBounds = !PDebug.debugBounds;
            break;
        case KeyEvent.VK_C:
            clearAllFlags();
            break;
        case KeyEvent.VK_D:
            PDebug.debugRegionManagement = !PDebug.debugRegionManagement;
            break;
        case KeyEvent.VK_F:
            PDebug.debugFullBounds = !PDebug.debugFullBounds;
            break;
        case KeyEvent.VK_M:
            PDebug.debugPrintUsedMemory = !PDebug.debugPrintUsedMemory;
            break;
        case KeyEvent.VK_P:
            PDebug.debugPaintCalls = !PDebug.debugPaintCalls;
            break;
        case KeyEvent.VK_R:
            PDebug.debugPrintFrameRate = !PDebug.debugPrintFrameRate;
            break;
        case KeyEvent.VK_T:
            PDebug.debugThreads = !PDebug.debugThreads;
            break;
        }
    }
    
    /**
     * Clears all flags.
     */
    private void clearAllFlags() {
        boolean value = false;
        PDebug.debugBounds = value;
        PDebug.debugFullBounds = value;
        PDebug.debugPaintCalls = value;
        PDebug.debugPrintFrameRate = value;
        PDebug.debugPrintUsedMemory = value;
        PDebug.debugRegionManagement = value;
        PDebug.debugThreads = value;
    }
}
