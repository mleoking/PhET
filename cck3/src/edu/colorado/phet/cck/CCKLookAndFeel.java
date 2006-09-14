package edu.colorado.phet.cck;

import java.awt.*;

public class CCKLookAndFeel {
    private static Font font;

    public static Font getFont() {
        return font;
    }

    static {
        Font font1280 = new Font( "Lucida Sans", Font.PLAIN, 16 );
        Font font1040 = new Font( "Lucida Sans", Font.PLAIN, 9 );
        Font font800 = new Font( "Lucida Sans", Font.PLAIN, 6 );

        Font uifont = font1040;
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
//        System.out.println( "d = " + d );
        if( d.width > 1024 ) {
            uifont = font1280;
//            System.out.println( "Chose font for width> 1280" );
        }
        else if( d.width <= 800 ) {
            uifont = font800;
//            System.out.println( "Chose font for <=800" );
        }
        else {
//            System.out.println( "Chose font for width between 1024" );
        }
        font = uifont;

    }

}