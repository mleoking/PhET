package edu.colorado.phet.common.view.plaf;

import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.util.ArrayList;

public class LectureLookAndFeel2 extends MetalLookAndFeel {
    Font font;
    Color foregroundColor;
    Color backgroundColor;
    private ArrayList def;

    public LectureLookAndFeel2() {
        this(new Font("Lucida Sans", 0, 26), new Color(225, 240, 230), new Color(85, 70, 95));
    }

    public LectureLookAndFeel2(Font font, Color foregroundColor, Color backgroundColor) {
        this.font = font;
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
        install();
    }

    String[] types = new String[]{
        "Button", "MenuItem", "Panel", "Dialog",
        "CheckBox", "RadioButton", "ComboBox",
        "Menu", "MenuItem", "MenuBar",
        "Slider", "Spinner", "Border", "TextField", "TextArea",
        "Label", "TextPane", "FormattedTextField", "List"
    };

    protected void initComponentDefaults(UIDefaults table) {
        super.initComponentDefaults(table);
        ColorUIResource background = new ColorUIResource(backgroundColor);
        ColorUIResource foreground = new ColorUIResource(foregroundColor);
        FontUIResource fontResource = new FontUIResource(font);
        InsetsUIResource insets = new InsetsUIResource(2, 2, 2, 2);
        def = new ArrayList();
        for (int i = 0; i < types.length; i++) {
            String type = types[i];
            add(type + ".font", fontResource);
            add(type + ".foreground", foreground);
            add(type + ".background", background);
            add(type + ".margin", insets);
            add(type + ".inactiveForeground", foreground);
        }

        add("TitledBorder.font", fontResource);
        add("TitledBorder.titleColor", foreground);

        Object[] defaults = def.toArray();
        table.putDefaults(defaults);
    }

    private void add(String s, Object o) {
        def.add(s);
        def.add(o);
    }

    private void install() {
        UIManager.installLookAndFeel( SimStrings.get( "LectureLookAndFeel2.LectureLabel" ), getClass().getName());
    }


}