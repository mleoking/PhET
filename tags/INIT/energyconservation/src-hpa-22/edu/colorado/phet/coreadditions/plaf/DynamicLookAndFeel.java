package edu.colorado.phet.coreadditions.plaf;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.util.ArrayList;

public class DynamicLookAndFeel extends MetalLookAndFeel {
    Font font;
    Color foregroundColor;
    Color backgroundColor;

    public DynamicLookAndFeel( Font font, Color foregroundColor, Color backgroundColor ) {
        this.font = font;
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
    }

    String[] types = new String[]{
        "Button", "MenuItem", "Panel", "Dialog",
        "CheckBox", "RadioButton", "ComboBox",
        "Menu", "MenuItem", "MenuBar",
        "Slider"
    };

    protected void initComponentDefaults( UIDefaults table ) {
        super.initComponentDefaults( table );
        ColorUIResource background = new ColorUIResource( backgroundColor );
        ColorUIResource foreground = new ColorUIResource( foregroundColor );
        FontUIResource fontResource = new FontUIResource( font );
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
        Object[] defaults = def.toArray();
        table.putDefaults( defaults );
    }


}