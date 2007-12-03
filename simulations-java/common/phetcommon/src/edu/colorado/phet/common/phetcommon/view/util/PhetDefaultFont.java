/* Copyright 2007, University of Colorado */
package edu.colorado.phet.common.phetcommon.view.util;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.*;

/**
 * Simplified interface for instantiating the default font used in PhET simulations.
 */
public class PhetDefaultFont extends Font {
    private static final Font REFERENCE_FONT = getReferenceFont();
    private static final String FONT_NAME = REFERENCE_FONT.getFontName();
    public static final String LUCIDA_SANS = "Lucida Sans";

    public PhetDefaultFont() {
        this( getReferenceFont().getSize() );
    }

    private static Font getReferenceFont() {
        Font referenceFont = new JLabel().getFont();

        if (isJapaneseLocale()) {
            referenceFont = getPreferredJAFont();
        }
        
        return referenceFont;
    }

    private static String getPreferredFontName( String fontName ) {
        String[] names = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

        if (isJapaneseLocale()) {
             return REFERENCE_FONT.getFontName();
        }
        else if ( !Arrays.asList( names ).contains( fontName ) ) {
            return REFERENCE_FONT.getFontName();
        }

        return fontName;
    }

    public PhetDefaultFont( String fontName ) {
        this( fontName, REFERENCE_FONT.getStyle(), REFERENCE_FONT.getSize() );
    }

    public PhetDefaultFont( String fontName, int style, float size ) {
        super( getPreferredFontName( fontName ), style, (int)size );
    }

    /**
     * Constructs a PhetDefaultFont with a specified point size.
     *
     * @param size the size of the font.
     */
    public PhetDefaultFont( int size ) {
        this( Font.PLAIN, size );
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

    /**
     * Returns the font family name for the default font.
     *
     * @return the font family name for the default font
     */
    public static String getDefaultFontName() {
        return FONT_NAME;
    }

    /**
     * Returns the font size for the default font.
     *
     * @return the font size for the default font
     */
    public static int getDefaultFontSize() {
        return getReferenceFont().getSize();
    }

    public static Font getPreferredJAFont() {
        float defaultSize = new JLabel().getFont().getSize();

        String[] preferredJAFonts = new String[]{"MS Mincho", "MS Gothic", "Osaka"};
        for ( int i = 0; i < preferredJAFonts.length; i++ ) {
            String preferredJAFont = preferredJAFonts[i];
            ArrayList fonts = new ArrayList( Arrays.asList( GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts() ) );
            for ( int k = 0; k < fonts.size(); k++ ) {
                Font o = (Font) fonts.get( k );
                if ( o.getName().equals( preferredJAFont ) ) {
                    System.out.println( "Chose Font: " + o );
                    return o.deriveFont(defaultSize);
                }
            }
        }
        return new JLabel().getFont();
    }

    public static boolean isJapaneseLocale() {
        return PhetResources.readLocale().getLanguage().equalsIgnoreCase( "ja" );
    }
}
