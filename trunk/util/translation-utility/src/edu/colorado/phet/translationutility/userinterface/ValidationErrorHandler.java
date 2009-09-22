package edu.colorado.phet.translationutility.userinterface;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import edu.colorado.phet.translationutility.userinterface.TargetTextArea.ValidationErrorListener;

/**
 * Handles validation errors by displaying them in a dialog.
 * <p>
 * Since our validation is triggered by text fields losing focus, this handler
 * ignores additional errors while the dialog is open.  We need to do this because
 * opening a dialog changes focus, and we don't want to trigger validation (and possibly
 * a second dialog) on the text field that gained focus when our problem
 * text field lost focus.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ValidationErrorHandler implements ValidationErrorListener {
    
    private final Component parent;
    private boolean isHandlingError;

    public ValidationErrorHandler( Component parent ) {
        this.parent = parent;
        isHandlingError = false;
    }
    
    public void validationError( String key, ArrayList<String> missingPlaceholders, ArrayList<String> missingTags ) {

        if ( !isHandlingError ) {
            isHandlingError = true;

            String message = "Translation with key=" + key + " has errors:";

            if ( missingPlaceholders != null ) {
                message += "\n";
                message += "missing MessageFormat placeholders:";
                for ( String placeholder : missingPlaceholders ) {
                    message += " " + placeholder;
                }
            }

            if ( missingTags != null ) {
                message += "\n";
                message += "missing HTML tags:";
                for ( String tag : missingTags ) {
                    message += " " + tag;
                }
            }

            JOptionPane.showMessageDialog( parent, message, "Error", JOptionPane.ERROR_MESSAGE );
            isHandlingError = false;
        }
    }
    
    public boolean isHandlingError() {
        return isHandlingError;
    }

}
