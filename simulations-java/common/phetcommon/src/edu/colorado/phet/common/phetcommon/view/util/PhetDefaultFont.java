/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.common.phetcommon.view.util;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.*;

/**
 * PhetDefaultFont provides an interface for instantiating the default font used in PhET simulations.
 */
public class PhetDefaultFont extends Font {
    
    // preferred physical font names for various ISO language codes
    //TODO: read these from a properties file
    private static final String[] JA_PREFERRED_FONTS = { "MS Mincho", "MS Gothic", "Osaka" }; // Japanese
    private static final String[] AR_PREFERRED_FONTS = { "Lucida Sans Regular" }; // Arabic
    
    // we'll use this font if we have no font preference, or if no preferred font is installed
    private static final Font FALLBACK_FONT = new JLabel().getFont();
    
    // the font used to create instances of PhetDefaultFont
    private static final Font DEFAULT_FONT = createDefaultFont();

    /**
     * Constructs a PhetDefaultFont with a default style and point size.
     */
    public PhetDefaultFont() {
        this( getDefaultFontSize() );
    }
    
    /**
     * Constructs a PhetDefaultFont with a default style and specified point size.
     *
     * @param size the size of the font.
     */
    public PhetDefaultFont( int size ) {
        this( getDefaultFontStyle(), size );
    }

    /**
     * Constructs a PhetDefaultFont with a specified style and point size.
     *
     * @param style
     * @param size
     */
    public PhetDefaultFont( int style, int size ) {
        super( getDefaultFontName(), style, size );
    }

    /**
     * Constructs a PhetDefaultFont with a specified font size, and whether it is bold.
     *
     * @param size the font size
     * @param bold whether it is bold.
     */
    public PhetDefaultFont( int size, boolean bold ) {
        this( size, bold, false );
    }

    /**
     * Constructs a PhetDefaultFont font with a specified size, and whether it is bold and/or italicized.
     *
     * @param size    the font size.
     * @param bold    whether it is bold
     * @param italics whether it is italicized.
     */
    public PhetDefaultFont( int size, boolean bold, boolean italics ) {
        this( ( bold ? Font.BOLD : Font.PLAIN ) | ( italics ? Font.ITALIC : Font.PLAIN ), size );
    }

    /**
     * Returns the font face name for the default font.
     *
     * @return the font face name for the default font
     */
    public static String getDefaultFontName() {
        return DEFAULT_FONT.getFontName();
    }

    /**
     * Returns the font size for the default font.
     *
     * @return the font size for the default font
     */
    public static int getDefaultFontSize() {
        return DEFAULT_FONT.getSize();
    }
    
    /**
     * Returns the font style for the default font.
     *
     * @return the font style for the default font
     */
    public static int getDefaultFontStyle() {
        return DEFAULT_FONT.getStyle();
    }

    /*
     * Creates the font that will be used to create all instances of PhetDefaultFont.
     */
    private static Font createDefaultFont() {

        Font defaultFont = FALLBACK_FONT;

        //TODO: read the list of preferred fonts for a local from a properties file
        if ( isLocale( "ja" ) ) {
            defaultFont = getPreferredFont( JA_PREFERRED_FONTS, FALLBACK_FONT );
        }
        else if ( isLocale( "ar" ) ) {
            defaultFont = getPreferredFont( AR_PREFERRED_FONTS, FALLBACK_FONT );
        }

//        System.out.println( "PhetDefaultFont.createDefaultFont defaultFont=" + defaultFont.toString() );
        return defaultFont;
    }
    
    /*
     * Looks through a list of preferred physical font names, ordered by decreasing preference.
     * The first match with a font installed on the system is returned.
     * If there is no match, then the defaultFont is returned.
     */
    private static Font getPreferredFont( String[] preferredFontNames, Font defaultFont ) {
        
        Font preferredFont = defaultFont;

        for ( int i = 0; i < preferredFontNames.length; i++ ) {
            String preferredFontName = preferredFontNames[i];
            ArrayList fonts = new ArrayList( Arrays.asList( GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts() ) );
//            System.out.println( "PhetDefaultFonts.getPreferredFont fonts=" + fonts );
            for ( int k = 0; k < fonts.size(); k++ ) {
                Font o = (Font) fonts.get( k );
                if ( o.getName().equals( preferredFontName ) ) {
                    preferredFont = o.deriveFont( preferredFont.getSize() );
                }
            }
        }

        return preferredFont;
    }

    /*
     * Determines if the locale matches a specified language code.
     */
    private static boolean isLocale( String language ) {
        return PhetResources.readLocale().getLanguage().equalsIgnoreCase( language );
    }
}
