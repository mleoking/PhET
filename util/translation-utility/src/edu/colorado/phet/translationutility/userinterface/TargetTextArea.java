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
    
    private final String key;
    private final MessageFormatValidator messageFormatValidator;
    private final HTMLValidator htmlValidator;
    private final ArrayList<ValidationErrorListener> listeners;

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
        listeners = new ArrayList<ValidationErrorListener>();
        
        setEditable( true );
        
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
                setBackground( Color.WHITE );
            }
        } );
        
        messageFormatValidator = new MessageFormatValidator( sourceValue );
        htmlValidator = new HTMLValidator( sourceValue );
    }

    public String getKey() {
        return key;
    }
    
    /**
     * Validates the text area's value, comparing it to the source value.
     * Notifies interested listeners if there are validation errors.
     */
    public void validateValue() {

        // validate
        String targetString = getText();
        ArrayList<String> missingPlaceholders = messageFormatValidator.validate( targetString );
        ArrayList<String> missingTags = htmlValidator.validate( targetString );

        // report errors
        if ( missingPlaceholders != null || missingTags != null ) {
            setBackground( Color.RED );
            fireValidationError( key, missingPlaceholders, missingTags );
        }
    }
    
    /**
     * Interface for notifying about validation errors.
     */
    public interface ValidationErrorListener {
        public void validationError( String key, ArrayList<String> missingPlaceholders, ArrayList<String> missingTags );
    }
    
    public void addValidationErrorListener( ValidationErrorListener listener ) {
        listeners.add( listener );
    }
    
    public void removeValidationErrorListener( ValidationErrorListener listener ) {
        listeners.remove( listener );
    }
    
    private void fireValidationError( String key, ArrayList<String> missingPlaceholders, ArrayList<String> missingTags ) {
        for ( ValidationErrorListener listener : listeners ) {
            listener.validationError( key, missingPlaceholders, missingTags );
        }
    }
}
