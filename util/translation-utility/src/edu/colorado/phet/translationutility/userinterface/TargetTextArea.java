package edu.colorado.phet.translationutility.userinterface;

import java.awt.Component;
import java.awt.event.ActionEvent;

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
public class TargetTextArea extends TUTextArea {

    private final String _key;

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
        
        _key = key;
        
        setEditable( true );
        
        // tab or shift-tab will move you to the next or previous text field
        getInputMap( JComponent.WHEN_FOCUSED ).put( KeyStroke.getKeyStroke( "TAB" ), NEXT_FOCUS_ACTION.getValue( Action.NAME ) );
        getInputMap( JComponent.WHEN_FOCUSED ).put( KeyStroke.getKeyStroke( "shift TAB" ), PREVIOUS_FOCUS_ACTION.getValue( Action.NAME ) );
        getActionMap().put( NEXT_FOCUS_ACTION.getValue( Action.NAME ), NEXT_FOCUS_ACTION );
        getActionMap().put( PREVIOUS_FOCUS_ACTION.getValue( Action.NAME ), PREVIOUS_FOCUS_ACTION );
    }

    public String getKey() {
        return _key;
    }
}
