/* Copyright 2008, University of Colorado */

package edu.colorado.phet.common.phetcommon.updates.dialogs;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.MessageFormat;

import javax.swing.JDialog;
import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.dialogs.ErrorDialog;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * This error dialog indicates that something went wrong with an update.
 * It includes a summary message and "Details" button to get more info.
 * The "Details" button opens another dialog that shows a stack trace.
 */
public class UpdateErrorDialog extends ErrorDialog {
    
    private static final String ERROR_MESSAGE = PhetCommonResources.getString( "Common.updates.errorMessage" );

    public UpdateErrorDialog( Frame owner, final Exception exception ) {
        super( owner, getErrorMessage(), exception );
        SwingUtils.centerDialogInParent( this );
    }
    
    protected static String getErrorMessage() {
        Object[] args = { HTMLUtils.getPhetHomeHref() };
        String message = MessageFormat.format( ERROR_MESSAGE, args );
        return message;
    }
    
    // test
    public static void main( String[] args ) {
        // dialog must have an owner if you want cursor to change over hyperlinks
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
        JDialog dialog = new UpdateErrorDialog( frame, new IOException() );
        dialog.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                System.exit( 0 );
            }
            public void windowClosed( WindowEvent e ) {
                System.exit( 0 );
            }
        } );
        dialog.setVisible( true );
    }
}
