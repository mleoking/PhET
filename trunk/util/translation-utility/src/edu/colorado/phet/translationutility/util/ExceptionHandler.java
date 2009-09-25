/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility.util;

import javax.swing.JDialog;

import edu.colorado.phet.translationutility.TUStrings;

/**
 * ExceptionHandler handles exceptions, including how the user is notified.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExceptionHandler {
    
    private ExceptionHandler() {}

    public static void handleFatalException( Exception e ) {
        e.printStackTrace();
        JDialog dialog = new ExceptionDialog( null, TUStrings.FATAL_ERROR_TITLE, e );
        dialog.setVisible( true );
        System.exit( 1 ); // non-zero status to indicate abnormal termination
    }
    
    public static void handleNonFatalException( Exception e ) {
        e.printStackTrace();
        JDialog dialog = new ExceptionDialog( null, TUStrings.ERROR_TITLE, e );
        dialog.setVisible( true );
    }
}
