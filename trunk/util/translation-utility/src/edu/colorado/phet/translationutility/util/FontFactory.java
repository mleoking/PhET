/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility.util;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import javax.swing.JLabel;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.translationutility.TUResources;

/**
 * FontFactory is a factory used to create fonts.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FontFactory {
    
    private static final Font DEFAULT_FONT = new JLabel().getFont();

    /* not intended for instantiation */
    private FontFactory() {}
    
    /**
     * Creates a Font for a specified locale.
     * <p>
     * NOTE: This is probably not necessary on Macintosh, but doesn't hurt.
     * 
     * @param locale
     * @return Font
     */
    public static Font createFont( Locale locale ) {
        return createFont( locale, DEFAULT_FONT.getStyle(), DEFAULT_FONT.getSize() );
    }
    
    /**
     * Creates a Font for a specified locale, style and size.
     * 
     * @param locale
     * @param style
     * @param size
     * @return Font
     */
    public static Font createFont( Locale locale, int style, int size ) {
        Font font = new PhetFont( style, size );
        String[] preferredFontNames = TUResources.getPreferredFontNames( locale );
        if ( preferredFontNames != null ) {
            for ( int i = 0; i < preferredFontNames.length; i++ ) {
                String fontName = preferredFontNames[i];
                ArrayList fonts = new ArrayList( Arrays.asList( GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts() ) );
                for ( int j = 0; j < fonts.size(); j++ ) {
                    Font o = (Font) fonts.get( j );
                    if ( o.getName().equals( fontName ) ) {
                        font = new Font( o.getName(), style, size );
                    }
                }
            }
        }
        assert( font != null );
        return font;
    }
}
