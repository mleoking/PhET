/* Copyright 2007, University of Colorado */
package edu.colorado.phet.common.phetcommon.view.util;

import javax.swing.*;
import java.awt.Font;

/**
 * Simplified interface for instantiating the default font used in PhET simulations.
 */
public class PhetDefaultFont extends Font {
    
    private static final Font REFERENCE_FONT = new JLabel().getFont();
    private static final String FONT_NAME = REFERENCE_FONT.getFontName();

    public PhetDefaultFont() {
        this( REFERENCE_FONT.getSize() );
    }
    /**
     * Constructs a PhetDefaultFont with a specified point size.
     *
     * @param size the size of the font.
     */
    public PhetDefaultFont( int size ) {
        this( Font.PLAIN, size);
    }
    
    /**
     * This constructor is more similar to the Font constructor, 
     * but supplies the font name.
     * 
     * @param style
     * @param size
     */
    public PhetDefaultFont( int style, int size ) {
        super( FONT_NAME, style, size );
    }

    /**
     * Constructs a PhetDefaultFont with the specified font size, and whether it is bold.
     *
     * @param size the font size
     * @param bold whether it is bold.
     */
    public PhetDefaultFont( int size, boolean bold ) {
        this( size, bold, false );
    }

    /**
     * Constructs a Lucida Sans font with the specified size, and whether it is bold and/or italicized.
     *
     * @param size    the font size.
     * @param bold    whether it is bold
     * @param italics whether it is italicized.
     */
    public PhetDefaultFont( int size, boolean bold, boolean italics ) {
        this( ( bold ? Font.BOLD : Font.PLAIN ) | ( italics ? Font.ITALIC : Font.PLAIN ), size );
    }

}
