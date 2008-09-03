/* Copyright 2005-2007, University of Colorado */

package edu.colorado.phet.common.phetcommon.util;

import java.awt.Component;

import javax.swing.JOptionPane;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;

/**
 * DialogUtils is a collection of convenience methods for showing JOptionPanes.  Localization is handled by PhetLookAndFeel.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @author Sam Reid
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
        return JOptionPane.showConfirmDialog( parentComponent, message,PhetCommonResources.getInstance().getLocalizedString( "Common.title.confirm" ),optionType);
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
        JOptionPane.showMessageDialog( parentComponent, message,title, messageType);
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

}
