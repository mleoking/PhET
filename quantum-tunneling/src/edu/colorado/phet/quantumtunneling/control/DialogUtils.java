/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.control;

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
    private DialogUtils( String message ) {}
    
    /**
     * Shows a localized confirmation dialog with "Yes" and "No" options.
     * The default selection is "No".  The dialog title is "Confirm".
     * Returns the user's selection.  Clicking the dialog's closed 
     * button is equivalent to selecting "No".
     * 
     * @param parentComponent
     * @param message
     * @return JOptionPane.YES_OPTION or JOptionPane.NO_OPTION
     */
    public static int showConfirmDialog( Component parentComponent, String message ) {
        
        // Get localized strings
        String title = SimStrings.get( "title.confirm" );
        String yes = SimStrings.get( "choice.yes" );
        String no = SimStrings.get( "choice.no" );
        
        // Create an option pane
        JOptionPane pane = new JOptionPane( message, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION );
        Object[] options = { no, yes };
        pane.setOptions( options );
        pane.setInitialValue( no );
        
        // Put the pane in a dialog
        JDialog dialog = pane.createDialog( parentComponent, title );
        dialog.show();
        
        Object selection = pane.getValue();
        if ( selection == yes ) {
            return JOptionPane.YES_OPTION;
        }
        else {
            return JOptionPane.NO_OPTION;
        }
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
