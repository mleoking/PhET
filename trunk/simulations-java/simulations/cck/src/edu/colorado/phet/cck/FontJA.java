package edu.colorado.phet.cck;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Author: Sam Reid
 * Aug 6, 2007, 1:51:00 PM
 */
public class FontJA {
    public static void setupJAFonts() {
        Font font = getPreferredJAFont();
        if( font == null ) {
            JOptionPane.showMessageDialog( null, "Couldn't find a font for Japanese." );
        }
        System.out.println( "font = " + font );
    }

    public static Font getPreferredJAFont() {
        String[] preferredJAFonts = new String[]{"MS Mincho", "MS Gothic", "Osaka"};
        for( int i = 0; i < preferredJAFonts.length; i++ ) {
            String preferredJAFont = preferredJAFonts[i];
            ArrayList fonts = new ArrayList( Arrays.asList( GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts() ) );
            for( int k = 0; k < fonts.size(); k++ ) {
                Font o = (Font)fonts.get( k );
                if( o.getName().equals( preferredJAFont ) ) {
                    return o;
                }
            }
        }
        return null;
    }

    public static boolean isJapaneseLocale() {
        return PhetResources.readLocale().getLanguage().equalsIgnoreCase( "ja" );
    }

    public static String getFontName( String defaultValue ) {
        if (isJapaneseLocale()){
            return getPreferredJAFont().getName();
        }else{
            return defaultValue;
        }
    }
}
