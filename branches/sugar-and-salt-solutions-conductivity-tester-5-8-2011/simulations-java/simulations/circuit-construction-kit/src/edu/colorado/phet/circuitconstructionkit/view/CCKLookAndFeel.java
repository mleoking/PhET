// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.view;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

import java.awt.*;

public class CCKLookAndFeel {
    public static final Color COPPER = new Color(217, 135, 25);

    private static Font font;
    public static final Color HIGHLIGHT_COLOR = Color.yellow;
    public static final Color toolboxColor = new Color(241, 241, 241);
    public static final double HIGHLIGHT_SCALE = 1.5;

    public static Font getFont() {
        if (font == null) {
            init();
        }
        return font;
    }

    private static void init() {
        Font font1280 = new PhetFont(Font.PLAIN, 16);
        Font font1040 = new PhetFont(Font.PLAIN, 9);
        Font font800 = new PhetFont(Font.PLAIN, 6);

        Font uifont = font1040;
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        if (d.width > 1024) {
            uifont = font1280;
        } else if (d.width <= 800) {
            uifont = font800;
        } else {
        }
        font = uifont;
    }

}