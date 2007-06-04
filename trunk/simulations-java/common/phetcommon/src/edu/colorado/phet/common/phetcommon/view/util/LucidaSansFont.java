/* Copyright 2007, University of Colorado */
package edu.colorado.phet.common.phetcommon.view.util;

import java.awt.Font;

/**
 * Simplified interface for using a platform-independent (JRE bundled) font.
 */
public class LucidaSansFont extends Font {
    
    private static final String FONT_NAME = "Lucida Sans";
    
    /**
     * This constructor is more similar to the Font constructor, 
     * but supplies the font name.
     * 
     * @param style
     * @param size
     */
    public LucidaSansFont( int style, int size ) {
        super( FONT_NAME, style, size );
    }
    
    /**
     * Constructs a LucidaSansFont with a specified point size.
     *
     * @param size the size of the font.
     */
    public LucidaSansFont( int size ) {
        this( size, false );
    }

    /**
     * Constructs a LucidaSansFont with the specified font size, and whether it is bold.
     *
     * @param size the font size
     * @param bold whether it is bold.
     */
    public LucidaSansFont( int size, boolean bold ) {
        this( size, bold, false );
    }

    /**
     * Constructs a Lucida Sans font with the specified size, and whether it is bold and/or italicized.
     *
     * @param size    the font size.
     * @param bold    whether it is bold
     * @param italics whether it is italicized.
     */
    public LucidaSansFont( int size, boolean bold, boolean italics ) {
        this( ( bold ? Font.BOLD : Font.PLAIN ) | ( italics ? Font.ITALIC : Font.PLAIN ), size );
    }
}
