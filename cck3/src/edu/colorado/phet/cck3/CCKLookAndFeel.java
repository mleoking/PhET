package edu.colorado.phet.cck3;

import smooth.metal.SmoothLookAndFeel;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;
import java.awt.*;
import java.util.ArrayList;

public class CCKLookAndFeel extends SmoothLookAndFeel {
    private static Font font;
    private static Color foregroundColor;
    private static Color backgroundColor;

    private String[] types = new String[]{
        "Button", "MenuItem", "Panel", "Dialog",
        "CheckBox", "RadioButton", "ComboBox",
        "Menu", "MenuItem", "MenuBar",
        "Slider"
    };

    static {
//        Font font1280 = new Font( "Lucida Sans", Font.PLAIN, 26 );
//        Font font1040 = new Font( "Lucida Sans", Font.BOLD, 14 );

        Font font1280 = new Font( "Lucida Sans", Font.PLAIN, 18 );
        Font font1040 = new Font( "Lucida Sans", Font.BOLD, 10 );
        
        Font uifont = font1040;

        if( Toolkit.getDefaultToolkit().getScreenSize().width > 1024 ) {
            uifont = font1280;
        }
        font = uifont;

        backgroundColor = CCK3Module.backgroundColor;
        foregroundColor = Color.black;
    }

    public CCKLookAndFeel() {
    }

    protected void initComponentDefaults( UIDefaults table ) {
        super.initComponentDefaults( table );
        ColorUIResource background = new ColorUIResource( backgroundColor );
        ColorUIResource foreground = new ColorUIResource( foregroundColor );
        FontUIResource fontResource = new FontUIResource( font );
        FontUIResource borderFont=new FontUIResource( new Font( "Lucida Sans",Font.ITALIC, 16));

        InsetsUIResource insets = new InsetsUIResource( 2, 2, 2, 2 );
        ArrayList def = new ArrayList();
        for( int i = 0; i < types.length; i++ ) {
            String type = types[i];
            def.add( type + ".font" );
            def.add( fontResource );
            def.add( type + ".foreground" );
            def.add( foreground );
            def.add( type + ".background" );
            def.add( background );
            def.add( type + ".margin" );
            def.add( insets );
        }
        def.add("TitledBorder.font");
        def.add(borderFont );
        Object[] defaults = def.toArray();
        table.putDefaults( defaults );
    }


}