/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view;

import smooth.basic.SmoothTitledBorder;
import smooth.windows.SmoothLookAndFeel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;
import java.awt.*;
import java.util.ArrayList;

/**
 * PhetLookAndFeel
 * <p/>
 * Sample usage:
 * <code>
 * //Choose the default look and feel for your system, adding text antialias for windows.
 * //This must be done early in the application so no components get constructed with the wrong UI.
 * PhetLookAndFeel.setLookAndFeel();
 * //Create the usual PhetLookAndFeel (whatever we deem that to be)
 * PhetLookAndFeel lookAndFeel = new PhetLookAndFeel();
 * //customize it here for your own application.
 * lookAndFeel.setBackgroundColor( Color.blue );
 * lookAndFeel.setForegroundColor( Color.red );
 * lookAndFeel.setFont( new Font( "Lucida Sans", Font.BOLD, 20 ) );
 * //Apply the total look and feel (the usual PhetLookAndFeel + your changes)
 * // to your system defaults.
 * lookAndFeel.apply();
 * </code>
 * * <p/>
 *
 * @author ?
 * @version $Revision$
 */
public class PhetLookAndFeel {

    public static final ScreenSizeHandler screenSizeHandler = new ScreenSizeHandler();

    private Font font;
    private Color foregroundColor;
    private Color backgroundColor;

    private static final String[] types = new String[]{
        "Button", "MenuItem", "Panel", "Dialog",
        "CheckBox", "RadioButton", "ComboBox",
        "Menu", "MenuItem", "MenuBar",
        "Slider", "CheckBoxMenuItem", "RadioButtonMenuItem",
        "TextField", "TextArea", "Spinner", "Label", "TextPane"
    };

    public PhetLookAndFeel() {
        font = new Font( "Lucida Sans", Font.PLAIN, screenSizeHandler.getFontSize() );
        foregroundColor = Color.black;
        backgroundColor = new Color( 200, 240, 200 );
    }

    public Font getFont() {
        return font;
    }

    public void setFont( Font font ) {
        this.font = font;
    }

    public Color getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor( Color foregroundColor ) {
        this.foregroundColor = foregroundColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor( Color backgroundColor ) {
        this.backgroundColor = backgroundColor;
    }

    private Object[] constructDefaults() {
        ColorUIResource background = new ColorUIResource( backgroundColor );
        ColorUIResource foreground = new ColorUIResource( foregroundColor );
        FontUIResource fontResource = new FontUIResource( font );
        FontUIResource borderFont = new FontUIResource( new Font( "Lucida Sans", Font.ITALIC, font.getSize() ) );

        InsetsUIResource insets = new InsetsUIResource( 2, 2, 2, 2 );
        ArrayList def = new ArrayList();
        for( int i = 0; i < types.length; i++ ) {
            String type = types[i];
            add( def, type, "font", fontResource );
            add( def, type, "foreground", foreground );
            add( def, type, "background", background );
            add( def, type, "margin", insets );
        }
        add( def, "TitledBorder", "font", borderFont );
        add( def, "TextField", "background", new ColorUIResource( Color.white ) );
        return def.toArray();
    }

    private void add( ArrayList defaults, String type, String property, Object resource ) {
        defaults.add( type + "." + property );
        defaults.add( resource );
    }

    /**
     * Use this to simply modify an existing Look and Feel defaults.
     */
    public void putDefaults( UIDefaults table ) {
        Object[] defaults = constructDefaults();
        table.putDefaults( defaults );
    }

    /**
     * Use this to simply modify the existing existing Look and Feel.
     */
    public void apply() {
        UIDefaults defaults = UIManager.getDefaults();
        putDefaults( defaults );
    }

    public static Border createSmoothBorder( String s ) {
        return new SmoothTitledBorder( s );
    }

    public static void setLookAndFeel() {
        String os = "";
        try {
            os = System.getProperty( "os.name" ).toLowerCase();
        }
        catch( Throwable t ) {
            t.printStackTrace();
        }

        if( os.indexOf( "windows" ) >= 0 ) {
            try {
                UIManager.setLookAndFeel( new SmoothLookAndFeel() );
            }
            catch( UnsupportedLookAndFeelException e ) {
                e.printStackTrace();
            }
        }
        else {
            try {
                UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
            }
            catch( ClassNotFoundException e ) {
                e.printStackTrace();
            }
            catch( InstantiationException e ) {
                e.printStackTrace();
            }
            catch( IllegalAccessException e ) {
                e.printStackTrace();
            }
            catch( UnsupportedLookAndFeelException e ) {
                e.printStackTrace();
            }
        }
    }

    private static class ScreenSizeHandler {
        private int fontSize;

        public ScreenSizeHandler() {
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            System.out.println( "d = " + d );
            if( d.width > 1024 ) {
                fontSize = 16;
            }
            else if( d.width <= 800 ) {
                fontSize = 8;
            }
            else {
                fontSize = 10;
            }
        }

        public int getFontSize() {
            return fontSize;
        }
    }
}