/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility.util;

import edu.colorado.phet.common.phetcommon.util.DialogUtils;
import edu.colorado.phet.translationutility.TUResources;

/**
 * ExceptionHandler handles exceptions, including how the user is notified.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExceptionHandler {
    
    private static final String FATAL_ERROR_DIALOG_TITLE = TUResources.getString( "title.fatalErrorDialog" );
    private static final String ERROR_DIALOG_TITLE = TUResources.getString( "title.errorDialog" );
    
    private ExceptionHandler() {}

    public static void handleFatalException( Exception e ) {
        e.printStackTrace();
        DialogUtils.showErrorDialog( null, e.getMessage(), FATAL_ERROR_DIALOG_TITLE );
        System.exit( 1 ); // non-zero status to indicate abnormal termination
    }
    
    public static void handleNonFatalException( Exception e ) {
        e.printStackTrace();
        DialogUtils.showErrorDialog( null, e.getMessage(), ERROR_DIALOG_TITLE );
    }
}
