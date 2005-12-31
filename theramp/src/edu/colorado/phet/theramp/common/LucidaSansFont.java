package edu.colorado.phet.theramp.common;

import java.awt.*;

/**
 * We provide explicit support for the Lucida Sans font because it is guaranteed to be supported on all JREs.
 */

public class LucidaSansFont extends Font {
    /**
     * Constructs a LucidaSansFont with a specified point size
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
        super( "Lucida Sans", ( bold ? Font.BOLD : Font.PLAIN ) | ( italics ? Font.ITALIC : Font.PLAIN ), size );
    }
}
