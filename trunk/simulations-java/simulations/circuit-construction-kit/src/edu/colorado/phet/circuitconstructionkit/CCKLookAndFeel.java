package edu.colorado.phet.circuitconstructionkit;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;

public class CCKLookAndFeel {
    //    public static final Color COPPER = new Color( Integer.parseInt( "D98719", 16 ) );//new Color(214, 18, 34);
    public static final Color COPPER = new Color( 217, 135, 25 );

    private static Font font;
    public static final Color HIGHLIGHT_COLOR = Color.yellow;
    public static final Color toolboxColor = new Color( 241, 241, 241 );
    public static final double HIGHLIGHT_SCALE = 1.5;

    public static Font getFont() {
        if ( font == null ) {
            init();
        }
        return font;
    }

    private static void init() {
        Font font1280 = new PhetDefaultFont( Font.PLAIN, 16 );
        Font font1040 = new PhetDefaultFont( Font.PLAIN, 9 );
        Font font800 = new PhetDefaultFont( Font.PLAIN, 6 );

        Font uifont = font1040;
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
//        System.out.println( "d = " + d );
        if ( d.width > 1024 ) {
            uifont = font1280;
//            System.out.println( "Chose font for width> 1280" );
        }
        else if ( d.width <= 800 ) {
            uifont = font800;
//            System.out.println( "Chose font for <=800" );
        }
        else {
//            System.out.println( "Chose font for width between 1024" );
        }
        font = uifont;
    }

}