package edu.colorado.phet.cck;

import edu.colorado.phet.common.phetcommon.view.util.FontJA;

import java.awt.*;

/**
 * Author: Sam Reid
 * Aug 6, 2007, 6:55:20 PM
 */
public class CCKFontProvider {
    public static Font getFont( String name, int style, int size ) {
        return new Font( FontJA.getFontName( name ), style, size );
    }

    public static Font getFont( Font titleFont ) {
        return getFont( titleFont.getName(), titleFont.getStyle(), titleFont.getSize() );
    }
}
