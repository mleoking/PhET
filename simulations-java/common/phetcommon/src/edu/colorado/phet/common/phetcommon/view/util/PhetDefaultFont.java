/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.common.phetcommon.view.util;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * PhetDefaultFont provides an interface for instantiating the default font used in PhET simulations.
 */
public class PhetDefaultFont extends Font {
    
    // we'll use this font if we have no font preference, or if no preferred font is installed
    private static final Font FALLBACK_FONT = new Font( "LucidaSans", Font.PLAIN, 12 );
    
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
        String languageCode = PhetResources.readLocale().getLanguage().toLowerCase();
        String[] preferredFonts = PhetCommonResources.getPreferredFontNames( languageCode );
        if ( preferredFonts != null ) {
            defaultFont = getPreferredFont( preferredFonts, FALLBACK_FONT );
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
        
        Font preferredFont = null;

        for ( int i = 0; preferredFont == null && i < preferredFontNames.length; i++ ) {
            String preferredFontName = preferredFontNames[i];
            ArrayList fonts = new ArrayList( Arrays.asList( GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts() ) );
//            System.out.println( "PhetDefaultFonts.getPreferredFont fonts=" + fonts );
            for ( int k = 0; preferredFont == null && k < fonts.size(); k++ ) {
                Font o = (Font) fonts.get( k );
                if ( o.getName().equals( preferredFontName ) ) {
                    preferredFont = o.deriveFont( defaultFont.getStyle(), defaultFont.getSize2D() );
                }
            }
        }
        
        if ( preferredFont == null ) {
            preferredFont = defaultFont;
        }

        return preferredFont;
    }
}
