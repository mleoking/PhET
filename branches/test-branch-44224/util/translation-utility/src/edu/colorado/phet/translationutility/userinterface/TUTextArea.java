package edu.colorado.phet.translationutility.userinterface;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.border.Border;

/**
 * Base class for text areas in the main translation panel.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class TUTextArea extends JTextArea {
    
    private static final Color SELECTION_COLOR = Color.GREEN;
    private static final int COLUMNS = 20;
    private static final Border BORDER = BorderFactory.createCompoundBorder( 
            /* outside */ BorderFactory.createLineBorder( Color.BLACK, 1 ), 
            /* inside */ BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) );
    
    public TUTextArea( String value ) {
        super( value );
        setColumns( COLUMNS );
        setLineWrap( true );
        setWrapStyleWord( true );
        setFocusable( true ); // must be true for Find selection to work
        setBorder( BORDER );
        setSelectionColor( SELECTION_COLOR );
    }
}