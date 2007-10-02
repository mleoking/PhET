/* Copyright 2005-2007, University of Colorado */

package edu.colorado.phet.common.phetcommon.util;

import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;


/**
 * DialogUtils is a collection of dialog utilities, all of which support localization.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DialogUtils {

    /* Not intended for instantiation */
    private DialogUtils() {
    }

    /**
     * Shows a localized confirmation dialog.  The dialog title is "Confirm".
     * Returns the user's selection.  Clicking the dialog's closed
     * button is equivalent to selecting "Cancel".
     *
     * @param parentComponent
     * @param message
     * @param optionType      one of the JOptionPane optionTypes
     * @return JOptionPane.YES_OPTION or JOptionPane.NO_OPTION
     */
    public static int showConfirmDialog( Component parentComponent, String message, int optionType ) {

        // Get localized strings
        String title = PhetCommonResources.getInstance().getLocalizedString( "Common.title.confirm" );
        String yes = PhetCommonResources.getInstance().getLocalizedString( "Common.choice.yes" );
        String no = PhetCommonResources.getInstance().getLocalizedString( "Common.choice.no" );
        String ok = PhetCommonResources.getInstance().getLocalizedString( "Common.choice.ok" );
        String cancel = PhetCommonResources.getInstance().getLocalizedString( "Common.choice.cancel" );

        // Create an option pane
        JOptionPane pane = new JOptionPane( message, JOptionPane.QUESTION_MESSAGE, optionType );
        Object[] options = null;
        switch( optionType ) {
            case JOptionPane.YES_NO_CANCEL_OPTION:
            case JOptionPane.DEFAULT_OPTION:
                options = new Object[]{yes, no, cancel};
                pane.setOptions( options );
                pane.setInitialValue( cancel );
                break;
            case JOptionPane.YES_NO_OPTION:
                options = new Object[]{yes, no};
                pane.setOptions( options );
                pane.setInitialValue( no );
                break;
            case JOptionPane.OK_CANCEL_OPTION:
                options = new Object[]{ok, cancel};
                pane.setOptions( options );
                pane.setInitialValue( cancel );
                break;
            default:
                throw new IllegalArgumentException( "unsupported optionType: " + optionType );
        }

        // Put the pane in a dialog
        JDialog dialog = pane.createDialog( parentComponent, title );
        SwingUtils.centerDialogInParent( dialog );
        dialog.show();

        // Process the user's selection
        int result = JOptionPane.CANCEL_OPTION;
        Object selection = pane.getValue();
        if ( selection == yes ) {
            result = JOptionPane.YES_OPTION;
        }
        else if ( selection == ok ) {
            result = JOptionPane.OK_OPTION;
        }
        else if ( selection == no ) {
            result = JOptionPane.NO_OPTION;
        }
        else if ( selection == cancel ) {
            result = JOptionPane.CANCEL_OPTION;
        }
        return result;
    }

    /**
     * Shows a localized message dialog with "OK" option.
     *
     * @param parentComponent
     * @param message
     * @param title
     * @param messageType     one of the JOptionPane message types
     */
    public static void showMessageDialog( Component parentComponent, String message, String title, int messageType ) {

        // Get localized strings
        String ok = PhetCommonResources.getInstance().getLocalizedString( "Common.choice.ok" );

        // Create an option pane
        JOptionPane pane = new JOptionPane( message, messageType );
        Object[] options = {ok};
        pane.setOptions( options );
        pane.setInitialValue( ok );

        // Put the pane in a dialog
        JDialog dialog = pane.createDialog( parentComponent, title );
        SwingUtils.centerDialogInParent( dialog );
        dialog.show();
    }

    /*
    * Convenience methods, for various types of dialogs.
    */

    public static void showErrorDialog( Component parentComponent, String message, String title ) {
        showMessageDialog( parentComponent, message, title, JOptionPane.ERROR_MESSAGE );
    }

    public static void showInformationDialog( Component parentComponent, String message, String title ) {
        showMessageDialog( parentComponent, message, title, JOptionPane.INFORMATION_MESSAGE );
    }

    public static void showWarningDialog( Component parentComponent, String message, String title ) {
        showMessageDialog( parentComponent, message, title, JOptionPane.WARNING_MESSAGE );
    }

    public static void showQuestionDialog( Component parentComponent, String message, String title ) {
        showMessageDialog( parentComponent, message, title, JOptionPane.QUESTION_MESSAGE );
    }
}
