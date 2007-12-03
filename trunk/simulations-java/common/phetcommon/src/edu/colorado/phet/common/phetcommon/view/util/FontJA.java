package edu.colorado.phet.common.phetcommon.view.util;

import java.awt.*;
import java.util.Locale;
import javax.swing.*;

/**
 * Provides support for finding and returning Japanese fonts.
 */
public class FontJA {
    public static void setupJAFonts() {
        Font font = PhetDefaultFont.getPreferredJAFont();
        if ( font == null ) {
            JOptionPane.showMessageDialog( null, "Couldn't find a font for Japanese." );
        }
        System.out.println( "font = " + font );
    }

    public static String getFontName( String defaultValue ) {
        if ( PhetDefaultFont.isJapaneseLocale() ) {
            return PhetDefaultFont.getPreferredJAFont().getName();
        }
        else {
            return defaultValue;
        }
    }

    public static Font getFont( String name, int style, int size ) {
        return new Font( FontJA.getFontName( name ), style, size );
    }

    public static Font getFont( Font titleFont ) {
        return getFont( titleFont.getName(), titleFont.getStyle(), titleFont.getSize() );
    }

    public static void main( String[] args ) {
        Locale.setDefault( new Locale( "ja" ) );
        String name = "\u9285\u7DDA\u3092\u53D6\u308A\u51FA\u3059\u3002";
        System.out.println( "name = " + name );
        JFrame frame = new JFrame();

        JButton contentPane = new JButton( name );
        contentPane.setFont( getFont( "MS PGothic", Font.PLAIN, 10 ) );

        frame.setContentPane( contentPane );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocation( Toolkit.getDefaultToolkit().getScreenSize().width / 2 - frame.getWidth() / 2,
                           Toolkit.getDefaultToolkit().getScreenSize().height / 2 - frame.getHeight() / 2 );
        frame.show();
    }
}
