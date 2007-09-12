package edu.colorado.phet.lasers;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * Created by IntelliJ IDEA.
* User: Sam
* Date: Sep 12, 2007
* Time: 7:57:03 AM
*/
class LandF extends MetalLookAndFeel {
    Color backgroundColor = new Color( 60, 80, 60 );
    Color buttonBackgroundColor = new Color( 60, 60, 100 );
    Color controlTextColor = new Color( 230, 230, 230 );
    Font controlFont = new Font( "SansSerif", Font.BOLD, 22 );
    static String[] controlTypes = new String[]{
            "Menu",
            "MenuItem",
            "RadioButton",
            "Button",
            "CheckBox",
            "Label"
    };

    public LandF( Color backgroundColor, Color buttonBackgroundColor, Color controlTextColor, Font controlFont ) {
        this.backgroundColor = backgroundColor;
        this.buttonBackgroundColor = buttonBackgroundColor;
        this.controlTextColor = controlTextColor;
        this.controlFont = controlFont;
    }

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
                , "MenuBar.background", background
                , "Slider.background", background
                , "RadioButton.background", background
                , "CheckBox.background", background
                , "OptionPane.background", background
                , "TabbedPane.background", background
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

        // Set the background color of the buttons is we are running Java version 1.4
        if( System.getProperty( "java.version" ).startsWith( "1.4" ) ) {
            table.putDefaults( new Object[]{
                    "Button.background", buttonBackground
            } );
        }
    }
}
