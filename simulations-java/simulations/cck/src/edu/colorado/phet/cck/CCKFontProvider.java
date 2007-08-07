package edu.colorado.phet.cck;

import java.awt.*;

/**
 * Author: Sam Reid
 * Aug 6, 2007, 6:55:20 PM
 */
public class CCKFontProvider {
    public static Font getFont( String name, int style, int size ) {
        return new Font( FontJA.getFontName( name ), style, size );
    }
}
