package edu.colorado.phet.movingman.common.plaf;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class LectureLookAndFeel extends MetalLookAndFeel {
    Color backgroundColor = new Color(60, 80, 60);
    Color buttonBackgroundColor = new Color(60, 60, 100);
    Color controlTextColor = new Color(230, 230, 230);
    Font controlFont = new Font("SansSerif", Font.BOLD, 22);
    static String[] controlTypes = new String[]{
        "Menu",
        "MenuItem",
        "RadioButton",
        "Button",
        "CheckBox",
        "Label"
    };

    protected void initComponentDefaults(UIDefaults table) {
        super.initComponentDefaults(table);
        ArrayList def = new ArrayList();
        ColorUIResource textColor = new ColorUIResource(controlTextColor);
        FontUIResource fuir = new FontUIResource(controlFont);
        for (int i = 0; i < controlTypes.length; i++) {
            String controlType = controlTypes[i];
            def.add(controlType + ".foreground");
            def.add(textColor);
            def.add(controlType + ".font");
            def.add(fuir);
        }
        ColorUIResource background = new ColorUIResource(backgroundColor);
        ColorUIResource buttonBackground = new ColorUIResource(buttonBackgroundColor);

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
        def.addAll(Arrays.asList(defaults));

        table.putDefaults(def.toArray());
    }

}