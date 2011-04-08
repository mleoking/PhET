// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.phetcommon.view.util;

import java.awt.*;
import java.util.Locale;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * PhetFont provides an interface for instantiating the default font used in PhET simulations.
 */
public class PhetFont extends Font {

    // We'll use this font if we have no font preference, or if no preferred font is installed
    // By deriving the font from a Swing component's font, we should get a platform-specific font 
    // that looks very nice compared to Java fonts, while still having control over the style and size.
    private static final Font FALLBACK_FONT = new JTextField().getFont().deriveFont( Font.PLAIN, 12f );

    // the font used to create instances of PhetFont
    private static final Font DEFAULT_FONT = createDefaultFont();

    /**
     * Constructs a PhetFont with a default style and point size.
     */
    public PhetFont() {
        this( getDefaultFontSize() );
    }

    /**
     * Constructs a PhetFont with a default style and specified point size.
     *
     * @param size the size of the font.
     */
    public PhetFont( int size ) {
        this( getDefaultFontStyle(), size );
    }

    /**
     * Constructs a PhetFont with a specified style and point size.
     *
     * @param style
     * @param size
     */
    public PhetFont( int style, int size ) {
        super( getDefaultFontName(), style, size );
    }

    /**
     * Constructs a PhetFont with a specified font size, and whether it is bold.
     *
     * @param size the font size
     * @param bold whether it is bold.
     */
    public PhetFont( int size, boolean bold ) {
        this( size, bold, false );
    }

    /**
     * Constructs a PhetFont font with a specified size, and whether it is bold and/or italicized.
     *
     * @param size    the font size.
     * @param bold    whether it is bold
     * @param italics whether it is italicized.
     */
    public PhetFont( int size, boolean bold, boolean italics ) {
        this( ( bold ? Font.BOLD : Font.PLAIN ) | ( italics ? Font.ITALIC : Font.PLAIN ), size );
    }

    /**
     * If this font is a preferred font, then it will have a different family name than the fallback font.
     * This assumes that the fallback font is not specified as a preferred font.
     * See #2104; other ways of solving this problem require more radical changes to PhetFont.
     *
     * @return
     */
    public boolean isPreferred() {
        return ( !getFamily().equals( FALLBACK_FONT.getFamily() ) );
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
     * Creates the font that will be used to create all instances of PhetFont.
     */
    private static Font createDefaultFont() {
        return getPreferredFont( PhetResources.readLocale() );
    }

    /**
     * Gets the preferred font for a specified locale.
     * <p/>
     * If the locale has a list of preferred fonts, look through that list
     * and return the first match with a font installed on the system.
     * If there is no match, then return the default font.
     */
    public static Font getPreferredFont( Locale locale, int style, int size ) {

        Font preferredFont = null;

        String[] preferredFontNames = PhetCommonResources.getPreferredFontNames( locale );
        if ( preferredFontNames != null ) {

            // this is expensive, so do it once.
            Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();

            // return the first preferred font found on the system
            for ( int i = 0; preferredFont == null && i < preferredFontNames.length; i++ ) {
                String preferredFontName = preferredFontNames[i];
                for ( int k = 0; preferredFont == null && k < fonts.length; k++ ) {
                    Font font = fonts[k];
                    if ( font.getFontName().equals( preferredFontName ) ) {
                        preferredFont = font.deriveFont( style, size );
//                        System.out.println( "PhetFont.getPreferredFont preferredFont=" + preferredFont );//XXX
                    }
                }
            }
        }

        // if no preferred font was found, return the fallback font
        if ( preferredFont == null ) {
            preferredFont = FALLBACK_FONT.deriveFont( style, size );
        }

        return preferredFont;
    }

    public static Font getPreferredFont( Locale locale ) {
        return getPreferredFont( locale, FALLBACK_FONT.getStyle(), FALLBACK_FONT.getSize() );
    }

    public static void main( String[] args ) {

        System.out.println( "Java " + System.getProperty( "java.version" ) + ", " +
                            System.getProperty( "os.name" ) + " " + System.getProperty( "os.version" ) );

        System.out.println( "Our current choice for FALLBACK_FONT..." );
        System.out.println( "> " + FALLBACK_FONT );

        System.out.println( "Alternatives we've tried..." );

        // Mac: creates a Java font that doesn't look good
        System.out.println( "> " + new Font( "Lucida Sans", Font.PLAIN, 12 ) );

        // Mac: creates an Apple-specific font that looks good, but is larger than Windows default font
        System.out.println( "> " + new JTextField().getFont() );

        // Mac: creates an Apple-specific font, and we have control over style and size
        System.out.println( "> " + new JTextField().getFont().deriveFont( Font.PLAIN, 12f ) );

        // Mac: creates a Java font that doesn't look good
        System.out.println( "> " + new Font( new JTextField().getFont().getName(), Font.PLAIN, 12 ) );
    }
}
