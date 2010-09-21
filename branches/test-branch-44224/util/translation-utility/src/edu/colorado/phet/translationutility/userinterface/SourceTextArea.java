package edu.colorado.phet.translationutility.userinterface;

import java.awt.Color;

import javax.swing.JPanel;

/**
 * SourceTextArea contain a string in the source language.
 * Source strings appear in the middle column of the interface.
 * They are searchable but not editable.
 */
public class SourceTextArea extends TUTextArea {

    private static final Color SOURCE_BACKGROUND = new JPanel().getBackground();
    
    public SourceTextArea( String value ) {
        super( value );
        setEditable( false );
        setBackground( SOURCE_BACKGROUND );
    }
}
