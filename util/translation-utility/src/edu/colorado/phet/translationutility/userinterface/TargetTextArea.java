package edu.colorado.phet.translationutility.userinterface;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import edu.colorado.phet.translationutility.util.HTMLValidator;
import edu.colorado.phet.translationutility.util.MessageFormatValidator;


/**
 * TargetTextArea contains a string in the target language, associated with a key.
 * Target strings appear in the right column of the interface.
 * They are searchable and editable.
 * Pressing tab or shift-tab moves focus forward or backward.
 */
public class TargetTextArea extends TUTextArea {
    
    private static final Color OK_COLOR = Color.WHITE;
    private static final Color INVALID_COLOR = Color.RED;
    
    private final String key;
    private final MessageFormatValidator messageFormatValidator;
    private final HTMLValidator htmlValidator;
    private String errorMessage;
    private boolean isValid;

    public static final Action NEXT_FOCUS_ACTION = new AbstractAction( "Move Focus Forwards" ) {
        public void actionPerformed( ActionEvent evt ) {
            ( (Component) evt.getSource() ).transferFocus();
        }
    };
    
    public static final Action PREVIOUS_FOCUS_ACTION = new AbstractAction( "Move Focus Backwards" ) {
        public void actionPerformed( ActionEvent evt ) {
            ( (Component) evt.getSource() ).transferFocusBackward();
        }
    };
    
    public TargetTextArea( String key, String sourceValue, String value ) {
        super( value );
        
        this.key = key;
        
        setEditable( true );
        setBackground( OK_COLOR );
        
        // tab or shift-tab will move you to the next or previous text field
        getInputMap( JComponent.WHEN_FOCUSED ).put( KeyStroke.getKeyStroke( "TAB" ), NEXT_FOCUS_ACTION.getValue( Action.NAME ) );
        getInputMap( JComponent.WHEN_FOCUSED ).put( KeyStroke.getKeyStroke( "shift TAB" ), PREVIOUS_FOCUS_ACTION.getValue( Action.NAME ) );
        getActionMap().put( NEXT_FOCUS_ACTION.getValue( Action.NAME ), NEXT_FOCUS_ACTION );
        getActionMap().put( PREVIOUS_FOCUS_ACTION.getValue( Action.NAME ), PREVIOUS_FOCUS_ACTION );
        
        addFocusListener( new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                validateValue();
            }
        });
        
        addKeyListener( new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                clearError();
            }
        } );
        
        messageFormatValidator = new MessageFormatValidator( sourceValue );
        htmlValidator = new HTMLValidator( sourceValue );
        validateValue();
    }
    
    private void clearError() {
        isValid = true;
        setBackground( OK_COLOR );
        errorMessage = null;
    }
    
    public boolean isValid() {
        validateValue();
        return isValid;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }

    public String getKey() {
        return key;
    }
    
    /**
     * Validates the text area's value, comparing it to the source value.
     * Notifies interested listeners if there are validation errors.
     */
    public void validateValue() {
        
        clearError();

        // validate
        String targetString = getText();
        ArrayList<String> missingPlaceholders = messageFormatValidator.validate( targetString );
        ArrayList<String> missingTags = htmlValidator.validate( targetString );

        // report errors
        if ( missingPlaceholders != null || missingTags != null ) {
            isValid = false;
            setBackground( INVALID_COLOR );
            errorMessage = createErrorMessage( missingPlaceholders, missingTags );
        }
    }
    
    private String createErrorMessage( ArrayList<String> missingPlaceholders, ArrayList<String> missingTags ) {
        
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

        return message;
        
    }
}
