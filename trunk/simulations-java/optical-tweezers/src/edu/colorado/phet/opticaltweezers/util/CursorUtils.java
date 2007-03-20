/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.util;

import java.awt.Cursor;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.PhetFrame;

/**
 * CursorUtils is a collection of Cursor-related utilities.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CursorUtils {

    public static final Cursor DEFAULT_CURSOR = Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR );
    public static final Cursor WAIT_CURSOR = Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR );
    
    /* Not intended for instantiation */
    private CursorUtils() {}
    
    /**
     * Turns the wait cursor on and off.
     * 
     * @param enabled true or false
     */
    public static void setWaitCursorEnabled( boolean enabled ) {
        PhetFrame frame = PhetApplication.instance().getPhetFrame();
        if ( enabled ) {
            frame.setCursor( WAIT_CURSOR );
        }
        else {
            frame.setCursor( DEFAULT_CURSOR );
        }
    }
    
}
