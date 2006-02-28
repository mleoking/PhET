/**
 * Class: IdealGasLAF
 * Package: edu.colorado.phet.idealgas.view
 * Author: Another Guy
 * Date: Oct 14, 2004
 */
package edu.colorado.phet.idealgas.view;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class IdealGasLAF extends MetalLookAndFeel {
//    static Color BACKGROUND_COLOR = new Color( 60, 80, 60 );
//    static Color buttonBackgroundColor = new Color( 100, 120, 60 );
//    static Color controlTextColor = new Color( 230, 230, 230 );
//    static Font font = new Font( "SansSerif", Font.BOLD, 16 );
//
//    public IdealGasLAF() {
//        super( BACKGROUND_COLOR, buttonBackgroundColor, controlTextColor, font );
//    }
//    static private class LandF extends MetalLookAndFeel {
    Color backgroundColor = new Color( 200, 200, 220 );
//            Color BACKGROUND_COLOR = new Color( 160, 180, 160 );
    Color buttonBackgroundColor = new Color( 160, 160, 200 );
    Color controlTextColor = new Color( 0, 0, 0 );
    Font controlFont = new Font( "SansSerif", Font.BOLD, 12 );
    static String[] controlTypes = new String[]{
        "Menu",
        "MenuItem",
        "RadioButton",
        "Button",
        "CheckBox",
        "Label"
    };

    protected void initComponentDefaults( UIDefaults table ) {
        super.initComponentDefaults( table );
        ArrayList def = new ArrayList();
        ColorUIResource textColor = new ColorUIResource( controlTextColor );
        FontUIResource fuir = new FontUIResource( controlFont );
        for( int i = 0; i < controlTypes.length; i++ ) {
            String controlType = controlTypes[i];
            def.add( controlType + ".foreground" );
            def.add( textColor );
            def.add( controlType + ".font" );
            def.add( fuir );
        }
        ColorUIResource background = new ColorUIResource( backgroundColor );
        ColorUIResource buttonBackground = new ColorUIResource( buttonBackgroundColor );

        Object[] defaults = {
            "Panel.background", background
            , "Menu.background", background
            , "MenuItem.background", background
            , "MenuBar.background", background
            , "Slider.background", background
            , "RadioButton.background", background
            , "CheckBox.background", background
            , "Button.background", buttonBackground
        };
        def.addAll( Arrays.asList( defaults ) );
        table.putDefaults( def.toArray() );

        Font font = (Font)table.get( "Label.font" );
        Color color = (Color)table.get( "Label.foreground" );
        Object[] moreDefaults = {
            "TextField.font", font
            , "Spinner.font", font
            , "FormattedTextField.font", font
            , "TitledBorder.font", font
            , "TitledBorder.titleColor", color
        };
        table.putDefaults( moreDefaults );
    }
//        }
}


