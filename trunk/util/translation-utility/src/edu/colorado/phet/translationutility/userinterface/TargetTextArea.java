// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.translationutility.userinterface;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;


/**
 * TargetTextArea contains a string in the target language, associated with a key.
 * Target strings appear in the right column of the interface.
 * They are searchable and editable.
 * Pressing tab or shift-tab moves focus forward or backward.
 */
/* package private */ class TargetTextArea extends TUTextArea {
    
    private final String key;
    private String savedValue;

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
    
    public TargetTextArea( String key, String value ) {
        super( value );
        
        this.key = key;
        this.savedValue = value;
        
        setEditable( true );
        
        // tab or shift-tab will move you to the next or previous text field
        getInputMap( JComponent.WHEN_FOCUSED ).put( KeyStroke.getKeyStroke( "TAB" ), NEXT_FOCUS_ACTION.getValue( Action.NAME ) );
        getInputMap( JComponent.WHEN_FOCUSED ).put( KeyStroke.getKeyStroke( "shift TAB" ), PREVIOUS_FOCUS_ACTION.getValue( Action.NAME ) );
        getActionMap().put( NEXT_FOCUS_ACTION.getValue( Action.NAME ), NEXT_FOCUS_ACTION );
        getActionMap().put( PREVIOUS_FOCUS_ACTION.getValue( Action.NAME ), PREVIOUS_FOCUS_ACTION );
        
        // ensure that the text field is visible when doing tab traversal
        addFocusListener( new FocusAdapter() {
            public void focusGained( FocusEvent e ) {
                TargetTextArea.this.scrollRectToVisible( TargetTextArea.this.getBounds() );
            }
        });
    }
    
    public String getKey() {
        return key;
    }
    
    public boolean isDirty() {
        boolean dirty = false;
        String value = getText();
        if ( savedValue == null || savedValue.length() == 0 ) {
            dirty = ( value != null && value.length() != 0 );
        }
        else {
            dirty = !savedValue.equals( value );
        }
        return dirty;
    }
    
    public void markSaved() {
        savedValue = getText();
    }
}
