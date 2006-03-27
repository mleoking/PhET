/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.util;

import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import edu.colorado.phet.common.view.util.SimStrings;


/**
 * DialogUtils is a collection of dialog utilities,
 * all of which support localization.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DialogUtils {
    
    /* Not intended for instantiation */
    private DialogUtils() {}
    
    /**
     * Shows a localized confirmation dialog.  The dialog title is "Confirm".
     * Returns the user's selection.  Clicking the dialog's closed 
     * button is equivalent to selecting "Cancel".
     * 
     * @param parentComponent
     * @param message
     * @param optionType one of the JOptionPane optionTypes
     * @return JOptionPane.YES_OPTION or JOptionPane.NO_OPTION
     */
    public static int showConfirmDialog( Component parentComponent, String message, int optionType ) {
        
        // Get localized strings
        String title = SimStrings.get( "title.confirm" );
        String yes = SimStrings.get( "choice.yes" );
        String no = SimStrings.get( "choice.no" );
        String ok = SimStrings.get( "choice.ok" );
        String cancel = SimStrings.get( "choice.cancel" );
        
        // Create an option pane
        JOptionPane pane = new JOptionPane( message, JOptionPane.QUESTION_MESSAGE, optionType );
        Object[] options = null;
        switch ( optionType ) {
        case JOptionPane.YES_NO_CANCEL_OPTION:
        case JOptionPane.DEFAULT_OPTION:
            options = new Object[] { yes, no, cancel };
            pane.setOptions( options );
            pane.setInitialValue( cancel );
            break;
        case JOptionPane.YES_NO_OPTION:
            options = new Object[] { yes, no };
            pane.setOptions( options );
            pane.setInitialValue( no );
            break;
        case JOptionPane.OK_CANCEL_OPTION:
            options = new Object[] { ok, cancel };
            pane.setOptions( options );
            pane.setInitialValue( cancel );
            break;
        default:
            throw new IllegalArgumentException( "unsupported optionType: " + optionType );
        }
        
        // Put the pane in a dialog
        JDialog dialog = pane.createDialog( parentComponent, title );
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
     * @param messageType one of the JOptionPane message types
     */
    public static void showMessageDialog( Component parentComponent, String message, String title, int messageType ) {
        
        // Get localized strings
        String ok = SimStrings.get( "choice.ok" );
        
        // Create an option pane
        JOptionPane pane = new JOptionPane( message, messageType );
        Object[] options = { ok };
        pane.setOptions( options );
        pane.setInitialValue( ok );
        
        // Put the pane in a dialog
        JDialog dialog = pane.createDialog( parentComponent, title );
        dialog.show();
    }
}
