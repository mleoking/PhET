package edu.colorado.phet.translationutility.userinterface;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.border.Border;

/**
 * Base classes for text area in the main translation panel.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class TUTextArea extends JTextArea {
    
    private static final int TEXT_AREA_COLUMNS = 20;
    private static final Border TEXT_AREA_BORDER = BorderFactory.createCompoundBorder( 
            /* outside */ BorderFactory.createLineBorder( Color.BLACK, 1 ), 
            /* inside */ BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) );
    private static final Color SELECTION_COLOR = Color.GREEN;
    
    public TUTextArea( String value ) {
        super( value );
        setColumns( TEXT_AREA_COLUMNS );
        setLineWrap( true );
        setWrapStyleWord( true );
        setFocusable( true ); // must be true for Find selection to work
        setBorder( TEXT_AREA_BORDER );
        setSelectionColor( SELECTION_COLOR );
    }
}